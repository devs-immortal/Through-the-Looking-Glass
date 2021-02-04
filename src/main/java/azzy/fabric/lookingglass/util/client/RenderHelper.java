package azzy.fabric.lookingglass.util.client;

import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.util.math.MatrixStack;

public class RenderHelper {

    public static void renderVCNCuboid(MatrixStack matrices, VertexConsumer consumer, int r, int g, int b, int a) {
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
