package azzy.fabric.lookingglass.block;

import net.minecraft.block.*;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.tag.BlockTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Random;

public class SalmonEggBlock extends TurtleEggBlock implements Waterloggable {

    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    public SalmonEggBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(WATERLOGGED, false));
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return state.get(WATERLOGGED);
    }

    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return ctx.getWorld().isWater(ctx.getBlockPos()) ? super.getPlacementState(ctx).with(WATERLOGGED, true) : super.getPlacementState(ctx);
    }

    public static boolean isValidGround(World world, BlockPos pos) {
        BlockState state = world.getBlockState(pos);
        BlockState upState = world.getBlockState(pos.up());
    return (world.isWater(pos.up()) || upState.isOf(LookingGlassBlocks.SALMON_EGGS) && upState.get(WATERLOGGED)) && (state.isIn(BlockTags.SAND) || state.isIn(BlockTags.CORAL_BLOCKS) || state.isOf(Blocks.GRAVEL));
    }

    @Override
    public void onBlockAdded(BlockState state, World world, BlockPos pos, BlockState oldState, boolean notify) {
        if (isValidGround(world, pos) && !world.isClient) {
            world.syncWorldEvent(2005, pos, 0);
        }

    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if (this.shouldHatchProgress(world) && isValidGround(world, pos.down())) {
            int i = state.get(HATCH);
            if (i < 2) {
                world.playSound(null, pos, SoundEvents.BLOCK_HONEY_BLOCK_FALL, SoundCategory.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
                world.setBlockState(pos, state.with(HATCH, i + 1), 2);
            } else {
                world.playSound(null, pos, SoundEvents.BLOCK_SLIME_BLOCK_BREAK, SoundCategory.BLOCKS, 0.7F, 0.9F + random.nextFloat() * 0.2F);
                world.removeBlock(pos, false);

                for(int j = 0; j < state.get(EGGS); ++j) {
                    world.syncWorldEvent(2001, pos, Block.getRawIdFromState(state));
                    SalmonEntity salmonEntity = EntityType.SALMON.create(world);
                    salmonEntity.refreshPositionAndAngles(pos.getX() + random.nextDouble(), pos.getY() + 0.25D + random.nextDouble(), pos.getZ() + random.nextDouble(), random.nextFloat() * 360, 0);
                    salmonEntity.setFromBucket(true);
                    world.spawnEntity(salmonEntity);
                }
            }
        }
    }

    private boolean shouldHatchProgress(World world) {
        float f = world.getSkyAngle(1.0F);
        if ((double)f < 1.0D && (double)f > 0.8D) {
            return true;
        } else {
            return world.random.nextInt(300) == 0;
        }
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(WATERLOGGED);
    }
}
