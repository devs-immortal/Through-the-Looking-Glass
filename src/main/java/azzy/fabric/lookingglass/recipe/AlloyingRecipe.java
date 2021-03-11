package azzy.fabric.lookingglass.recipe;

import azzy.fabric.incubus_core.json.JsonUtils;
import azzy.fabric.incubus_core.recipe.IngredientStack;
import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.blockentity.AlloyFurnaceEntity;
import com.google.gson.JsonObject;
import net.fabricmc.loader.lib.gson.MalformedJsonException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.List;

import static azzy.fabric.lookingglass.LookingGlassCommon.FFLog;

public class AlloyingRecipe implements LookingGlassRecipe<AlloyFurnaceEntity> {

    private final List<IngredientStack> ingredients;
    private final ItemStack output;
    private final Identifier id;

    public AlloyingRecipe(Identifier id, List<IngredientStack> ingredients, ItemStack output) {
        this.ingredients = ingredients;
        this.output = output;
        this.id = id;
    }

    @Override
    public boolean matches(AlloyFurnaceEntity inv, World world) {
        return IngredientStack.matchInvExclusively(inv, ingredients, 2, 0);
    }

    @Override
    public ItemStack craft(AlloyFurnaceEntity inv) {
        ItemStack outSlot = inv.getStack(2);
        if(outSlot.isEmpty()) {
            inv.setStack(2, output.copy());
            IngredientStack.decrementExclusively(inv, ingredients, 2, 0);
        }
        else if(outSlot.getCount() + output.getCount() <= outSlot.getMaxCount() && output.isItemEqual(outSlot)) {
            inv.getStack(2).increment(output.getCount());
            IngredientStack.decrementExclusively(inv, ingredients, 2, 0);
        }
        return output.copy();
    }

    @Override
    public String getGroup() {
        return "minecraft:smelting";
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return output;
    }

    @Override
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(LookingGlassBlocks.ALLOY_FURNACE_BLOCK);
    }

    @Override
    public DefaultedList<Ingredient> getPreviewInputs() {
        return IngredientStack.listIngredients(ingredients);
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return LookingGlassRecipes.ALLOYING_SERIALIZER;
    }

    @Override
    public LookingGlassRecipeType<?> getType() {
        return LookingGlassRecipes.ALLOYING_RECIPE;
    }

    @Override
    public List<IngredientStack> getInputs() {
        return ingredients;
    }

    public static class AlloyingRecipeSerializer implements RecipeSerializer<AlloyingRecipe> {

        @Override
        public AlloyingRecipe read(Identifier id, JsonObject json) {

            List<IngredientStack> ingredients;
            ItemStack output;

            try {
                if(!(json.has("inputs") && json.has("output")))
                    throw new MalformedJsonException("Invalid Alloying Recipe Json");
                ingredients = JsonUtils.ingredientsFromJson(json.getAsJsonArray("inputs"), 2);
                output = JsonUtils.stackFromJson(json.getAsJsonObject("output"));
            } catch (Exception e) {
                FFLog.error("Exception found while loading Alloying recipe json " + id.toString() + " ", e);
                return null;
            }

            return new AlloyingRecipe(id, ingredients, output);
        }

        @Override
        public AlloyingRecipe read(Identifier id, PacketByteBuf buf) {
            return new AlloyingRecipe(id, IngredientStack.decodeByteBuf(buf, 2), buf.readItemStack());
        }

        @Override
        public void write(PacketByteBuf buf, AlloyingRecipe recipe) {
            recipe.getInputs().forEach(ingredientStack -> ingredientStack.write(buf));
            buf.writeItemStack(recipe.output);
        }
    }
}
