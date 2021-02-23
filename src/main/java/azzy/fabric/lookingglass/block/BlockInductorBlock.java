package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.mixin.RecipeMapAccessor;
import azzy.fabric.lookingglass.recipe.InductionRecipe;
import azzy.fabric.lookingglass.recipe.LookingGlassRecipes;
import azzy.fabric.lookingglass.util.BlockEntityMover;
import azzy.fabric.lookingglass.util.FalseInventory;
import azzy.fabric.lookingglass.util.LookingGlassSounds;
import dev.technici4n.fasttransferlib.api.energy.EnergyIo;
import jdk.nashorn.internal.ir.annotations.Ignore;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.recipe.Recipe;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.border.WorldBorder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class BlockInductorBlock extends AbstractInductorBlock implements FalseInventory {

    public final List<Block> tempBlockHolder = new ArrayList<>();

    public BlockInductorBlock(Settings settings) {
        super(settings);
        this.setDefaultState(getStateManager().getDefaultState().with(POWERED, false));
    }

    @Override
    @SuppressWarnings("unchecked")
    protected void internalInduct(World world, BlockState inductor, Direction facing, BlockPos pos) {
        WorldBorder border = world.getWorldBorder();
        int max = 15;
        for (int i = 1; i <= 15; i++) {
            BlockPos probePos = pos.offset(facing, i);
            BlockState state = world.getBlockState(probePos);
            if(!world.isChunkLoaded(probePos)) {
                max = i - 1;
                break;
            }
            if(state.getBlock() instanceof AbstractTesseractBlock) {
                ((AbstractTesseractBlock) state.getBlock()).activate(world, probePos, facing, pos);
                return;
            }
            if(state.getHardness(world, probePos) < 0.0F || world.getBlockState(probePos.offset(facing)).isOf(TTLGBlocks.INTERMINAL_CORE) || state.isOf(TTLGBlocks.INTERMINAL_CORE) || !border.contains(probePos)) {
                max = i - 1;
                break;
            }
        }

        for(int check = max; check >= 1; check--) {
            BlockPos probePos = pos.offset(facing, check);
            if(world.isChunkLoaded(probePos.getX() >> 4, probePos.getZ() >> 4) && !world.isAir(probePos)) {
                tempBlockHolder.add(world.getBlockState(probePos).getBlock());
            }
        }
        Optional<InductionRecipe> recipe  =
                ((Map<Identifier, InductionRecipe>) (Object)
                ((RecipeMapAccessor) world.getRecipeManager())
                .getRecipes()
                .get(LookingGlassRecipes.INDUCTION_RECIPE))
                .values()
                .stream()
                .filter(candidate -> candidate.matches(this, world))
                .findFirst();


        if(recipe.isPresent()) {
            boolean spawned = false;
            for(; max >= 1; max--) {
                BlockPos probePos = pos.offset(facing, max);
                if(tempBlockHolder.contains(world.getBlockState(probePos).getBlock())) {
                    world.breakBlock(probePos, false);
                    if(!world.isClient()) {
                        ((ServerWorld) world).spawnParticles(ParticleTypes.END_ROD, probePos.getX() + 0.5, probePos.getY() + 0.5, probePos.getZ() + 0.5, 15 + world.getRandom().nextInt(10), 0, 0.05, 0, 0.045);
                        world.playSound(null, probePos, LookingGlassSounds.FINIS_BREAK, SoundCategory.BLOCKS, 0.4F, 2.0F);
                    }
                    if(!spawned) {
                        dropStack(world, probePos, recipe.get().getOutput());
                        spawned = true;
                    }
                }
            }
            tempBlockHolder.clear();
            return;
        }

        tempBlockHolder.clear();

        for(; max >= 1; max--) {
            BlockPos probePos = pos.offset(facing, max);
            BlockPos offPos = probePos.offset(facing);
            if(world.isChunkLoaded(probePos.getX() >> 4, probePos.getZ() >> 4) && world.isAir(offPos) && World.isInBuildLimit(offPos) && border.contains(offPos)) {
                BlockState state = world.getBlockState(probePos);
                BlockEntity entity = world.getBlockEntity(probePos);
                if(!(state.getHardness(world, probePos) < 0.0F || state.isAir() || state.getFluidState() != Fluids.EMPTY.getDefaultState())) {
                    if(entity == null) {
                        world.removeBlock(probePos, true);
                        world.setBlockState(offPos, state);
                    }
                    else if(state.getBlock() instanceof AbstractTesseractBlock){
                    }
                    else {
                        BlockEntityMover.tryMoveEntity(world, probePos, facing);
                    }
                }
            }
        }
        world.playSound(null, pos, SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT, SoundCategory.BLOCKS, 0.2F, 1.15F + (world.getRandom().nextFloat() / 10F));
    }
}
