package azzy.fabric.lookingglass.item;

import azzy.fabric.lookingglass.util.SpikeUtility;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.util.ActionResult;

public class DiamondSpikeBlockItem extends BlockItem {
    public DiamondSpikeBlockItem(Block diamondSpikeBlock, FabricItemSettings fabricItemSettings) {
        super(diamondSpikeBlock, fabricItemSettings);
    }

    // Change the Vector plate to the spiked version of the vector plate
    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        return SpikeUtility.useOnBlock(context, 3);
    }
}