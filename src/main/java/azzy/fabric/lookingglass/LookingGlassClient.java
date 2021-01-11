package azzy.fabric.lookingglass;

import azzy.fabric.lookingglass.gui.ProjectorContainer;
import azzy.fabric.lookingglass.gui.ProjectorGUI;
import azzy.fabric.lookingglass.particle.TTLGParticles;
import azzy.fabric.lookingglass.render.ChunkAnchorRenderer;
import azzy.fabric.lookingglass.render.ProjectorRenderer;
import azzy.fabric.lookingglass.render.TesseractRenderer;
import azzy.fabric.lookingglass.render.WormholeRenderer;
import azzy.fabric.lookingglass.util.client.RenderCache;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

import static azzy.fabric.lookingglass.LookingGlassCommon.*;
import static azzy.fabric.lookingglass.block.TTLGBlocks.*;

@Environment(EnvType.CLIENT)
public class LookingGlassClient implements ClientModInitializer {

    public static int textureIdCounter = 0;
    public static final int LIGHTMAP_MAX_LUMINANCE = 14680160;
    //private static Future<?> layerCleanLock;

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(PROJECTORENTITY, ProjectorRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(CHUNKLOADER_ENTITY, ChunkAnchorRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(WORMHOLE_ENTITY, WormholeRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BLOCK_TESSERACT_ENTITY, TesseractRenderer::new);
        ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier(MODID, "projector_gui"), (syncID, id, player, buf) -> new ProjectorContainer( new ProjectorGUI(ScreenHandlerType.ANVIL, syncID, player.inventory, ScreenHandlerContext.create(player.world, buf.readBlockPos())), player));
        BlockRenderLayerMap.INSTANCE.putBlock(PROJECTORBLOCK, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(CHUNKLOADERBLOCK, RenderLayer.getCutoutMipped());
        TTLGParticles.init();
        RenderCache.init();

        //ClientTickEvents.END_WORLD_TICK.register(clientWorld -> {
        //    if(clientWorld.getTime() % 20 == 0 && (layerCleanLock == null || layerCleanLock.isDone())) {
        //        layerCleanLock = RenderCache.cleanLayerCache();
        //    }
        //});
    }
}
