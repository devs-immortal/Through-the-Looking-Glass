package azzy.fabric.lookingglass.util;

import azzy.fabric.lookingglass.blockentity.ProjectorEntity;
import azzy.fabric.lookingglass.render.ProjectorRenderer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
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

    public static RenderLayer getPortalLayer(Identifier texture, int blendMode) {
        Transparency transparency2;
        Texture texture2;
        if (blendMode <= 1) {
            transparency2 = TRANSLUCENT_TRANSPARENCY;
            texture2 = new Texture(texture, false, false);
        } else {
            transparency2 = ADDITIVE_TRANSPARENCY;
            texture2 = new Texture(texture, false, false);
        }

        return of("ff_portal_layer", VertexFormats.POSITION_COLOR, 7, 256, false, true, RenderLayer.MultiPhaseParameters.builder().transparency(transparency2).texture(texture2).texturing(new PortalTexturing(blendMode)).fog(BLACK_FOG).build(false));
    }
}
