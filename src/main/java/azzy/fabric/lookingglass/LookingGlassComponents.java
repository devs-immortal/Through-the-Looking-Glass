package azzy.fabric.lookingglass;

import azzy.fabric.lookingglass.transport.NetworkManager;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.component.ComponentRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import net.minecraft.util.Identifier;

public class LookingGlassComponents implements WorldComponentInitializer {

    public static final ComponentKey<NetworkManager> NETWORK_MANAGER_KEY = ComponentRegistry.getOrCreate(new Identifier(LookingGlassCommon.MODID, "network_manager"), NetworkManager.class);

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        registry.register(NETWORK_MANAGER_KEY, NetworkManager::new);
    }
}
