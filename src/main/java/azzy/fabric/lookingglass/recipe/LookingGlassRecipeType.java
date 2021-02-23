package azzy.fabric.lookingglass.recipe;

import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;

public class LookingGlassRecipeType<T extends Recipe<?>> implements RecipeType<T> {

    private final Identifier id;

    public LookingGlassRecipeType(Identifier id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return id.toString();
    }

    public Identifier getId() {
        return id;
    }
}
