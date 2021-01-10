package azzy.fabric.lookingglass.util;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface TexManRegEdit {
    void unregisterTexture(final Identifier id);
}
