package azzy.fabric.lookingglass.mixin;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import com.google.common.collect.ImmutableSet;
import net.minecraft.block.BlockState;
import net.minecraft.block.BubbleColumnBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Set;

@Mixin(BubbleColumnBlock.class)
public class BubbleColumnMixin {

    @Inject(method = "canPlaceAt", at = @At("HEAD"), cancellable = true)
    public void aaa(BlockState state, WorldView world, BlockPos pos, CallbackInfoReturnable<Boolean> cir) {
        if(world.getBlockState(pos.down()) == LookingGlassBlocks.BRINE_FISSURE.getDefaultState()) {
            cir.setReturnValue(true);
            cir.cancel();
        }
    }
}
