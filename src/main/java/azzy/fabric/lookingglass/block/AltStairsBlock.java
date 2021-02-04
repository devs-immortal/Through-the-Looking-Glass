package azzy.fabric.lookingglass.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.StairsBlock;

public class AltStairsBlock extends StairsBlock {

    protected AltStairsBlock(Block base, Settings settings) {
        super(base.getDefaultState(), settings);
    }
}
