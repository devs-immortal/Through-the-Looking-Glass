package azzy.fabric.lookingglass.render;

import azzy.fabric.lookingglass.LookingGlassClient;
import azzy.fabric.lookingglass.util.client.RenderHelper;
import azzy.fabric.lookingglass.util.client.TTLGRenderLayers;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.math.Vec3d;
import org.apache.commons.lang3.tuple.Triple;

public class TesseractRenderer<T extends TesseractRenderable> extends BlockEntityRenderer {

    public TesseractRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(BlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if(((TesseractRenderable) entity).shouldRender()) {
            if(((TesseractRenderable) entity).shouldRenderCore()) {
                matrices.push();
                matrices.translate(0.5, 0.5, 0.5);
                matrices.scale(0.35F, 0.35F, 0.35F);
                Vec3d pos = dispatcher.camera.getPos().subtract(Vec3d.ofCenter(entity.getPos()));
                float horDegs = (float) Math.toDegrees(Math.atan(pos.x / pos.z));
                double horComp = Math.sqrt(Math.pow(pos.x, 2) + Math.pow(pos.z, 2));
                float verDegs = (float) Math.toDegrees(Math.atan(pos.y / horComp));
                if(!(pos.z < 0))
                    verDegs = -verDegs;
                matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(horDegs));
                matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(verDegs));
                MinecraftClient.getInstance().getItemRenderer().renderItem(((TesseractRenderable) entity).getCoreItem(), ModelTransformation.Mode.NONE, LookingGlassClient.LIGHTMAP_MAX_LUMINANCE, overlay, matrices, vertexConsumers);
                matrices.pop();
            }

            matrices.push();
            matrices.translate(0.5, 0.5, 0.5);
            matrices.scale(0.45F, 0.45F, 0.45F);
            matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion((entity.getWorld().getTime() + tickDelta) * 2F));
            matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(45));
            matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(45));
            Triple<Integer, Integer, Integer> color = ((TesseractRenderable) entity).getColor();
            RenderHelper.renderVCNCuboid(matrices, vertexConsumers.getBuffer(TTLGRenderLayers.FLAT_BLOOM), color.getLeft(), color.getMiddle(), color.getRight(), 150);
            matrices.pop();
        }
    }
}
