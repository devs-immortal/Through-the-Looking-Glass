package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.FishBreederEntity;
import azzy.fabric.lookingglass.item.LookingGlassItems;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.TurtleEggBlock;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.entity.passive.SalmonEntity;
import net.minecraft.entity.passive.TropicalFishEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class FishBreederBlock extends LookingGlassBlock implements BlockEntityProvider {

    public FishBreederBlock(Settings settings) {
        super(settings, true);
    }

    @Override
    protected void pulseUpdate(BlockState state, World world, BlockPos pos, boolean on) {
        super.pulseUpdate(state, world, pos, on);
    }

    @Override
    public boolean hasRandomTicks(BlockState state) {
        return state.get(WATERLOGGED);
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        BlockEntity entity = world.getBlockEntity(pos);
        if(!world.isClient() && entity != null) {
            ItemStack stack = ((FishBreederEntity) entity).getStack(0);
            if(!stack.isEmpty()) {
                stack.decrement(1);
                Box breedZone = new Box(pos.add(-6, -6, -6), pos.add(6, 0, 6));
                List<FishEntity> fishes = world.getEntitiesByClass(FishEntity.class, breedZone, ignored -> true);
                Iterator<BlockPos> iterator = BlockPos.iterate(pos.add(-6, -6, -6), pos.add(6, 0, 6)).iterator();
                int egg = 0;
                while(iterator.hasNext()) {
                    if(world.getBlockState(iterator.next()).isOf(LookingGlassBlocks.SALMON_EGGS))
                        egg++;
                }
                int salmon = 0, cod = 0, tropical = 0;
                if(fishes.size() <= 20 && fishes.size() > 1) {
                    for (FishEntity fishEntity : fishes) {
                        if(fishEntity instanceof SalmonEntity) {
                            salmon++;
                            if(salmon >= 2 && random.nextInt(20) < salmon - egg) {
                                BlockPos spawnPos = pickPos(pos, random);
                                boolean valid = false;
                                while (world.isWater(spawnPos)) {
                                    if(SalmonEggBlock.isValidGround(world, spawnPos.down())) {
                                        valid = true;
                                        break;
                                    }
                                    spawnPos = spawnPos.down();
                                }
                                if(valid) {
                                    world.setBlockState(spawnPos, LookingGlassBlocks.SALMON_EGGS.getDefaultState().with(TurtleEggBlock.EGGS, random.nextInt(4) + 1).with(WATERLOGGED, true));
                                }
                            }
                        }
                        else if(fishEntity instanceof CodEntity) {
                            cod++;
                            if(cod >= 2 && random.nextInt(16) < cod) {
                                BlockPos spawnPos = pickPos(pos, random);
                                if(world.isWater(spawnPos)) {
                                    world.spawnParticles(ParticleTypes.HEART, fishEntity.getX(), fishEntity.getY(), fishEntity.getZ(), 3 + random.nextInt(4), 0, 0, 0, 0.05);
                                    CodEntity codEntity = EntityType.COD.create(world);
                                    codEntity.setPosition(spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5);
                                    codEntity.setFromBucket(true);
                                    world.spawnEntity(codEntity);
                                }
                            }
                        }
                        else if(fishEntity instanceof TropicalFishEntity) {
                            tropical++;
                            if(tropical >= 2 && random.nextInt(24) < tropical) {
                                BlockPos spawnPos = pickPos(pos, random);
                                if(world.isWater(spawnPos)) {
                                    world.spawnParticles(ParticleTypes.HEART, fishEntity.getX(), fishEntity.getY(), fishEntity.getZ(), 3 + random.nextInt(4), 0, 0, 0, 0.05);
                                    TropicalFishEntity tropEntity = EntityType.TROPICAL_FISH.create(world);
                                    tropEntity.setPosition(spawnPos.getX() + 0.5, spawnPos.getY() + 0.5, spawnPos.getZ() + 0.5);
                                    tropEntity.setFromBucket(true);
                                    world.spawnEntity(tropEntity);
                                }
                            }
                        }
                    }
                }
                world.spawnParticles(new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(LookingGlassItems.FISH_FEED)), pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 40, 0.2, 0, 0.2, 0.1);
                world.playSound(null, pos, SoundEvents.ENTITY_FOX_EAT, SoundCategory.BLOCKS, 1F, 2F);
            }
        }
    }

    private BlockPos pickPos(BlockPos pos, Random random) {
        return new BlockPos(pos.getX() + random.nextInt(11) - 5, pos.getY() - random.nextInt(6), pos.getZ() + random.nextInt(11) - 5);
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new FishBreederEntity(pos, state);
    }
}
