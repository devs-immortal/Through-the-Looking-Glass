package azzy.fabric.lookingglass.util.client;

import azzy.fabric.lookingglass.LookingGlassCommon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public abstract class TTLGRenderLayers extends RenderLayer{
    private static final String DYNAMIC_RENDERLAYER = "lookingglass/trans_no_difflight_";


    private TTLGRenderLayers() {
        super("ae", VertexFormats.POSITION, 0, 0, false, true, null, null);
    }

    public static RenderLayer getTransNoDiff(Identifier texture){
        final RenderLayer.MultiPhaseParameters parameters = RenderLayer.MultiPhaseParameters
                .builder()
                .texture(new RenderPhase.Texture(texture, true, true))
                .transparency(TRANSLUCENT_TRANSPARENCY)
                .diffuseLighting(DISABLE_DIFFUSE_LIGHTING)
                .lightmap(ENABLE_LIGHTMAP)
                .overlay(DISABLE_OVERLAY_COLOR)
                .build(true);

        return RenderLayer.of(DYNAMIC_RENDERLAYER + texture,
                VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
                GL11.GL_QUADS, 256, false, true, parameters);
    }

    public static final RenderLayer FLAT_BLOOM = RenderLayer.of("lookingglass:flat_bloom", VertexFormats.POSITION_COLOR, GL11.GL_QUADS, 256, false, true, RenderLayer.MultiPhaseParameters.builder().texture(new RenderPhase.Texture(new Identifier(LookingGlassCommon.MODID, "textures/special/flat.png"), false, false)).diffuseLighting(DISABLE_DIFFUSE_LIGHTING).transparency(TRANSLUCENT_TRANSPARENCY).target(RenderPhase.ITEM_TARGET).lightmap(DISABLE_LIGHTMAP).fog(NO_FOG).layering(RenderPhase.VIEW_OFFSET_Z_LAYERING).writeMaskState(ALL_MASK).build(true));

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
