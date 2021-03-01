package azzy.fabric.lookingglass.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class VectorPlateBlockItem extends BlockItem {
    public VectorPlateBlockItem(Block vectorSpikeBlock, FabricItemSettings fabricItemSettings) {
        super(vectorSpikeBlock, fabricItemSettings);
    }
}