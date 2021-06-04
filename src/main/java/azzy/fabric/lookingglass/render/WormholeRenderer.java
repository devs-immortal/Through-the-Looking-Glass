package azzy.fabric.lookingglass.render;

import azzy.fabric.lookingglass.blockentity.WormholeEntity;
import azzy.fabric.lookingglass.util.client.RenderHelper;
import azzy.fabric.lookingglass.util.client.TTLGRenderLayers;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.EndPortalBlockEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
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
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((world.getTime() + offset + tickDelta)));
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion((world.getTime() * 2 + offset + tickDelta)));
            matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((world.getTime() * 7 - offset + tickDelta)));
            matrices.scale(scale, scale, scale);

            RenderHelper.renderVCNCuboid(matrices, vertexConsumers.getBuffer(TTLGRenderLayers.getPortalLayer(EndPortalBlockEntityRenderer.PORTAL_TEXTURE, 1)), 255, 255, 255, alpha);
            matrices.scale(1.001F, 1.001F, 1.001F);
            RenderHelper.renderVCNCuboid(matrices, vertexConsumers.getBuffer(TTLGRenderLayers.getPortalLayer(new Identifier("textures/environment/clouds.png"), 1)), 100, 180, 200, (41 - a) / 2);
            matrices.pop();
        }
    }
}
