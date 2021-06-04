package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.SuffuserEntity;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class SuffuserBlock extends HorizontalMachineBlock {

    public SuffuserBlock(Settings settings) {
        super(settings, false, 13);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new SuffuserEntity(pos, state);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        if(player.isSneaking()) {
            if(!world.isClient())
                player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            return ActionResult.SUCCESS;
        }
        else {
            BlockEntity entity = world.getBlockEntity(pos);
            if(entity != null) {
                ItemStack stack = player.getStackInHand(hand);
                if(stack.isEmpty()) {
                    player.setStackInHand(hand, ((SuffuserEntity) entity).removeStack(0));
                }
                else {
                    ItemStack machineStack = ((SuffuserEntity) entity).getStack(0);
                    ((SuffuserEntity) entity).setStack(0, stack);
                    player.setStackInHand(hand, machineStack);
                }
                entity.markDirty();
                if(!world.isClient())
                    ((SuffuserEntity) entity).sync();
                return ActionResult.success(world.isClient());
            }
        }
        return super.onUse(state, world, pos, player, hand, hit);
    }
}
