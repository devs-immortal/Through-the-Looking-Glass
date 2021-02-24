package azzy.fabric.lookingglass.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;

public class CursedEarthBlockItem extends BlockItem {
    public CursedEarthBlockItem(Block cursedEarthBlock, FabricItemSettings fabricItemSettings) {
        super(cursedEarthBlock, fabricItemSettings);
    }
}