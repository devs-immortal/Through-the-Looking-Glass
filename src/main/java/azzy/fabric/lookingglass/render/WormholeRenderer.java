package azzy.fabric.lookingglass.render;

import azzy.fabric.lookingglass.blockentity.WormholeEntity;
import azzy.fabric.lookingglass.util.client.TTLGRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class WormholeRenderer extends BlockEntityRenderer<WormholeEntity> {

    public WormholeRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(WormholeEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        World world = entity.getWorld();
        float scale = (float) (((Math.sin((world.getTime() + tickDelta) / 20))) / 25 + 1.0);

        for(int a = entity.getOnTicks(); a > 0; a--){
            scale *= 1.03;
            int alpha = 260 - ((-26 + a));
            int offset = a * 2;

            matrices.push();
            matrices.translate(0.5, 2.0 + Math.sin((world.getTime() - offset + tickDelta) / 5) / 2, 0.5);
            matrices.scale(0.15F, 0.15F, 0.15F);
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((world.getTime() + offset + tickDelta)));
            matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion((world.getTime() * 2 + offset + tickDelta)));
            matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((world.getTime() * 7 - offset + tickDelta)));
            matrices.scale(scale, scale, scale);

            renderCuboid(matrices, vertexConsumers.getBuffer(TTLGRenderLayers.getPortalLayer(EndPortalBlockEntityRenderer.PORTAL_TEXTURE, 1)), 255, 255, 255, alpha);
            matrices.scale(1.001F, 1.001F, 1.001F);
            renderCuboid(matrices, vertexConsumers.getBuffer(TTLGRenderLayers.getPortalLayer(new Identifier("textures/environment/clouds.png"), 1)), 100, 180, 200, (41 - a) / 2);
            matrices.pop();
        }
    }

    private void renderCuboid(MatrixStack matrices, VertexConsumer consumer, int r, int g, int b, int a) {
        MatrixStack.Entry matrix = matrices.peek();
        matrices.translate(-0.5, -0.5, -0.5);
        consumer.vertex(matrix.getModel(), 0, 0, 0).color(r, g, b, a).next();
        consumer.vertex(matrix.getModel(), 0, 1, 0).color(r, g, b, a).next();
        consumer.vertex(matrix.getModel(), 1, 1, 0).color(r, g, b, a).next();
        consumer.vertex(matrix.getModel(), 1, 0, 0).color(r, g, b, a).next();
        matrices.translate(1, 0, 0);
        consumer.vertex(matrix.getModel(), 0, 0, 0).color(r, g, b, a).next();
        consumer.vertex(matrix.getModel(), 0, 1, 0).color(r, g, b, a).next();
        consumer.vertex(matrix.getModel(), 0, 1, 1).color(r, g, b, a).next();
        consumer.vertex(matrix.getModel(), 0, 0, 1).color(r, g, b, a).next();
        matrices.translate(0, 0, 1);
        consumer.vertex(matrix.getModel(), 0, 0, 0).color(r, g, b, a).next();
        consumer.vertex(matrix.getModel(), 0, 1, 0).color(r, g, b, a).next();
        consumer.vertex(matrix.getModel(), -1, 1, 0).color(r, g, b, a).next();
        consumer.vertex(matrix.getModel(), -1, 0, 0).color(r, g, b, a).next();
        matrices.translate(-1, 0, 0);
        consumer.vertex(matrix.getModel(), 0, 0, 0).color(r, g, b, a).next();
        consumer.vertex(matrix.getModel(), 0, 1, 0).color(r, g, b, a).next();
        consumer.vertex(matrix.getModel(), 0, 1, -1).color(r, g, b, a).next();
        consumer.vertex(matrix.getModel(), 0, 0, -1).color(r, g, b, a).next();
        matrices.translate(0, 0, -1);
        consumer.vertex(matrix.getModel(), 0, 0, 0).color(r, g, b, a).next();
        consumer.vertex(matrix.getModel(), 1, 0, 0).color(r, g, b, a).next();
        consumer.vertex(matrix.getModel(), 1, 0, 1).color(r, g, b, a).next();
        consumer.vertex(matrix.getModel(), 0, 0, 1).color(r, g, b, a).next();
        matrices.translate(0, 1, 1);
        consumer.vertex(matrix.getModel(), 0, 0, 0).color(r, g, b, a).next();
        consumer.vertex(matrix.getModel(), 1, 0, 0).color(r, g, b, a).next();
        consumer.vertex(matrix.getModel(), 1, 0, -1).color(r, g, b, a).next();
        consumer.vertex(matrix.getModel(), 0, 0, -1).color(r, g, b, a).next();
        matrices.translate(0.5, -0.5, -0.5);
    }
}
