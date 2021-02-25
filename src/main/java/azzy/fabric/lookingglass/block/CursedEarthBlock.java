package azzy.fabric.lookingglass.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

@SuppressWarnings("deprecated")
public class CursedEarthBlock extends LookingGlassBlock {
    public CursedEarthBlock(FabricBlockSettings settings) {
        super(settings, false);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (!world.isClient)
            doTick(world, pos, random);
    }

    /**
     * Properties of cursed earth
     * If cursed earth is on fire, it'll spread the fire to nearby cursed earth blocks
     * If it's on fire, it will also burn itself out by turning into sand, gravel or dirt
     * If it's in the dark, it will spread itself at a very slow rate to nearby dirt, grass or path blocks
     * If it's in the dark, it will spawn mobs that are more powerful than usual, but also die within a set timer.
     *
     * @param world  The world object
     * @param pos    The position of the cursed earth block
     * @param random A Random object
     */
    protected void doTick(ServerWorld world, BlockPos pos, Random random) {
        int light = world.getLightLevel(pos.up());
        System.out.println("Reached here...: '" + light + "'.");

        // Light level 14 is torch.
        // Light level 15 is fire, lava, etc.
        if (light >= 14) {
            // For every nearby block, if we're on fire, spread it to nearby blocks.
            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    for (int z = -1; z < 2; z++) {
                        BlockState blockState = world.getBlockState(pos.up());
                        boolean onFire = (blockState.getMaterial() == Material.FIRE);

                        BlockPos tmpPos = pos.add(x, y, z);
                        // If the nearby block is a cursed earth block
                        if (onFire && (world.getBlockState(tmpPos).getBlock() == LookingGlassBlocks.CURSED_EARTH_BLOCK)) {
                            if (random.nextInt(5) == 0) {
                                // 1 in 5 chance to spread fire to this neighbor.
                                world.setBlockState(tmpPos.up(), Blocks.FIRE.getDefaultState());
                            }
                        }
                        // If the nearby block is on fire
                        else if (world.getBlockState(tmpPos.up()).getMaterial() == Material.FIRE) {
                            if (random.nextInt(5) == 0) {
                                // 1 in 5 chance to catch self fire.
                                world.setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
                            }
                        }
                    }
                }
            }
        } else if (light <= 7) {
            // It's dark.  Spread the curse to nearby blocks.
            for (int x = -1; x < 2; x++) {
                for (int y = -1; y < 2; y++) {
                    for (int z = -1; z < 2; z++) {
                        BlockPos tmpPos = pos.add(x, y, z);
                        Block neighbor = world.getBlockState(tmpPos).getBlock();
                        if ((neighbor == Blocks.DIRT) || (neighbor == Blocks.GRASS_BLOCK) || (neighbor == Blocks.GRASS_PATH) || (neighbor == Blocks.COARSE_DIRT)) {
                            if (random.nextInt(50) == 0) {
                                // 1 in 50 chance to turn neighbor into cursed earth.
                                world.setBlockState(tmpPos, LookingGlassBlocks.CURSED_EARTH_BLOCK.getDefaultState());
                            }
                        }
                    }
                }
            }
        }

        BlockState blockState = world.getBlockState(pos.up());
        boolean onFire = (blockState.getMaterial() == Material.FIRE);

        if (onFire) {
            // If the block catches on fire, it turns to dirt, gravel or sand.
            turnCursedEarthToSlag(world, pos, random);
        }

        // TODO 4:  Spawn random mobs as required
    }

    private void turnCursedEarthToSlag(ServerWorld world, BlockPos pos, Random random) {
        int replaceFlag = random.nextInt(10);

        switch (replaceFlag) {
            case 0:
                // Replace to dirt.
                world.setBlockState(pos, Blocks.DIRT.getDefaultState());
                return;
            case 1:
                // Replace to gravel.
                world.setBlockState(pos, Blocks.GRAVEL.getDefaultState());
                return;
            case 2:
                // Replace to sand.
                world.setBlockState(pos, Blocks.SAND.getDefaultState());
        }
    }
}