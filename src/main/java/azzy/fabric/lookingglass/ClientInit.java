package azzy.fabric.lookingglass;

import azzy.fabric.lookingglass.gui.ProjectorContainer;
import azzy.fabric.lookingglass.gui.ProjectorGUI;
import azzy.fabric.lookingglass.render.ChunkLoaderRenderer;
import azzy.fabric.lookingglass.render.ProjectorRenderer;
import azzy.fabric.lookingglass.render.WormholeRenderer;
import azzy.fabric.lookingglass.util.CacheCrimes;
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


import static azzy.fabric.lookingglass.LookingGlass.*;

@Environment(EnvType.CLIENT)
public class ClientInit implements ClientModInitializer {

    public static int textureIdCounter = 1;

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(PROJECTORENTITY, ProjectorRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(CHUNKLOADER_ENTITY, ChunkLoaderRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(WORMHOLE_ENTITY, WormholeRenderer::new);
        ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier(MODID, "projector_gui"), (syncID, id, player, buf) -> new ProjectorContainer( new ProjectorGUI(ScreenHandlerType.ANVIL, syncID, player.inventory, ScreenHandlerContext.create(player.world, buf.readBlockPos())), player));
        BlockRenderLayerMap.INSTANCE.putBlock(PROJECTORBLOCK, RenderLayer.getCutoutMipped());
        BlockRenderLayerMap.INSTANCE.putBlock(CHUNKLOADERBLOCK, RenderLayer.getCutoutMipped());
        CacheCrimes.init();
    }
}
