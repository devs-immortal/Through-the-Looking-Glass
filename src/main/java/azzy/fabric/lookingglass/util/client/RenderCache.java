package azzy.fabric.lookingglass.util.client;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import java.io.Closeable;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static azzy.fabric.lookingglass.LookingGlassClient.textureIdCounter;
import static azzy.fabric.lookingglass.LookingGlassCommon.MODID;

public class RenderCache {

    private static HashMap<String, MirrorFuture<AbstractTexture>> futureCache;
    private static volatile HashMap<String, BakedRenderLayer> bakedLayerCache;
    private static final ExecutorService bakedLayerManager = Executors.newSingleThreadExecutor();

    public static void init(){
        futureCache = new HashMap<>();
        bakedLayerCache = new HashMap<>();
    }

    public static Optional<CompletableFuture<AbstractTexture>> getFuture(String url){
        if(futureCache.containsKey(url)){
            MirrorFuture<AbstractTexture> future = futureCache.get(url);
            if(future.future.isDone() && !future.complete) {
                future.complete = true;
            }
            return Optional.of(future.future);
        }
        return Optional.empty();
    }

    public static void putFuture(String url, CompletableFuture<AbstractTexture> future){
        futureCache.put(url, new MirrorFuture<>(future));
    }

    static private Identifier generateId() {
        final int id = textureIdCounter++;
        return new Identifier(MODID, "dynamic/dispmod_" + id);
    }

    public static BakedRenderLayer bakeRenderLayer(String url, NativeImageBackedTexture texture) {
        Identifier backingTextureId = generateId();
        BakedRenderLayer bakedRenderLayer = BakedRenderLayer.of(backingTextureId, texture);
        if(bakedRenderLayer != null)
            bakedLayerCache.put(url, bakedRenderLayer);
        return bakedRenderLayer;
    }

    public static boolean checkRenderLayer(String url, World world) {
        if(bakedLayerCache.containsKey(url)) {
            return bakedLayerCache.get(url).checkAndUpdateLayer(world);
        }
        return false;
    }

    public static BakedRenderLayer getBakedLayer(String url) {
        return bakedLayerCache.get(url);
    }

    private static void layerCacheFlush() {
        long timeStamp = MinecraftClient.getInstance().world.getTime();
        for(Map.Entry<String, BakedRenderLayer> layerPair : bakedLayerCache.entrySet()) {
            BakedRenderLayer bakedLayer = layerPair.getValue();
            if(bakedLayer != null) {
                if(timeStamp - bakedLayer.getLastTickStamp() > 100) {
                    bakedLayer.close();
                    bakedLayerCache.remove(layerPair.getKey());
                }
            }
        }
    }

    public static Future<?> cleanLayerCache() {
        return bakedLayerManager.submit(RenderCache::layerCacheFlush);
    }

    public static class BakedRenderLayer implements Closeable {
        private final Identifier id;
        private final RenderLayer renderLayer;
        private final NativeImageBackedTexture texture;
        private long lastTickStamp;

        private BakedRenderLayer(Identifier id, NativeImageBackedTexture texture) {
            this.id = id;
            this.texture = texture;
            MinecraftClient.getInstance().getTextureManager().registerTexture(id, texture);
            this.renderLayer = TTLGRenderLayers.getTransNoDiff(id);
        }

        public static BakedRenderLayer of(Identifier id, NativeImageBackedTexture texture) {
            if(id != null && texture != null) {
                return new BakedRenderLayer(id, texture);
            }
            return null;
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

        public long getLastTickStamp() {
            return lastTickStamp;
        }

        public boolean checkAndUpdateLayer(World world) {
            lastTickStamp = world.getTime();
            return id != null && renderLayer != null && texture != null && texture.getImage() != null;
        }

        @Override
        public void close() {
            ((TexManRegEdit) MinecraftClient.getInstance().getTextureManager()).unregisterTexture(id);
            texture.close();
        }
    }

    static class MirrorFuture<T> extends CompletableFuture<T> {
        boolean complete;
        CompletableFuture<T> future;
        MirrorFuture(CompletableFuture<T> future) {
            this.future = future;
        }
    }
}
