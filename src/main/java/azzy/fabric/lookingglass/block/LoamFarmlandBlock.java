package azzy.fabric.lookingglass.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.CropBlock;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.Random;

public class LoamFarmlandBlock extends FarmlandBlock {

    public LoamFarmlandBlock(Settings settings) {
        super(settings);
        setDefaultState(getDefaultState().with(MOISTURE, 7));
    }

    public void randomTick(BlockState state, ServerWorld world, BlockPos pos, Random random) {
        Block block = world.getBlockState(pos.up()).getBlock();
        if(block instanceof CropBlock)
            ((CropBlock) block).grow(world, random, pos.up(), state);

    }

    public void onLandedUpon(World world, BlockState state, BlockPos pos, Entity entity, float distance) {
        if (!world.isClient && world.random.nextFloat() < distance - 0.5F && entity instanceof LivingEntity && (entity instanceof PlayerEntity || world.getGameRules().getBoolean(GameRules.DO_MOB_GRIEFING)) && entity.getWidth() * entity.getWidth() * entity.getHeight() > 0.512F) {
            world.setBlockState(pos, pushEntitiesUpBeforeBlockChange(world.getBlockState(pos), LookingGlassBlocks.LOAM.getDefaultState(), world, pos));
        }

        super.onLandedUpon(world, state, pos, entity, distance);
    }
}
