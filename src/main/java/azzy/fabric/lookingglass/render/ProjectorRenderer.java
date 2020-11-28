package azzy.fabric.lookingglass.render;

import azzy.fabric.lookingglass.util.CacheCrimes;
import azzy.fabric.lookingglass.util.FunkedUpTextureManager;
import azzy.fabric.lookingglass.util.RenderCrimes;
import azzy.fabric.lookingglass.blockentity.ProjectorEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.*;
import net.minecraft.client.render.block.entity.BlockEntityRenderDispatcher;
import net.minecraft.client.render.block.entity.BlockEntityRenderer;
import net.minecraft.client.render.model.json.ModelTransformation;
import net.minecraft.client.texture.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.client.util.math.Vector3f;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static azzy.fabric.lookingglass.LookingGlassClient.textureIdCounter;
import static azzy.fabric.lookingglass.LookingGlassCommon.MODID;

public class ProjectorRenderer extends BlockEntityRenderer<ProjectorEntity> {

    //hoi
    private RenderLayer renderLayer;
    private Identifier backingTextureId;
    private NativeImageBackedTexture texture = null;

    public ProjectorRenderer(BlockEntityRenderDispatcher dispatcher) {
        super(dispatcher);
    }

    @Override
    public boolean rendersOutsideBoundingBox(ProjectorEntity blockEntity) {
        return true;
    }

