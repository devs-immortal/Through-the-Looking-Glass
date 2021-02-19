package azzy.fabric.lookingglass.util;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ClientNetworkingUtils {

    public static void init() {

        ClientPlayNetworking.registerGlobalReceiver(GeneralNetworking.TRANSLATEABLE_PACKET, ((minecraftClient, clientPlayNetworkHandler, packetByteBuf, packetSender) -> {

            String translationKey = packetByteBuf.readString();

            minecraftClient.execute(() -> {
                Text text = new TranslatableText(translationKey);
                MinecraftClient.getInstance().player.sendMessage(text, true);
            });
        }));
    }
}
