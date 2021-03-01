package azzy.fabric.lookingglass.mixin;

import it.unimi.dsi.fastutil.longs.Long2ReferenceOpenHashMap;
import it.unimi.dsi.fastutil.objects.ReferenceLinkedOpenHashSet;
import me.jellysquid.mods.lithium.common.util.collections.BlockEntityList;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.CommandBlockBlockEntity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(value = BlockEntityList.class, remap = false)
public abstract class LithiumBlockEntityListMixin {

    @Shadow @Final private ReferenceLinkedOpenHashSet<BlockEntity> allBlockEntities;

    @Shadow @Final private Long2ReferenceOpenHashMap<BlockEntity> posMap;

    @Shadow
    private static long getEntityPos(BlockEntity e) {
        return 0;
    }

    @Shadow @Final private Long2ReferenceOpenHashMap<List<BlockEntity>> posMapMulti;

    /**
     * @author Azazelthedemonlord
     * @reason Prevents a crash when moving block entities.
     */
    @Overwrite()
    private boolean addNoDoubleAdd(BlockEntity blockEntity, boolean exceptionOnDoubleAdd) {
        boolean added = this.allBlockEntities.add(blockEntity);
        if (added && this.posMap != null) {
            long pos = getEntityPos(blockEntity);
            BlockEntity prev = (BlockEntity)this.posMap.putIfAbsent(pos, blockEntity);
            if (prev != null) {
                List<BlockEntity> multiEntry = this.posMapMulti.computeIfAbsent(pos, (l) -> {
                    return new ArrayList<>();
                });
                if (multiEntry.size() == 0) {
                    multiEntry.add(prev);
                }

                multiEntry.add(blockEntity);
            }
        }

        return added;
    }

}
