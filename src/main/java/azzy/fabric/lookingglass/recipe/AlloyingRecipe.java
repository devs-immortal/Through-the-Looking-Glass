package azzy.fabric.lookingglass.recipe;

import azzy.fabric.lookingglass.block.TTLGBlocks;
import azzy.fabric.lookingglass.blockentity.AlloyFurnaceEntity;
import com.google.gson.JsonObject;
import net.fabricmc.loader.lib.gson.MalformedJsonException;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static azzy.fabric.lookingglass.LookingGlassCommon.FFLog;

public class AlloyingRecipe implements LookingGlassRecipe<AlloyFurnaceEntity> {

    private final Ingredient inputA, inputB;
    private final ItemStack output;
    private final Identifier id;

    public AlloyingRecipe(Identifier id, Ingredient inputA, Ingredient inputB, ItemStack output) {
        this.inputA = inputA;
        this.inputB = inputB;
        this.output = output;
        this.id = id;
    }

    @Override
    public boolean matches(AlloyFurnaceEntity inv, World world) {
        return (inputA.test(inv.getStack(0)) ^ inputA.test(inv.getStack(1))) && (inputB.test(inv.getStack(0)) ^ inputB.test(inv.getStack(1)));
    }

    @Override
    public ItemStack craft(AlloyFurnaceEntity inv) {
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
        return new ItemStack(TTLGBlocks.ALLOY_FURNACE_BLOCK);
    }

    @Override
    public DefaultedList<Ingredient> getPreviewInputs() {
        DefaultedList<Ingredient> inputs = DefaultedList.of();
        inputs.add(inputA);
        inputs.add(inputB);
        return inputs;
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
    public List<Ingredient> getInputs() {
        return Arrays.asList(inputA, inputB);
    }

    public static class AlloyingRecipeSerializer implements RecipeSerializer<AlloyingRecipe> {

        @Override
        public AlloyingRecipe read(Identifier id, JsonObject json) {

            Ingredient inputA;
            Ingredient inputB;
            Item output;
            int count;

            try {
                if(!(json.has("inputA") && json.has("inputB") && json.has("output")))
                    throw new MalformedJsonException("Invalid Induction Recipe Json");
                inputA = Ingredient.fromJson(json.get("inputA"));
                inputB = Ingredient.fromJson(json.get("inputB"));
                output = Registry.ITEM.get(Identifier.tryParse(json.get("output").getAsString()));
                count = json.has("count") ? json.get("count").getAsInt() : 1;
            } catch (Exception e) {
                FFLog.error("Exception found while loading Induction recipe json " + id.toString() + " ", e);
                return null;
            }

            return new AlloyingRecipe(id, inputA, inputB, new ItemStack(output, count));
        }

        @Override
        public AlloyingRecipe read(Identifier id, PacketByteBuf buf) {
            ItemStack output = buf.readItemStack();
            Ingredient inputA = Ingredient.fromPacket(buf);
            Ingredient inputB = Ingredient.fromPacket(buf);
            return new AlloyingRecipe(id, inputA, inputB, output);
        }

        @Override
        public void write(PacketByteBuf buf, AlloyingRecipe recipe) {
            buf.writeItemStack(recipe.output);
            recipe.inputA.write(buf);
            recipe.inputB.write(buf);
        }
    }
}
