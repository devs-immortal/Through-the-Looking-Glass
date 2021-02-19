package azzy.fabric.lookingglass.block;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalFacingBlock;
import net.minecraft.block.ShapeContext;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.particle.EndRodParticle;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleFactory;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.particle.DefaultParticleType;
import net.minecraft.particle.ParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.util.Random;

public class NebulousSaltBlock extends Block{

    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 3, 16);


    public NebulousSaltBlock(Settings settings) {
        super(settings);
    }

    @Override
    public void randomDisplayTick(BlockState state, World world, BlockPos pos, Random random) {
        int tries = random.nextInt(5);
        for(int i = 0; i <= tries; i++) {
            BlockPos endPos = pos.add((random.nextDouble() * 17) - 8, (random.nextDouble() * 33) - 16, (random.nextDouble() * 17) - 8);
            int chance = Math.abs(endPos.getManhattanDistance(pos)) / 3;
            if(random.nextInt(chance + 3) == 0) {
                world.addParticle(ParticleTypes.END_ROD, true, endPos.getX(), endPos.getY(), endPos.getZ(), 0, 0, 0);
            }
        }
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if(!world.isClient()) {
            if(!(entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative())) {
                Random random = world.getRandom();
                if(random.nextInt(101) == 0) {
                    if(random.nextBoolean()) {
                        ((LivingEntity) entity).applyStatusEffect(new StatusEffectInstance(StatusEffects.LEVITATION, 10 + random.nextInt(20), 39, false, false, false));
                    }
                    else
                        entity.requestTeleport(entity.getX() + (random.nextInt(33) - 16), entity.getY() + random.nextInt(9),entity.getZ() + (random.nextInt(33) - 16));
                    world.playSoundFromEntity(null, entity, SoundEvents.ENTITY_ENDER_EYE_DEATH, SoundCategory.PLAYERS, 2.0F, 1.5F);
                    ((ServerWorld) world).spawnParticles(ParticleTypes.END_ROD, entity.getX() + (entity.getWidth() / 2.0), entity.getY() + (entity.getHeight() / 2.0), entity.getZ() + (entity.getWidth() / 2.0), 10 + world.getRandom().nextInt(10), 0, 0, 0, 0.08);
                }
            }
        }
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        return !world.isAir(pos.down());
    }
}
