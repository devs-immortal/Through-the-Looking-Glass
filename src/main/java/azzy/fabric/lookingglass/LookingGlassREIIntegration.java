package azzy.fabric.lookingglass;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.item.LookingGlassItems;
import azzy.fabric.lookingglass.recipe.*;
import azzy.fabric.lookingglass.util.rei.*;
import me.shedaniel.rei.api.EntryStack;
import me.shedaniel.rei.api.RecipeHelper;
import me.shedaniel.rei.api.plugins.REIPluginV0;
import me.shedaniel.rei.plugin.DefaultPlugin;
import net.minecraft.recipe.Recipe;
import net.minecraft.util.Identifier;

import java.util.function.Function;
import java.util.function.Predicate;

public class LookingGlassREIIntegration implements REIPluginV0 {

    @Override
    public void registerRecipeDisplays(RecipeHelper recipeHelper) {
        recipeHelper.registerRecipes(LookingGlassRecipes.ALLOYING_RECIPE.getId(), generateRecipeConditional(AlloyingRecipe.class), LookingGlassRecipeDisplay::new);
        recipeHelper.registerRecipes(LookingGlassRecipes.INDUCTION_RECIPE.getId(), generateRecipeConditional(InductionRecipe.class), LookingGlassRecipeDisplay::new);
        recipeHelper.registerRecipes(LookingGlassRecipes.FREEZING_RECIPE.getId(), generateRecipeConditional(FreezingRecipe.class), LookingGlassRecipeDisplay::new);
        recipeHelper.registerRecipes(LookingGlassRecipes.GRINDING_RECIPE.getId(), generateRecipeConditional(GrindingRecipe.class), LookingGlassRecipeDisplay::new);
        recipeHelper.registerRecipes(LookingGlassRecipes.MIXING_RECIPE.getId(), generateRecipeConditional(MixingRecipe.class), LookingGlassRecipeDisplay::new);
    }

    @Override
    public void registerPluginCategories(RecipeHelper recipeHelper) {
        recipeHelper.registerCategory(new AlloyingRecipeCategory());
        recipeHelper.registerCategory(new InductionRecipeCategory());
        recipeHelper.registerCategory(new FreezingRecipeCategory());
        recipeHelper.registerCategory(new GrindingRecipeCategory());
        recipeHelper.registerCategory(new MixingRecipeCategory());
    }

    @Override
    public void registerOthers(RecipeHelper recipeHelper) {
        recipeHelper.registerWorkingStations(DefaultPlugin.SMELTING, EntryStack.create(LookingGlassBlocks.POWERED_FURNACE_BLOCK));

        recipeHelper.registerWorkingStations(LookingGlassRecipes.ALLOYING_RECIPE.getId(), EntryStack.create(LookingGlassBlocks.ALLOY_FURNACE_BLOCK));
        recipeHelper.registerWorkingStations(LookingGlassRecipes.INDUCTION_RECIPE.getId(), EntryStack.create(LookingGlassBlocks.BLOCK_INDUCTOR_BLOCK));
        recipeHelper.registerWorkingStations(LookingGlassRecipes.GRINDING_RECIPE.getId(), EntryStack.create(LookingGlassBlocks.GRINDER_BLOCK));
        recipeHelper.registerWorkingStations(LookingGlassRecipes.MIXING_RECIPE.getId(), EntryStack.create(LookingGlassBlocks.MIXER_BLOCK));

        recipeHelper.registerWorkingStations(DefaultPlugin.BLASTING, EntryStack.create(LookingGlassItems.BLAST_UPGRADE_ITEM));
        recipeHelper.registerWorkingStations(LookingGlassRecipes.FREEZING_RECIPE.getId(), EntryStack.create(LookingGlassItems.FREEZER_UPGRADE_ITEM));
    }

    @Override
    public Identifier getPluginIdentifier() {
        return new Identifier(LookingGlassCommon.MODID, "lookingglass_rei_integration");
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T extends LookingGlassRecipe<?>> Predicate<Recipe> generateRecipeConditional(Class<T> clazz) {
        return recipe -> clazz.isAssignableFrom(recipe.getClass()) && !((T) recipe).isEmpty();
    }
}
