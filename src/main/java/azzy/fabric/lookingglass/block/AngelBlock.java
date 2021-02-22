package azzy.fabric.lookingglass.block;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;

public class AngelBlock extends Block {
    public static final Block EXAMPLE_ANGEL_BLOCK = new Block(FabricBlockSettings.of(Material.BAMBOO).breakInstantly());

    public AngelBlock(FabricBlockSettings settings) {
        super(settings);
    }
}