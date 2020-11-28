package azzy.fabric.lookingglass.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

public class CoreBlock extends Block {

    private static final VoxelShape SHAPE = Block.createCuboidShape(4, 4, 4, 12, 12,12);

    public CoreBlock(Settings settings) {
        super(settings.nonOpaque());
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }
}
