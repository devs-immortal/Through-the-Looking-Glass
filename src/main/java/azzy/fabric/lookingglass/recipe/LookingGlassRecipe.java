package azzy.fabric.lookingglass.recipe;

import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;

import java.util.Collections;
import java.util.List;

public interface LookingGlassRecipe<C extends Inventory> extends Recipe<C> {

    @Override
    LookingGlassRecipeType<?> getType();

    List<Ingredient> getInputs();

    @Override
    default String getGroup() {
        return "lookingglass:undefined";
    }

    default List<ItemStack> getOutputs() {
        return Collections.singletonList(getOutput());
    }
}
