package azzy.fabric.lookingglass.render;

import azzy.fabric.lookingglass.blockentity.ChunkAnchorEntity;
import azzy.fabric.lookingglass.item.LookingGlassItems;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.world.World;

public class ChunkAnchorRenderer extends BlockEntityRenderer<ChunkAnchorEntity> {

    private static final ItemStack item = new ItemStack(LookingGlassItems.DATA_SHARD);

    public ChunkAnchorRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
        item.addEnchantment(Enchantments.SHARPNESS, 1);
    }

    @Override
    public void render(ChunkAnchorEntity entity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {
        World world = entity.getWorld();

        double bounce = Math.sin((world.getTime() + tickDelta) / 18);
        float count = entity.getStack(0).getCount() * 2;
        float spin = -(world.getTime() + tickDelta);
        int tearLight = 14680160;
        ItemStack focus = new ItemStack(Items.CHISELED_QUARTZ_BLOCK);

        matrices.push();
        bounce /= 4;
        matrices.translate(0.5, (1.25 + (count/ 5)) + bounce, 0.5);
        bounce *= 4;
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(spin * Math.max(1, count/2)));
        MinecraftClient.getInstance().getItemRenderer().renderItem(item, ModelTransformation.Mode.GROUND, light, overlay, matrices, vertexConsumers);
        matrices.pop();

        matrices.push();
        bounce /= 2.5;
        spin = -spin;
        matrices.translate(0.5, (1.375 + (count/ 5)) + bounce, 0.5);
        matrices.scale(0.75f, 0.75f, 0.75f);
        float secSpeen = (world.getTime() + tickDelta);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(secSpeen));
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(secSpeen));
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(secSpeen));
        for(int i = (int) count; i > 0; i--){
            float tertiarySpeen = (spin * Math.max(1, count / 4)) / 4;
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(tertiarySpeen));
            if(i % 2 == 0){
                matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion(tertiarySpeen * 1.5f));
            }
            else{
                matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(tertiarySpeen * 2));
            }
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(360f/count));
            double offset = Math.max((count / 6), 1);
            matrices.translate(offset, 0, 0);
            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((spin * (count)) + 90));
            MinecraftClient.getInstance().getItemRenderer().renderItem(focus, ModelTransformation.Mode.GROUND, tearLight, overlay, matrices, vertexConsumers);
            matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion((spin * (count)) + 90));
            matrices.translate(-offset, 0, 0);
        }
        matrices.pop();
    }

    static {
        item.addEnchantment(Enchantments.AQUA_AFFINITY, 1);
    }

    @Override
    public boolean rendersOutsideBoundingBox(ChunkAnchorEntity blockEntity) {
        return true;
    }
}
