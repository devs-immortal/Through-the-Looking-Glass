package azzy.fabric.lookingglass.transport;

import azzy.fabric.lookingglass.block.PowerConduitBlock;
import azzy.fabric.lookingglass.blockentity.PowerConduitEntity;
import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.energy.EnergyIo;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.level.LevelProperties;

import java.util.*;
import java.util.stream.Collectors;

public class PowerNetwork implements EnergyIo {

    public final UUID networkId;
    public final List<BlockPos> invalidCables;
    public final PowerConduitBlock cableType;

    private final List<BlockPos> cables;
    private final World world;
    private double cachedMaxEnergy;
    private double energy;
    private boolean revalidationCached;
    private boolean revalidationRequestTick;

    public PowerNetwork(World world, PowerConduitBlock cableType, UUID networkId) {
        this.world = world;
        this.networkId = networkId;
        this.cableType = cableType;
        cables = new ArrayList<>();
        invalidCables = new ArrayList<>();
    }

    public PowerNetwork(World world, NbtCompound nbt) {
        this.world = world;
        this.networkId = nbt.getUuid("networkId");
        cables = Arrays.stream(nbt.getLongArray("cables")).mapToObj(BlockPos::fromLong).collect(Collectors.toList());
        invalidCables = Arrays.stream(nbt.getLongArray("invalid")).mapToObj(BlockPos::fromLong).collect(Collectors.toList());
        cableType = (PowerConduitBlock) Registry.BLOCK.get(Identifier.tryParse(nbt.getString("cableType")));
        energy = nbt.getDouble("energy");
        cachedMaxEnergy = nbt.getDouble("maxEnergy");
        revalidationCached = nbt.getBoolean("revalidating");
    }

    public void tick() {
        if(revalidationCached && !revalidationRequestTick) {
            revalidateCables();
            revalidateEnergyCapacity();
            revalidationCached = false;
        }

        if(revalidationRequestTick) {
            revalidationRequestTick = false;
        }
    }

    public void requestNetworkRevalidation() {
        revalidationCached = true;
        revalidationRequestTick = true;
    }

    public void revalidateCables() {
        List<BlockPos> invalidatedCables = cables
                .stream()
                .filter(pos -> !world.getBlockState(pos).isOf(cableType))
                .collect(Collectors.toList());

        if(!invalidatedCables.isEmpty()) {
            for (BlockPos cable : invalidatedCables) {
                int adjacency = 0;

                for (Direction direction : Direction.values()) {
                    BlockEntity entity = world.getBlockEntity(cable.offset(direction));
                    if(entity instanceof PowerConduitEntity && entity.getCachedState().isOf(cableType)) {
                        adjacency++;
                    }
                }

                if(adjacency > 1) {
                    invalidCables.add(cable);
                }
                cables.remove(cable);
            }

        }


    }

    public void revalidateEnergyCapacity() {
        cachedMaxEnergy = cables.size() * cableType.transferRate;

        if(energy > cachedMaxEnergy)
            energy = cachedMaxEnergy;
    }

    public void yieldTo(PowerNetwork newNetwork, NetworkManager manager) {
        manager.managedNetworks.remove(networkId);
        cables.stream()
                .map(world::getBlockEntity)
                .filter(Objects::nonNull)
                .forEach(conduit -> {
                    ((PowerConduitEntity) conduit).switchNetwork(newNetwork, manager);
                    newNetwork.appendCable(conduit.getPos());
                });
        newNetwork.energy += energy;
        cables.clear();
    }

    public boolean appendCable(BlockPos pos) {
        if(!cables.contains(pos)) {
            cables.add(pos);
            cachedMaxEnergy += cableType.transferRate;
            return true;
        }
        return false;
    }

    public boolean containsCable(BlockPos pos, PowerConduitBlock cableType) {
        return this.cableType == cableType && cables.contains(pos);
    }

    public boolean isEmpty() {
        return cables.isEmpty();
    }

    public int getConnectedCables() {
        return cables.size();
    }

    @Override
    public double getEnergyCapacity() {
        return cachedMaxEnergy;
    }

    @Override
    public double getEnergy() {
        return energy;
    }

    public boolean isEnergyFull() {
        if(energy > cachedMaxEnergy)
            energy = cachedMaxEnergy;
        return energy >= cachedMaxEnergy;
    }

    public boolean isEnergyEmpty() {
        if(energy < 0.0)
            energy = 0.0;
        return energy <= 0.0;
    }

    public double insertInternal(double amount, double cap, Simulation simulation) {
        double minReturn = Math.max(0, amount - cap);
        return minReturn + insert(Math.min(amount, cap), simulation);
    }

    @Override
    public double insert(double amount, Simulation simulation) {
        if (simulation == Simulation.SIMULATE) {
            return Math.max(0, amount - (cachedMaxEnergy - energy));
        }
        double remnant = Math.max(0, amount - (cachedMaxEnergy - energy));
        energy += (amount - remnant);
        return remnant;
    }

    public double extractInternal(double amount, double cap, Simulation simulation) {
        return extract(Math.min(amount, cap), simulation);
    }

    @Override
    public double extract(double maxAmount, Simulation simulation) {
        if(simulation == Simulation.SIMULATE) {
            return Math.min(energy, maxAmount);
        }
        double drain = Math.min(energy, maxAmount);
        energy -= drain;
        return drain;
    }

    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putUuid("networkId", networkId);
        nbt.putLongArray("cables", cables.stream().mapToLong(BlockPos::asLong).toArray());
        nbt.putLongArray("invalid", invalidCables.stream().mapToLong(BlockPos::asLong).toArray());
        nbt.putString("cableType", Registry.BLOCK.getId(cableType).toString());
        nbt.putDouble("energy", energy);
        nbt.putDouble("maxEnergy", cachedMaxEnergy);
        nbt.putBoolean("revalidating", revalidationCached);
        return nbt;
    }

    @Override
    public String toString() {
        LevelProperties properties = world.getLevelProperties() instanceof LevelProperties ? (LevelProperties) world.getLevelProperties() : null;
        List<Chunk> chunks = cables.stream().map(world::getChunk).distinct().collect(Collectors.toList());
        StringBuilder text = new StringBuilder("Power network: " + networkId.toString() + " - level: " + (properties == null ? "UNAVAILABLE" : properties.getLevelName()) + " - world: " + world.getRegistryKey().getValue().toString() + " - chunks: ");
        chunks.forEach(chunk -> text.append(chunk.getPos().toString()).append(" "));
        return text.toString();
    }
}
