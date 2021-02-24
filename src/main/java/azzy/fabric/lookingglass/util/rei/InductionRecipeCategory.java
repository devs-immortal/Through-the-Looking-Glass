package azzy.fabric.lookingglass.util.rei;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.recipe.AlloyingRecipe;
import azzy.fabric.lookingglass.recipe.InductionRecipe;
import azzy.fabric.lookingglass.recipe.LookingGlassRecipes;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.widgets.Widgets;
import me.shedaniel.rei.gui.widget.Widget;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InductionRecipeCategory extends AbstractLookingGlassRecipeCategory<InductionRecipe> {

    public InductionRecipeCategory() {
        super(LookingGlassRecipes.INDUCTION_RECIPE, EntryStack.create(LookingGlassBlocks.BLOCK_INDUCTOR_BLOCK));
    }

    @Override
    public @NotNull List<Widget> setupDisplay(LookingGlassRecipeDisplay<InductionRecipe> recipeDisplay, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        Point start = new Point(bounds.getCenterX() - 41, bounds.y + 10);
        widgets.add(Widgets.createCategoryBase(bounds));
        widgets.add(Widgets.createSlot(new Point(start.x - 17, start.y  - 1)).entries(getInput(recipeDisplay, 0)).markInput());
        widgets.add(Widgets.createSlot(new Point(start.x + 1, start.y - 1)).entries(getInput(recipeDisplay, 1)).markInput());
        widgets.add(Widgets.createSlot(new Point(start.x - 17, start.y + 18)).entries(getInput(recipeDisplay, 2)).markInput());
        widgets.add(Widgets.createSlot(new Point(start.x + 1, start.y + 18)).entries(getInput(recipeDisplay, 3)).markInput());
        widgets.add(Widgets.createSlot(new Point(start.x + 32, start.y + 9)).entries(Collections.singleton(EntryStack.create(LookingGlassBlocks.BLOCK_INDUCTOR_BLOCK))).disableBackground());
        widgets.add(Widgets.createResultSlotBackground(new Point(start.x + 67, start.y + 9)));
        widgets.add(Widgets.createSlot(new Point(start.x + 67, start.y + 9)).entries(getOutput(recipeDisplay, 0)).disableBackground().markOutput());

        //widgets.add(ReiPlugin.createProgressBar(bounds.x + 46 + 21, bounds.y + 30, recipeDisplay.getTime() * 50, GuiBuilder.ProgressDirection.RIGHT));
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 54;
    }
}
