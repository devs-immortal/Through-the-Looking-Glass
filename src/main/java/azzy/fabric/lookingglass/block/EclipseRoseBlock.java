package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.util.PositionUtils;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.WitherRoseBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;

import java.util.List;
import java.util.Random;

public class EclipseRoseBlock extends WitherRoseBlock {

    private static final int ECLIPSE_TRIGGER_RADIUS = 8; // Radius in which the eclipse rose can be triggered from a player
    private static final int RESET_ECLIPSE_COOLDOWN = 60; // Cool down in ticks before the eclipse state is reset
    private boolean eclipse = false; // Whether the rose is currently projecting an eclipse
    private BlockPos lastClosestPosApplied; // Closest position of the flower which last applied the eclipse effect

    private int randomDisplayTickCount = 0;
    private int eclipseCooldown = 0;

    public EclipseRoseBlock(StatusEffect effect, Settings settings) {
        super(effect, settings);
        ClientTickEvents.START_WORLD_TICK.register(this::eclipsePhaseTick);
        ClientTickEvents.START_CLIENT_TICK.register(client -> eclipseRadiusCheckTick());
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        super.onEntityCollision(state, world, pos, entity);
        if (world.isClient && world.getDifficulty() != Difficulty.PEACEFUL) {
            if (entity instanceof LivingEntity) {
                LivingEntity livingEntity = (LivingEntity) entity;
                if(livingEntity instanceof PlayerEntity) {
                    PlayerEntity playerEntity = (PlayerEntity) livingEntity;
                    if(playerEntity.isCreative()) {
                        return;
                    }
                }
                // Add blindness effect if a player comes in contact with the plant
                StatusEffectInstance blindnessEffectInstance = new StatusEffectInstance(StatusEffects.BLINDNESS, 120, 0);
                livingEntity.applyStatusEffect(blindnessEffectInstance);
            }
        }
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        final BlockPos immutablePos = new BlockPos(pos);
        super.randomDisplayTick(state, world, pos, random);

        VoxelShape voxelShape = this.getOutlineShape(state, world, pos, ShapeContext.absent());
        Vec3d vec3d = voxelShape.getBoundingBox().getCenter();
        double x = (double)pos.getX() + vec3d.x;
        double z = (double)pos.getZ() + vec3d.z;

        if (random.nextBoolean()) {
            if(randomDisplayTickCount >= 3) {
                for(int i=0 ; i < 5; i++) {
                    // Generate larger particle effects
                    world.addParticle(ParticleTypes.SQUID_INK, x + random.nextDouble() / 5.0D, (double)pos.getY() + (1.5D - random.nextDouble()), z + random.nextDouble() / 5.0D, 0.0D, 0.5D, 0.0D);
                }
                eclipseDisplayTick(world, immutablePos);
                randomDisplayTickCount = 0;
            }
            else {
                randomDisplayTickCount++;
            }
        }
    }

    @Environment(EnvType.CLIENT)
    private void eclipseDisplayTick(World world, BlockPos pos) {
        BlockPos minPos = pos.add(-ECLIPSE_TRIGGER_RADIUS, -ECLIPSE_TRIGGER_RADIUS, -ECLIPSE_TRIGGER_RADIUS);
        BlockPos maxPos = pos.add(ECLIPSE_TRIGGER_RADIUS, ECLIPSE_TRIGGER_RADIUS, ECLIPSE_TRIGGER_RADIUS);

        // Get surrounding entities around the block
        List<Entity> entities = world.getOtherEntities(null, new Box(minPos, maxPos),
                entity -> {
                    if(entity instanceof PlayerEntity) {
                        // Check if the player is in survival/adventure mode and the dimension has a sky
                        ClientPlayerEntity player = (ClientPlayerEntity) entity;
                        return (!player.isCreative() || !player.isSpectator()) && player.world.getDimension().hasSkyLight();
                    }
                    return false;
                });

        for(Entity entity : entities) {
            // We know the entity will be a player, therefore cast it
            ClientPlayerEntity cpe = (ClientPlayerEntity) entity;
            if(!eclipse) {
                boolean triggerEclipse = false;
                if(lastClosestPosApplied == null) {
                    triggerEclipse = true;
                }
                else {
                    BlockPos playerPos = cpe.getBlockPos();
                    int squareDistance = PositionUtils.getSquaredDistance(playerPos, pos);
                    int prevSquaredDistance = PositionUtils.getSquaredDistance(playerPos, lastClosestPosApplied);

                    if(squareDistance <= prevSquaredDistance) {
                        triggerEclipse = true;
                    }
                }
                if(triggerEclipse) {
                    lastClosestPosApplied = pos;
                    eclipse = true;
                }
            }
            return;
        }
    }

    @Environment(EnvType.CLIENT)
    public void eclipsePhaseTick(ClientWorld clientWorld) {
        // Trigger an eclipse if a player is detected nearby an eclipse rose block
        if(eclipse) {
            if(eclipseCooldown > 0) {
                eclipseCooldown--;
                if(eclipseCooldown == 0) {
                    eclipse = false;
                    return;
                }
            }
            long timeOfDay = clientWorld.getTimeOfDay();

            // Gradually move the sun or moon across the sky
            if(timeOfDay < 17950) {
                clientWorld.setTimeOfDay(timeOfDay + 95);
            }
            else if(timeOfDay > 18050) {
                clientWorld.setTimeOfDay(timeOfDay - 95);
            }
            else {
                clientWorld.setTimeOfDay(18000);
            }
        }
    }

    @Environment(EnvType.CLIENT)
    private void eclipseRadiusCheckTick() {
        // If the eclipse phase is triggered check if the client entity is still near a rose
        if(eclipse) {
            ClientPlayerEntity player = MinecraftClient.getInstance().player;
            if(player != null && lastClosestPosApplied != null) {
                int[] distanceVec = new int[3];
                distanceVec[0] = Math.abs(player.getBlockPos().getX() - lastClosestPosApplied.getX());
                distanceVec[1] = Math.abs(player.getBlockPos().getY() - lastClosestPosApplied.getY());
                distanceVec[2] = Math.abs(player.getBlockPos().getZ() - lastClosestPosApplied.getZ());

                for(int distance : distanceVec) {
                    // If player is outside the eclipse effect radius remove the eclipse lighting effect instantly
                    if (distance > ECLIPSE_TRIGGER_RADIUS) {
                        if(eclipseCooldown == 0) {
                            eclipseCooldown = RESET_ECLIPSE_COOLDOWN;
                        }
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void onBroken(WorldAccess world, BlockPos pos, BlockState state) {
        super.onBroken(world, pos, state);
        eclipseCooldown = RESET_ECLIPSE_COOLDOWN;
    }

    public boolean isEclipse() { return eclipse; }
}
