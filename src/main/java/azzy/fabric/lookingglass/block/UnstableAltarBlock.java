package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.DisplayPedestalEntity;
import azzy.fabric.lookingglass.blockentity.LookingGlassBE;
import azzy.fabric.lookingglass.blockentity.UnstableAltarEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.explosion.Explosion;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@SuppressWarnings("deprecated")
public class UnstableAltarBlock extends LookingGlassBlock implements BlockEntityProvider {
    private static final VoxelShape SHAPE = Block.createCuboidShape(3, 0, 3, 13, 13, 13);

    public UnstableAltarBlock(Settings settings) {
        super(settings, false);
        BlockState defaultState = getStateManager().getDefaultState();
        setDefaultState(defaultState.with(WATERLOGGED, false));
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        // Nothing to do if the player is using while in sneak mode.
        if (player.isInSneakingPose())
            return ActionResult.PASS;

        BlockEntity entity = world.getBlockEntity(pos);
        ItemStack stack = player.getStackInHand(hand);

        // The player's hand is empty.  Return doing nothing.
        if (ItemStack.EMPTY.equals(stack))
            return ActionResult.PASS;

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

        // The item in player's hand is different from the item in the pedestal.
        if (!stack.isItemEqual(inventory)) {
            return ActionResult.PASS;
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
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return LookingGlassBE::tickStatic;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new UnstableAltarEntity(pos, state);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        UnstableAltarEntity unstableAltarEntity = (UnstableAltarEntity) world.getBlockEntity(pos);
        if (unstableAltarEntity != null) {
            if (unstableAltarEntity.isMultiBlockFormed) {
                // Multiblock is formed successfully.  Destroy it since the user is breaking the pedestal, which is a part of it.
                List<LookingGlassBE> multiBlockEntitiesList = unstableAltarEntity.multiBlockBlockEntitiesList;

                for (LookingGlassBE lookingGlassBE : multiBlockEntitiesList) {
                    if (lookingGlassBE instanceof UnstableAltarEntity) {
                        UnstableAltarEntity iteratedUnstableAltarEntity = (UnstableAltarEntity) lookingGlassBE;
                        iteratedUnstableAltarEntity.multiBlockBlockEntitiesList = null;
                        iteratedUnstableAltarEntity.multiBlockPositionsList = null;
                        iteratedUnstableAltarEntity.isMultiBlockFormed = false;
                    } else {
                        DisplayPedestalEntity iteratedDisplayPedestalEntity = (DisplayPedestalEntity) lookingGlassBE;
                        iteratedDisplayPedestalEntity.multiBlockBlockEntitiesList = null;
                        iteratedDisplayPedestalEntity.multiBlockPositionsList = null;
                        iteratedDisplayPedestalEntity.isMultiBlockFormed = false;
                    }
                }
            }
        }

        world.createExplosion(player, pos.getX(), pos.getY(), pos.getZ(), 6.0f, Explosion.DestructionType.DESTROY);
        super.onBreak(world, pos, state, player);
    }
}
