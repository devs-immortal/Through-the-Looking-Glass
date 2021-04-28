package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.util.SpikeUtility;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SpikesBlock extends LookingGlassBlock {
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 2, 16);

    public SpikesBlock(FabricBlockSettings settings) {
        super(settings, false);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
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

        int spikeType;

        if (this == LookingGlassBlocks.WOODEN_SPIKE_BLOCK)
            spikeType = 1;
        else if (this == LookingGlassBlocks.IRON_SPIKE_BLOCK)
            spikeType = 2;
        else if (this == LookingGlassBlocks.DIAMOND_SPIKE_BLOCK)
            spikeType = 3;
        else if (this == LookingGlassBlocks.NETHERITE_SPIKE_BLOCK)
            spikeType = 4;
        else
            // This scenario should never happen.  It means I've created a new spike type and not yet handled it right.
            return;

        SpikeUtility.damageEntity(entity, world, spikeType);
    }

    /**
     * This method can potentially be used to propagate enchantments to the block from the item, and use it to calculate damage.
     * It's theoretically possible to store the ItemStack within the block and pass it to a false player to wield and use for damage calculations too.
     *
     * @param world     World
     * @param pos       Block Position
     * @param state     Block State
     * @param placer    Placing User
     * @param itemStack The Item being placed.
     */
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
//        ListTag enchantments = itemStack.getEnchantments();
//        Block placedBlock = state.getBlock();
    }
}
