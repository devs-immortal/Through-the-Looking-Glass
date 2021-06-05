package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.item.LookingGlassItems;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class AngelBlock extends Block {
    public AngelBlock(FabricBlockSettings settings) {
        super(settings);
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        ItemStack angelBlockStack = LookingGlassItems.ANGEL_BLOCK.getDefaultStack();
        angelBlockStack.setCount(1);

        // Try to port the angel block directly into the player inventory.  If you can't do that, drop it at their feet.
        if (!player.getInventory().insertStack(angelBlockStack)) {
            ItemEntity angelBlockItemEntity = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), angelBlockStack);
            world.spawnEntity(angelBlockItemEntity);
        }
    }
}
