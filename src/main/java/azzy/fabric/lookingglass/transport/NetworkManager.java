package azzy.fabric.lookingglass.transport;

import azzy.fabric.lookingglass.LookingGlassComponents;
import azzy.fabric.lookingglass.block.PowerConduitBlock;
import azzy.fabric.lookingglass.blockentity.PowerConduitEntity;
import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import dev.technici4n.fasttransferlib.api.energy.EnergyIo;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelProperties;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static azzy.fabric.lookingglass.LookingGlassCommon.FFLog;

public class NetworkManager implements AutoSyncedComponent, ServerTickingComponent {

    public static final Direction[] DIRECTIONS = Direction.values();

    public final World world;
    public final Object2ObjectOpenHashMap<UUID, PowerNetwork> managedNetworks = new Object2ObjectOpenHashMap<>(32);

    public NetworkManager(World world) {
        this.world = world;
        if(world.getLevelProperties() instanceof LevelProperties)
            FFLog.info((!world.isClient() ? "Initializing 'ServerLevel" : "Pairing 'Client") + "[" + ((LevelProperties) world.getLevelProperties()).getLevelName() + "]'/" + world.getRegistryKey().getValue().toString() + " network manager");
    }

    public static @NotNull NetworkManager getNetworkManager(World world) {
        return LookingGlassComponents.NETWORK_MANAGER_KEY.get(world);
    }

    @Override
    public void serverTick() {
        managedNetworks.values().forEach(network -> {
            if(!network.isEmpty()) {
                network.tick();
            }
            else {
                managedNetworks.remove(network.networkId);
                return;
            }

            if(!network.invalidCables.isEmpty()) {
                managedNetworks.remove(network.networkId);

                Block cableType = network.cableType;
                Queue<BlockPos> nextGen = new LinkedList<>();
                Set<BlockPos> traversedBlocks = new HashSet<>();

                for (BlockPos invalidCable : network.invalidCables) {
                    traversedBlocks.add(invalidCable);

                    for (Direction baseDir : DIRECTIONS) {

                        BlockPos start = invalidCable.offset(baseDir);
                        traversedBlocks.add(start);
                        nextGen.add(start);

                        while(!nextGen.isEmpty()) {
                            BlockPos next = nextGen.poll();
                            BlockEntity conduit = world.getBlockEntity(next);

                            if(conduit instanceof PowerConduitEntity && conduit.getCachedState().isOf(cableType)) {
                                PowerNetwork newNetwork = joinOrCreateNetwork(world, (EnergyIo) conduit, (PowerConduitBlock) conduit.getCachedState().getBlock(), next);
                                ((PowerConduitEntity) conduit).switchNetwork(newNetwork, this);

                                if(!managedNetworks.containsValue(newNetwork)) {
                                    managedNetworks.put(newNetwork.networkId, newNetwork);
                                }

                                for (Direction headDir : DIRECTIONS) {
                                    BlockPos head = next.offset(headDir);

                                    if(!traversedBlocks.contains(head)) {
                                        traversedBlocks.add(head);
                                        nextGen.add(head);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
    }

    public Optional<PowerNetwork> tryJoinNetwork(UUID networkId) {
        return Optional.ofNullable(managedNetworks.get(networkId));
    }

    public @NotNull PowerNetwork joinOrCreateNetwork(@NotNull World world, @NotNull EnergyIo cable, @NotNull PowerConduitBlock cableType, @NotNull BlockPos pos) {
        Iterator<UUID> networkIds = managedNetworks.keySet().iterator();
        List<PowerNetwork> adjNetworks = new ArrayList<>();
        while(networkIds.hasNext()) {
            UUID networkId = networkIds.next();
            PowerNetwork network = managedNetworks.get(networkId);
            if(network.containsCable(pos, cableType)) {
                return network;
            }
            else {
                for (Direction direction : DIRECTIONS) {
                    if(network.containsCable(pos.offset(direction), cableType)) {
                        adjNetworks.add(network);
                    }
                }

            }
        }

        if(!adjNetworks.isEmpty()) {
            PowerNetwork survivor;
            if(adjNetworks.size() == 1) {
                survivor = adjNetworks.get(0);
                survivor.appendCable(pos);
            }
            else {
                survivor = new PowerNetwork(world, cableType, UUID.randomUUID());
                adjNetworks.stream().filter(network -> network != survivor).forEach(network -> network.yieldTo(survivor, this));
                survivor.appendCable(pos);
                managedNetworks.put(survivor.networkId, survivor);
            }
            return survivor;
        }

        PowerNetwork network = new PowerNetwork(world, cableType, UUID.randomUUID());
        managedNetworks.put(network.networkId, network);
        if(!world.isClient()) {
            FFLog.info("Created new Power Network at " + pos.toString());
        }
        network.appendCable(pos);
        return network;
    }

    @Override
    public void readFromNbt(@NotNull NbtCompound tag) {
        int savedNetworks = tag.getInt("size");
        FFLog.info("Loading " + savedNetworks + " power networks");

        for (int i = 0; i < savedNetworks; i++) {
            PowerNetwork network = new PowerNetwork(world, tag.getCompound("" + i));

            if(network.getConnectedCables() < 1) {
                FFLog.error("Network " + network.networkId.toString() + " is empty, skipping!");
                continue;
            }

            managedNetworks.put(network.networkId, network);
            FFLog.info("Network loaded: " + network.toString());
        }
    }

    @Override
    public void writeToNbt(@NotNull NbtCompound tag) {
        List<UUID> uuids = managedNetworks.keySet().stream().toList();;
        tag.putInt("size", uuids.size());

        for (int i = 0; i < uuids.size(); i++) {
            UUID networkId = uuids.get(i);

            tag.putUuid("" + i, networkId);
            tag.put("" + i, managedNetworks.get(networkId).writeNbt(new NbtCompound()));
        }
    }
}
