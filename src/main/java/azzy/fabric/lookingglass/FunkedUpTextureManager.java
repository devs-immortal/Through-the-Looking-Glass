package azzy.fabric.lookingglass;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public interface FunkedUpTextureManager {

    static FunkedUpTextureManager from(final TextureManager self){
        return (FunkedUpTextureManager) self;
    }

    void unregisterTexture(final Identifier id);
}
