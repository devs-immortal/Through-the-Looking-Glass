package azzy.fabric.lookingglass.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import org.jetbrains.annotations.Nullable;

public class HorizontalMachineBlock extends LookingGlassBlock {

    public HorizontalMachineBlock(Settings settings, boolean loggable, final int activeLight) {
        super(activeLight > 0 ? settings.luminance(state -> state.get(Properties.LIT) ? activeLight : 0) : settings, loggable);
        this.setDefaultState(getDefaultState().with(Properties.LIT, false));
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx).with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing().getOpposite());
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);
        builder.add(Properties.LIT);
        super.appendProperties(builder);
    }
}
