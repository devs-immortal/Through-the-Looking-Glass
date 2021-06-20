package azzy.fabric.lookingglass;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.item.LookingGlassItems;
import azzy.fabric.lookingglass.recipe.*;
import azzy.fabric.lookingglass.recipe.rei.*;
import me.shedaniel.rei.api.client.plugins.REIClientPlugin;
import me.shedaniel.rei.api.client.registry.category.CategoryRegistry;
import me.shedaniel.rei.api.client.registry.display.DisplayRegistry;
import me.shedaniel.rei.api.common.category.CategoryIdentifier;
import me.shedaniel.rei.api.common.util.EntryStacks;
import me.shedaniel.rei.plugin.client.DefaultClientPlugin;
import net.minecraft.recipe.Recipe;

import java.util.function.Predicate;

public class LookingGlassREIIntegration implements REIClientPlugin {
    @Override
    public void registerDisplays(DisplayRegistry registry) {
        registry.registerFiller(LookingGlassRecipe.class, LookingGlassREIisplay::new);
        //recipeHelper.registerRecipes(LookingGlassRecipes.ALLOYING_RECIPE.getId(), generateRecipeConditional(AlloyingRecipe.class), LookingGlassRecipeDisplay::new);
        //recipeHelper.registerRecipes(LookingGlassRecipes.INDUCTION_RECIPE.getId(), generateRecipeConditional(InductionRecipe.class), LookingGlassRecipeDisplay::new);
        //recipeHelper.registerRecipes(LookingGlassRecipes.FREEZING_RECIPE.getId(), generateRecipeConditional(FreezingRecipe.class), LookingGlassRecipeDisplay::new);
        //recipeHelper.registerRecipes(LookingGlassRecipes.GRINDING_RECIPE.getId(), generateRecipeConditional(GrindingRecipe.class), LookingGlassRecipeDisplay::new);
        //recipeHelper.registerRecipes(LookingGlassRecipes.MIXING_RECIPE.getId(), generateRecipeConditional(MixingRecipe.class), LookingGlassRecipeDisplay::new);
    }


    @Override
    public void registerCategories(CategoryRegistry registry) {
        registry.add(new AlloyingRecipeCategory());
        registry.add(new InductionRecipeCategory());
        registry.add(new FreezingRecipeCategory());
        registry.add(new GrindingRecipeCategory());
        registry.add(new MixingRecipeCategory());

        registry.addWorkstations(CategoryIdentifier.of(LookingGlassRecipes.ALLOYING_RECIPE.getId()), EntryStacks.of(LookingGlassBlocks.ALLOY_FURNACE_BLOCK));
        registry.addWorkstations(CategoryIdentifier.of(DefaultClientPlugin.SMELTING.getIdentifier()), EntryStacks.of(LookingGlassBlocks.POWERED_FURNACE_BLOCK));

        registry.addWorkstations(CategoryIdentifier.of(LookingGlassRecipes.INDUCTION_RECIPE.getId()), EntryStacks.of(LookingGlassBlocks.BLOCK_INDUCTOR_BLOCK));
        registry.addWorkstations(CategoryIdentifier.of(LookingGlassRecipes.GRINDING_RECIPE.getId()), EntryStacks.of(LookingGlassBlocks.GRINDER_BLOCK));
        registry.addWorkstations(CategoryIdentifier.of(LookingGlassRecipes.MIXING_RECIPE.getId()), EntryStacks.of(LookingGlassBlocks.MIXER_BLOCK));

        registry.addWorkstations(CategoryIdentifier.of(DefaultClientPlugin.BLASTING.getIdentifier()), EntryStacks.of(LookingGlassItems.BLAST_UPGRADE_ITEM));
        registry.addWorkstations(CategoryIdentifier.of(LookingGlassRecipes.FREEZING_RECIPE.getId()), EntryStacks.of(LookingGlassItems.FREEZER_UPGRADE_ITEM));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static <T extends LookingGlassRecipe<?>> Predicate<Recipe> generateRecipeConditional(Class<T> clazz) {
        return recipe -> clazz.isAssignableFrom(recipe.getClass()) && !((T) recipe).isEmpty();
    }
}
