package azzy.fabric.lookingglass.util.rei;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.recipe.LookingGlassRecipes;
import azzy.fabric.lookingglass.recipe.MixingRecipe;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.util.EntryStacks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MixingRecipeCategory extends AbstractLookingGlassRecipeCategory<MixingRecipe> {

    public MixingRecipeCategory() {
        super(LookingGlassRecipes.MIXING_RECIPE, EntryStacks.of(LookingGlassBlocks.MIXER_BLOCK));
    }

    @Override
    public @NotNull List<Widget> setupDisplay(LookingGlassREIisplay<MixingRecipe> recipeDisplay, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        Point start = new Point(bounds.getCenterX() - 36, bounds.y + 10);
        widgets.add(Widgets.createCategoryBase(bounds));
        widgets.add(Widgets.createResultSlotBackground(new Point(start.x + 61, start.y + 9)));
        widgets.add(Widgets.createSlot(new Point(start.x - 17, start.y  - 1)).entries(getInput(recipeDisplay, 0)).markInput());
        widgets.add(Widgets.createSlot(new Point(start.x + 1, start.y - 1)).entries(getInput(recipeDisplay, 1)).markInput());
        widgets.add(Widgets.createSlot(new Point(start.x - 17, start.y  + 18)).entries(getInput(recipeDisplay, 2)).markInput());
        widgets.add(Widgets.createSlot(new Point(start.x + 1, start.y + 18)).entries(getInput(recipeDisplay, 3)).markInput());
        widgets.add(Widgets.createArrow(new Point(start.x + 24, start.y + 8)).animationDurationTicks(400));
        widgets.add(Widgets.createSlot(new Point(start.x + 61, start.y + 9)).entries(getOutput(recipeDisplay, 0)).disableBackground().markOutput());
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 51;
    }
}
