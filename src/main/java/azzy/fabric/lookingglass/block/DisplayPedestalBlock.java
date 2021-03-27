package azzy.fabric.lookingglass.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;

@SuppressWarnings("deprecated")
public class DisplayPedestalBlock extends LookingGlassBlock {
    private static final VoxelShape SHAPE = Block.createCuboidShape(5, 0, 5, 11, 12, 11);

    public DisplayPedestalBlock(Settings settings) {
        super(settings, false);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}
