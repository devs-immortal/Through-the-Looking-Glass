package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.particle.TTLGParticles;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import java.util.Random;

public class InterminalCoreBlock extends CoreBlock {
    public InterminalCoreBlock(Settings settings) {
        super(settings);
    }

    @Environment(EnvType.CLIENT)
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        if(random.nextInt(2) == 0) {
            Direction direction = Direction.random(random);
            if (direction != Direction.UP) {
                BlockPos blockPos = pos.offset(direction);
                double d = 0.1 + (world.getRandom().nextDouble() / 1.4);
                double e = 0.05 +  (world.getRandom().nextDouble() / 1.35);
                double f = 0.1 + (world.getRandom().nextDouble() / 1.4);
                world.addParticle(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, (double)pos.getX() + d, (double)pos.getY() + e, (double)pos.getZ() + f, 0.0D, 0.0D, 0.0D);
            }
        }
    }
}
