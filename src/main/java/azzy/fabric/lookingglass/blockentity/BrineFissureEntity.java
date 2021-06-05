package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Heightmap;

import java.util.Random;

public class BrineFissureEntity extends BlockEntity implements LookingGlassTickable {

    public BrineFissureEntity(BlockPos pos, BlockState state) {
        super(LookingGlassBlocks.BRINE_FISSURE_ENTITY, pos, state);
    }

    @Override
    public void tick() {
        if(world != null) {
            BlockPos basePos = pos;
            Random random = world.getRandom();
            basePos = basePos.up();
            for(int i = -12; i <= 12; i++) {
                for(int j = -12; j <= 12; j++) {
                    BlockPos testPos = basePos.add(i, 0, j);
                    if(random.nextInt(200) == 0) {
                        for (int k = 0; k < random.nextInt(2); k++) {
                            if(world.isWater(testPos) && random.nextInt(testPos.getManhattanDistance(basePos) / 2 + 1) == 0) {
                                BlockPos steamPos = world.getTopPosition(Heightmap.Type.WORLD_SURFACE, testPos).down();
                                BlockPos bubblePos = steamPos;
                                int height = 0;
                                while(world.isWater(bubblePos.down())) {
                                    bubblePos = bubblePos.down();
                                    height++;
                                }
                                world.addImportantParticle(ParticleTypes.BUBBLE_COLUMN_UP, true, bubblePos.getX() + random.nextFloat(), bubblePos.getY() + ((random.nextFloat() / 1.01F) * height), bubblePos.getZ() + random.nextFloat(), 0, Math.abs(random.nextGaussian() / 13), 0);
                                if(random.nextInt(14) == 0) {
                                    world.addImportantParticle(ParticleTypes.CLOUD, true, steamPos.getX() + random.nextFloat(), steamPos.getY() + 1.05, steamPos.getZ() + random.nextFloat(), 0, Math.abs(random.nextGaussian() / 12), 0);
                                }
                            }
                        }
                    }
                }
                //world.getNonSpectatingEntities(LivingEntity.class, new Box(pos.add(-3, -0.5, -3), pos.add(3, 3, 3))).stream().filter(Entity::isInsideWaterOrBubbleColumn).forEach(entity -> {
                //    if(!entity.isSubmergedInWater()) {
                //        entity.applyStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 20, 1));
                //    }
                //    else {
                //        entity.damage(DamageSource.IN_FIRE, 1F);
                //    }
                //    Vec3d velocity = entity.getVelocity();
                //    entity.setVelocity(velocity.x, Math.max(-0.3, velocity.y - 0.0005), velocity.z);
                //});
            }
        }
    }
}
