package azzy.fabric.lookingglass.render;

import azzy.fabric.lookingglass.LookingGlassClient;
import azzy.fabric.lookingglass.blockentity.CreativeEnergySourceEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;

public class CreativeEnergySourceRenderer extends BlockEntityRenderer<CreativeEnergySourceEntity> {

    public CreativeEnergySourceRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(CreativeEnergySourceEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        matrices.push();
        matrices.translate(0.5, 0.5, 0.5);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((entity.getWorld().getTime() + tickDelta) / 4));
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((entity.getWorld().getTime() + tickDelta) / 4));
        matrices.scale(0.55F, 0.55F, 0.55F);
        MinecraftClient.getInstance().getItemRenderer().renderItem(CreativeEnergySourceEntity.REDSPEEN, ModelTransformation.Mode.NONE, LookingGlassClient.LIGHTMAP_MAX_LUMINANCE, overlay, matrices, vertexConsumers);
        matrices.pop();
    }
}
