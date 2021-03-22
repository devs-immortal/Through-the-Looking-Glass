package azzy.fabric.lookingglass.biome;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import com.google.common.collect.ImmutableList;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.BasaltColumnsFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.Random;

/**
 * Yes, I did in fact just copy paste the basalt column code.
 */
public class SaltPillarFeature extends Feature<BasaltColumnsFeatureConfig> {
    private static final ImmutableList<Block> field_24132;

    public SaltPillarFeature(Codec<BasaltColumnsFeatureConfig> codec) {
        super(codec);
    }

    public boolean generate(StructureWorldAccess structureWorldAccess, ChunkGenerator chunkGenerator, Random random, BlockPos blockPos, BasaltColumnsFeatureConfig basaltColumnsFeatureConfig) {
        int i = chunkGenerator.getSeaLevel();
        if (!method_30379(structureWorldAccess, i, blockPos.mutableCopy())) {
            return false;
        } else {
            int j = basaltColumnsFeatureConfig.getHeight().getValue(random);
            boolean bl = random.nextFloat() < 0.9F;
            int k = Math.min(j, bl ? 5 : 8);
            int l = bl ? 50 : 15;
            boolean bl2 = false;
            Iterator var12 = BlockPos.iterateRandomly(random, l, blockPos.getX() - k, blockPos.getY(), blockPos.getZ() - k, blockPos.getX() + k, blockPos.getY(), blockPos.getZ() + k).iterator();

            while(var12.hasNext()) {
                BlockPos blockPos2 = (BlockPos)var12.next();
                int m = j - blockPos2.getManhattanDistance(blockPos);
                if (m >= 0) {
                    bl2 |= this.method_27096(structureWorldAccess, i, blockPos2, m, basaltColumnsFeatureConfig.getReach().getValue(random));
                }
            }

            return bl2;
        }
    }

    private boolean method_27096(WorldAccess worldAccess, int i, BlockPos blockPos, int j, int k) {
        boolean bl = false;
        Iterator var7 = BlockPos.iterate(blockPos.getX() - k, blockPos.getY(), blockPos.getZ() - k, blockPos.getX() + k, blockPos.getY(), blockPos.getZ() + k).iterator();

        while(true) {
            int l;
            BlockPos blockPos3;
            do {
                if (!var7.hasNext()) {
                    return bl;
                }

                BlockPos blockPos2 = (BlockPos)var7.next();
                l = blockPos2.getManhattanDistance(blockPos);
                blockPos3 = method_27095(worldAccess, i, blockPos2) ? method_27094(worldAccess, i, blockPos2.mutableCopy(), l) : method_27098(worldAccess, blockPos2.mutableCopy(), l);
            } while(blockPos3 == null);

            int m = j - l / 2;

            for(BlockPos.Mutable mutable = blockPos3.mutableCopy(); m >= 0; --m) {
                if (method_27095(worldAccess, i, mutable)) {
                    this.setBlockState(worldAccess, mutable, LookingGlassBlocks.SALT_CLUSTER_BLOCK.getDefaultState());
                    mutable.move(Direction.UP);
                    bl = true;
                } else {
                    if (!worldAccess.getBlockState(mutable).isOf(LookingGlassBlocks.SALT_CLUSTER_BLOCK)) {
                        break;
                    }

                    mutable.move(Direction.UP);
                }
            }
        }
    }

    @Nullable
    private static BlockPos method_27094(WorldAccess worldAccess, int i, BlockPos.Mutable mutable, int j) {
        while(mutable.getY() > 1 && j > 0) {
            --j;
            if (method_30379(worldAccess, i, mutable)) {
                return mutable;
            }

            mutable.move(Direction.DOWN);
        }

        return null;
    }

    private static boolean method_30379(WorldAccess worldAccess, int i, BlockPos.Mutable mutable) {
        if (!method_27095(worldAccess, i, mutable)) {
            return false;
        } else {
            BlockState blockState = worldAccess.getBlockState(mutable.move(Direction.DOWN));
            mutable.move(Direction.UP);
            return !blockState.isAir() && !field_24132.contains(blockState.getBlock());
        }
    }

    @Nullable
    private static BlockPos method_27098(WorldAccess worldAccess, BlockPos.Mutable mutable, int i) {
        while(mutable.getY() < worldAccess.getHeight() && i > 0) {
            --i;
            BlockState blockState = worldAccess.getBlockState(mutable);
            if (field_24132.contains(blockState.getBlock())) {
                return null;
            }

            if (blockState.isAir()) {
                return mutable;
            }

            mutable.move(Direction.UP);
        }

        return null;
    }

    private static boolean method_27095(WorldAccess worldAccess, int i, BlockPos blockPos) {
        BlockState blockState = worldAccess.getBlockState(blockPos);
        return blockState.isAir() || blockState.isOf(Blocks.WATER) && blockPos.getY() <= i;
    }

    static {
        field_24132 = ImmutableList.of(Blocks.WATER, Blocks.LAVA, Blocks.BEDROCK, Blocks.MAGMA_BLOCK, Blocks.SOUL_SAND, Blocks.NETHER_BRICKS, Blocks.NETHER_BRICK_FENCE, Blocks.NETHER_BRICK_STAIRS, Blocks.NETHER_WART, Blocks.CHEST, Blocks.SPAWNER);
    }
}
