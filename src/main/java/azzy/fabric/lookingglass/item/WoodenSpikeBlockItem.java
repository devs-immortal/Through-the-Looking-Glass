package azzy.fabric.lookingglass.item;

import azzy.fabric.lookingglass.util.SpikeUtility;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

public class WoodenSpikeBlockItem extends BlockItem {
    public WoodenSpikeBlockItem(Block woodenSpikeBlock, FabricItemSettings fabricItemSettings) {
        super(woodenSpikeBlock, fabricItemSettings);
    }

    // Change the Vector plate to the spiked version of the vector plate
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        ActionResult actionResult = SpikeUtility.useOnBlock(context, 1);

        // This happens when the targeted block isn't a vector plate.
        if (actionResult == ActionResult.CONSUME)
            return super.useOnBlock(context);

        return actionResult;
    }
}