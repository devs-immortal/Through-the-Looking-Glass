package azzy.fabric.lookingglass.util;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.TextureManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;

import java.io.Closeable;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import static azzy.fabric.lookingglass.LookingGlassClient.textureIdCounter;
import static azzy.fabric.lookingglass.LookingGlassCommon.MODID;

public class CacheCrimes {

    private static volatile HashMap<String, CompletableFuture<AbstractTexture>> futureCache;
    private static volatile HashMap<String, BakedRenderLayer> bakedLayerCache;

    public static void init(){
        futureCache = new HashMap<>();
        bakedLayerCache = new HashMap<>();
    }

    public static Optional<CompletableFuture<AbstractTexture>> getFuture(String url){
        if(futureCache.containsKey(url))
            return Optional.of(futureCache.get(url));
        return Optional.empty();
    }

    public static void putFuture(String url, CompletableFuture<AbstractTexture> future){
        futureCache.put(url, future);
    }

    static private Identifier generateId() {
        final int id = textureIdCounter++;
        return new Identifier(MODID, "dynamic/dispmod_" + id);
    }

    public static BakedRenderLayer bakeRenderLayer(String url, NativeImageBackedTexture texture) {
        Identifier backingTextureId = generateId();
        BakedRenderLayer bakedRenderLayer = new BakedRenderLayer(backingTextureId, texture);
        bakedLayerCache.put(url, bakedRenderLayer);
        return bakedRenderLayer;
    }

    public static boolean checkRenderLayer(String url) {
        if(bakedLayerCache.containsKey(url)) {
            return bakedLayerCache.get(url).checkIntegrity();
        }
        return false;
    }

    public static BakedRenderLayer getBakedLayer(String url) {
        return bakedLayerCache.get(url);
    }

    public static class BakedRenderLayer implements Closeable {
        private final Identifier id;
        private final RenderLayer renderLayer;
        private final NativeImageBackedTexture texture;

        public BakedRenderLayer(Identifier id, NativeImageBackedTexture texture) {
            this.id = id;
            this.texture = texture;
            MinecraftClient.getInstance().getTextureManager().registerTexture(id, texture);
            this.renderLayer = RenderCrimes.getTransNoDiff(id);
        }

        public Identifier getId() {
            return id;
        }

        public RenderLayer getRenderLayer() {
            return renderLayer;
        }

        public NativeImageBackedTexture getTexture() {
            return texture;
        }

        public boolean checkIntegrity() {
            return id != null && renderLayer != null && texture != null;
        }

        @Override
        public void close() throws IOException {

        }
    }
}
