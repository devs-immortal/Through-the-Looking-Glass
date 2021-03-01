package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.util.LookingGlassJsonManager;
import azzy.fabric.lookingglass.util.SpikeUtility;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
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
    public static final IntProperty SPIKE_UPGRADE = IntProperty.of("spike_upgrade", 0, 4);
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 2, 16);
    private final WeakReference<ServerPlayerEntity> fakePlayer = null;
    private final ServerPlayerEntity fakePlayerEntity = null;
    private final int velocity;

    public VectorPlateBlock(FabricBlockSettings settings, int velocity) {
        super(settings, false);
        BlockState defaultState = getStateManager().getDefaultState();
        setDefaultState(defaultState.with(SPIKE_UPGRADE, 0));
        this.velocity = velocity;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(Properties.HORIZONTAL_FACING);
        builder.add(SPIKE_UPGRADE);
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

        // Don't hurt players.  Don't hurt items.
        if (entity instanceof PlayerEntity)
            return;

        // Just a NPE avoidance check.
        if (world.getServer() == null)
            return;

        // 0 = North (-z), 1 = East (x), 2 = South (z), 3 = West (-x)
        Direction facingDirection = state.get(Properties.HORIZONTAL_FACING);

        // Here is the spike damage of the codebase.
        int spikeUpgrade = state.get(SPIKE_UPGRADE);
        switch (facingDirection) {
            case NORTH:
                // North (-z) direction
                entity.addVelocity(2, 0, -1 * velocity);
                break;
            case EAST:
                // East (x) direction
                entity.addVelocity(velocity, 0, 0);
                break;
            case SOUTH:
                // South (z) direction
                entity.addVelocity(0, 0, velocity);
                break;
            case WEST:
                // West (-x) direction
                entity.addVelocity(-1 * velocity, 0, 0);
        }

        // We move items, but we don't damage them - bad karma!
        if (!(entity instanceof LivingEntity))
            return;

        // If the vector plate has a spike upgrade, call this method to bring the hurt!
        if (spikeUpgrade > 0) {
            SpikeUtility.damageEntity(entity, LookingGlassJsonManager.getDamageForSpike(spikeUpgrade), world, spikeUpgrade);
        }
    }


}