package azzy.fabric.lookingglass;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.RenderPhase;
import net.minecraft.client.render.VertexFormats;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL11;

@Environment(EnvType.CLIENT)
public abstract class RenderCrimes extends RenderLayer{
    private static final String CURSED_RENDERLAYER = "lookingglass/cutout_no_difflight";


    private RenderCrimes() {
        super("", VertexFormats.POSITION, 0, 0, false, true, null, null);
    }

    public static RenderLayer getTransNoDiff(final Identifier texture){
        final RenderLayer.MultiPhaseParameters parameters = RenderLayer.MultiPhaseParameters
                .builder()
                .texture(new RenderPhase.Texture(texture, false, false))
                .transparency(TRANSLUCENT_TRANSPARENCY)
                .diffuseLighting(DISABLE_DIFFUSE_LIGHTING)
                .alpha(ONE_TENTH_ALPHA)
                .lightmap(ENABLE_LIGHTMAP)
                .overlay(DISABLE_OVERLAY_COLOR)
                .build(true);

        return RenderLayer.of(CURSED_RENDERLAYER,
                VertexFormats.POSITION_COLOR_TEXTURE_LIGHT_NORMAL,
                GL11.GL_QUADS, 256, false, true, parameters);
    }
}
