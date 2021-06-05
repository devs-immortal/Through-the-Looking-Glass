package azzy.fabric.lookingglass.particle;


import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.particle.AscendingParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.SpriteProvider;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particle.DefaultParticleType;

import java.util.Random;

public class NullTearParticle extends AscendingParticle {
    protected NullTearParticle(ClientWorld world, double x, double y, double z, double velocityX, double velocityY, double velocityZ, float scaleMultiplier, SpriteProvider spriteProvider) {
        super(world, x, y, z, 0.1F, -0.1F, 0.1F, velocityX, velocityY, velocityZ, scaleMultiplier, spriteProvider, 0.0F, 200, -5.0E-4f, false);
        this.colorRed = 0.7294118F;
        this.colorGreen = 0.69411767F;
        this.colorBlue = 0.7607843F;
    }

    @Environment(EnvType.CLIENT)
    public record Factory(SpriteProvider spriteProvider) implements ParticleFactory<DefaultParticleType> {
        public Particle createParticle(DefaultParticleType defaultParticleType, ClientWorld clientWorld, double d, double e, double f, double g, double h, double i) {
            Random random = clientWorld.random;
            double j = (double) random.nextFloat() * -1.9D * (double) random.nextFloat() * 0.1D;
            double k = (double) random.nextFloat() * -0.5D * (double) random.nextFloat() * 0.1D * 5.0D;
            double l = (double) random.nextFloat() * -1.9D * (double) random.nextFloat() * 0.1D;
            return new NullTearParticle(clientWorld, d, e, f, j, k, l, 1.0F, this.spriteProvider);
        }
    }
}
