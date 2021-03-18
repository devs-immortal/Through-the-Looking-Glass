package azzy.fabric.lookingglass;

import azzy.fabric.lookingglass.gui.LookingGlassGUIs;
import azzy.fabric.lookingglass.particle.LookingGlassParticles;
import azzy.fabric.lookingglass.render.*;
import azzy.fabric.lookingglass.util.ClientNetworkingUtils;
import azzy.fabric.lookingglass.util.client.RenderCache;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.minecraft.client.render.RenderLayer;

import static azzy.fabric.lookingglass.block.LookingGlassBlocks.*;

@Environment(EnvType.CLIENT)
public class LookingGlassClient implements ClientModInitializer {

    public static int textureIdCounter = 0;
    public static final int LIGHTMAP_MAX_LUMINANCE = 14680160;
    //private static Future<?> layerCleanLock;

    @Override
    public void onInitializeClient() {
        //Registering renderers
        BlockEntityRendererRegistry.INSTANCE.register(PROJECTORENTITY, ProjectorRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(CHUNKLOADER_ENTITY, ChunkAnchorRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(WORMHOLE_ENTITY, WormholeRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BLOCK_TESSERACT_ENTITY, TesseractRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(FISH_BREEDER_ENTITY, FishBreederRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(CREATIVE_ENERGY_SOURCE_ENTITY, CreativeEnergySourceRenderer::new);

        //Render layers
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(), FISH_BREEDER_BLOCK, BLOCK_TESSERACT_BLOCK, GHOST_GLASS, ETHEREAL_GLASS, REVERSE_ETHEREAL_GLASS, RED_GLASS);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(), WOODEN_SPIKE_BLOCK, IRON_SPIKE_BLOCK, DIAMOND_SPIKE_BLOCK, NETHERITE_SPIKE_BLOCK);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(), CORN_PLANT);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), CREATIVE_ENERGY_SOURCE_BLOCK, PINK_GEL_BLOCK, DARK_GLASS, DARK_GHOST_GLASS, DARK_ETHEREAL_GLASS, REVERSE_DARK_ETHEREAL_GLASS, GLOW_GLASS);

        LookingGlassParticles.init();
        LookingGlassGUIs.initClient();
        ClientNetworkingUtils.init();
        RenderCache.init();
    }
}
