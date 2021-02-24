package azzy.fabric.lookingglass.util.rei;

import azzy.fabric.lookingglass.blockentity.LookingGlassBE;
import azzy.fabric.lookingglass.recipe.LookingGlassRecipe;
import azzy.fabric.lookingglass.recipe.LookingGlassRecipeType;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeCategory;
import me.shedaniel.rei.gui.entries.RecipeEntry;
import me.shedaniel.rei.gui.entries.SimpleRecipeEntry;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public abstract class AbstractLookingGlassRecipeCategory<R extends LookingGlassRecipe<?>> implements RecipeCategory<LookingGlassRecipeDisplay<R>> {

    private final LookingGlassRecipeType<R> recipeType;
    private final EntryStack logo;

    public AbstractLookingGlassRecipeCategory(LookingGlassRecipeType<R> recipeType, EntryStack logo) {
        this.recipeType = recipeType;
        this.logo = logo;
    }

    @Override
    public @NotNull Identifier getIdentifier() {
        return recipeType.getId();
    }

    @Override
    public @NotNull String getCategoryName() {
        return I18n.translate(recipeType.getId().toString());
    }

    @Override
    public @NotNull RecipeEntry getSimpleRenderer(LookingGlassRecipeDisplay<R> recipe) {
        return SimpleRecipeEntry.create(Collections.singletonList(recipe.getInputEntries().get(0)), recipe.getOutputEntries());
    }

    @NotNull
    @Override
    public EntryStack getLogo() {
        return logo;
    }

    public List<EntryStack> getInput(LookingGlassRecipeDisplay<R> recipeDisplay, int index) {
        List<List<EntryStack>> inputs = recipeDisplay.getInputEntries();
        return inputs.size() > index ? inputs.get(index) : Collections.emptyList();
    }

    public List<EntryStack> getOutput(LookingGlassRecipeDisplay<R> recipeDisplay, int index) {
        List<List<EntryStack>> outputs = recipeDisplay.getResultingEntries();
        return outputs.size() > index ? outputs.get(index) : Collections.emptyList();
    }
}
