package azzy.fabric.lookingglass.render;

import azzy.fabric.lookingglass.blockentity.SuffuserEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.WorldRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SuffuserRenderer extends BlockEntityRenderer<SuffuserEntity> {


    public SuffuserRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public void render(SuffuserEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        ItemStack stack = entity.getStack(0);
        World world = entity.getWorld();
        if(world != null && !stack.isEmpty()) {
            matrices.push();
            matrices.translate(0.5, 1.334F + Math.sin((world.getTime() + tickDelta) / 24F) / 8F, 0.5);
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((world.getTime() + tickDelta) * 12F));
            MinecraftClient.getInstance().getItemRenderer().renderItem(stack, ModelTransformation.Mode.GROUND, WorldRenderer.getLightmapCoordinates(world, entity.getPos().up()), overlay, matrices, vertexConsumers);
            matrices.pop();
        }
    }

    @Override
    public boolean rendersOutsideBoundingBox(SuffuserEntity blockEntity) {
        return true;
    }
}
