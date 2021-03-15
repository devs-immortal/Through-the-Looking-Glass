package azzy.fabric.lookingglass.item;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This item absorbs cursed earth and drops them into the player's inventory.
 * The cursed earth is replaced with dirt in the world.
 * If the player inventory is full, the cursed earth item will be lost.
 */
public class SacredShovelItem extends ToolItem {
    public SacredShovelItem(ToolMaterial material, Settings settings) {
        super(material, settings);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        BlockPos userPosition = user.getBlockPos();

        for (int x = -9; x < 10; x++) {
            for (int y = -9; y < 10; y++) {
                for (int z = -9; z < 10; z++) {
                    BlockPos scanPos = new BlockPos(userPosition.getX() + x, userPosition.getY() + y, userPosition.getZ() + z);
                    BlockState scanBlockState = world.getBlockState(scanPos);
                    Block scanBlock = scanBlockState.getBlock();

                    if (LookingGlassBlocks.CURSED_EARTH_BLOCK.equals(scanBlock)) {
                        world.setBlockState(scanPos, Blocks.DIRT.getDefaultState());
                        ItemStack cursedEarthItemStack = LookingGlassItems.CURSED_EARTH_BLOCK.getDefaultStack();
                        cursedEarthItemStack.setCount(1);
                        user.inventory.insertStack(cursedEarthItemStack);
                        return super.use(world, user, hand);
                    }
                }
            }
        }

        return super.use(world, user, hand);
    }
}