    @Override
    public void render(ProjectorEntity blockEntity, float tickDelta, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, int overlay) {

        CompletableFuture<AbstractTexture> image;
        String rawUrl = blockEntity.url;
        ItemStack item = blockEntity.inventory.get(0);
        Optional<CompletableFuture<AbstractTexture>> future = CacheCrimes.getFuture(rawUrl);

        if(future.isPresent())
            image = future.get();

        else{
            image = CompletableFuture.supplyAsync(() -> {
                try {
                    InputStream imageUrl = new URL(rawUrl).openStream();
                    AbstractTexture futureTexture = new NativeImageBackedTexture(NativeImage.read(imageUrl));
                    imageUrl.close();
                    return futureTexture;
                } catch (IOException e) {
                    return null;
                }
            });
            CacheCrimes.putFuture(rawUrl, image);
        }

        if(!image.isDone())
            return;

        try {
            texture = (NativeImageBackedTexture) image.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        renderSpeen(blockEntity, tickDelta, matrices, vertexConsumers, light, overlay);

        matrices.push();

        matrices.translate(blockEntity.disX, blockEntity.disY, blockEntity.disZ);
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion((float) blockEntity.rotX));
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((float) blockEntity.rotY));
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((float) blockEntity.rotZ));
        matrices.scale((float) blockEntity.scale, (float) blockEntity.scale, (float) blockEntity.scale);

        if (blockEntity.displayState == 0 && texture != null) {

            final RenderLayer cursedLayer = getRenderLayer();
            VertexConsumer consumer = vertexConsumers.getBuffer(cursedLayer);

            texture.getImage().mirrorVertically();
            matrices.scale(1, texture.getImage().getHeight()/texture.getImage().getWidth(), 1);
            MatrixStack.Entry matrix = matrices.peek();
            consumer.vertex(matrix.getModel(), 0, 0, 0).color(255, 255, 255, 255).texture(0, 1).light(14680160).normal(matrix.getNormal(), 1, 1, 1).next();
            consumer.vertex(matrix.getModel(), 0, 1, 0).color(255, 255, 255, 255).texture(0, 0).light(14680160).normal(matrix.getNormal(), 1, 1, 1).next();
            consumer.vertex(matrix.getModel(), 1, 1, 0).color(255, 255, 255, 255).texture(1, 0).light(14680160).normal(matrix.getNormal(), 1, 1, 1).next();
            consumer.vertex(matrix.getModel(), 1, 0, 0).color(255, 255, 255, 255).texture(1, 1).light(14680160).normal(matrix.getNormal(), 1, 1, 1).next();


            matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion(180));
            matrices.translate(-1, 0, 0);

            Sprite sprite = MinecraftClient.getInstance().getSpriteAtlas(SpriteAtlasTexture.BLOCK_ATLAS_TEXTURE).apply(new Identifier("minecraft:block/obsidian"));
            VertexConsumer backface = vertexConsumers.getBuffer(RenderLayer.getSolid());

            backface.vertex(matrix.getModel(), 0, 0, (float) (0.01 / blockEntity.scale)).color(255, 255, 255, 255).texture(sprite.getMinU(), sprite.getMinV()).light(14680160).normal(matrix.getNormal(), 1, 1, 1).next();
            backface.vertex(matrix.getModel(), 0, 1, (float) (0.01 / blockEntity.scale)).color(255, 255, 255, 255).texture(sprite.getMinU(), sprite.getMaxV()).light(14680160).normal(matrix.getNormal(), 1, 1, 1).next();
            backface.vertex(matrix.getModel(), 1, 1, (float) (0.01 / blockEntity.scale)).color(255, 255, 255, 255).texture(sprite.getMaxU(), sprite.getMaxV()).light(14680160).normal(matrix.getNormal(), 1, 1, 1).next();
            backface.vertex(matrix.getModel(), 1, 0, (float) (0.01 / blockEntity.scale)).color(255, 255, 255, 255).texture(sprite.getMaxU(), sprite.getMinV()).light(14680160).normal(matrix.getNormal(), 1, 1, 1).next();
        }
        else if(blockEntity.displayState == 1 && !item.isEmpty()){
            MinecraftClient.getInstance().getItemRenderer().renderItem(item, ModelTransformation.Mode.NONE, 14680160, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);
        }
        else if(blockEntity.displayState == 2){
            matrices.scale(0.1f, 0.1f, 0.1f);
            matrices.translate(0, 2f, 0);
            matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion(180));
            try {
                MinecraftClient.getInstance().textRenderer.draw(matrices, blockEntity.sign, -(blockEntity.sign.length() * 6f) / 2f, 0f, Integer.parseInt(blockEntity.color.replace("0x", ""), 16));
            }
            catch (Exception ignored){}
        }
        else if(blockEntity.displayState == 3){

        }
        else if(blockEntity.displayState == 4){
        }
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
            case (3):
            case (4):
                display = Items.BLACK_STAINED_GLASS; break;
            default: display = Items.GLASS;
        }

        ItemStack speen = new ItemStack(display);

        matrices.translate(0.5, 1+offset, 0.5);
        matrices.scale(0.2f, 0.2f, 0.2f);
        matrices.multiply(Vector3f.POSITIVE_Y.getDegreesQuaternion((blockEntity.getWorld().getTime() + tickDelta) * 4));
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion((blockEntity.getWorld().getTime() + tickDelta) * 6));
        MinecraftClient.getInstance().getItemRenderer().renderItem(speen, ModelTransformation.Mode.NONE, 14680160, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);
        matrices.multiply(Vector3f.NEGATIVE_X.getDegreesQuaternion((blockEntity.getWorld().getTime() + tickDelta) * 4));
        matrices.multiply(Vector3f.POSITIVE_Z.getDegreesQuaternion((blockEntity.getWorld().getTime() + tickDelta) * 6));
        MinecraftClient.getInstance().getItemRenderer().renderItem(speen, ModelTransformation.Mode.NONE, 14680160, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);
        matrices.multiply(Vector3f.NEGATIVE_Y.getDegreesQuaternion((blockEntity.getWorld().getTime() + tickDelta) * 6));
        matrices.multiply(Vector3f.POSITIVE_X.getDegreesQuaternion((blockEntity.getWorld().getTime() + tickDelta) * 1));
        MinecraftClient.getInstance().getItemRenderer().renderItem(speen, ModelTransformation.Mode.NONE, 14680160, OverlayTexture.DEFAULT_UV, matrices, vertexConsumers);

        matrices.pop();
    }

    static private Identifier generateId() {
        final int id = textureIdCounter++;
        return new Identifier(MODID, "dynamic/dispmod_" + id);
    }


    public RenderLayer getRenderLayer(){
        if(renderLayer == null){
            final TextureManager texMan = MinecraftClient.getInstance().getTextureManager();
            backingTextureId = generateId();

            texMan.registerTexture(backingTextureId, texture);
            renderLayer = RenderCrimes.getTransNoDiff(backingTextureId, this);
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
