package azzy.fabric.lookingglass;

import azzy.fabric.lookingglass.block.TTLGBlocks;
import azzy.fabric.lookingglass.recipe.AlloyingRecipe;
import azzy.fabric.lookingglass.recipe.LookingGlassRecipes;
import azzy.fabric.lookingglass.util.rei.AlloyingRecipeCategory;
import azzy.fabric.lookingglass.util.rei.LookingGlassRecipeDisplay;
import me.shedaniel.rei.api.BaseBoundsHandler;
import me.shedaniel.rei.api.DisplayHelper;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import me.shedaniel.rei.plugin.DefaultPlugin;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.Identifier;

public class LookingGlassREIIntegration implements REIPluginV0 {

    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        recipeHelper.registerRecipes(LookingGlassRecipes.ALLOYING_RECIPE.getId(), AlloyingRecipe.class, LookingGlassRecipeDisplay::new);
    }

    @Override
    public void registerPluginCategories(RecipeHelper recipeHelper) {
        recipeHelper.registerCategory(new AlloyingRecipeCategory());
    }

    @Override
    public void registerOthers(RecipeHelper recipeHelper) {
        recipeHelper.registerWorkingStations(DefaultPlugin.SMELTING, EntryStack.create(TTLGBlocks.POWERED_FURNACE_BLOCK.asItem()));
        recipeHelper.registerWorkingStations(LookingGlassRecipes.ALLOYING_RECIPE.getId(), EntryStack.create(TTLGBlocks.ALLOY_FURNACE_BLOCK));
    }

    @Override
    public Identifier getPluginIdentifier() {
        return new Identifier(LookingGlassCommon.MODID, "lookingglass_rei_integration");
    }
}
