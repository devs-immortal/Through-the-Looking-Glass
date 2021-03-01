package azzy.fabric.lookingglass.item;

import azzy.fabric.lookingglass.block.VectorPlateBlock;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NetheriteSpikeBlockItem extends BlockItem {
    public NetheriteSpikeBlockItem(Block netheriteSpikeBlock, FabricItemSettings fabricItemSettings) {
        super(netheriteSpikeBlock, fabricItemSettings);
    }

    // Change the Vector plate to the spiked version of the vector plate
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient)
            return ActionResult.PASS;

        BlockPos usedBlockPos = context.getBlockPos();
        BlockState targetBlockState = world.getBlockState(usedBlockPos);
        targetBlockState = targetBlockState.with(VectorPlateBlock.SPIKE_UPGRADE, 4);
        world.setBlockState(usedBlockPos, targetBlockState);

        ItemStack usedItem = context.getStack();
        usedItem.decrement(1);

        return ActionResult.SUCCESS;
    }
}