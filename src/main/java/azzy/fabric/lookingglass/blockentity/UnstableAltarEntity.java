package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.LookingGlassConstants;
import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.util.ParticleUtils;
import azzy.fabric.lookingglass.util.json.LookingGlassJsonManager;
import azzy.fabric.lookingglass.vo.UnstableAltarRecipeVO;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.List;

public class UnstableAltarEntity extends LookingGlassBE implements Tickable {
    public boolean isMultiBlockFormed = false;
    public List<BlockPos> multiBlockPositionsList = null;
    public List<LookingGlassBE> multiBlockBlockEntitiesList = null;
    int craftingCheckCounter = 0;
    boolean craftingInProgress = false;
    UnstableAltarRecipeVO unstableAltarRecipeVO = null;

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        super.writeNbt(tag);
        tag.putBoolean("isMultiBlockFormed", isMultiBlockFormed);
        return tag;
    }

    @Override
    public void readNbt(BlockState state, NbtCompound tag) {
        super.readNbt(state, tag);

        isMultiBlockFormed = tag.getBoolean("isMultiBlockFormed");
    }

    public void populateMultiBlockPositions() {
        // Shouldn't normally occur.
        if (world == null)
            return;

        List<BlockPos> pedestalPositionList = new ArrayList<>();

        // Found the unstable altar.  Now check in all directions to see if we can find display pedestals.
        for (int x = -3; x < 4; x += 3) {
            for (int z = -3; z < 4; z += 3) {
                // Central spot.  Since we already found the unstable altar, we don't need to check it again.
                if ((x == 0) && (z == 0))
                    continue;

                int posX = pos.getX() + x;
                int posY = pos.getY();
                int posZ = pos.getZ() + z;

                BlockPos pedestalPos = new BlockPos(posX, posY, posZ);
                BlockState pedestalBlockState = world.getBlockState(pedestalPos);
                Block pedestalBlock = pedestalBlockState.getBlock();

                // This block isn't a display pedestal.  Keep moving.
                if (!LookingGlassBlocks.DISPLAY_PEDESTAL_BLOCK.equals(pedestalBlock))
                    continue;

                pedestalPositionList.add(pedestalPos);
            }
        }

        // TODO:  Change this to be a configuration driven setting, since this forces us to need 8 pedestals to complete the multiblock.
        if (pedestalPositionList.size() < 8) {
            // We didn't find all 8 pedestals.  Return silently doing nothing.
            isMultiBlockFormed = false;
            return;
        }

        List<BlockPos> multiBlockPositionList = new ArrayList<>();
        multiBlockPositionList.add(pos);
        multiBlockPositionList.addAll(pedestalPositionList);
        List<LookingGlassBE> multiBlockTileEntityList = new ArrayList<>();
        multiBlockTileEntityList.add((LookingGlassBE) world.getBlockEntity(pos));

        for (BlockPos displayPedestalBlockPos : pedestalPositionList) {
            DisplayPedestalEntity displayPedestalEntity = (DisplayPedestalEntity) world.getBlockEntity(displayPedestalBlockPos);
            // This shouldn't be occurring either.  Return silently doing nothing.
            if (displayPedestalEntity == null) {
                isMultiBlockFormed = false;
                LookingGlassCommon.FFLog.warn("Not able to get display pedestal's tile entity'.  Kindly contact the mod authors at '" + LookingGlassConstants.GITHUB_ISSUE_URL + "' to log an issue.");
                return;
            }
            multiBlockTileEntityList.add(displayPedestalEntity);
        }
        // Set the flag that the multiblock has been formed! - STARTS
        isMultiBlockFormed = true;
        multiBlockBlockEntitiesList = multiBlockTileEntityList;
        multiBlockPositionsList = multiBlockPositionList;

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
            ParticleUtils.spawnParticles(ParticleUtils.GREEN, ((ServerWorld) world), pos, 1.2, 30);
            for (BlockPos pedestalPos : pedestalPositionList) {
                ParticleUtils.spawnParticles(ParticleUtils.GREEN, ((ServerWorld) world), pedestalPos, 1.2, 30);
            }
        }
    }

    public UnstableAltarEntity() {
        super(LookingGlassBlocks.UNSTABLE_ALTAR_ENTITY, 1);
    }

    public void startCrafting() {
        if (!craftingInProgress) {
            UnstableAltarRecipeVO recipeVO = new UnstableAltarRecipeVO();
            for (LookingGlassBE lookingGlassBE : multiBlockBlockEntitiesList) {
                UnstableAltarEntity unstableAltarEntity;
                DisplayPedestalEntity displayPedestalEntity;

                if (lookingGlassBE instanceof UnstableAltarEntity) {
                    unstableAltarEntity = (UnstableAltarEntity) lookingGlassBE;
                    ItemStack itemStack = unstableAltarEntity.getStack(0);
                    if (ItemStack.EMPTY.equals(itemStack))
                        continue;
                    recipeVO.inputList.add(itemStack);
                } else if (lookingGlassBE instanceof DisplayPedestalEntity) {
                    displayPedestalEntity = (DisplayPedestalEntity) lookingGlassBE;
                    ItemStack itemStack = displayPedestalEntity.getStack(0);
                    if (ItemStack.EMPTY.equals(itemStack))
                        continue;
                    recipeVO.inputList.add(itemStack);
                } else {
                    // Should never occur in real world.
                    LookingGlassCommon.FFLog.warn("Invalid entity found in unstable altar multiblock: '" + lookingGlassBE + "'");
                    return;
                }
            }

            unstableAltarRecipeVO = LookingGlassJsonManager.UNSTABLE_ALTAR_RECIPE_MAP.get(recipeVO.generateKey());
        }

        if (unstableAltarRecipeVO != null) {
            // Found a valid recipe.
            craftingInProgress = true;

            if ((world != null) && !world.isClient) {
                // Shouldn't ever happen.  But weirder things have happened.  Here, I'm checking to see if the multiblock only ever needs the unstable altar
                // (I'm thinking about make the # of required display pedestals configurable.
                // This is just a fail-safe check.
                // TODO:  Modify this to be a configuration driven setting to allow for a configurable number of pedestals in the multiblock.  This number will be unstable altar + # of pedestals.
                if (multiBlockBlockEntitiesList.size() < 9)
                    return;

                for (int i = 1; i < multiBlockBlockEntitiesList.size(); i++) {
                    // First entry in the lists is the unstable altar.  The remaining 4 are the display pedestals.
                    DisplayPedestalEntity iteratedDisplayPedestalEntity = (DisplayPedestalEntity) multiBlockBlockEntitiesList.get(i);
                    ItemStack itemStack = iteratedDisplayPedestalEntity.getStack(0);
                    if (!ItemStack.EMPTY.equals(itemStack))
                        ParticleUtils.spawnItemParticles((ServerWorld) world, pos, multiBlockPositionsList.get(i), itemStack);
                }
            }
        }
    }

    public void tick() {
        // The multiblock isn't formed.  There's nothing to do here.
        if (!isMultiBlockFormed)
            return;

        // Only occurs on server reload.  I thought about persisting this data, but ultimately decided against it since it's not really necessary to persist.
        // This method also resets the isMultiBlockFormed to false if there's an issue detecting the multiblock, to avoid log spam.
        if ((multiBlockBlockEntitiesList == null) || (multiBlockBlockEntitiesList.isEmpty()))
            populateMultiBlockPositions();

        ItemStack inventory = getStack(0);
        if (inventory.isEmpty()) {
            return;
        }

        // Only do crafting check every second or so.  Doing a greater than or equals check to avoid lag induced tick misses.
        if ((craftingCheckCounter++ >= 20) || (craftingInProgress)) {
            craftingCheckCounter = 0;
            startCrafting();
        }
    }
}
