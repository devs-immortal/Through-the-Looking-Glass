package azzy.fabric.lookingglass.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import java.util.Random;

public class CursedEarthBlock extends LookingGlassBlock {
    public CursedEarthBlock(FabricBlockSettings settings) {
        super(settings, false);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        doTick(world, pos, true);
    }

    protected void doTick(ServerWorld world,BlockPos pos, boolean fastSpreading) {
        int light = world.getLightLevel(pos.up());
        System.out.println("Reached here...");
    }
}