package azzy.fabric.lookingglass.recipe;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.blockentity.GrinderEntity;
import azzy.fabric.lookingglass.util.IngredientStack;
import azzy.fabric.lookingglass.util.json.JsonUtils;
import com.google.gson.JsonObject;
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

public class GrindingRecipe implements LookingGlassRecipe<GrinderEntity> {

    private final IngredientStack input;
    private final ItemStack mainOutput, secondaryOutput;
    private final float chance;
    private final Identifier id;

    public GrindingRecipe(Identifier id, IngredientStack input, ItemStack outputTop, ItemStack outputBottom, float chance) {
        this.input = input;
        this.mainOutput = outputTop;
        this.secondaryOutput = outputBottom;
        this.chance = chance;
        this.id = id;
    }

    @Override
    public boolean matches(GrinderEntity inv, World world) {
        return input.test(inv.getStack(0));
    }

    @Override
    public ItemStack craft(GrinderEntity inv) {
        ItemStack outSlot = inv.getStack(1);
        ItemStack secSlot = inv.getStack(2);
        if(outSlot.isEmpty()) {
            inv.setStack(1, mainOutput.copy());
            inv.getStack(0).decrement(input.getCount());
            if(inv.getWorld().getRandom().nextFloat() <= chance) {
                if(secSlot.isEmpty()) {
                    inv.setStack(2, secondaryOutput.copy());
                }
                else if(secSlot.getCount() + secSlot.getCount() <= secSlot.getMaxCount() && secSlot.isItemEqual(secSlot)) {
                    inv.getStack(2).increment(secondaryOutput.getCount());
                }
            }
        }
        else if(outSlot.getCount() + mainOutput.getCount() <= outSlot.getMaxCount() && mainOutput.isItemEqual(outSlot)) {
            inv.getStack(1).increment(mainOutput.getCount());
            inv.getStack(0).decrement(input.getCount());
            if(inv.getWorld().getRandom().nextFloat() <= chance) {
                if(secSlot.isEmpty()) {
                    inv.setStack(2, secondaryOutput.copy());
                }
                else if(secSlot.getCount() + secSlot.getCount() <= secSlot.getMaxCount() && secSlot.isItemEqual(secSlot)) {
                    inv.getStack(2).increment(secondaryOutput.getCount());
                }
            }
        }
        return mainOutput.copy();
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return mainOutput;
    }

    @Override
    public ItemStack getRecipeKindIcon() {
        return new ItemStack(LookingGlassBlocks.GRINDER_BLOCK);
    }

    @Override
    public DefaultedList<Ingredient> getPreviewInputs() {
        return IngredientStack.listIngredients(Collections.singletonList(input));
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
    public List<IngredientStack> getInputs() {
        return Collections.singletonList(input);
    }

    @Override
    public List<ItemStack> getOutputs() {
        return Arrays.asList(mainOutput, secondaryOutput);
    }

    public float getChance() {
        return chance;
    }

    public static class GrindingRecipeSerializer implements RecipeSerializer<GrindingRecipe> {

        @Override
        public GrindingRecipe read(Identifier id, JsonObject json) {
            IngredientStack input;
            ItemStack output, secondary;
            float chance;

            try {
                if(!(json.has("output") && json.has("input")))
                    throw new MalformedJsonException("Invalid Grinding Recipe Json");
                input = JsonUtils.ingredientFromJson(json.getAsJsonObject("input"));
                output = JsonUtils.stackFromJson(json.getAsJsonObject("output"));
                secondary = JsonUtils.stackFromJson(json.getAsJsonObject("secondary"));
                chance = json.get("chance").getAsFloat();
            } catch (Exception e) {
                FFLog.error("Exception found while loading Grinding recipe json " + id.toString() + " ", e);
                return null;
            }
            return new GrindingRecipe(id, input, output, secondary, chance);
        }

        @Override
        public GrindingRecipe read(Identifier id, PacketByteBuf buf) {
            ItemStack outputTop = buf.readItemStack();
            ItemStack outputBottom = buf.readItemStack();
            IngredientStack input = IngredientStack.fromByteBuf(buf);
            float chance = buf.readFloat();
            return new GrindingRecipe(id, input, outputTop, outputBottom, chance);
        }

        @Override
        public void write(PacketByteBuf buf, GrindingRecipe recipe) {
            buf.writeItemStack(recipe.mainOutput);
            buf.writeItemStack(recipe.secondaryOutput);
            recipe.input.write(buf);
            buf.writeFloat(recipe.chance);
        }
    }
}
