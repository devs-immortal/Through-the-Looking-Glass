package azzy.fabric.lookingglass.entity.model;

import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.entity.FlarefinKoiEntity;
import net.minecraft.client.render.entity.EntityRenderDispatcher;
import net.minecraft.client.render.entity.MobEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.MathHelper;

public class FlarefinKoiRenderer extends MobEntityRenderer<FlarefinKoiEntity, FlarefinKoiModel> {

    private static final Identifier TEXTURE = new Identifier(LookingGlassCommon.MODID, "textures/entity/flarefin_koi.png");

    public FlarefinKoiRenderer(EntityRenderDispatcher entityRenderDispatcher) {
        super(entityRenderDispatcher, new FlarefinKoiModel(), 0.3f);
    }

    @Override
    public Identifier getTexture(FlarefinKoiEntity entity) {
        return TEXTURE;
    }

    @Override
    protected void setupTransforms(FlarefinKoiEntity entity, MatrixStack matrices, float animationProgress, float bodyYaw, float tickDelta) {
        super.setupTransforms(entity, matrices, animationProgress, bodyYaw, tickDelta);
        if(entity.getAir() <= 20) {
            float i = 4.3F * MathHelper.sin(0.6F * animationProgress);
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(i));
            if (!entity.isTouchingWater()) {
                matrices.translate(0.10000000149011612D, 0.10000000149011612D, -0.10000000149011612D);
                matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(90.0F));
            }
        }
    }
}
