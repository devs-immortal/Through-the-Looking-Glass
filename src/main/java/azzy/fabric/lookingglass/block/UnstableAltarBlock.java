package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.UnstableAltarEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import static azzy.fabric.lookingglass.LookingGlassConstants.UNSTABLE_MULTI_BLOCK_FORMED;

@SuppressWarnings("deprecated")
public class UnstableAltarBlock extends LookingGlassBlock implements BlockEntityProvider {
    public static final BooleanProperty MULTI_BLOCK_FORMED = BooleanProperty.of(UNSTABLE_MULTI_BLOCK_FORMED);
    private static final VoxelShape SHAPE = Block.createCuboidShape(3, 0, 3, 13, 13, 13);

    public UnstableAltarBlock(Settings settings) {
        super(settings, false);
        BlockState defaultState = getStateManager().getDefaultState();
        setDefaultState(defaultState.with(MULTI_BLOCK_FORMED, false).with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(MULTI_BLOCK_FORMED);
        super.appendProperties(builder);
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

    public void overrideDefaultState(BlockState state) {
        setDefaultState(state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        // Nothing to do if the player is using while in sneak mode.
        if (player.isInSneakingPose())
            return ActionResult.PASS;

        BlockEntity entity = world.getBlockEntity(pos);
        ItemStack stack = player.getStackInHand(hand);

        UnstableAltarEntity unstableAltarEntity = (UnstableAltarEntity) entity;

        // Shouldn't happen in normal usage, but better safe than sorry.
        if (unstableAltarEntity == null)
            return ActionResult.PASS;
        ItemStack inventory = unstableAltarEntity.getStack(0);
        if (inventory.isEmpty()) {
            // There's nothing in the display stand.  Take the stack from player and return.
            unstableAltarEntity.setStack(0, stack);
            player.setStackInHand(hand, ItemStack.EMPTY);
            return ActionResult.SUCCESS;
        }

        // Get the max stack size.
        int maxStackSize = inventory.getMaxCount();
        int currentCount = inventory.getCount();

        // The display stand is already full.  Return doing nothing.
        if (currentCount == maxStackSize) {
            return ActionResult.FAIL;
        }

        int difference = maxStackSize - currentCount;

        // Move as much as we possibly can.
        int toMoveCount = Math.min(difference, stack.getCount());
        ItemStack toMoveStack = stack.copy();
        toMoveStack.setCount(toMoveCount);

        unstableAltarEntity.setStack(0, toMoveStack);
        stack.decrement(toMoveCount);

        return ActionResult.SUCCESS;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new UnstableAltarEntity();
    }
}
