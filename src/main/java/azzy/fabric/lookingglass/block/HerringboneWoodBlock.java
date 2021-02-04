package azzy.fabric.lookingglass.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class HerringboneWoodBlock extends Block {

    public static final IntProperty VARIATION = IntProperty.of("variation", 0, 3);

    public HerringboneWoodBlock(Settings settings) {
        super(settings);
    }

    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        BlockPos pos = ctx.getBlockPos();
        int diff = pos.getX() - pos.getZ();
        if(diff % 3 == 0) {
            return getDefaultState().with(VARIATION, 0);
        }
        else if(diff % 2 == 0) {
            return getDefaultState().with(VARIATION, 1);
        }
        else {
            return getDefaultState().with(VARIATION, 2);
        }
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(VARIATION);
    }
}
