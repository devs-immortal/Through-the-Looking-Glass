package azzy.fabric.lookingglass.recipe;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.blockentity.MixerEntity;
import azzy.fabric.lookingglass.util.BufUtils;
import azzy.fabric.lookingglass.util.json.JsonUtils;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.fabricmc.loader.lib.gson.MalformedJsonException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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

    private final DefaultedList<Ingredient> ingredients;
    private final ItemStack output;
    private final Identifier id;

    public MixerRecipe(Identifier id, DefaultedList<Ingredient> ingredients, ItemStack output) {
        this.ingredients = ingredients;
        this.output = output;
        this.id = id;
    }

    @Override
    public boolean matches(MixerEntity inv, World world) {
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
        return ingredients;
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
        return ingredients;
    }

    public static class MixerRecipeSerializer implements RecipeSerializer<MixerRecipe> {

        @Override
        public MixerRecipe read(Identifier id, JsonObject json) {
            DefaultedList<Ingredient> ingredients;
            ItemStack output;

            try {
                if(!(json.has("inputs") && json.has("output")))
                    throw new MalformedJsonException("Invalid Alloying Recipe Json");
                ingredients = JsonUtils.ingredientsFromJson(json.getAsJsonArray("inputs"), 4);
                output = JsonUtils.stackFromJson(json.getAsJsonObject("output"));
            } catch (Exception e) {
                FFLog.error("Exception found while loading Alloying recipe json " + id.toString() + " ", e);
                return null;
            }

            return new MixerRecipe(id, ingredients, output);
        }

        @Override
        public MixerRecipe read(Identifier id, PacketByteBuf buf) {
            ItemStack output = buf.readItemStack();
            DefaultedList<Ingredient> ingredients = BufUtils.ingredientsFromBuf(buf);
            return new MixerRecipe(id, ingredients, output);
        }

        @Override
        public void write(PacketByteBuf buf, MixerRecipe recipe) {
            buf.writeItemStack(recipe.output);
            BufUtils.ingredientsToBuf(recipe.ingredients, buf);
        }
    }
}
