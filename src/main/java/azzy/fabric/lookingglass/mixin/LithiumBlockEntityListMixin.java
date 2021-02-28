package azzy.fabric.lookingglass.mixin;

import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import me.jellysquid.mods.lithium.common.util.collections.BlockEntityList;
import net.minecraft.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(BlockEntityList.class)
public abstract class LithiumBlockEntityListMixin {

    @Inject(method = {"addNoDoubleAdd"}, at = {@At("HEAD")}, remap = false)
    private void addNoDoubleAdd(BlockEntity blockEntity, boolean exceptionOnDoubleAdd, CallbackInfoReturnable<Boolean> cir) {
        exceptionOnDoubleAdd = false;
    }

}
