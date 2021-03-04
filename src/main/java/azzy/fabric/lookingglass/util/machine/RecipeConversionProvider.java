package azzy.fabric.lookingglass.util.machine;

import azzy.fabric.lookingglass.recipe.LookingGlassRecipe;
import net.minecraft.recipe.RecipeType;

public interface RecipeConversionProvider {
    RecipeType<?> getRecipeType();
}
