package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.recipe.EnchantingRecipe;
import azzy.fabric.lookingglass.recipe.LookingGlassRecipes;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

public class EnchanterEntity extends LookingGlassMachine implements LookingGlassTickable {

    private final HashMap<Direction, SuffuserEntity> suffusers = new HashMap<>();
    private int totalEnchantingPower;
    private EnchantingRecipe trackedRecipe;
    private boolean active, powered;

    public EnchanterEntity(BlockPos pos, BlockState state) {
        super(LookingGlassBlocks.ENCHANTER_ENTITY, pos, state, MachineTier.ADVANCED, 2, 1000);
    }

    @Override
    public void tick() {
        if(world != null) {
             powered = world.isReceivingRedstonePower(pos);

             if(powered && !active) {
                 notifyRedstoneActivation();
             }

             if(active) {
                 if(!trackedRecipe.matches(this, world)) {
                     active = false;
                     trackedRecipe = null;
                 }

                 if(world.getTime() % 50 == 0) {
                     world.playSound(null, pos, SoundEvents.BLOCK_BEACON_AMBIENT, SoundCategory.BLOCKS, 5.75F, 1.5F);
                 }

                 if(world.isClient()) {
                     clearSuffusers();
                 }

                 trackedRecipe.craft(this);
                 active = false;
                 trackedRecipe = null;
             }
        }
    }

    //  Attempt to initiate crafting upon receiving a redstone pulse.
    public void notifyRedstoneActivation() {
        if(world != null) {
            for (Direction direction : Direction.values()) {
                if(direction.getHorizontal() >= 0) {

                    //  Check if a suffuser is already present in a certain direction, otherwise seek one out.
                    SuffuserEntity suffuser = suffusers.computeIfAbsent(direction, dir -> {
                        for (int distance = 5; distance <= 17; distance++) {
                            BlockEntity entity = world.getBlockEntity(pos.offset(dir, distance));
                            if(entity instanceof SuffuserEntity) {
                                //Update the suffuser's range
                                ((SuffuserEntity) entity).setRange(distance);
                                return (SuffuserEntity) entity;
                            }
                        }
                        return null;
                    });

                    //An enchanter requires all four suffusers be present to function.
                    if(suffuser == null) {
                        return;
                    }

                    totalEnchantingPower += suffuser.getEnchantingPower();
                }
            }

            Optional<EnchantingRecipe> recipeOptional = world.getRecipeManager().getFirstMatch(LookingGlassRecipes.ENCHANTING_RECIPE, this, world);

            if(recipeOptional.isPresent()) {
                world.playSound(null, pos, SoundEvents.BLOCK_ENCHANTMENT_TABLE_USE, SoundCategory.BLOCKS, 2F, 0.5F);
                trackedRecipe = recipeOptional.get();
                active = true;
            }
        }
    }

    public void clearSuffusers() {
        suffusers.values().forEach(suffuserEntity -> suffuserEntity.setStack(0, ItemStack.EMPTY));
    }

    @Override
    public ItemStack getStack(int slot) {
        if (slot >= 2) {
            return suffusers.get(Direction.byId(slot)).getStack(0);
        }
        return super.getStack(slot);
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        if (slot >= 2) {
            suffusers.get(Direction.byId(slot)).setStack(slot, stack);
            return;
        }
        super.setStack(slot, stack);
    }

    public void spawnOutput(ItemStack stack) {
        ItemScatterer.spawn(world, pos.getX(), pos.getY() + 2, pos.getZ(), stack);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return null;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        return super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
    }
}
