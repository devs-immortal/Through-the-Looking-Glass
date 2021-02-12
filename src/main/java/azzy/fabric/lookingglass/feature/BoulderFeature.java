package azzy.fabric.lookingglass.feature;

import com.mojang.serialization.Codec;
import net.minecraft.block.Block;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.StructureWorldAccess;
import net.minecraft.world.gen.chunk.ChunkGenerator;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.SingleStateFeatureConfig;

import java.util.Iterator;
import java.util.Random;

public class BoulderFeature extends Feature<SingleStateFeatureConfig> {

    public BoulderFeature(Codec<SingleStateFeatureConfig> configCodec) {
        super(configCodec);
    }

    @Override
    public boolean generate(StructureWorldAccess world, ChunkGenerator chunkGenerator, Random random, BlockPos pos, SingleStateFeatureConfig config) {
        for(; pos.getY() > 3; pos = pos.down()) {
            if(world.isWater(pos) || world.isWater(pos))
                return false;
            if (!world.isAir(pos.down())) {
                Block block = world.getBlockState(pos.down()).getBlock();
                if (BlockTags.SAND.contains(block) || isStone(block)) {
                    break;
                }
            }
        }

        if (pos.getY() <= 3) {
            return false;
        } else {
            for(int i = 0; i < 5; ++i) {
                int j = random.nextInt(3);
                int k = random.nextInt(3);
                int l = random.nextInt(3);
                float f = (float)(j + k + l) * 0.333F + 0.5F;
                Iterator var11 = BlockPos.iterate(pos.add(-j, -k, -l), pos.add(j, k, l)).iterator();

                while(var11.hasNext()) {
                    BlockPos blockPos2 = (BlockPos)var11.next();
                    if (blockPos2.getSquaredDistance(pos) <= (double)(f * f)) {
                        world.setBlockState(blockPos2, config.state, 4);
                    }
                }

                pos = pos.add(-1 + random.nextInt(3), -random.nextInt(3), -1 + random.nextInt(3));
            }

            return true;
        }
    }
}
