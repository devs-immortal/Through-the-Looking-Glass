package azzy.fabric.lookingglass.util.rei;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.recipe.GrindingRecipe;
import azzy.fabric.lookingglass.recipe.LookingGlassRecipes;
import me.shedaniel.math.Point;
import me.shedaniel.math.Rectangle;
import me.shedaniel.rei.api.client.gui.widgets.Widget;
import me.shedaniel.rei.api.client.gui.widgets.Widgets;
import me.shedaniel.rei.api.common.entry.EntryStack;
import me.shedaniel.rei.api.common.util.EntryStacks;
import net.minecraft.text.LiteralText;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class GrindingRecipeCategory extends AbstractLookingGlassRecipeCategory<GrindingRecipe> {

    public GrindingRecipeCategory() {
        super(LookingGlassRecipes.GRINDING_RECIPE, EntryStacks.of(LookingGlassBlocks.GRINDER_BLOCK));
    }

    @Override
    public @NotNull List<Widget> setupDisplay(LookingGlassREIisplay<GrindingRecipe> recipeDisplay, Rectangle bounds) {
        List<Widget> widgets = new ArrayList<>();
        Point start = new Point(bounds.getCenterX() - 41, bounds.y + 20);
        widgets.add(Widgets.createCategoryBase(bounds));
        widgets.add(Widgets.createSlot(new Point(start.x + 1, start.y + 9)).entries(getInput(recipeDisplay, 0)).markInput());
        widgets.add(Widgets.createArrow(new Point(start.x + 24, start.y + 8)).animationDurationTicks(200));
        widgets.add(Widgets.createSlot(new Point(start.x + 61, start.y  - 1)).entries(getOutput(recipeDisplay, 0)).markOutput());
        widgets.add(Widgets.createSlot(new Point(start.x + 61, start.y + 18)).entries(getOutput(recipeDisplay, 1)).markOutput());
        widgets.add(Widgets.createLabel(new Point(bounds.x + bounds.width - 5, bounds.y + 5),
                new LiteralText("secondary output chance")).noShadow().rightAligned().color(0xFF404040, 0xFFBBBBBB));
        widgets.add(Widgets.createLabel(new Point(bounds.x + bounds.width - 5, bounds.y + 14),
                new LiteralText(String.format("%.1f", (recipeDisplay.getRecipe().getChance() * 100.0)) + "%")).noShadow().rightAligned().color(0xFF404040, 0xFFBBBBBB));
        return widgets;
    }

    @Override
    public int getDisplayHeight() {
        return 64;
    }
}
