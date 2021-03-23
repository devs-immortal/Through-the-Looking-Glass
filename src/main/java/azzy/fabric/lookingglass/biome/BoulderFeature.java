package azzy.fabric.lookingglass.biome;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;

import java.util.Iterator;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class BoulderFeature extends Feature<SingleStateFeatureConfig> {

    public BoulderFeature(Codec<SingleStateFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random,  BlockPos pos, SingleStateFeatureConfig config) {
        for (int loops = 0; !world.getBlockState(pos).isOf(Blocks.BEDROCK) && loops < 500; pos = pos.down()) {

            loops++;

            if (random.nextDouble() < 0.95)
                return false;

            AtomicBoolean invalid = new AtomicBoolean(false);

            BlockPos.iterate(pos.add(-2, -2, -2), pos.add(2, 2, 2)).forEach(test -> {
                if (world.isWater(test))
                    invalid.set(true);
            });

            if (invalid.get())
                return false;

            if (!world.isAir(pos.down())) {
                Block block = world.getBlockState(pos.down()).getBlock();
                if (BlockTags.SAND.contains(block) || isStone(block) || block.is(LookingGlassBlocks.WHITEDUST)) {
                    break;
                }
            }
        }

        int width = random.nextInt(8) + 3;
        int length = random.nextInt(8) + 3;
        int height = random.nextInt(8) + 3;

        BlockPos finalPos = pos.down((int) (height / 1.85));
        BlockPos.iterate(pos.add(-width, -height, -length), pos.add(width, height, length)).forEach(placePos -> {
            if(testElipsoid(width, height, length, placePos, finalPos)) {
                world.setBlockState(placePos, config.state, 2);
            }
        });

            return true;
    }

    private boolean testElipsoid(int a, int b, int c, BlockPos test, BlockPos center) {
        int x = Math.abs(test.getX() - center.getX());
        int y = Math.abs(test.getY() - center.getY());
        int z = Math.abs(test.getZ() - center.getZ());
        return Math.pow(x, 2) / Math.pow(a, 2) + Math.pow(y, 2) / Math.pow(b, 2) + Math.pow(z, 2) / Math.pow(c, 2)  <= 1;
    }
}
