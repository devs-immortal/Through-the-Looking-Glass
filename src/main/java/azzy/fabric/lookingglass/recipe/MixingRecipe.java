package azzy.fabric.lookingglass.recipe;

import azzy.fabric.incubus_core.json.JsonUtils;
import azzy.fabric.incubus_core.recipe.IngredientStack;
import azzy.fabric.incubus_core.recipe.OptionalStack;
import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.blockentity.MixerEntity;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.lib.gson.MalformedJsonException;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static azzy.fabric.lookingglass.LookingGlassCommon.FFLog;

public class MixingRecipe implements LookingGlassRecipe<MixerEntity> {

    public static final MixingRecipe EMPTY = new MixingRecipe(new Identifier(LookingGlassCommon.MODID, "empty"), Collections.unmodifiableList(Arrays.asList(IngredientStack.EMPTY, IngredientStack.EMPTY, IngredientStack.EMPTY, IngredientStack.EMPTY)), OptionalStack.EMPTY);

    private final List<IngredientStack> ingredients;
    private final OptionalStack output;
    private final Identifier id;

    public MixingRecipe(Identifier id, List<IngredientStack> ingredients, OptionalStack output) {
        this.ingredients = ingredients;
        this.output = output;
        this.id = id;
    }

    @Override
    public boolean matches(MixerEntity inv, World world) {
        return !isEmpty() && IngredientStack.matchInvExclusively(inv, ingredients, 4, 0);
    }

    @Override
    public boolean isEmpty() {
        return this == EMPTY;
    }

    @Override
    public ItemStack craft(MixerEntity inv) {
        ItemStack outSlot = inv.getStack(4);
        ItemStack optional = output.getFirstStack();
        if(optional != null) {
            if(outSlot.isEmpty()) {
                inv.setStack(4, optional.copy());
                IngredientStack.decrementExclusively(inv, ingredients, 4, 0);
            }
            else if(outSlot.getCount() + output.getCount() <= outSlot.getMaxCount() && output.itemMatch(outSlot)) {
                inv.getStack(4).increment(output.getCount());
                IngredientStack.decrementExclusively(inv, ingredients, 4, 0);
            }
            return optional.copy();
        }
        return ItemStack.EMPTY;
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return output.getFirstStack();
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(LookingGlassBlocks.MIXER_BLOCK);
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return IngredientStack.listIngredients(ingredients);
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return LookingGlassRecipes.MIXING_SERIALIZER;
    }

    @Override
    public LookingGlassRecipeType<?> getType() {
        return LookingGlassRecipes.MIXING_RECIPE;
    }

    @Override
    public List<IngredientStack> getInputs() {
        return ingredients;
    }

    public static class MixingRecipeSerializer implements RecipeSerializer<MixingRecipe> {

        @Override
        public MixingRecipe read(Identifier id, JsonObject json) {
            List<IngredientStack> ingredients;
            OptionalStack output;
            boolean optional = false;

            try {
                if(json.has("optional")) {
                    optional = json.get("optional").getAsBoolean();
                }

                if(!(json.has("inputs") && json.has("output")))
                    throw new MalformedJsonException("Invalid Alloying Recipe Json");
                ingredients = JsonUtils.ingredientsFromJson(json.getAsJsonArray("inputs"), 4);
                output = JsonUtils.optionalStackFromJson(json.getAsJsonObject("output"));

                if(output.isEmpty())
                    return EMPTY;

            } catch (Exception e) {
                if(optional && e instanceof JsonSyntaxException)
                    return EMPTY;
                FFLog.error("Exception found while loading Alloying recipe json " + id.toString() + " ", e);
                return null;
            }

            return new MixingRecipe(id, ingredients, output);
        }

        @Override
        public MixingRecipe read(Identifier id, PacketByteBuf buf) {
            OptionalStack output = OptionalStack.fromByteBuf(buf);
            List<IngredientStack> ingredients = IngredientStack.decodeByteBuf(buf, 4);
            return new MixingRecipe(id, ingredients, output);
        }

        @Override
        public void write(PacketByteBuf buf, MixingRecipe recipe) {
            recipe.output.write(buf);
            recipe.getInputs().forEach(ingredientStack -> ingredientStack.write(buf));
        }
    }
}
