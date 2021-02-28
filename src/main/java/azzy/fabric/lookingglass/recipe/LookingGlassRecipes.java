package azzy.fabric.lookingglass.recipe;

import azzy.fabric.lookingglass.LookingGlassCommon;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

public class LookingGlassRecipes {

    public static void init() {}

    public static final LookingGlassRecipeType<InductionRecipe> INDUCTION_RECIPE = registerType("induction");
    public static final LookingGlassRecipeType<AlloyingRecipe> ALLOYING_RECIPE = registerType("alloying");
    public static final LookingGlassRecipeType<FreezingRecipe> FREEZING_RECIPE = registerType("freezing");

    public static final InductionRecipe.InductionRecipeSerializer INDUCTION_SERIALIZER = registerSerializer("induction", new InductionRecipe.InductionRecipeSerializer());
    public static final AlloyingRecipe.AlloyingRecipeSerializer ALLOYING_SERIALIZER = registerSerializer("alloying", new AlloyingRecipe.AlloyingRecipeSerializer());
    public static final FreezingRecipe.FreezingRecipeSerializer FREEZING_SERIALIZER = registerSerializer("freezing", new FreezingRecipe.FreezingRecipeSerializer());

    public static <T extends Recipe<?>> LookingGlassRecipeType<T> registerType(String name) {
        final Identifier id = new Identifier(LookingGlassCommon.MODID, name);
        return Registry.register(Registry.RECIPE_TYPE, id, new LookingGlassRecipeType<>(id));
    }

    private static <S extends RecipeSerializer<T>, T extends Recipe<?>> S registerSerializer(String id, S serializer) {
        return Registry.register(Registry.RECIPE_SERIALIZER, new Identifier(LookingGlassCommon.MODID, id), serializer);
    }
}
