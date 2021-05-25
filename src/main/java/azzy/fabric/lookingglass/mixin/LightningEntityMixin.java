package azzy.fabric.lookingglass.mixin;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import net.minecraft.block.WitherRoseBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LightningEntity.class)
public abstract class LightningEntityMixin extends Entity {

    @Shadow private int ambientTick;

    public LightningEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Inject(method="tick", at=@At("TAIL"))
    public void tick(CallbackInfo ci) {
        if(ambientTick >= 0) {
            if(!world.isClient) {
                BlockPos pos = getBlockPos();
                if(world.getBlockState(pos).getBlock() instanceof WitherRoseBlock) {
                    world.setBlockState(pos, LookingGlassBlocks.ECLIPSE_ROSE.getDefaultState());
                }
            }
        }
    }
}
