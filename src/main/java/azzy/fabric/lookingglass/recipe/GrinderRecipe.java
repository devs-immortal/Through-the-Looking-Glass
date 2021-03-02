package azzy.fabric.lookingglass.recipe;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.blockentity.GrinderEntity;
import azzy.fabric.lookingglass.blockentity.PoweredFurnaceEntity;
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static azzy.fabric.lookingglass.LookingGlassCommon.FFLog;

public class GrinderRecipe implements LookingGlassRecipe<GrinderEntity> {

    private final Ingredient input;
    private final ItemStack outputTop, outputBottom;
    private final float chance;
    private final Identifier id;

    public GrinderRecipe(Identifier id, Ingredient input, ItemStack outputTop, ItemStack outputBottom, float chance) {
        this.input = input;
        this.outputTop = outputTop;
        this.outputBottom = outputBottom;
        this.chance = chance;
        this.id = id;
    }

    @Override
    public boolean matches(GrinderEntity inv, World world) {
        return input.test(inv.getStack(0));
    }

    @Override
    public ItemStack craft(GrinderEntity inv) {
        return outputTop.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return outputTop;
    }

    @Override
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(LookingGlassBlocks.GRINDER_BLOCK);
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
        return LookingGlassRecipes.GRINDING_SERIALIZER;
    }

    @Override
    public LookingGlassRecipeType<?> getType() {
        return LookingGlassRecipes.GRINDING_RECIPE;
    }

    @Override
    public List<Ingredient> getInputs() {
        return Collections.singletonList(input);
    }

    @Override
    public List<ItemStack> getOutputs() {
        return Arrays.asList(outputTop, outputBottom);
    }

    public float getChance() {
        return chance;
    }

    public static class GrinderRecipeSerializer implements RecipeSerializer<GrinderRecipe> {

        @Override
        public GrinderRecipe read(Identifier id, JsonObject json) {
            Ingredient input;
            Item outputTop, outputBottom;
            float chance;
            int countTop, countBottom;

            try {
                if(!(json.has("outputTop") && json.has("input")))
                    throw new MalformedJsonException("Invalid Grinding Recipe Json");
                input = Ingredient.fromJson(json.get("input"));
                outputTop = Registry.ITEM.get(Identifier.tryParse(json.get("outputTop").getAsString()));
                outputBottom = Registry.ITEM.get(Identifier.tryParse(json.get("outputBottom").getAsString()));
                countTop = json.has("countTop") ? json.get("countTop").getAsInt() : 1;
                countBottom = json.has("countBottom") ? json.get("countBottom").getAsInt() : 1;
                chance = json.get("chance").getAsFloat();
            } catch (Exception e) {
                FFLog.error("Exception found while loading Grinding recipe json " + id.toString() + " ", e);
                return null;
            }

            ItemStack secOut = outputBottom == Items.AIR ? ItemStack.EMPTY : new ItemStack(outputBottom, countBottom);
            return new GrinderRecipe(id, input, new ItemStack(outputTop, countTop), secOut, chance);
        }

        @Override
        public GrinderRecipe read(Identifier id, PacketByteBuf buf) {
            ItemStack outputTop = buf.readItemStack();
            ItemStack outputBottom = buf.readItemStack();
            float chance = buf.readFloat();
            Ingredient input = Ingredient.fromPacket(buf);
            return new GrinderRecipe(id, input, outputTop, outputBottom, chance);
        }

        @Override
        public void write(PacketByteBuf buf, GrinderRecipe recipe) {
            buf.writeItemStack(recipe.outputTop);
            buf.writeItemStack(recipe.getOutput());
            buf.writeFloat(recipe.chance);
            recipe.input.write(buf);
        }
    }
}
