package azzy.fabric.lookingglass.recipe;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.blockentity.AlloyFurnaceEntity;
import azzy.fabric.lookingglass.blockentity.MixerEntity;
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
import java.util.List;

import static azzy.fabric.lookingglass.LookingGlassCommon.FFLog;

public class MixerRecipe implements LookingGlassRecipe<MixerEntity> {

    private final Ingredient inputA, inputB, inputC, inputD;
    private final ItemStack output;
    private final Identifier id;

    public MixerRecipe(Identifier id, Ingredient inputA, Ingredient inputB, Ingredient inputC, Ingredient inputD, ItemStack output) {
        this.inputA = inputA;
        this.inputB = inputB;
        this.inputC = inputC;
        this.inputD = inputD;
        this.output = output;
        this.id = id;
    }

    @Override
    public boolean matches(MixerEntity inv, World world) {
        ItemStack a = inv.getStack(0);
        ItemStack b = inv.getStack(1);
        ItemStack c = inv.getStack(2);
        ItemStack d = inv.getStack((3));
        return (inputA.test(a) ^ inputA.test(b) ^ inputA.test(c) ^ inputA.test(d)) && (inputB.test(a) ^ inputB.test(b) ^ inputB.test(c) ^ inputB.test(d)) && (inputC.test(a) ^ inputC.test(b) ^ inputC.test(c) ^ inputC.test(d)) && (inputD.test(a) ^ inputD.test(b) ^ inputD.test(c) ^ inputD.test(d));
    }

    @Override
    public ItemStack craft(MixerEntity inv) {
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
        inputs.add(inputA);
        inputs.add(inputB);
        inputs.add(inputC);
        inputs.add(inputD);
        return inputs;
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
    public List<Ingredient> getInputs() {
        return Arrays.asList(inputA, inputB);
    }

    public static class MixerRecipeSerializer implements RecipeSerializer<MixerRecipe> {

        @Override
        public MixerRecipe read(Identifier id, JsonObject json) {

            Ingredient inputA, inputB, inputC, inputD;
            Item output;
            int count;

            try {
                if(!(json.has("inputA") && json.has("inputB") && json.has("inputC") && json.has("inputD") && json.has("output")))
                    throw new MalformedJsonException("Invalid Alloying Recipe Json");
                inputA = Ingredient.fromJson(json.get("inputA"));
                inputB = Ingredient.fromJson(json.get("inputB"));
                inputC = Ingredient.fromJson(json.get("inputC"));
                inputD = Ingredient.fromJson(json.get("inputD"));
                output = Registry.ITEM.get(Identifier.tryParse(json.get("output").getAsString()));
                count = json.has("count") ? json.get("count").getAsInt() : 1;
            } catch (Exception e) {
                FFLog.error("Exception found while loading Alloying recipe json " + id.toString() + " ", e);
                return null;
            }

            return new MixerRecipe(id, inputA, inputB, inputC, inputD, new ItemStack(output, count));
        }

        @Override
        public MixerRecipe read(Identifier id, PacketByteBuf buf) {
            ItemStack output = buf.readItemStack();
            Ingredient inputA = Ingredient.fromPacket(buf);
            Ingredient inputB = Ingredient.fromPacket(buf);
            Ingredient inputC = Ingredient.fromPacket(buf);
            Ingredient inputD = Ingredient.fromPacket(buf);
            return new MixerRecipe(id, inputA, inputB, inputC, inputD, output);
        }

        @Override
        public void write(PacketByteBuf buf, MixerRecipe recipe) {
            buf.writeItemStack(recipe.output);
            recipe.inputA.write(buf);
            recipe.inputB.write(buf);
            recipe.inputC.write(buf);
            recipe.inputD.write(buf);
        }
    }
}
