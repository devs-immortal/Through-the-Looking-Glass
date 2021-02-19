package azzy.fabric.lookingglass.util;

import azzy.fabric.lookingglass.blockentity.ProjectorEntity;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import static azzy.fabric.lookingglass.LookingGlassCommon.MODID;

public class GeneralNetworking {

    public static final Identifier TRANSLATEABLE_PACKET = new Identifier(MODID, "p_translateable");
    public static final Identifier PROJECTOR_SYNC_PACKET = new Identifier(MODID, "p_general");

    public static void init() {
        ServerPlayNetworking.registerGlobalReceiver(PROJECTOR_SYNC_PACKET, ((minecraftServer, serverPlayerEntity, serverPlayNetworkHandler, packet, packetSender) -> {
            BlockPos pos = packet.readBlockPos();

            double rotX = packet.readDouble();
            double rotY = packet.readDouble();
            double rotZ = packet.readDouble();
            double disX = packet.readDouble();
            double disY = packet.readDouble();
            double disZ = packet.readDouble();
            double scale = packet.readDouble();
            String url = packet.readString(2048);
            String sign = packet.readString(2048);
            String color = packet.readString(256);

            minecraftServer.execute(() -> {
                ServerWorld world = (ServerWorld) serverPlayerEntity.world;
                BlockEntity entity = world.getBlockEntity(pos);
                if(entity instanceof ProjectorEntity) {
                    ProjectorEntity projector = (ProjectorEntity) entity;
                    projector.rotX = rotX;
                    projector.rotY = rotY;
                    projector.rotZ = rotZ;
                    projector.disX = disX;
                    projector.disY = disY;
                    projector.disZ = disZ;
                    projector.scale =scale;
                    projector.url = url;
                    projector.sign = sign ;
                    projector.color =color;
                    projector.sync();
                }
            });
        }));
    }
}
