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
        BlockState unstableAltarState;
        Block unstableAltarBlock = null;
        List<BlockPos> pedestalPositionList = new ArrayList<>();
        List<BlockState> pedestalBlockStateList = new ArrayList<>();

        // Don't want to do these in the client.
//        if(!world.isClient)
//            return;

        BlockPos[] cardinalPositions = {new BlockPos(-3, 0, 0), new BlockPos(0, 0, -3), new BlockPos(3, 0, 0), new BlockPos(0, 0, 3)};

        for (BlockPos tmpPos : cardinalPositions) {
            int posX = pos.getX() + tmpPos.getX();
            int posZ = pos.getZ() + tmpPos.getZ();
            int posY = pos.getY();
            unstableAltarPos = new BlockPos(posX, posY, posZ);
            unstableAltarState = world.getBlockState(unstableAltarPos);
            unstableAltarBlock = unstableAltarState.getBlock();
            if (LookingGlassBlocks.UNSTABLE_ALTAR_BLOCK.equals(unstableAltarBlock)) {
                unstableAltarFound = true;
                break;
            }
        }
        // Didn't find unstable altar anywhere.  Silently return doing nothing.
        if (!unstableAltarFound) {
            return;
        }

        // Found the unstable altar.  Now check in all 4 cardinal directions to see if we can find display pedestals.
        for (BlockPos cardinalPosition : cardinalPositions) {
            int posX = unstableAltarPos.getX() + cardinalPosition.getX();
            int posY = unstableAltarPos.getY();
            int posZ = unstableAltarPos.getZ() + cardinalPosition.getZ();

            BlockPos pedestalPos = new BlockPos(posX, posY, posZ);
            BlockState pedestalBlockState = world.getBlockState(pedestalPos);
            Block pedestalBlock = pedestalBlockState.getBlock();

            // This block isn't a display pedestal.  Keep moving.
            if (!LookingGlassBlocks.DISPLAY_PEDESTAL_BLOCK.equals(pedestalBlock))
                continue;

            pedestalPositionList.add(pedestalPos);
            pedestalBlockStateList.add(pedestalBlockState);
        }

        if (pedestalPositionList.size() < 4) {
            // We didn't find all 4 pedestals.  Return silently doing nothing.
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

        if (!world.isClient) {
            ParticleUtils.spawnParticles(ParticleUtils.GREEN, ((ServerWorld) world), unstableAltarPos, 1.2, 30);
            ParticleUtils.spawnParticles(ParticleUtils.GREEN, ((ServerWorld) world), pedestalPositionList.get(0), 1.2, 30);
            ParticleUtils.spawnParticles(ParticleUtils.GREEN, ((ServerWorld) world), pedestalPositionList.get(1), 1.2, 30);
            ParticleUtils.spawnParticles(ParticleUtils.GREEN, ((ServerWorld) world), pedestalPositionList.get(2), 1.2, 30);
            ParticleUtils.spawnParticles(ParticleUtils.GREEN, ((ServerWorld) world), pedestalPositionList.get(3), 1.2, 30);
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        // Nothing to do if the player is using while in sneak mode.
        if (player.isInSneakingPose())
            return ActionResult.PASS;

        BlockEntity entity = world.getBlockEntity(pos);
        ItemStack stack = player.getStackInHand(hand);

        DisplayPedestalEntity displayPedestalEntity = (DisplayPedestalEntity) entity;

        // Shouldn't happen in normal usage, but better safe than sorry.
        if (displayPedestalEntity == null)
            return ActionResult.PASS;
        ItemStack inventory = displayPedestalEntity.getStack(0);
        if (inventory.isEmpty()) {
            // There's nothing in the display stand.  Take the stack from player and return.
            displayPedestalEntity.setStack(0, stack);
            player.setStackInHand(hand, ItemStack.EMPTY);
            checkStartRitual(state, world, pos, player, hand, hit);
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

        displayPedestalEntity.setStack(0, toMoveStack);
        stack.decrement(toMoveCount);

        checkStartRitual(state, world, pos, player, hand, hit);

        return ActionResult.SUCCESS;
    }

    public void checkStartRitual(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        DisplayPedestalEntity displayPedestalEntity = (DisplayPedestalEntity) world.getBlockEntity(pos);
        // Shouldn't occur.  Return doing nothing.
        if (displayPedestalEntity == null) {
            LookingGlassCommon.FFLog.warn("Not able to get display pedestal's tile entity'.  Kindly contact the mod authors at '" + LookingGlassConstants.GITHUB_ISSUE_URL + "' to log an issue.");
            return;
        }

        if (!displayPedestalEntity.isMultiBlockFormed) {
            // Multiblock isn't formed.  Return doing nothing.
            return;
        }
        List<BlockPos> multiBlockPosList = displayPedestalEntity.multiBlockPositionsList;
        List<LookingGlassBE> multiBlockEntityList = displayPedestalEntity.multiBlockBlockEntitiesList;

        if (!world.isClient) {
            for (int i = 1; i < 5; i++) {
                // First entry in the lists is the unstable altar.  The remaining 4 are the display pedestals.
                DisplayPedestalEntity iteratedDisplayPedestalEntity = (DisplayPedestalEntity) multiBlockEntityList.get(i);
                ParticleUtils.spawnItemParticles((ServerWorld) world, multiBlockPosList.get(0), multiBlockPosList.get(i), iteratedDisplayPedestalEntity.getStack(0));
            }
        }
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
