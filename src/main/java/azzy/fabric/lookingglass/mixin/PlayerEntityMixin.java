package azzy.fabric.lookingglass.mixin;

import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Shadow
    public abstract void sendMessage(Text message, boolean actionBar);

    @Inject(method = {"onKilledOther"}, at = {@At("HEAD")}, cancellable = true)
    public void onKilledOther(ServerWorld serverWorld, LivingEntity livingEntity, CallbackInfo ci) {
        List<Block> CURSING_ELIGIBLE_BLOCKS = Arrays.asList(Blocks.DIRT, Blocks.GRASS_BLOCK, Blocks.GRAVEL, Blocks.SAND, Blocks.GRASS_PATH, Blocks.COARSE_DIRT);

        BlockPos enchanterPos = livingEntity.getBlockPos();
        BlockState enchanterBlockState = serverWorld.getBlockState(enchanterPos);
        Block enchanterBlock = enchanterBlockState.getBlock();

        LookingGlassCommon.FFLog.warn("(" + enchanterPos.getX() + ", " + enchanterPos.getY() + ", " + enchanterPos.getZ() + "): " + enchanterBlock.toString());

        // The mob isn't killed on an enchanter.  We're not interested.
        if (!Blocks.ENCHANTING_TABLE.equals(enchanterBlock)) {
            return;
        }

        // Can't see the sky from this position.  We're not interested.
        if (!serverWorld.isSkyVisible(enchanterPos)) {
            sendMessage(new TranslatableText("cursedearth.skynotvisible"), true);
            return;
        }

        long timeOfDay = serverWorld.getTimeOfDay() % 24000;

        // The time isn't near midnight.  So we return doing nothing.
        if ((timeOfDay <= 17500) || (timeOfDay > 18500)) {
            sendMessage(new TranslatableText("cursedearth.notrighttime"), true);
            return;
        }

        int ambientLightLevel = serverWorld.getLightLevel(enchanterPos);

        if (ambientLightLevel > 7) {
            sendMessage(new TranslatableText("cursedearth.noambientlight"), true);
            return;
        }

        for (int x = -1; x < 2; x++) {
            for (int z = -1; z < 2; z++) {
                BlockPos surroundingBlockPos = new BlockPos(enchanterPos.getX() + x, enchanterPos.getY(), enchanterPos.getZ() + z);
                BlockState surroundingBlockState = serverWorld.getBlockState(surroundingBlockPos);
                Block surroundingBlock = surroundingBlockState.getBlock();
                Block blockBeneath = serverWorld.getBlockState(surroundingBlockPos.down()).getBlock();

                // The enchanter isn't surrounded by redstone.  Return doing nothing.
                if (!Blocks.REDSTONE_WIRE.equals(surroundingBlock)) {
                    if ((x != 0) && (z != 0)) {
                        sendMessage(new TranslatableText("cursedearth.nosurroundingredstone"), true);
                        return;
                    }
                }

                // If the block beneath isn't suitable (dirt, grass, path, gravel, sand), we return doing nothing.
                if (!CURSING_ELIGIBLE_BLOCKS.contains(blockBeneath)) {
                    sendMessage(new TranslatableText("cursedearth.notongrass"), true);
                    return;
                }
            }
        }

        // Everything appears to be kosher.  Let's channel Zeus and turn the surrounding eligible blocks into cursed earth.
        LightningEntity lightningEntity = new LightningEntity(EntityType.LIGHTNING_BOLT, serverWorld);
        lightningEntity.setPos(enchanterPos.getX(), enchanterPos.getY() + 1, enchanterPos.getZ());
        serverWorld.spawnEntity(lightningEntity);

        int enchX = enchanterPos.getX();
        int enchY = enchanterPos.getY();
        int enchZ = enchanterPos.getZ();

        LookingGlassCommon.FFLog.warn("(" + enchX + ", " + enchY + ", " + enchZ + "): " + enchanterBlock.toString());

        for (int x = -5; x < 6; x++) {
            for (int y = -5; y < 6; y++) {
                for (int z = -5; z < 6; z++) {
                    BlockPos currBlockPos = new BlockPos(enchX + x, enchY + y, enchZ + z);


                    BlockPos blockAbovePos = currBlockPos.up();
                    BlockState blockAboveState = serverWorld.getBlockState(blockAbovePos);
                    Block blockAbove = blockAboveState.getBlock();

                    // If the block above isn't a translucent block, ignore it and move to next one.
                    // I kinda need to check this way since the isSkyVisible method of serverWorld isn't working properly in this version of fabric at least.
                    if (!blockAbove.isTranslucent(blockAboveState, serverWorld, blockAbovePos))
                        continue;

                    BlockState currBlockState = serverWorld.getBlockState(currBlockPos);
                    Block currBlock = currBlockState.getBlock();

                    LookingGlassCommon.FFLog.warn("(" + currBlockPos.getX() + ", " + currBlockPos.getY() + ", " + currBlockPos.getZ() + "): " + currBlock.toString());

                    if (CURSING_ELIGIBLE_BLOCKS.contains(currBlock)) {
                        serverWorld.setBlockState(currBlockPos, LookingGlassBlocks.CURSED_EARTH_BLOCK.getDefaultState());
                    }
                }
            }
        }
    }
}
