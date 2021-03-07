package azzy.fabric.lookingglass.particle;

import azzy.fabric.lookingglass.LookingGlassCommon;
import net.fabricmc.fabric.api.client.particle.v1.ParticleFactoryRegistry;
import net.fabricmc.fabric.api.particle.v1.FabricParticleTypes;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LookingGlassParticles {

    public static DefaultParticleType NULL_TEAR_PARTICLE;

    public static void init() {
        NULL_TEAR_PARTICLE = Registry.register(Registry.PARTICLE_TYPE, new Identifier(LookingGlassCommon.MODID, "null_tear_particle"), FabricParticleTypes.simple(true));
        ParticleFactoryRegistry.getInstance().register(NULL_TEAR_PARTICLE, NullTearParticle.Factory::new);
    }


}
