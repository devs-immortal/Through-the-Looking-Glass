package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.LookingGlass;
import azzy.fabric.lookingglass.util.BlockEntityMover;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;

public class BlockInductorBlock extends AbstractInductorBlock {

    public BlockInductorBlock(Settings settings) {
        super(settings);
    }

    @Override
    protected void internalInduct(World world, BlockState inductor, Direction facing, BlockPos pos) {
        WorldBorder border = world.getWorldBorder();
        for(int i = 15; i >= 1; i--) {
            BlockPos probePos = pos.offset(facing, i);
            BlockPos offPos = probePos.offset(facing);
            if(!World.isHeightInvalid(offPos) && border.contains(offPos)) {
                BlockState state = world.getBlockState(probePos);
                BlockEntity entity = world.getBlockEntity(probePos);
                if(!(state.getHardness(world, probePos) < 0.0F || state.isAir())) {
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
