package azzy.fabric.lookingglass.recipe;

import azzy.fabric.incubus_core.json.JsonUtils;
import azzy.fabric.incubus_core.recipe.IngredientStack;
import azzy.fabric.incubus_core.recipe.OptionalStack;
import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.blockentity.PoweredFurnaceEntity;
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

import java.util.Collections;
import java.util.List;

import static azzy.fabric.lookingglass.LookingGlassCommon.FFLog;

public class FreezingRecipe implements LookingGlassRecipe<PoweredFurnaceEntity> {

    public static final FreezingRecipe EMPTY = new FreezingRecipe(new Identifier(LookingGlassCommon.MODID, "empty"), IngredientStack.EMPTY, OptionalStack.EMPTY);

    private final IngredientStack input;
    private final OptionalStack output;
    private final Identifier id;

    public FreezingRecipe(Identifier id, IngredientStack input, OptionalStack output) {
        this.input = input;
        this.output = output;
        this.id = id;
    }

    @Override
    public boolean matches(PoweredFurnaceEntity inv, World world) {
        return !isEmpty() && input.test(inv.getStack(0));
    }

    @Override
    public boolean isEmpty() {
        return this == EMPTY;
    }

    @Override
    public ItemStack craft(PoweredFurnaceEntity inv) {
        ItemStack outSlot = inv.getStack(1);

        ItemStack optional = output.getFirstStack();

        if(optional != null) {
            if(outSlot.isEmpty()) {
                inv.setStack(1, optional.copy());
                inv.getStack(0).decrement(input.getCount());
            }
            else if(outSlot.getCount() + output.getCount() <= outSlot.getMaxCount() && output.itemMatch(outSlot)) {
                inv.getStack(1).increment(output.getCount());
                inv.getStack(0).decrement(input.getCount());
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
        return new ItemStack(LookingGlassBlocks.ALLOY_FURNACE_BLOCK);
    }

    @Override
    public DefaultedList<Ingredient> getIngredients() {
        return IngredientStack.listIngredients(Collections.singletonList(input));
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return LookingGlassRecipes.FREEZING_SERIALIZER;
    }

    @Override
    public LookingGlassRecipeType<?> getType() {
        return LookingGlassRecipes.FREEZING_RECIPE;
    }

    @Override
    public List<IngredientStack> getInputs() {
        return Collections.singletonList(input);
    }

    public static class FreezingRecipeSerializer implements RecipeSerializer<FreezingRecipe> {

        @Override
        public FreezingRecipe read(Identifier id, JsonObject json) {

            IngredientStack input;
            OptionalStack output;
            boolean optional = false;

            try {
                if(json.has("optional")) {
                    optional = json.get("optional").getAsBoolean();
                }

                if(!(json.has("input") && json.has("output")))
                    throw new MalformedJsonException("Invalid Freezing Recipe Json");
                input = JsonUtils.ingredientFromJson(json.getAsJsonObject("input"));
                output = JsonUtils.optionalStackFromJson(json.getAsJsonObject("output"));

                if(output.isEmpty())
                    return EMPTY;

            } catch (Exception e) {
                if(optional && e instanceof JsonSyntaxException)
                    return EMPTY;
                FFLog.error("Exception found while loading Freezing recipe json " + id.toString() + " ", e);
                return null;
            }

            return new FreezingRecipe(id, input, output);
        }

        @Override
        public FreezingRecipe read(Identifier id, PacketByteBuf buf) {
            OptionalStack output = OptionalStack.fromByteBuf(buf);
            IngredientStack input = IngredientStack.fromByteBuf(buf);
            return new FreezingRecipe(id, input, output);
        }

        @Override
        public void write(PacketByteBuf buf, FreezingRecipe recipe) {
            recipe.output.write(buf);
            recipe.input.write(buf);
        }
    }
}
