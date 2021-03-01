package azzy.fabric.lookingglass;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.item.LookingGlassItems;
import azzy.fabric.lookingglass.recipe.AlloyingRecipe;
import azzy.fabric.lookingglass.recipe.FreezingRecipe;
import azzy.fabric.lookingglass.recipe.InductionRecipe;
import azzy.fabric.lookingglass.recipe.LookingGlassRecipes;
import azzy.fabric.lookingglass.util.rei.AlloyingRecipeCategory;
import azzy.fabric.lookingglass.util.rei.FreezingRecipeCategory;
import azzy.fabric.lookingglass.util.rei.InductionRecipeCategory;
import azzy.fabric.lookingglass.util.rei.LookingGlassRecipeDisplay;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import me.shedaniel.rei.plugin.DefaultPlugin;
import net.minecraft.util.Identifier;

public class LookingGlassREIIntegration implements REIPluginV0 {

    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        recipeHelper.registerRecipes(LookingGlassRecipes.ALLOYING_RECIPE.getId(), AlloyingRecipe.class, LookingGlassRecipeDisplay::new);
        recipeHelper.registerRecipes(LookingGlassRecipes.INDUCTION_RECIPE.getId(), InductionRecipe.class, LookingGlassRecipeDisplay::new);
        recipeHelper.registerRecipes(LookingGlassRecipes.FREEZING_RECIPE.getId(), FreezingRecipe.class, LookingGlassRecipeDisplay::new);
    }

    @Override
    public void registerPluginCategories(RecipeHelper recipeHelper) {
        recipeHelper.registerCategory(new AlloyingRecipeCategory());
        recipeHelper.registerCategory(new InductionRecipeCategory());
        recipeHelper.registerCategory(new FreezingRecipeCategory());
    }

    @Override
    public void registerOthers(RecipeHelper recipeHelper) {
        recipeHelper.registerWorkingStations(DefaultPlugin.SMELTING, EntryStack.create(LookingGlassBlocks.POWERED_FURNACE_BLOCK));
        recipeHelper.registerWorkingStations(DefaultPlugin.BLASTING, EntryStack.create(LookingGlassItems.BLAST_UPGRADE_ITEM));
        recipeHelper.registerWorkingStations(LookingGlassRecipes.ALLOYING_RECIPE.getId(), EntryStack.create(LookingGlassBlocks.ALLOY_FURNACE_BLOCK));
        recipeHelper.registerWorkingStations(LookingGlassRecipes.INDUCTION_RECIPE.getId(), EntryStack.create(LookingGlassBlocks.BLOCK_INDUCTOR_BLOCK));
        recipeHelper.registerWorkingStations(LookingGlassRecipes.FREEZING_RECIPE.getId(), EntryStack.create(LookingGlassItems.FREEZER_UPGRADE_ITEM));
    }

    @Override
    public Identifier getPluginIdentifier() {
        return new Identifier(LookingGlassCommon.MODID, "lookingglass_rei_integration");
    }
}
