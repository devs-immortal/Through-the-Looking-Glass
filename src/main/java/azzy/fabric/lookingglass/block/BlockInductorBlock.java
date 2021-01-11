package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.util.BlockEntityMover;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;

public class BlockInductorBlock extends AbstractInductorBlock {

    public BlockInductorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(getStateManager().getDefaultState().with(POWERED, false));
    }

    @Override
    protected void internalInduct(World world, BlockState inductor, Direction facing, BlockPos pos) {
        WorldBorder border = world.getWorldBorder();
        int max = 15;
        for (int i = 1; i <= 15; i++) {
            BlockPos probePos = pos.offset(facing, i);
            BlockState state = world.getBlockState(probePos);
            if(!world.isChunkLoaded(probePos)) {
                max = i - 1;
                break;
            }
            if(state.getBlock() instanceof AbstractTesseractBlock) {
                ((AbstractTesseractBlock) state.getBlock()).activate(world, probePos, facing, pos);
                return;
            }
            if(state.getHardness(world, probePos) < 0.0F || world.getBlockState(probePos.offset(facing)).isOf(TTLGBlocks.INTERMINAL_CORE) || state.isOf(TTLGBlocks.INTERMINAL_CORE) || !border.contains(probePos)) {
                max = i - 1;
                break;
            }
        }
        for(; max >= 1; max--) {
            BlockPos probePos = pos.offset(facing, max);
            BlockPos offPos = probePos.offset(facing);
            if(world.isAir(offPos) && World.isInBuildLimit(offPos) && border.contains(offPos)) {
                BlockState state = world.getBlockState(probePos);
                BlockEntity entity = world.getBlockEntity(probePos);
                if(!(state.getHardness(world, probePos) < 0.0F || state.isAir() || state.getFluidState() != Fluids.EMPTY.getDefaultState())) {
                    if(entity == null) {
                        world.removeBlock(probePos, true);
                        world.setBlockState(offPos, state);
                    }
                    else if(state.getBlock() instanceof AbstractTesseractBlock){
                    }
                    else {
                        BlockEntityMover.tryMoveEntity(world, probePos, facing);
                    }
                }
            }
        }
    }
}
