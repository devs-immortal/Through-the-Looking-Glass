package azzy.fabric.lookingglass.util;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.item.BoneMealItem;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;

public class ClientNetworkingUtils {

    public static void init() {

        ClientPlayNetworking.registerGlobalReceiver(GeneralNetworking.TRANSLATEABLE_PACKET, ((minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {

            final String translationKey = packetByteBuf.readString();

            minecraftClient.execute(() -> {
                Text text = new TranslatableText(translationKey);
                if (MinecraftClient.getInstance().player != null) {
                    MinecraftClient.getInstance().player.sendMessage(text, true);
                }
            });
        }));

        ClientPlayNetworking.registerGlobalReceiver(GeneralNetworking.BONEMEAL_PARTICLES, (((minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {

            final BlockPos pos = packetByteBuf.readBlockPos();
            final int count = packetByteBuf.readInt();

            minecraftClient.execute(() -> {
                if (minecraftClient.world != null) {
                    BoneMealItem.createParticles(minecraftClient.world, pos, count);
                }
            });
        })));
    }
}
