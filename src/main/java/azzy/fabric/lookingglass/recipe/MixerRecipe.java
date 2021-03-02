package azzy.fabric.lookingglass.recipe;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.blockentity.MixerEntity;
import com.google.gson.JsonArray;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

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
        List<Ingredient> ingredients = Arrays.asList(inputA, inputB, inputC, inputD);
        List<ItemStack> invStacks = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            invStacks.add(inv.getStack(i));
        }
        AtomicInteger matches = new AtomicInteger();
        ingredients.forEach(ingredient -> {
            for (int i = 0; i < invStacks.size(); i++) {
                if(ingredient.isEmpty()) {
                    matches.getAndIncrement();
                    break;
                }
                ItemStack stack = invStacks.get(i);
                if(ingredient.test(stack)) {
                    matches.getAndIncrement();
                    invStacks.remove(i);
                    break;
                }
            }
        });
        return matches.get() == 4;
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
        return new ItemStack(LookingGlassBlocks.MIXER_BLOCK);
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

            DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(4, Ingredient.EMPTY);
            Item output;
            int count;

            try {
                if(!(json.has("inputs") && json.has("output")))
                    throw new MalformedJsonException("Invalid Alloying Recipe Json");
                JsonArray inputs = json.getAsJsonArray("inputs");
                for (int i = 0; i < inputs.size(); i++) {
                    ingredients.set(i, Ingredient.fromJson(inputs.get(i)));
                }
                output = Registry.ITEM.get(Identifier.tryParse(json.get("output").getAsString()));
                count = json.has("count") ? json.get("count").getAsInt() : 1;
            } catch (Exception e) {
                FFLog.error("Exception found while loading Alloying recipe json " + id.toString() + " ", e);
                return null;
            }

            return new MixerRecipe(id, ingredients.get(0), ingredients.get(1), ingredients.get(2), ingredients.get(3), new ItemStack(output, count));
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
