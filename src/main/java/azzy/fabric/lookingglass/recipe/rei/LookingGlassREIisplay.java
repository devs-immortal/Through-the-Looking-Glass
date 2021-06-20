package azzy.fabric.lookingglass.recipe.rei;

import azzy.fabric.lookingglass.recipe.LookingGlassRecipe;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.display.Display;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.util.CollectionUtils;
import me.shedaniel.rei.api.common.util.EntryIngredients;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class LookingGlassREIisplay<R extends LookingGlassRecipe<?>> implements Display {

    private final R recipe;
    private final List<EntryIngredient> inputs;
    private final List<EntryIngredient> outputs;

    public LookingGlassREIisplay(R recipe) {
        this.recipe = recipe;
        this.inputs = CollectionUtils.map(recipe.getInputs(), stack -> EntryIngredients.ofItemStacks(stack.getStacks()));
        this.outputs = recipe.getOutputs().stream().map(EntryIngredients::of).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    public LookingGlassREIisplay(Recipe<?> recipe) {
        this((R) recipe);
    }

    @Override
    public @NotNull List<EntryIngredient> getInputEntries() {
        return inputs;
    }

    @Override
    public List<EntryIngredient> getOutputEntries() {
        return outputs;
    }

    public R getRecipe() {
        return recipe;
    }

    @Override
    public @NotNull List<EntryIngredient> getRequiredEntries() {
        return getInputEntries();
    }

    @Override
    public CategoryIdentifier<?> getCategoryIdentifier() {
        return CategoryIdentifier.of(recipe.getType().getId());
    }

    @Override
    public Optional<Identifier> getDisplayLocation() {
        return Optional.ofNullable(recipe).map(Recipe::getId);
    }
}
