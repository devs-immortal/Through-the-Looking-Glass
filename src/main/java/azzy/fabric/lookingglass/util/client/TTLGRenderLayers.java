package azzy.fabric.lookingglass.util.client;

import azzy.fabric.lookingglass.LookingGlassCommon;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public abstract class TTLGRenderLayers extends RenderLayer{
    private static final String DYNAMIC_RENDERLAYER = "lookingglass/trans_no_difflight_";


    private TTLGRenderLayers() {
        super("ae", VertexFormats.POSITION, VertexFormat.DrawMode.QUADS, 0, false, true, null, null);
    }

    public static RenderLayer getTransNoDiff(Identifier texture){
        final RenderLayer.MultiPhaseParameters parameters = RenderLayer.MultiPhaseParameters
                .builder()
                .texture(new RenderPhase.Texture(texture, true, true))
                .transparency(TRANSLUCENT_TRANSPARENCY)
                .shader(Shader.BLOCK_SHADER)
                .lightmap(ENABLE_LIGHTMAP)
                .overlay(DISABLE_OVERLAY_COLOR)
                .build(true);

        return RenderLayerConstructor.buildMultiPhase(DYNAMIC_RENDERLAYER + texture,
                VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
                VertexFormat.DrawMode.QUADS, 256, false, true, parameters);
    }

    public static final RenderLayer FLAT_BLOOM = RenderLayerConstructor.buildMultiPhase("lookingglass:flat_bloom", VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS, 256, false, true, RenderLayer.MultiPhaseParameters.builder().texture(Textures.create().add(new Identifier(LookingGlassCommon.MODID, "textures/special/flat.png"), false, false).build()).shader(Shader.BLOCK_SHADER).transparency(TRANSLUCENT_TRANSPARENCY).target(RenderPhase.ITEM_TARGET).lightmap(DISABLE_LIGHTMAP).layering(RenderPhase.VIEW_OFFSET_Z_LAYERING).writeMaskState(ALL_MASK).build(true));

    public static RenderLayer getPortalLayer(int transparency, boolean gateway, Identifier ... textures) {
        Transparency trans = switch (transparency) {
            default -> RenderPhase.NO_TRANSPARENCY;
            case 1 -> TRANSLUCENT_TRANSPARENCY;
            case 2 -> RenderPhase.ADDITIVE_TRANSPARENCY;
        };

        var builder = Textures.create();

        for (Identifier texture : textures) {
            builder.add(texture, false, false);
        }

        return RenderLayerConstructor.buildMultiPhase("ff_portal_layer", VertexFormats.POSITION_COLOR, VertexFormat.DrawMode.QUADS, 256, false, true, RenderLayer.MultiPhaseParameters.builder().transparency(Transparency.NO_TRANSPARENCY).texture(builder.build()).shader(gateway ? RenderPhase.END_GATEWAY_SHADER : RenderPhase.END_PORTAL_SHADER).build(false));
    }
}
