package azzy.fabric.lookingglass.recipe;

import azzy.fabric.incubus_core.recipe.IngredientStack;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;

import java.util.Collections;
import java.util.List;

public interface LookingGlassRecipe<C extends Inventory> extends Recipe<C> {

    boolean isEmpty();

    @Override
    LookingGlassRecipeType<?> getType();

    List<IngredientStack> getInputs();

    @Override
    default String getGroup() {
        return "lookingglass:undefined";
    }

    default List<ItemStack> getOutputs() {
        return Collections.singletonList(getOutput());
    }
}
