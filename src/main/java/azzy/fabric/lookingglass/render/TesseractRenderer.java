package azzy.fabric.lookingglass.render;

import azzy.fabric.lookingglass.LookingGlassClient;
import azzy.fabric.lookingglass.blockentity.TesseractRenderable;
import azzy.fabric.lookingglass.item.TTLGItems;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Quaternion;

public class TesseractRenderer<T extends TesseractRenderable> extends BlockEntityRenderer {

    public TesseractRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(BlockEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        if(((TesseractRenderable) entity).shouldRender()) {
            matrices.translate(0.5, 0.5, 0.5);
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((entity.getWorld().getTime() + tickDelta) / 10));
            MinecraftClient.getInstance().getItemRenderer().renderItem(new ItemStack(TTLGItems.DATA_SHARD), ModelTransformation.Mode.NONE, LookingGlassClient.LIGHTMAP_MAX_LUMINANCE, overlay, matrices, vertexConsumers);
            //matrices.translate(-0.4, -0.4, -0.4);
        }
    }
}
