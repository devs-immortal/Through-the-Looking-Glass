package azzy.fabric.lookingglass.util;

import azzy.fabric.lookingglass.render.ProjectorRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public abstract class RenderCrimes extends RenderLayer{
    private static final String CURSED_RENDERLAYER = "lookingglass/trans_no_difflight";


    private RenderCrimes() {
        super("ae", VertexFormats.POSITION, 0, 0, false, true, null, null);
    }

    public static RenderLayer getTransNoDiff(Identifier texture, ProjectorRenderer renderer){
        final RenderLayer.MultiPhaseParameters parameters = RenderLayer.MultiPhaseParameters
                .builder()
                .texture(new RenderPhase.Texture(texture, true, true))
                .transparency(TRANSLUCENT_TRANSPARENCY)
                .diffuseLighting(DISABLE_DIFFUSE_LIGHTING)
                .lightmap(ENABLE_LIGHTMAP)
                .overlay(DISABLE_OVERLAY_COLOR)
                .build(true);

        return RenderLayer.of(CURSED_RENDERLAYER + renderer.hashCode(),
                VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
                GL11.GL_QUADS, 256, false, true, parameters);
    }
}
