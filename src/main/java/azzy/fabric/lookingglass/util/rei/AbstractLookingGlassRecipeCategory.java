package azzy.fabric.lookingglass.util.rei;

import azzy.fabric.lookingglass.recipe.LookingGlassRecipe;
import azzy.fabric.lookingglass.recipe.LookingGlassRecipeType;
import me.shedaniel.rei.api.client.gui.DisplayRenderer;
import me.shedaniel.rei.api.client.gui.Renderer;
import me.shedaniel.rei.api.client.gui.SimpleDisplayRenderer;
import me.shedaniel.rei.api.client.registry.display.DisplayCategory;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.entry.EntryIngredient;
import me.shedaniel.rei.api.common.entry.EntryStack;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public abstract class AbstractLookingGlassRecipeCategory<R extends LookingGlassRecipe<?>> implements DisplayCategory<LookingGlassREIisplay<R>> {

    private final LookingGlassRecipeType<R> recipeType;
    private final EntryStack<?> logo;

    public AbstractLookingGlassRecipeCategory(LookingGlassRecipeType<R> recipeType, EntryStack<?> logo) {
        this.recipeType = recipeType;
        this.logo = logo;
    }

    @Override
    public @NotNull Identifier getIdentifier() {
        return recipeType.getId();
    }

    @Override
    public Renderer getIcon() {
        return logo;
    }

    @Override
    public CategoryIdentifier<? extends LookingGlassREIisplay<R>> getCategoryIdentifier() {
        return CategoryIdentifier.of(recipeType.getId());
    }

    @Override
    public Text getTitle() {
        return new TranslatableText(recipeType.getId().toString());
    }

    @Override
    public DisplayRenderer getDisplayRenderer(LookingGlassREIisplay<R> display) {
        return SimpleDisplayRenderer.from(Collections.singletonList(display.getInputEntries().get(0)), display.getOutputEntries());
    }


    public EntryIngredient getInput(LookingGlassREIisplay<R> recipeDisplay, int index) {
        List<EntryIngredient> inputs = recipeDisplay.getInputEntries();
        return inputs.size() > index ? inputs.get(index) : EntryIngredient.empty();
    }

    public EntryIngredient getOutput(LookingGlassREIisplay<R> recipeDisplay, int index) {
        List<EntryIngredient> outputs = recipeDisplay.getOutputEntries();
        return outputs.size() > index ? outputs.get(index) : EntryIngredient.empty();
    }
}
