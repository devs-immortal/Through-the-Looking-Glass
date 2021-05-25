package azzy.fabric.lookingglass.block;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.biome.Biome;

import java.util.Random;

@SuppressWarnings("deprecation")
public class CornPlantBlock extends FernBlock {

    public static final BooleanProperty BASE = BooleanProperty.of("base");
    public static final BooleanProperty FLOWER = BooleanProperty.of("flower");

    public CornPlantBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(FLOWER, false).with(Properties.AGE_3, 0));
    }

    @Override
    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        growInternal(state, world, pos, random);
    }

    private void growInternal(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if(world.getLightLevel(pos) >= 9) {
            int height = 0;

            while (world.getBlockState(pos.down(height + 1)).isOf(this)) {
                height++;
            }

            BlockState floor = world.getBlockState(pos.down(height + 1));

            Biome biome = world.getBiome(pos);

            float growthChance = 0.01F;

            if(world.isRaining())
                growthChance += 0.02F;

            if(biome.getCategory() == Biome.Category.PLAINS)
                growthChance += 0.04F;

            if(world.isAir(pos.up()) && world.isAir(pos.up(1)))
                growthChance += 0.04F;

            if(floor.isOf(LookingGlassBlocks.LOAM))
                growthChance *= 2;

            if(biome.getPrecipitation() == Biome.Precipitation.SNOW) {
                growthChance /= 4;
                if(!world.isSkyVisibleAllowingSea(pos))
                    growthChance *= 3;
            }

            if(world.getRandom().nextFloat() <= growthChance) {

                int stage = state.get(Properties.AGE_3);

                if(state.get(BASE)) {
                    if(stage == 1) {
                        if (tryExtendUpwards(world, pos.up(), false))
                            world.setBlockState(pos, state.with(Properties.AGE_3, stage + 1));
                    }
                    else if(stage < 2) {
                        world.setBlockState(pos, state.with(Properties.AGE_3, stage + 1));
                    }
                }
                else {

                    boolean flower = state.get(FLOWER);

                    if(stage == (flower ? 0 : 1)) {
                        if(flower) {

                            world.setBlockState(pos, state.with(Properties.AGE_3, stage + 1));

                            for (int i = 1; i <= height; i++) {
                                BlockPos stalkPos = pos.down(i);
                                BlockState stalk = world.getBlockState(stalkPos);
                                if(stalk.isOf(this)) {
                                    world.setBlockState(stalkPos, stalk.with(Properties.AGE_3, 3));
                                }
                            }

                        }
                        else {

                            boolean blossom = random.nextFloat() >= Math.pow(0.25F, height);

                            if (tryExtendUpwards(world, pos.up(), blossom))
                                world.setBlockState(pos, state.with(Properties.AGE_3, stage + 1));

                        }
                    }
                    else if(stage < (flower ? 1 : 2)) {
                        world.setBlockState(pos, state.with(Properties.AGE_3, stage + 1));
                    }
                }
            }
        }
    }

    @Override
    public OffsetType getOffsetType() {
        return OffsetType.XZ;
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);
        if(!world.getBlockTickScheduler().isScheduled(pos.up(), this)) {
            world.getBlockTickScheduler().schedule(pos.up(), this, 1);
        }
    }

    @Override
    public void grow(ServerWorld world, Random random, BlockPos pos, BlockState state) {
        growInternal(state, world, pos, random);
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        if(!world.getBlockState(pos.down()).isOf(this))
            world.breakBlock(pos, true);
        if(world.getBlockState(pos.up()).isOf(this) && !world.getBlockTickScheduler().isScheduled(pos.up(), this)) {
            world.getBlockTickScheduler().schedule(pos.up(), this, 1);
        }
        super.scheduledTick(state, world, pos, random);
    }

    @Override
    public BlockState getStateForNeighborUpdate(BlockState state, Direction direction, BlockState newState, WorldAccess world, BlockPos pos, BlockPos posFrom) {
        if(world.getBlockState(pos.down()).isOf(this))
            return state;
        return !canPlaceAt(state, world, pos) ? Blocks.AIR.getDefaultState() : state;
    }

    private boolean tryExtendUpwards(World world, BlockPos pos, boolean flower) {
        BlockState replaceable = world.getBlockState(pos);
        if(replaceable.getMaterial().isReplaceable() && world.getFluidState(pos).isEmpty() && !replaceable.isOf(this)) {
            world.setBlockState(pos, getDefaultState().with(BASE, false).with(FLOWER, flower));
            return true;
        }
        return false;
    }

    @Override
    public boolean canPlaceAt(BlockState state, WorldView world, BlockPos pos) {
        if(!world.isClient()) {
            BlockState floor = world.getBlockState(pos.down());
            return floor.isOf(LookingGlassBlocks.LOAM) || floor.isOf(Blocks.DIRT) || floor.isOf(Blocks.GRASS_BLOCK) || floor.isOf(Blocks.PODZOL) || ((ServerWorld) world).getTagManager().getBlocks().getTagOrEmpty(new Identifier("c", "dirt")).contains(floor.getBlock());
        }
        return false;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return Block.createCuboidShape(2.0D, 0.0D, 2.0D, 14.0D, 16.0D, 14.0D);
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(Properties.AGE_3, BASE, FLOWER);
    }
}
