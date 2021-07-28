package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.EnchanterEntity;
import azzy.fabric.lookingglass.blockentity.LookingGlassMachine;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class EnchanterBlock extends LookingGlassBlock implements BlockEntityProvider {

    public EnchanterBlock(Settings settings) {
        super(settings, false);
    }

    @Override
    protected void pulseUpdate(BlockState state, World world, BlockPos pos, boolean on) {
        BlockEntity entity = world.getBlockEntity(pos);
        if(entity != null && on) {
            ((EnchanterEntity) entity).notifyRedstoneActivation();
        }
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        return super.onUse(state, world, pos, player, hand, hit);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return LookingGlassMachine::tickStatic;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new EnchanterEntity(pos, state);
    }
}
