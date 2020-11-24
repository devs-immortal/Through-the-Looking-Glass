package azzy.fabric.lookingglass.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FacingBlock;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractInductorBlock extends FacingBlock {

    public static final BooleanProperty POWERED = BooleanProperty.of("powered");

    public AbstractInductorBlock(Settings settings) {
        super(settings);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return getDefaultState().with(FACING, ctx.getPlayerLookDirection().getOpposite());
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        tryInduct(world, state, pos);
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        tryInduct(world, state, pos);
    }

    public static void tryInduct(World world, BlockState inductor, BlockPos pos) {
        if(!world.isClient() && inductor.isOf(TTLGBlocks.BLOCK_INDUCTOR_BLOCK) && isPowered(world, inductor.get(FACING), inductor, pos)) {
            ((AbstractInductorBlock) inductor.getBlock()).internalInduct(world, inductor, inductor.get(FACING), pos);
        }
    }

    protected abstract void internalInduct(World world, BlockState inductor, Direction facing, BlockPos pos);

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, POWERED);
    }

    public static boolean isPowered(World world, Direction facing, BlockState inductor, BlockPos pos) {
        if(world.isReceivingRedstonePower(pos)) {
            if(!inductor.get(POWERED)) {
                world.setBlockState(pos, inductor.with(POWERED, true));
                return true;
            }
        }
        else
            world.setBlockState(pos, inductor.with(POWERED, false));
        return false;
    }
}
