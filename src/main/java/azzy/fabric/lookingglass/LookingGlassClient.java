package azzy.fabric.lookingglass;

import azzy.fabric.lookingglass.gui.*;
import azzy.fabric.lookingglass.particle.TTLGParticles;
import azzy.fabric.lookingglass.render.*;
import azzy.fabric.lookingglass.util.client.RenderCache;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
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
        //Registering renderers
        BlockEntityRendererRegistry.INSTANCE.register(PROJECTORENTITY, ProjectorRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(CHUNKLOADER_ENTITY, ChunkAnchorRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(WORMHOLE_ENTITY, WormholeRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BLOCK_TESSERACT_ENTITY, TesseractRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(FISH_BREEDER_ENTITY, FishBreederRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(CREATIVE_ENERGY_SOURCE_ENTITY, CreativeEnergySourceRenderer::new);

        //Render layers
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(), FISH_BREEDER_BLOCK, BLOCK_TESSERACT_BLOCK);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), CREATIVE_ENERGY_SOURCE_BLOCK);

        //I will update this some day k I promise
        ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier(MODID, "projector_gui"), (syncID, id, player, buf) -> new ProjectorScreen( new ProjectorGuiDescription(ScreenHandlerType.ANVIL, syncID, player.inventory, ScreenHandlerContext.create(player.world, buf.readBlockPos())), player));


        TTLGParticles.init();
        LookingGlassGUIs.initClient();
        RenderCache.init();

        //ClientTickEvents.END_WORLD_TICK.register(clientWorld -> {
        //    if(clientWorld.getTime() % 20 == 0 && (layerCleanLock == null || layerCleanLock.isDone())) {
        //        layerCleanLock = RenderCache.cleanLayerCache();
        //    }
        //});
    }
}
