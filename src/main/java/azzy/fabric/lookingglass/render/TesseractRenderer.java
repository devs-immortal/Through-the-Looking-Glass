package azzy.fabric.lookingglass.render;

import azzy.fabric.lookingglass.LookingGlassClient;
import azzy.fabric.lookingglass.blockentity.BlockTesseractEntity;
import azzy.fabric.lookingglass.util.client.TTLGRenderLayers;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import org.apache.commons.lang3.tuple.Triple;
import vazkii.patchouli.client.RenderHelper;

public class TesseractRenderer<T extends BlockTesseractEntity & TesseractRenderable> implements BlockEntityRenderer<T> {
    public TesseractRenderer(BlockEntityRendererFactory.Context ctx) {
    }

    @Override
    public void render(T entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if(entity.shouldRender()) {
            if(entity.shouldRenderCore()) {
                matrices.push();
                matrices.translate(0.5, 0.5, 0.5);
                matrices.scale(0.35F, 0.35F, 0.35F);
                Vec3d pos = MinecraftClient.getInstance().getBlockEntityRenderDispatcher().camera.getPos().subtract(Vec3d.ofCenter(entity.getPos()));
                float horDegs = (float) Math.toDegrees(Math.atan(pos.x / pos.z));
                double horComp = Math.sqrt(Math.pow(pos.x, 2) + Math.pow(pos.z, 2));
                float verDegs = (float) Math.toDegrees(Math.atan(pos.y / horComp));
                if(!(pos.z < 0))
                    verDegs = -verDegs;
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(horDegs));
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(verDegs));
                MinecraftClient.getInstance().getItemRenderer().renderItem(((TesseractRenderable) entity).getCoreItem(), ModelTransformation.Mode.NONE, LookingGlassClient.LIGHTMAP_MAX_LUMINANCE, overlay, matrices, vertexConsumers, 0);
                matrices.pop();
            }

            matrices.push();
            matrices.translate(0.5, 0.5, 0.5);
            matrices.scale(0.45F, 0.45F, 0.45F);
            matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion((entity.getWorld().getTime() + tickDelta) * 2F));
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(45));
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(45));
            Triple<Integer, Integer, Integer> color = ((TesseractRenderable) entity).getColor();
            RenderHelper.renderVCNCuboid(matrices, vertexConsumers.getBuffer(TTLGRenderLayers.FLAT_BLOOM), color.getLeft(), color.getMiddle(), color.getRight(), 150);
            matrices.pop();
        }
    }
}
