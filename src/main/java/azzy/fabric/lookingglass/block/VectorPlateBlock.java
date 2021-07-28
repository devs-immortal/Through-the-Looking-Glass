package azzy.fabric.lookingglass.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

public class VectorPlateBlock extends LookingGlassBlock {
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 1, 16);
    private final float velocity;

    public VectorPlateBlock(FabricBlockSettings settings, float velocity) {
        super(settings, false);
        BlockState defaultState = getStateManager().getDefaultState();
        this.velocity = velocity;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);
        super.appendProperties(builder);
    }

    /**
     * Called when the block is placed.
     *
     * @param ctx Placement Context
     * @return Block State
     */
    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        // TODO:  The water logged bit in the end shouldn't be necessary.  But azzy's superclass has a bug right now and this is there until it's fixed.
        return super.getPlacementState(ctx).with(Properties.HORIZONTAL_FACING, ctx.getPlayerFacing()).with(WATERLOGGED, false);
    }

    /**
     * I want mobs to be able to spawn in this block, as if this block is like grass.
     *
     * @return true
     */
    @Override
    public boolean canMobSpawnInside() {
        return true;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
        return 0;
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient)
            return;

        // Just a NPE avoidance check.
        if (world.getServer() == null)
            return;

        // 0 = North (-z), 1 = East (x), 2 = South (z), 3 = West (-x)
        Direction facingDirection = state.get(Properties.HORIZONTAL_FACING);

        // Here is the spike damage of the codebase.
        switch (facingDirection) {
            case NORTH: {
                // North (-z) direction

                double vel = entity.getVelocity().z;
                if(vel > -velocity * 5)
                    entity.addVelocity(0, 0, -velocity);
                break;
            }
            case EAST: {
                // East (x) direction
                double vel = entity.getVelocity().z;
                if(vel < velocity * 5)
                    entity.addVelocity(velocity, 0, 0);
                break;
            }
            case SOUTH: {
                // South (z) direction
                double vel = entity.getVelocity().z;
                if(vel < velocity * 5)
                    entity.addVelocity(0, 0, velocity);
                break;
            }
            case WEST: {
                // West (-x) direction
                double vel = entity.getVelocity().x;
                if(vel > -velocity * 5)
                    entity.addVelocity(-velocity, 0, 0);
                break;
            }
        }
    }
}
