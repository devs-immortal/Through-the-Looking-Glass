package azzy.fabric.lookingglass.transport;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.energy.EnergyApi;
import dev.technici4n.fasttransferlib.api.energy.EnergyIo;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.level.LevelProperties;

import java.util.*;
import java.util.stream.Collectors;

public class PowerNetwork implements EnergyIo {

    public final UUID networkId;
    public final List<BlockPos> invalidCables = new ArrayList<>();

    private final List<BlockPos> cables;
    private final World world;
    private double cachedMaxEnergy;
    private double energy;
    private boolean revalidationCached;
    private boolean revalidationRequestTick;

    public PowerNetwork(World world, UUID networkId) {
        this.world = world;
        this.networkId = networkId;
        cables = new ArrayList<>();
    }

    public PowerNetwork(World world, NbtCompound nbt) {
        this.world = world;
        this.networkId = nbt.getUuid("id");
        cables = Arrays.stream(nbt.getLongArray("cables")).mapToObj(BlockPos::fromLong).collect(Collectors.toList());
        energy = nbt.getDouble("energy");
        cachedMaxEnergy = nbt.getDouble("maxEnergy");
        revalidationCached = nbt.getBoolean("revalidating");
    }

    public void tick() {
        if(revalidationCached && !revalidationRequestTick) {
            revalidateCables();
            revalidateEnergyCapacity();
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
                .filter(pos -> TransportComponentValidator.isPowerCable(world.getBlockState(pos).getBlock()))
                .collect(Collectors.toList());

        if(!invalidatedCables.isEmpty()) {
            for (BlockPos cable : invalidatedCables) {
                int adjacency = 0;

                for (Direction direction : Direction.values()) {
                    if(TransportComponentValidator.isPowerCable(world.getBlockState(cable.offset(direction)).getBlock())) {
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
        cachedMaxEnergy = cables
                .stream()
                .map(pos -> EnergyApi.SIDED.find(world, pos, Direction.DOWN))
                .filter(Objects::nonNull)
                .mapToDouble(EnergyIo::getEnergyCapacity)
                .sum();
    }

    public boolean appendCable(BlockPos pos, EnergyIo cable) {
        if(!cables.contains(pos)) {
            cables.add(pos);
            cachedMaxEnergy += cable.getEnergyCapacity();
            return true;
        }
        return false;
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
        return null;
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
