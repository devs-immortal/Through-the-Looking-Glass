package azzy.fabric.lookingglass.util.rei;

import azzy.fabric.lookingglass.blockentity.LookingGlassBE;
import azzy.fabric.lookingglass.recipe.LookingGlassRecipe;
import azzy.fabric.lookingglass.recipe.LookingGlassRecipes;
import com.google.common.collect.ImmutableList;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeDisplay;
import me.shedaniel.rei.utils.CollectionUtils;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class LookingGlassRecipeDisplay<R extends LookingGlassRecipe<? extends LookingGlassBE>> implements RecipeDisplay {

    private final R recipe;
    private final List<List<EntryStack>> inputs;
    private final List<List<EntryStack>> outputs;

    public LookingGlassRecipeDisplay(R recipe) {
        this.recipe = recipe;
        this.inputs = CollectionUtils.map(recipe.getInputs(), EntryStack::ofIngredient);
        this.outputs = CollectionUtils.map(EntryStack.ofItemStacks(recipe.getOutputs()), Collections::singletonList);
    }


    @Override
    public @NotNull List<List<EntryStack>> getResultingEntries() {
        return outputs;
    }

    @Override
    public @NotNull List<List<EntryStack>> getInputEntries() {
        return inputs;
    }

    @Override
    public @NotNull List<List<EntryStack>> getRequiredEntries() {
        return getInputEntries();
    }

    @Override
    public @NotNull Identifier getRecipeCategory() {
        return recipe.getType().getId();
    }

    @Override
    public @NotNull Optional<Identifier> getRecipeLocation() {
        return Optional.ofNullable(recipe).map(Recipe::getId);
    }
}
