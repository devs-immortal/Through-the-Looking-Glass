package azzy.fabric.lookingglass;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.entity.EntitySpawnPacket;
import azzy.fabric.lookingglass.entity.LookingGlassEntities;
import azzy.fabric.lookingglass.entity.model.FlarefinKoiModel;
import azzy.fabric.lookingglass.entity.model.FlarefinKoiRenderer;
import azzy.fabric.lookingglass.gui.LookingGlassGUIs;
import azzy.fabric.lookingglass.particle.LookingGlassParticles;
import azzy.fabric.lookingglass.render.*;
import azzy.fabric.lookingglass.util.ClientNetworkingUtils;
import azzy.fabric.lookingglass.util.client.RenderCache;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.rendereregistry.v1.BlockEntityRendererRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityModelLayerRegistry;
import net.fabricmc.fabric.api.client.rendereregistry.v1.EntityRendererRegistry;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.entity.FlyingItemEntityRenderer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.registry.Registry;

import java.util.UUID;

import static azzy.fabric.lookingglass.block.LookingGlassBlocks.*;

@Environment(EnvType.CLIENT)
public class LookingGlassClient implements ClientModInitializer {

    public static int textureIdCounter = 0;
    public static final int LIGHTMAP_MAX_LUMINANCE = 14680160;
    //private static Future<?> layerCleanLock;

    public static final Identifier PacketID = new Identifier(LookingGlassCommon.MODID, "spawn_packet");

    public void receiveEntityPacket() {
        ClientSidePacketRegistry.INSTANCE.register(PacketID, (ctx, byteBuf) -> {
            EntityType<?> et = Registry.ENTITY_TYPE.get(byteBuf.readVarInt());
            UUID uuid = byteBuf.readUuid();
            int entityId = byteBuf.readVarInt();
            Vec3d pos = EntitySpawnPacket.PacketBufUtil.readVec3d(byteBuf);
            float pitch = EntitySpawnPacket.PacketBufUtil.readAngle(byteBuf);
            float yaw = EntitySpawnPacket.PacketBufUtil.readAngle(byteBuf);
            ctx.getTaskQueue().execute(() -> {
                if (MinecraftClient.getInstance().world == null)
                    throw new IllegalStateException("Tried to spawn entity in a null world!");
                Entity e = et.create(MinecraftClient.getInstance().world);
                if (e == null)
                    throw new IllegalStateException("Failed to create instance of entity \"" + Registry.ENTITY_TYPE.getId(et) + "\"!");
                e.updateTrackedPosition(pos);
                e.setPos(pos.x, pos.y, pos.z);
                e.setPitch(pitch);
                e.setYaw(yaw);
                e.setId(entityId);
                e.setUuid(uuid);
                MinecraftClient.getInstance().world.addEntity(entityId, e);
            });
        });
    }

    @Override
    public void onInitializeClient() {
        //Registering renderers
        BlockEntityRendererRegistry.INSTANCE.register(PROJECTORENTITY, ProjectorRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(CHUNKLOADER_ENTITY, ChunkAnchorRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(WORMHOLE_ENTITY, WormholeRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(BLOCK_TESSERACT_ENTITY, TesseractRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(FISH_BREEDER_ENTITY, FishBreederRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(CREATIVE_ENERGY_SOURCE_ENTITY, CreativeEnergySourceRenderer::new);
        BlockEntityRendererRegistry.INSTANCE.register(SUFFUSER_ENTITY, SuffuserRenderer::new);

        EntityRendererRegistry.INSTANCE.register(LookingGlassEntities.FLAREFIN_KOI_ENTITY_TYPE, FlarefinKoiRenderer::new);

        EntityModelLayerRegistry.registerModelLayer(FlarefinKoiModel.LAYER, FlarefinKoiModel::getTexturedModelData);

        receiveEntityPacket();

        //Render layers
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(), FISH_BREEDER_BLOCK, BLOCK_TESSERACT_BLOCK, GHOST_GLASS, ETHEREAL_GLASS, REVERSE_ETHEREAL_GLASS, RED_GLASS);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getCutoutMipped(), CORN_PLANT, RED_SEAGRASS, TALL_RED_SEAGRASS, ECLIPSE_ROSE);
        BlockRenderLayerMap.INSTANCE.putBlocks(RenderLayer.getTranslucent(), CREATIVE_ENERGY_SOURCE_BLOCK, PINK_GEL_BLOCK, DARK_GLASS, DARK_GHOST_GLASS, DARK_ETHEREAL_GLASS, REVERSE_DARK_ETHEREAL_GLASS, GLOW_GLASS);

        //Clientside init
        ECLIPSE_ROSE.initClient();

        LookingGlassParticles.init();
        LookingGlassGUIs.initClient();
        ClientNetworkingUtils.init();
        RenderCache.init();
    }
}
