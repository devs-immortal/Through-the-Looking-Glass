package azzy.fabric.lookingglass.util.machine;

import net.minecraft.recipe.RecipeType;

public interface RecipeConversionProvider {
    RecipeType<?> getRecipeType();
}
