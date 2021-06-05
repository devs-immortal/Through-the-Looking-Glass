package azzy.fabric.lookingglass.block;

import com.mojang.datafixers.util.Function3;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;
import java.util.function.Supplier;

public abstract class AbstractTesseractBlock extends LookingGlassBlock implements BlockEntityProvider {

    private final BiFunction<BlockPos, BlockState, BlockEntity> blockEntity;

    public AbstractTesseractBlock(BiFunction<BlockPos, BlockState, BlockEntity> blockEntity, Settings settings) {
        super(settings, true);
        this.blockEntity = blockEntity;
    }

    abstract void activate(World world, BlockPos pos, Direction dir, BlockPos trigger);

    abstract boolean setData(World world, BlockPos pos, PlayerEntity user, Hand hand);

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(setData(world, pos, player, hand))
            return ActionResult.SUCCESS;
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        return true;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return blockEntity.apply(pos, state);
    }
}
