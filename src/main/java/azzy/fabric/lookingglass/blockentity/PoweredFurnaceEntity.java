package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.TTLGBlocks;
import azzy.fabric.lookingglass.gui.PoweredFurnaceGuiDescription;
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class PoweredFurnaceEntity extends LookingGlassMachine implements PropertyDelegateHolder {

    private SmeltingRecipe trackedRecipe;
    private int progress;

    public PoweredFurnaceEntity() {
        super(TTLGBlocks.POWERED_FURNACE_ENTITY, MachineTier.BASIC, 6, 16000);
    }

    @Override
    public void tick() {
        if(!world.isClient()) {
            if(trackedRecipe == null) {
                Optional<SmeltingRecipe> smeltable = world.getRecipeManager().getFirstMatch(RecipeType.SMELTING, this, world);
                smeltable.ifPresent(smeltingRecipe -> trackedRecipe = smeltingRecipe);
                tickRecipeProgression();
            }
            else {
                if(trackedRecipe.matches(this, world)) {
                    tickRecipeProgression();
                }
                else {
                    trackedRecipe = null;
                    progress = 0;
                }
            }
            if(world.getTime() % 10 == 0) {
                attemptPowerDraw();
            }
            markDirty();
            sync();
        }
    }

    private void tickRecipeProgression() {
        if(trackedRecipe != null) {
            if(progress >= 100) {
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
                if(power - 2 > 0) {
                    power -= 2;
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
                case 1: return 100;
                case 2: return (int) Math.round(power);
                case 3: return (int) baseMaxPower;
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
