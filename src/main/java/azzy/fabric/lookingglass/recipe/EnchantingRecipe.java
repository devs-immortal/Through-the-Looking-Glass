package azzy.fabric.lookingglass.recipe;

import azzy.fabric.incubus_core.json.JsonUtils;
import azzy.fabric.incubus_core.recipe.IngredientStack;
import azzy.fabric.incubus_core.recipe.OptionalStack;
import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.blockentity.AlloyFurnaceEntity;
import azzy.fabric.lookingglass.blockentity.EnchanterEntity;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.lib.gson.MalformedJsonException;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.StringNbtReader;
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

public class EnchantingRecipe implements LookingGlassRecipe<EnchanterEntity> {

    public static final EnchantingRecipe EMPTY = new EnchantingRecipe(new Identifier(LookingGlassCommon.MODID, "empty"), List.of(IngredientStack.EMPTY, IngredientStack.EMPTY), OptionalStack.EMPTY, Integer.MAX_VALUE, new NbtCompound());

    private final List<IngredientStack> ingredients;
    private final OptionalStack output;
    private final NbtCompound tag;
    private final int minEnchantingPower;
    private final Identifier id;

    public EnchantingRecipe(Identifier id, List<IngredientStack> ingredients, OptionalStack output, int minEnchantingPower, NbtCompound tag) {
        this.ingredients = ingredients;
        this.output = output;
        this.id = id;
        this.minEnchantingPower = minEnchantingPower;
        this.tag = tag;
    }

    @Override
    public boolean matches(EnchanterEntity inv, World world) {
        return !isEmpty() && IngredientStack.matchInvExclusively(inv, ingredients, 5, 1);
    }

    @Override
    public boolean isEmpty() {
        return this == EMPTY;
    }

    @Override
    public ItemStack craft(EnchanterEntity inv) {
        ItemStack optional = output.getFirstStack();
        if(optional != null) {
            IngredientStack.decrementExclusively(inv, ingredients, 4, 2);
            ItemStack output = optional.copy();
            if(tag != null) {
                output.setTag(tag.copy());
            }
            inv.spawnOutput(output);
            return output;
        }
        return ItemStack.EMPTY;
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
        ItemStack out = output.getFirstStack();
        if(out != null && tag != null) {
            out.setTag(tag);
        }
        return out;
    }

    @Override
    public ItemStack createIcon() {
        return new ItemStack(LookingGlassBlocks.ENCHANTER_BLOCK);
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
        return LookingGlassRecipes.ENCHANTING_SERIALIZER;
    }

    @Override
    public LookingGlassRecipeType<?> getType() {
        return LookingGlassRecipes.ENCHANTING_RECIPE;
    }

    @Override
    public List<IngredientStack> getInputs() {
        return ingredients;
    }

    public static class EnchantingRecipeSerializer implements RecipeSerializer<EnchantingRecipe> {

        @Override
        public EnchantingRecipe read(Identifier id, JsonObject json) {

            List<IngredientStack> ingredients;
            OptionalStack output;
            NbtCompound tag;
            int enchPower;
            boolean optional = false;

            try {
                if(json.has("optional")) {
                    optional = json.get("optional").getAsBoolean();
                }

                if(!(json.has("inputs") || json.has("output") || json.has("enchantingPower")))
                    throw new MalformedJsonException("Invalid Enchanting Recipe Json");

                ingredients = JsonUtils.ingredientsFromJson(json.getAsJsonArray("inputs"), 5);
                output = JsonUtils.optionalStackFromJson(json.getAsJsonObject("output"));
                tag = json.has("tag") ? StringNbtReader.parse(json.get("tag").getAsString()) : null;
                enchPower = json.get("minEnchantingPower").getAsInt();

            } catch (Exception e) {
                if(optional && e instanceof JsonSyntaxException)
                    return EMPTY;
                FFLog.error("Exception found while loading Enchanting recipe json " + id.toString() + " ", e);
                return null;
            }

            return new EnchantingRecipe(id, ingredients, output, enchPower, tag);
        }

        @Override
        public EnchantingRecipe read(Identifier id, PacketByteBuf buf) {
            return new EnchantingRecipe(id, IngredientStack.decodeByteBuf(buf, 5), OptionalStack.fromByteBuf(buf), buf.readInt(), buf.readNbt());
        }

        @Override
        public void write(PacketByteBuf buf, EnchantingRecipe recipe) {
            recipe.getInputs().forEach(ingredientStack -> ingredientStack.write(buf));
            recipe.output.write(buf);
            buf.writeInt(recipe.minEnchantingPower);
            buf.writeNbt(recipe.tag);
        }
    }
}
