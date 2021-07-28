package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.LookingGlassComponents;
import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.block.PowerConduitBlock;
import azzy.fabric.lookingglass.transport.NetworkManager;
import azzy.fabric.lookingglass.transport.PowerNetwork;
import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.energy.EnergyApi;
import dev.technici4n.fasttransferlib.api.energy.EnergyIo;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiCache;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.UUID;

public class PowerConduitEntity extends BlockEntity implements EnergyIo {

    @NotNull
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private Optional<PowerNetwork> parentNetwork = Optional.empty();
    @Nullable
    private UUID networkId;
    private final Object2ObjectOpenHashMap<Direction, BlockApiCache<EnergyIo, Direction>> connectionCache = new Object2ObjectOpenHashMap<>();
    public final double transferRate;
    private int tickOffset = -1, longTickOffset = -1;
    private boolean waiting;

    public PowerConduitEntity(BlockEntityType<PowerConduitEntity> type, double transferRate, BlockPos pos, BlockState state) {
        super(type, pos, state);
        this.transferRate = transferRate;
    }

    public void tickInternal(World world, BlockPos pos, BlockState state) {

        if(tickOffset == -1)
            tickOffset = world.random.nextInt(2);

        if(longTickOffset == -1) {
            longTickOffset = world.random.nextInt(10);
        }

        NetworkManager manager = LookingGlassComponents.NETWORK_MANAGER_KEY.get(world);

        if(parentNetwork.isEmpty()) {
            if(networkId != null) {
                manager.tryJoinNetwork(networkId).ifPresent(network -> parentNetwork = Optional.of(network));
            }
            else {
                PowerNetwork network = manager.joinOrCreateNetwork(world, this, (PowerConduitBlock) state.getBlock(), pos);
                parentNetwork = Optional.of(network);
                networkId = network.networkId;
            }
        }
        else if(!world.isClient()) {
            if(networkId == null) {
                networkId = parentNetwork.get().networkId;
            }

            if((world.getTime() + (waiting ? longTickOffset : tickOffset)) % (waiting ? 10 : 2) == 0) {

                boolean active = false;

                for (Direction direction : Direction.values()) {
                    if(connectionCache.containsKey(direction)) {
                        EnergyIo io = connectionCache.get(direction).find(direction.getOpposite());

                        if(io != null && !(io instanceof PowerConduitEntity || io instanceof LookingGlassMachine)) {

                            PowerNetwork network = parentNetwork.get();

                            if(io instanceof CreativeEnergySourceEntity) {
                                network.insert(network.getEnergyCapacity() - network.getEnergy(), Simulation.ACT);
                                active = true;
                                continue;
                            }

                            double adjustedTransferRate = transferRate * 2;

                            if(!network.isEnergyFull() && io.supportsExtraction() && !io.supportsInsertion() && io.getEnergy() > 0) {
                                double max = io.extract(adjustedTransferRate, Simulation.SIMULATE);
                                double leftover = network.insertInternal(max, adjustedTransferRate, Simulation.SIMULATE);
                                double transfer = max - leftover;
                                io.extract(transfer, Simulation.ACT);
                                network.insertInternal(transfer, adjustedTransferRate, Simulation.ACT);
                                active = true;
                            }
                            else if(!network.isEnergyEmpty() && io.supportsInsertion() && io.getEnergy() < io.getEnergyCapacity()) {
                                double max = network.extractInternal(io.getEnergyCapacity() - io.getEnergy(), adjustedTransferRate, Simulation.SIMULATE);
                                double leftover = io.insert(max, Simulation.SIMULATE);
                                double transfer = max - leftover;
                                network.extractInternal(transfer, adjustedTransferRate, Simulation.ACT);
                                io.insert(transfer, Simulation.ACT);
                                active = true;
                            }
                        }
                    }
                    else {
                        connectionCache.put(direction, BlockApiCache.create(EnergyApi.SIDED, (ServerWorld) world, pos.offset(direction)));
                    }
                }

                waiting = !active;
            }
        }
    }


    public static <T extends BlockEntity> void tick(World world, BlockPos pos, BlockState state, T t) {
        PowerConduitEntity conduit = (PowerConduitEntity) t;
        if(world != null) {
            conduit.tickInternal(world, pos, state);
        }
    }

    public void switchNetwork(@NotNull PowerNetwork network, NetworkManager manager) {
        if(manager == null || manager.world != this.world) {
            throw new IllegalCallerException("Cable tried to switch network at an invalid time! - " + pos.toString() + " - network - " + parentNetwork.map(PowerNetwork::toString).orElse("NONE"));
        }
        this.parentNetwork = Optional.of(network);
        this.networkId = network.networkId;
        markDirty();
    }

    public @NotNull Optional<PowerNetwork> getParentNetwork() {
        return parentNetwork;
    }

    @Override
    public void markRemoved() {
        parentNetwork.ifPresent(PowerNetwork::requestNetworkRevalidation);
        super.markRemoved();
    }

    @Override
    public double getEnergyCapacity() {
        return parentNetwork.map(PowerNetwork::getEnergyCapacity).orElse(0.0);
    }

    @Override
    public double getEnergy() {
        return parentNetwork.map(PowerNetwork::getEnergy).orElse(0.0);
    }

    @Override
    public double insert(double amount, Simulation simulation) {
        return parentNetwork.map(network -> network.insertInternal(amount, transferRate, simulation)).orElse(amount);
    }

    @Override
    public double extract(double maxAmount, Simulation simulation) {
        return parentNetwork.map(network -> network.extractInternal(maxAmount, transferRate, simulation)).orElse(0.0);
    }

    public void initialize(World world) {
        NetworkManager manager = NetworkManager.getNetworkManager(world);
        PowerNetwork network = manager.joinOrCreateNetwork(world, this, (PowerConduitBlock) getCachedState().getBlock(), pos);
        parentNetwork = Optional.of(network);
        networkId = network.networkId;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        if(networkId != null) {
            nbt.putUuid("network", networkId);
        }
        return super.writeNbt(nbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        if(nbt != null && nbt.contains("network")) {
            networkId = nbt.getUuid("network");
        }
        super.readNbt(nbt);
    }

    public static class Rose extends PowerConduitEntity {

        public Rose(BlockPos pos, BlockState state) {
            super(LookingGlassBlocks.ROSE_CABLE_ENTITY, 64, pos, state);
        }
    }

    public static class Enchanted extends PowerConduitEntity {

        public Enchanted(BlockPos pos, BlockState state) {
            super(LookingGlassBlocks.ENCHANTED_CABLE_ENTITY, 128, pos, state);
        }
    }

    public static class Brimsteel extends PowerConduitEntity {

        public Brimsteel(BlockPos pos, BlockState state) {
            super(LookingGlassBlocks.BRIMSTEEL_CABLE_ENTITY, 512, pos, state);
        }
    }
}
