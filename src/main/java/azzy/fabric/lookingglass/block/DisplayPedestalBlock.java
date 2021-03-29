package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.LookingGlassConstants;
import azzy.fabric.lookingglass.blockentity.DisplayPedestalEntity;
import azzy.fabric.lookingglass.blockentity.LookingGlassBE;
import azzy.fabric.lookingglass.blockentity.UnstableAltarEntity;
import azzy.fabric.lookingglass.util.ParticleUtils;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("deprecated")
public class DisplayPedestalBlock extends LookingGlassBlock implements BlockEntityProvider {
    private static final VoxelShape SHAPE = Block.createCuboidShape(5, 0, 5, 11, 12, 11);

    public DisplayPedestalBlock(Settings settings) {
        super(settings, false);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    /**
     * When the pedestal is placed, check to see if the unstable altar structure is complete.  If it is, set the BlockState appropriately.
     *
     * @param world     ServerWorld
     * @param pos       Placed Position
     * @param state     BlockState
     * @param placer    Player
     * @param itemStack Pedestal Stack
     */
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        boolean unstableAltarFound = false;
        BlockPos unstableAltarPos = null;
        List<BlockPos> pedestalPositionList = new ArrayList<>();

        for (int x = -3; x < 4; x += 3) {
            if (unstableAltarFound)
                break;

            for (int z = -3; z < 4; z += 3) {
                int posX = pos.getX() + x;
                int posZ = pos.getZ() + z;
                int posY = pos.getY();

                BlockPos tmpUnstableAltarPos = new BlockPos(posX, posY, posZ);
                BlockState tmpUnstableAltarState = world.getBlockState(tmpUnstableAltarPos);
                Block tmpUnstableAltarBlock = tmpUnstableAltarState.getBlock();
                if (LookingGlassBlocks.UNSTABLE_ALTAR_BLOCK.equals(tmpUnstableAltarBlock)) {
                    unstableAltarFound = true;
                    unstableAltarPos = tmpUnstableAltarPos;
                    break;
                }
            }
        }

        // Didn't find unstable altar anywhere.  Silently return doing nothing.
        if (!unstableAltarFound) {
            return;
        }

        // Found the unstable altar.  Now check in all directions to see if we can find display pedestals.
        for (int x = -3; x < 4; x += 3) {
            for (int z = -3; z < 4; z += 3) {
                // Central spot.  Since we already found the unstable altar, we don't need to check it again.
                if ((x == 0) && (z == 0))
                    continue;

                int posX = unstableAltarPos.getX() + x;
                int posY = unstableAltarPos.getY();
                int posZ = unstableAltarPos.getZ() + z;

                BlockPos pedestalPos = new BlockPos(posX, posY, posZ);
                BlockState pedestalBlockState = world.getBlockState(pedestalPos);
                Block pedestalBlock = pedestalBlockState.getBlock();

                // This block isn't a display pedestal.  Keep moving.
                if (!LookingGlassBlocks.DISPLAY_PEDESTAL_BLOCK.equals(pedestalBlock))
                    continue;

                pedestalPositionList.add(pedestalPos);
            }
        }

        if (pedestalPositionList.size() < 8) {
            // We didn't find all 8 pedestals.  Return silently doing nothing.
            return;
        }

        List<BlockPos> multiBlockPositionList = new ArrayList<>();
        multiBlockPositionList.add(unstableAltarPos);
        multiBlockPositionList.addAll(pedestalPositionList);
        List<LookingGlassBE> multiBlockTileEntityList = new ArrayList<>();
        multiBlockTileEntityList.add((LookingGlassBE) world.getBlockEntity(unstableAltarPos));

        for (BlockPos displayPedestalBlockPos : pedestalPositionList) {
            DisplayPedestalEntity displayPedestalEntity = (DisplayPedestalEntity) world.getBlockEntity(displayPedestalBlockPos);
            // This shouldn't be occurring either.  Return silently doing nothing.
            if (displayPedestalEntity == null) {
                LookingGlassCommon.FFLog.warn("Not able to get display pedestal's tile entity'.  Kindly contact the mod authors at '" + LookingGlassConstants.GITHUB_ISSUE_URL + "' to log an issue.");
                return;
            }
            multiBlockTileEntityList.add(displayPedestalEntity);
        }
        UnstableAltarEntity unstableAltarEntity = (UnstableAltarEntity) world.getBlockEntity(unstableAltarPos);
        // This shouldn't be occurring either.  Return silently doing nothing.
        if (unstableAltarEntity == null) {
            LookingGlassCommon.FFLog.warn("Not able to get unstable altar's tile entity.  Kindly contact the mod authors at '" + LookingGlassConstants.GITHUB_ISSUE_URL + "' to log an issue.");
            return;
        }
        // Set the flag that the multiblock has been formed! - STARTS
        unstableAltarEntity.isMultiBlockFormed = true;
        unstableAltarEntity.multiBlockBlockEntitiesList = multiBlockTileEntityList;
        unstableAltarEntity.multiBlockPositionsList = multiBlockPositionList;

        for (LookingGlassBE lookingGlassBE : multiBlockTileEntityList) {
            if (lookingGlassBE instanceof DisplayPedestalEntity) {
                DisplayPedestalEntity displayPedestalEntity = (DisplayPedestalEntity) lookingGlassBE;
                displayPedestalEntity.isMultiBlockFormed = true;
                displayPedestalEntity.multiBlockBlockEntitiesList = multiBlockTileEntityList;
                displayPedestalEntity.multiBlockPositionsList = multiBlockPositionList;
            }
        }
        // Set the flag that the multiblock has been formed! - ENDS

        // Celebratory particle effects.
        if (!world.isClient) {
            ParticleUtils.spawnParticles(ParticleUtils.GREEN, ((ServerWorld) world), unstableAltarPos, 1.2, 30);
            for (BlockPos pedestalPos : pedestalPositionList) {
                ParticleUtils.spawnParticles(ParticleUtils.GREEN, ((ServerWorld) world), pedestalPos, 1.2, 30);
            }
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        // Nothing to do if the player is using while in sneak mode.
        if (player.isInSneakingPose()) {
            return ActionResult.PASS;
        }

        BlockEntity entity = world.getBlockEntity(pos);
        ItemStack stack = player.getStackInHand(hand);

        // The player's hand is empty.  Return doing nothing.
        if (ItemStack.EMPTY.equals(stack))
            return ActionResult.PASS;

        DisplayPedestalEntity displayPedestalEntity = (DisplayPedestalEntity) entity;

        // Shouldn't happen in normal usage, but better safe than sorry.
        if (displayPedestalEntity == null)
            return ActionResult.PASS;
        ItemStack inventory = displayPedestalEntity.getStack(0);
        if (inventory.isEmpty()) {
            // There's nothing in the display stand.  Take the stack from player and return.
            displayPedestalEntity.setStack(0, stack);
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

        displayPedestalEntity.setStack(0, toMoveStack);
        stack.decrement(toMoveCount);

        return ActionResult.SUCCESS;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        DisplayPedestalEntity displayPedestalEntity = (DisplayPedestalEntity) world.getBlockEntity(pos);
        if (displayPedestalEntity != null) {
            if (displayPedestalEntity.isMultiBlockFormed) {
                // Multiblock is formed successfully.  Destroy it since the user is breaking the pedestal, which is a part of it.
                List<LookingGlassBE> multiBlockEntitiesList = displayPedestalEntity.multiBlockBlockEntitiesList;

                for (LookingGlassBE lookingGlassBE : multiBlockEntitiesList) {
                    if (lookingGlassBE instanceof UnstableAltarEntity) {
                        UnstableAltarEntity unstableAltarEntity = (UnstableAltarEntity) lookingGlassBE;
                        unstableAltarEntity.multiBlockBlockEntitiesList = null;
                        unstableAltarEntity.multiBlockPositionsList = null;
                        unstableAltarEntity.isMultiBlockFormed = false;
                    } else {
                        DisplayPedestalEntity iteratedDisplayPedestalEntity = (DisplayPedestalEntity) lookingGlassBE;
                        iteratedDisplayPedestalEntity.multiBlockBlockEntitiesList = null;
                        iteratedDisplayPedestalEntity.multiBlockPositionsList = null;
                        iteratedDisplayPedestalEntity.isMultiBlockFormed = false;
                    }
                }
            }
        }

        super.onBreak(world, pos, state, player);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockView world) {
        return new DisplayPedestalEntity();
    }
}
