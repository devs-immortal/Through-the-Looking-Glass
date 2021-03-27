package azzy.fabric.lookingglass.block;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static azzy.fabric.lookingglass.LookingGlassConstants.UNSTABLE_MULTI_BLOCK_FORMED;

@SuppressWarnings("deprecated")
public class UnstableEnchanterBlock extends LookingGlassBlock {
    public static final BooleanProperty MULTI_BLOCK_FORMED = BooleanProperty.of(UNSTABLE_MULTI_BLOCK_FORMED);

    public UnstableEnchanterBlock(Settings settings) {
        super(settings, false);
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        boolean multiblockFormed = state.get(MULTI_BLOCK_FORMED);
        // The unstable multiblock isn't formed.  Just return.
        if (!multiblockFormed) {
            super.onEntityCollision(state, world, pos, entity);
            return;
        }
    }
}
