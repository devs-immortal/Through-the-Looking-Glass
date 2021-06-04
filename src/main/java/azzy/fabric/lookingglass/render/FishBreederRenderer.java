package azzy.fabric.lookingglass.render;

import azzy.fabric.lookingglass.block.LookingGlassBlock;
import azzy.fabric.lookingglass.blockentity.FishBreederEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3f;

public class FishBreederRenderer extends BlockEntityRenderer<FishBreederEntity> {

    public FishBreederRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(FishBreederEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemStack stack = entity.getStack(0);
        if(!stack.isEmpty()) {
            matrices.push();
            matrices.scale(2F, 2F, 2F);
            if(entity.getCachedState().get(LookingGlassBlock.WATERLOGGED)) {
                matrices.translate(0.25, (Math.sin((entity.getWorld().getTime() + tickDelta) / 16) / 12 + 0.2), 0.25);
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((entity.getWorld().getTime() + tickDelta) / 4));
                MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers);
            }
            else {
                matrices.translate(0.25, 0.05, 0.15);
                matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(90));
                MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers);
            }
            matrices.pop();
        }
    }
}
