package azzy.fabric.lookingglass.blockentity;

import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SmeltingRecipe;

import java.util.Optional;

public class PoweredFurnaceBlock extends LookingGlassMachine {

    private SmeltingRecipe trackedRecipe;
    private int progress;

    public PoweredFurnaceBlock(BlockEntityType<?> type) {
        super(type, MachineTier.BASIC, 2, 16000);
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
                }
            }
            markDirty();
        }
    }

    private void tickRecipeProgression() {
        progress++;
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
    }
}
