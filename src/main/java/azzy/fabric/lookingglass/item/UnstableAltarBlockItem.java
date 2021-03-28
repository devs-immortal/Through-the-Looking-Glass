package azzy.fabric.lookingglass.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class UnstableAltarBlockItem extends BlockItem {
    public UnstableAltarBlockItem(Block unstableAltarBlock, FabricItemSettings fabricItemSettings) {
        super(unstableAltarBlock, fabricItemSettings);
    }
}
