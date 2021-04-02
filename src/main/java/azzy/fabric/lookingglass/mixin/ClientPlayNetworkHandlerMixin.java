package azzy.fabric.lookingglass.mixin;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.packet.s2c.play.WorldTimeUpdateS2CPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetworkHandler.class)
public class ClientPlayNetworkHandlerMixin {

    //onWorldTimeUpdate
    @Inject(method="onWorldTimeUpdate", at=@At("HEAD"), cancellable = true)
    void onWorldTimeUpdate(WorldTimeUpdateS2CPacket packet, CallbackInfo ci) {
        if(LookingGlassBlocks.ECLIPSE_ROSE.isEclipse()) {
            ci.cancel();
        }
    }
}
