package azzy.fabric.lookingglass.recipe;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.blockentity.AlloyFurnaceEntity;
import azzy.fabric.lookingglass.blockentity.PoweredFurnaceEntity;
import com.google.gson.JsonObject;
import net.fabricmc.loader.lib.gson.MalformedJsonException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static azzy.fabric.lookingglass.LookingGlassCommon.FFLog;

public class FreezingRecipe implements LookingGlassRecipe<PoweredFurnaceEntity> {

    private final Ingredient input;
    private final ItemStack output;
    private final Identifier id;

    public FreezingRecipe(Identifier id, Ingredient input, ItemStack output) {
        this.input = input;
        this.output = output;
        this.id = id;
    }

    @Override
    public boolean matches(PoweredFurnaceEntity inv, World world) {
        return input.test(inv.getStack(0));
    }

    @Override
    public ItemStack craft(PoweredFurnaceEntity inv) {
        return output.copy();
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
        DefaultedList<Ingredient> inputs = DefaultedList.of();
        inputs.add(input);
        return inputs;
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
    public List<Ingredient> getInputs() {
        return Collections.singletonList(input);
    }

    public static class FreezingRecipeSerializer implements RecipeSerializer<FreezingRecipe> {

        @Override
        public FreezingRecipe read(Identifier id, JsonObject json) {

            Ingredient input;
            Item output;
            int count;

            try {
                if(!(json.has("input") && json.has("output")))
                    throw new MalformedJsonException("Invalid Freezing Recipe Json");
                input = Ingredient.fromJson(json.get("input"));
                output = Registry.ITEM.get(Identifier.tryParse(json.get("output").getAsString()));
                count = json.has("count") ? json.get("count").getAsInt() : 1;
            } catch (Exception e) {
                FFLog.error("Exception found while loading Freezing recipe json " + id.toString() + " ", e);
                return null;
            }

            return new FreezingRecipe(id, input, new ItemStack(output, count));
        }

        @Override
        public FreezingRecipe read(Identifier id, PacketByteBuf buf) {
            ItemStack output = buf.readItemStack();
            Ingredient input = Ingredient.fromPacket(buf);
            return new FreezingRecipe(id, input, output);
        }

        @Override
        public void write(PacketByteBuf buf, FreezingRecipe recipe) {
            buf.writeItemStack(recipe.output);
            recipe.input.write(buf);
        }
    }
}
