package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.gui.PoweredFurnaceGuiDescription;
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static net.minecraft.state.property.Properties.LIT;

@SuppressWarnings("unchecked")
public class PoweredFurnaceEntity extends LookingGlassUpgradeableMachine implements PropertyDelegateHolder {

    private Recipe trackedRecipe;
    private int progress;

    public PoweredFurnaceEntity() {
        super(LookingGlassBlocks.POWERED_FURNACE_ENTITY, RecipeType.SMELTING, MachineTier.BASIC, 2, 100, 1000, 2);
    }

    @Override
    public void tick() {
        if(!world.isClient()) {
            if(trackedRecipe == null) {
                Optional<Recipe> smeltable = (Optional<Recipe>) world.getRecipeManager().getFirstMatch(getRecipeType(), this, world);
                smeltable.ifPresent(smeltingRecipe -> trackedRecipe = smeltingRecipe);
                tickRecipeProgression();
            }
            else {
                if(trackedRecipe.matches(this, world) && trackedRecipe.getType() == getRecipeType()) {
                    tickRecipeProgression();
                    if(!getCachedState().get(LIT))
                        world.setBlockState(pos, getCachedState().with(LIT, true));
                }
                else {
                    trackedRecipe = null;
                    progress = 0;
                    if(getCachedState().get(LIT))
                        world.setBlockState(pos, getCachedState().with(LIT, false));
                }
            }
            attemptPowerDraw();
            markDirty();
            sync();
        }
    }

    private void tickRecipeProgression() {
        if(trackedRecipe != null) {
            if(progress >= getProcessTime()) {
                ItemStack outSlot = inventory.get(1);
                if(outSlot.isEmpty()) {
                    inventory.set(1, trackedRecipe.craft(this));
                    inventory.get(0).decrement(1);
                    progress = 0;
                    sync();
                }
                ItemStack output = trackedRecipe.getOutput();
                if(outSlot.getCount() + output.getCount() <= outSlot.getMaxCount() && output.isItemEqual(outSlot)) {
                    inventory.get(1).increment(output.getCount());
                    inventory.get(0).decrement(1);
                    progress = 0;
                    sync();
                }
            }
            else {
                double drain = getPowerUsage();
                if(power - drain > 0) {
                    power -= drain;
                    progress++;
                }
                else if(progress > 0) {
                    progress--;
                }
            }
        }
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new PoweredFurnaceGuiDescription(syncId, inv, ScreenHandlerContext.create(world, pos));
    }

    @Override
    public PropertyDelegate getPropertyDelegate() {
        return delegate;
    }

    private final PropertyDelegate delegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0: return progress;
                case 1: return getProcessTime();
                case 2: return (int) Math.round(power);
                case 3: return (int) getEnergyCapacity();
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int size() {
            return 4;
        }
    };

}
