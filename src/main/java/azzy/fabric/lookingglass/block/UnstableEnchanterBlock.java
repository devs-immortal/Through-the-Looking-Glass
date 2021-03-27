package azzy.fabric.lookingglass.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import static azzy.fabric.lookingglass.LookingGlassConstants.UNSTABLE_MULTI_BLOCK_FORMED;

@SuppressWarnings("deprecated")
public class UnstableEnchanterBlock extends LookingGlassBlock {
    public static final BooleanProperty MULTI_BLOCK_FORMED = BooleanProperty.of(UNSTABLE_MULTI_BLOCK_FORMED);
    private static final VoxelShape SHAPE = Block.createCuboidShape(3, 0, 3, 13, 13, 13);

    public UnstableEnchanterBlock(Settings settings) {
        super(settings, false);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        boolean multiblockFormed = false;
        if (state.contains(MULTI_BLOCK_FORMED))
            multiblockFormed = state.get(MULTI_BLOCK_FORMED);

        // The unstable multiblock isn't formed.  Just return.
        if (!multiblockFormed) {
            super.onEntityCollision(state, world, pos, entity);
            return;
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }
}
