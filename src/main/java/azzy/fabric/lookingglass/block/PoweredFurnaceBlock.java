package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.PoweredFurnaceEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class PoweredFurnaceBlock extends LookingGlassBlock implements BlockEntityProvider {

    public PoweredFurnaceBlock(Settings settings) {
        super(settings, false);
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockView world) {
        return new PoweredFurnaceEntity();
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(!world.isClient() && !player.isSneaking()) {
            player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }
}
