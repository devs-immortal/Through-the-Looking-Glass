package azzy.fabric.lookingglass.util;

import net.minecraft.item.ItemStack;
import net.minecraft.particle.DustParticleEffect;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

@SuppressWarnings("unused")
public class ParticleUtils {
    public static final DustParticleEffect RED = new DustParticleEffect(1.0F, 0.0F, 0.0F, 1.0F);
    public static final DustParticleEffect GREEN = new DustParticleEffect(0.0F, 1.0F, 0.0F, 1.0F);
    public static final DustParticleEffect BLUE = new DustParticleEffect(0.0F, 0.0F, 1.0F, 1.0F);
    public static final DustParticleEffect CYAN = new DustParticleEffect(0.0F, 1.0F, 1.0F, 1.0F);
    public static final DustParticleEffect PURPLE = new DustParticleEffect(1.0F, 0.0F, 1.0F, 1.0F);

    public static <T extends ParticleEffect> void spawnParticles(T particle, ServerWorld world, BlockPos pos, double yOffset, int count) {
        double x = pos.getX() + 0.5D;
        double y = pos.getY() + yOffset;
        double z = pos.getZ() + 0.5D;

        world.spawnParticles(particle, x, y, z, count, 0, 0, 0, 0.1D);
    }

    public static void spawnItemParticles(ServerWorld serverWorld, BlockPos enchanterBlockPos, BlockPos pedestalPos, ItemStack stack) {
        double x = pedestalPos.getX() + (serverWorld.getRandom().nextDouble() * 0.2D) + 0.4D;
        double y = pedestalPos.getY() + (serverWorld.getRandom().nextDouble() * 0.2D) + 1.2D;
        double z = pedestalPos.getZ() + (serverWorld.getRandom().nextDouble() * 0.2D) + 0.4D;

        double velX = enchanterBlockPos.getX() - pedestalPos.getX();
        double velY = 0.25D;
        double velZ = enchanterBlockPos.getZ() - pedestalPos.getZ();

        serverWorld.spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, stack), x, y, z, 0, velX, velY, velZ, 0.18D);
    }
}
