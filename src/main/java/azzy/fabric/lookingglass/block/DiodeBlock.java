package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.DiodeEntity;
import azzy.fabric.lookingglass.blockentity.LookingGlassBE;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.IntProperty;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class DiodeBlock extends LookingGlassBlock implements BlockEntityProvider {

    private static final VoxelShape BASE = Block.createCuboidShape(0.0D, 0.0D, 0.0D, 16.0D, 2.0D, 16.0D);
    private static final VoxelShape[] HEADS = new VoxelShape[5];
    public static final IntProperty EXTENSION = IntProperty.of("extension", 0, 4);

    public DiodeBlock(Settings settings) {
        super(settings, false);
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.union(BASE, HEADS[state.get(EXTENSION)]);
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if (!player.getAbilities().allowModifyWorld) {
            return ActionResult.PASS;
        } else {
            world.setBlockState(pos, state.cycle(EXTENSION), 3);
            world.playSound(null, pos, SoundEvents.BLOCK_LEVER_CLICK, SoundCategory.BLOCKS, 1, 1);
            BlockEntity entity = world.getBlockEntity(pos);
            if(entity instanceof DiodeEntity) {
                ((DiodeEntity) entity).updateTimer(world.getBlockState(pos).get(EXTENSION));
            }
            return ActionResult.success(world.isClient);
        }
    }

    @Override
    public void neighborUpdate(BlockState state, World world, BlockPos pos, Block block, BlockPos fromPos, boolean notify) {
        super.neighborUpdate(state, world, pos, block, fromPos, notify);
        updatePowered(world, pos, state);
    }

    protected void updatePowered(World world, BlockPos pos, BlockState state) {
        BlockEntity entity = world.getBlockEntity(pos);
        if(entity instanceof DiodeEntity) {
            ((DiodeEntity) entity).onRedstoneUpdate();
        }
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        BlockEntity entity = world.getBlockEntity(pos);
        if(entity instanceof DiodeEntity)
            return ((DiodeEntity) entity).getRedstonePower();
        return 0;
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(EXTENSION);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return LookingGlassBE::tickStatic;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new DiodeEntity(pos, state);
    }

    static {
        HEADS[0] = Block.createCuboidShape(3, 2, 3, 13, 6,13);
        HEADS[1] = Block.createCuboidShape(3, 2, 3, 13, 8,13);
        HEADS[2] = Block.createCuboidShape(3, 2, 3, 13, 10,13);
        HEADS[3] = Block.createCuboidShape(3, 2, 3, 13, 12,13);
        HEADS[4] = Block.createCuboidShape(3, 2, 3, 13, 14,13);
    }
}
