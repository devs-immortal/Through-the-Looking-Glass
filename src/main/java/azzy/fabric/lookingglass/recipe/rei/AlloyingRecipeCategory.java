package azzy.fabric.lookingglass.recipe.rei;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.recipe.AlloyingRecipe;
import azzy.fabric.lookingglass.recipe.LookingGlassRecipes;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.util.EntryStacks;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AlloyingRecipeCategory extends AbstractLookingGlassRecipeCategory<AlloyingRecipe> {

    public AlloyingRecipeCategory() {
        super(LookingGlassRecipes.ALLOYING_RECIPE, EntryStacks.of(LookingGlassBlocks.ALLOY_FURNACE_BLOCK));
    }

    @Override
    public @NotNull List<Widget> setupDisplay(LookingGlassREIisplay<AlloyingRecipe> recipeDisplay, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        Point start = new Point(bounds.getCenterX() - 41, bounds.y + 10);
        widgets.add(Widgets.createCategoryBase(bounds));
        widgets.add(Widgets.createResultSlotBackground(new Point(start.x + 61, start.y + 9)));
        widgets.add(Widgets.createSlot(new Point(start.x + 1, start.y  - 1)).entries(getInput(recipeDisplay, 0)).markInput());
        widgets.add(Widgets.createSlot(new Point(start.x + 1, start.y + 18)).entries(getInput(recipeDisplay, 1)).markInput());
        widgets.add(Widgets.createArrow(new Point(start.x + 24, start.y + 8)).animationDurationTicks(200));
        widgets.add(Widgets.createSlot(new Point(start.x + 61, start.y + 9)).entries(getOutput(recipeDisplay, 0)).disableBackground().markOutput());

        //widgets.add(ReiPlugin.createProgressBar(bounds.x + 46 + 21, bounds.y + 30, recipeDisplay.getTime() * 50, GuiBuilder.ProgressDirection.RIGHT));
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 51;
    }
}
