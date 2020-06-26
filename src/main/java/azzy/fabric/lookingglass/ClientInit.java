package azzy.fabric.lookingglass;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.screen.ScreenProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.fluid.Fluid;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;


import java.util.ArrayList;
import java.util.List;

import static azzy.fabric.lookingglass.LookingGlass.MODID;
import static azzy.fabric.lookingglass.LookingGlass.PROJECTORENTITY;

@Environment(EnvType.CLIENT)
public class ClientInit implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        BlockEntityRendererRegistry.INSTANCE.register(PROJECTORENTITY, ProjectorRenderer::new);
        ScreenProviderRegistry.INSTANCE.registerFactory(new Identifier(MODID, "projector_gui"), (syncID, id, player, buf) -> new ProjectorContainer( new ProjectorGUI(ScreenHandlerType.ANVIL, syncID, player.inventory, ScreenHandlerContext.create(player.world, buf.readBlockPos())), player));
    }
}
