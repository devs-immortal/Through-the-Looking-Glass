package azzy.fabric.lookingglass.transport;

import dev.onyxstudios.cca.api.v3.component.sync.AutoSyncedComponent;
import dev.onyxstudios.cca.api.v3.component.tick.ServerTickingComponent;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import net.minecraft.client.MinecraftClient;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.World;
import net.minecraft.world.level.LevelProperties;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;

import static azzy.fabric.lookingglass.LookingGlassCommon.FFLog;

public class NetworkManager implements AutoSyncedComponent, ServerTickingComponent {

    public final World world;
    public final Object2ObjectOpenHashMap<UUID, PowerNetwork> managedNetworks = new Object2ObjectOpenHashMap<>(32);

    public NetworkManager(World world) {
        this.world = world;
        if(world.getLevelProperties() instanceof LevelProperties)
            FFLog.info((!world.isClient() ? "Initializing 'ServerLevel" : "Pairing 'Client") + "[" + ((LevelProperties) world.getLevelProperties()).getLevelName() + "]'/" + world.getRegistryKey().getValue().toString() + " network manager");
    }

    @Override
    public void serverTick() {
        managedNetworks.values().forEach(PowerNetwork::tick);
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
