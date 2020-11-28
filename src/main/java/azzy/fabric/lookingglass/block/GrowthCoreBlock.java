package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.LookingGlassCommon;
import io.netty.buffer.Unpooled;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Fertilizable;
import net.minecraft.item.BoneMealItem;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class GrowthCoreBlock extends CoreBlock {

    private final int grade;
    private final int chance;
    private final int loops;

    public GrowthCoreBlock(Settings settings, int range, int chance, int loops) {
        super(settings.ticksRandomly());
        this.grade = range;
        this.chance = chance;
        this.loops = loops;
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if(!world.isClient()) {
            super.randomTick(state, world, pos, random);
            BlockPos startPos = pos.add(-grade, 0, -grade);
            BlockPos endPos = pos.add(grade, 0, grade);
            BlockPos.iterate(startPos, endPos).forEach( target -> {
                BlockState targetState = world.getBlockState(target);
                Block crop = targetState.getBlock();
                if(world.getRandom().nextInt(6) <= chance && crop instanceof Fertilizable) {
                    if(grade + 1 <= 4) {
                        if(((Fertilizable) crop).isFertilizable(world, target, targetState, world.isClient())) {
                            ((Fertilizable) crop).grow(world, world.getRandom(), target, targetState);
                            sendGrowthParticlePacket(world, target, grade * 3);
                        }
                    }
                    else {
                        for(int i = 0; i < loops; i++) {
                            ((Fertilizable) crop).grow(world, world.getRandom(), target, targetState);
                            sendGrowthParticlePacket(world, target, grade * 2);
                        }
                    }
                }
            });
            sendGrowthParticlePacket(world, pos, grade);
        }
    }

    public static void sendGrowthParticlePacket(ServerWorld world, BlockPos pos, int count) {
        PacketByteBuf byteBuf = new PacketByteBuf(Unpooled.buffer());
        byteBuf.writeBlockPos(pos);
        byteBuf.writeInt(count);
        List<ServerPlayerEntity> players = world.getPlayers();
        for (ServerPlayerEntity player : players) {
            if(player.getBlockPos().isWithinDistance(pos, 64.0D))
                ServerSidePacketRegistry.INSTANCE.sendToPlayer(player, LookingGlassCommon.BLOCKPOS_TO_CLIENT_PACKET, byteBuf);
        }
    }
}
