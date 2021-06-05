package azzy.fabric.lookingglass.render;

import azzy.fabric.lookingglass.LookingGlassClient;
import azzy.fabric.lookingglass.blockentity.ProjectorEntity;
import azzy.fabric.lookingglass.util.client.RenderCache;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.BlockRenderManager;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactory;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3f;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class ProjectorRenderer implements BlockEntityRenderer<ProjectorEntity> {

    //hoi
    private RenderLayer renderLayer;
    private NativeImageBackedTexture texture = null;

    public ProjectorRenderer(BlockEntityRendererFactory.Context context) {
    }

    @Override
    public boolean rendersOutsideBoundingBox(ProjectorEntity blockEntity) {
        return true;
    }

    @Override
    public void render(ProjectorEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        ItemStack item = blockEntity.inventory.get(0);

        renderSpeen(blockEntity, tickDelta, matrices, vertexConsumers, light, overlay);
        matrices.push();

        matrices.translate(blockEntity.disX, blockEntity.disY, blockEntity.disZ);
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion((float) blockEntity.rotX));
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((float) blockEntity.rotY));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((float) blockEntity.rotZ));
        matrices.scale((float) blockEntity.scale, (float) blockEntity.scale, (float) blockEntity.scale);

        if (blockEntity.displayState == 0) {

            CompletableFuture<AbstractTexture> image;
            String rawUrl = blockEntity.url;

            Optional<CompletableFuture<AbstractTexture>> future = RenderCache.getFuture(rawUrl);

            if(future.isPresent())
                image = future.get();

            else{
                URL url;
                //validate the url
                try {
                    url = new URL(rawUrl);
                    url.toURI();
                }
                catch (Exception ignored) {
                    matrices.pop();
                    return;
                }
                image = CompletableFuture.supplyAsync(() -> {
                    try {
                        InputStream imageUrl = url.openStream();
                        AbstractTexture futureTexture = new NativeImageBackedTexture(NativeImage.read(imageUrl));
                        imageUrl.close();

                        return futureTexture;
                    } catch (IOException e) {
                        return null;
                    }
                });
                RenderCache.putFuture(rawUrl, image);
            }

            if(!image.isDone()) {
                matrices.pop();
                return;
            }

            try {
                texture = (NativeImageBackedTexture) image.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }

            final RenderLayer cursedLayer = getRenderLayer(rawUrl, blockEntity.getWorld());

            if(cursedLayer == null) {
                matrices.pop();
                return;
            }

            VertexConsumer consumer = vertexConsumers.getBuffer(cursedLayer);
            MatrixStack.Entry matrix = matrices.peek();

            if(texture.getImage() != null) {
                matrices.scale(1, ((float) texture.getImage().getHeight()) / texture.getImage().getWidth(), 1);
                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180F));
                matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion(180F));
                matrices.translate(0, -1, 0);
                consumer.vertex(matrix.getModel(), 0, 1, 0).color(255, 255, 255, 255).texture(1, 1).light(14680160).normal(matrix.getNormal(), 1, 1, 1).next();
                consumer.vertex(matrix.getModel(), 0, 0, 0).color(255, 255, 255, 255).texture(1, 0).light(14680160).normal(matrix.getNormal(), 1, 1, 1).next();
                consumer.vertex(matrix.getModel(), 1, 0, 0).color(255, 255, 255, 255).texture(0, 0).light(14680160).normal(matrix.getNormal(), 1, 1, 1).next();
                consumer.vertex(matrix.getModel(), 1, 1, 0).color(255, 255, 255, 255).texture(0, 1).light(14680160).normal(matrix.getNormal(), 1, 1, 1).next();

                matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(-180F));
            }

            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));

            Sprite sprite = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(new Identifier("minecraft:block/obsidian"));
            VertexConsumer backface = vertexConsumers.getBuffer(RenderLayer.getSolid());

            backface.vertex(matrix.getModel(), 0, 0, (float) (0.01 / blockEntity.scale)).color(255, 255, 255, 255).texture(sprite.getMinU(), sprite.getMinV()).light(14680160).normal(matrix.getNormal(), 1, 1, 1).next();
            backface.vertex(matrix.getModel(), 0, 1, (float) (0.01 / blockEntity.scale)).color(255, 255, 255, 255).texture(sprite.getMinU(), sprite.getMaxV()).light(14680160).normal(matrix.getNormal(), 1, 1, 1).next();
            backface.vertex(matrix.getModel(), 1, 1, (float) (0.01 / blockEntity.scale)).color(255, 255, 255, 255).texture(sprite.getMaxU(), sprite.getMaxV()).light(14680160).normal(matrix.getNormal(), 1, 1, 1).next();
            backface.vertex(matrix.getModel(), 1, 0, (float) (0.01 / blockEntity.scale)).color(255, 255, 255, 255).texture(sprite.getMaxU(), sprite.getMinV()).light(14680160).normal(matrix.getNormal(), 1, 1, 1).next();
        }
        else if(blockEntity.displayState == 1 && !item.isEmpty()){
            if(item.getItem() instanceof BlockItem) {
                BlockState state = ((BlockItem) item.getItem()).getBlock().getDefaultState();
                BlockRenderManager manager = MinecraftClient.getInstance().getBlockRenderManager();
                manager.getModelRenderer().render(blockEntity.getWorld(), manager.getModel(state), state, blockEntity.getPos().up(), matrices, vertexConsumers.getBuffer(RenderLayers.getMovingBlockLayer(state)), false, new Random(), state.getRenderingSeed(blockEntity.getPos()), OverlayTexture.DEFAULT_UV);
            }
            else {
                MinecraftClient.getInstance().getItemRenderer().renderItem(item, ModelTransformation.Mode.NONE, LookingGlassClient.LIGHTMAP_MAX_LUMINANCE, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 3);
            }
        }
        else if(blockEntity.displayState == 2){
            matrices.scale(0.1f, 0.1f, 0.1f);
            matrices.translate(0, 2f, 0);
            matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion(180));
            matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion(180));
            try {
                MinecraftClient.getInstance().textRenderer.draw(matrices, blockEntity.sign, -(blockEntity.sign.length() * 6f) / 2f, 0f, Integer.parseInt(blockEntity.color.replace("0x", ""), 16));
            } catch(Exception suckItTrollzer) {}
        }
        else if(blockEntity.displayState == 3){
            if(item.getOrCreateTag().contains("data") && item.getSubTag("data").contains("entity")) {
                EntityType<?> type = Registry.ENTITY_TYPE.get(Identifier.tryParse(item.getSubTag("data").getString("entity")));
                Entity entity = type.create(blockEntity.getWorld());
                EntityRenderer<Entity> renderer = (EntityRenderer<Entity>) MinecraftClient.getInstance().getEntityRenderDispatcher().getRenderer(entity);
                renderer.render(entity, 0, 0, matrices, vertexConsumers, LookingGlassClient.LIGHTMAP_MAX_LUMINANCE);
            }
        }
        //else if(blockEntity.displayState == 4){
        //}
        matrices.pop();
        renderLayer = null;
    }

    private void renderSpeen(ProjectorEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay){

        matrices.push();
        double offset = Math.sin((blockEntity.getWorld().getTime() + tickDelta) / 16.0) / 6.0;

        int state = blockEntity.displayState;
        Item display;

        switch(state){
            case (1): display = Items.HONEY_BLOCK; break;
            case (2): display = Items.REDSTONE_BLOCK; break;
            case (3): display = Items.SEA_LANTERN; break;
            case (4):
                display = Items.BLACK_STAINED_GLASS; break;
            default: display = Items.GLASS;
        }

        ItemStack speen = new ItemStack(display);

        matrices.translate(0.5, 1 + offset, 0.5);
        matrices.scale(0.2f, 0.2f, 0.2f);
        matrices.multiply(Vec3f.POSITIVE_Y.getDegreesQuaternion((blockEntity.getWorld().getTime() + tickDelta) * 4));
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion((blockEntity.getWorld().getTime() + tickDelta) * 6));
        MinecraftClient.getInstance().getItemRenderer().renderItem(speen, ModelTransformation.Mode.NONE, 14680160, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 0);
        matrices.multiply(Vec3f.NEGATIVE_X.getDegreesQuaternion((blockEntity.getWorld().getTime() + tickDelta) * 4));
        matrices.multiply(Vec3f.POSITIVE_Z.getDegreesQuaternion((blockEntity.getWorld().getTime() + tickDelta) * 6));
        MinecraftClient.getInstance().getItemRenderer().renderItem(speen, ModelTransformation.Mode.NONE, 14680160, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 1);
        matrices.multiply(Vec3f.NEGATIVE_Y.getDegreesQuaternion((blockEntity.getWorld().getTime() + tickDelta) * 6));
        matrices.multiply(Vec3f.POSITIVE_X.getDegreesQuaternion((blockEntity.getWorld().getTime() + tickDelta) * 1));
        MinecraftClient.getInstance().getItemRenderer().renderItem(speen, ModelTransformation.Mode.NONE, 14680160, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers, 2);

        matrices.pop();
    }

    public RenderLayer getRenderLayer(String url, World world){
        if(renderLayer == null){
            if(RenderCache.checkRenderLayer(url, world)) {
                renderLayer = RenderCache.getBakedLayer(url).getRenderLayer();
            }
            else {
                RenderCache.BakedRenderLayer layer = RenderCache.bakeRenderLayer(url, texture);
                if(layer != null)
                    return layer.getRenderLayer();
            }
        }
        return renderLayer;
    }

    //private void flushTexture(){
    //    if(backingTextureId != null){
    //        TextureManager manager = MinecraftClient.getInstance().getTextureManager();
    //        FunkedUpTextureManager.from(manager).unregisterTexture(backingTextureId);
    //    }
    //    if (texture != null) {
    //        texture.close();
    //        texture = null;
    //    }
    //}
}
