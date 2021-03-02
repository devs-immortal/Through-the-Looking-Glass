package azzy.fabric.lookingglass.item;

import azzy.fabric.lookingglass.util.SpikeUtility;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class VectorPlateBlockItem extends BlockItem {
    public VectorPlateBlockItem(Block vectorSpikeBlock, FabricItemSettings fabricItemSettings) {
        super(vectorSpikeBlock, fabricItemSettings);
    }

    /**
     * This method is overridden to ensure that you can't place vector plates on top of vector plates.  That's just tacky :).
     *
     * @param context Usage Context
     * @return ActionResult
     */
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        if (world.isClient)
            return ActionResult.PASS;

        BlockPos blockPos = context.getBlockPos();
        BlockState targetBlockState = world.getBlockState(blockPos);
        Block targetBlock = targetBlockState.getBlock();

        if (SpikeUtility.VECTOR_BLOCKS.contains(targetBlock))
            return ActionResult.PASS;

        return super.useOnBlock(context);
    }
}