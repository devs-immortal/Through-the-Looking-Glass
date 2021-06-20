package azzy.fabric.lookingglass.mixin;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BubbleColumnBlock.class)
public class BubbleColumnMixin {

    @Inject(method = "canPlaceAt", at = @At("HEAD"), cancellable = true)
    private void canPlaceExtender(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        BlockState floor = world.getBlockState(pos.down());
        if(floor == LookingGlassBlocks.BRINE_FISSURE.getDefaultState() || floor == LookingGlassBlocks.HOT_BASALT.getDefaultState()) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }

    @Inject(method = "getBubbleState", at = @At("HEAD"), cancellable = true)
    private static void bubbleStateExtender(BlockState state, CallbackInfoReturnable<BlockState> cir) {
        if(state.isOf(Blocks.BUBBLE_COLUMN)) {
            cir.setReturnValue(state);
            cir.cancel();
        }
        else if(state.isOf(LookingGlassBlocks.BRINE_FISSURE) || state.isOf(LookingGlassBlocks.HOT_BASALT)) {
            cir.setReturnValue(Blocks.BUBBLE_COLUMN.getDefaultState().with(BubbleColumnBlock.DRAG, true));
            cir.cancel();
        }
    }
}
