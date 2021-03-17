package azzy.fabric.lookingglass.recipe;

import azzy.fabric.incubus_core.json.JsonUtils;
import azzy.fabric.incubus_core.recipe.IngredientStack;
import azzy.fabric.incubus_core.recipe.OptionalStack;
import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.blockentity.GrinderEntity;
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
import java.util.Random;

import static azzy.fabric.lookingglass.LookingGlassCommon.FFLog;
import static azzy.fabric.lookingglass.LookingGlassCommon.MODID;

public class GrindingRecipe implements LookingGlassRecipe<GrinderEntity> {

    public static final GrindingRecipe EMPTY = new GrindingRecipe(new Identifier(MODID, "empty"), IngredientStack.EMPTY, OptionalStack.EMPTY, OptionalStack.EMPTY, 0F);

    private final IngredientStack input;
    private final OptionalStack mainOutput, secondaryOutput;
    private final float chance;
    private final Identifier id;

    public GrindingRecipe(Identifier id, IngredientStack input, OptionalStack outputTop, OptionalStack outputBottom, float chance) {
        this.input = input;
        this.mainOutput = outputTop;
        this.secondaryOutput = outputBottom;
        this.chance = chance;
        this.id = id;
    }

    @Override
    public boolean matches(GrinderEntity inv, World world) {
        return !isEmpty() && input.test(inv.getStack(0));
    }

    @Override
    public boolean isEmpty() {
        return this == EMPTY;
    }

    @Override
    public ItemStack craft(GrinderEntity inv) {
        ItemStack outSlot = inv.getStack(1);
        ItemStack mainOptional = mainOutput.getFirstStack();
        if(inv.getWorld() != null && mainOptional != null) {
            if(outSlot.isEmpty()) {
                inv.setStack(1, mainOptional.copy());
                inv.getStack(0).decrement(input.getCount());
                handleSecondaryOutputs(inv, inv.getWorld().getRandom());
            }
            else if(outSlot.getCount() + mainOutput.getCount() <= outSlot.getMaxCount() && mainOutput.itemMatch(outSlot)) {
                inv.getStack(1).increment(mainOutput.getCount());
                inv.getStack(0).decrement(input.getCount());
                handleSecondaryOutputs(inv, inv.getWorld().getRandom());
            }
            return mainOptional.copy();
        }

        return ItemStack.EMPTY;
    }

    private void handleSecondaryOutputs(GrinderEntity inv, Random random) {
        float trialChance = chance;
        ItemStack secOptional = secondaryOutput.getFirstStack();
        if(secOptional != null) {
            while (random.nextFloat() <= trialChance) {
                ItemStack secSlot = inv.getStack(2);
                if(secSlot.isEmpty()) {
                    inv.setStack(2, secOptional.copy());
                }
                else if(secSlot.getCount() + secSlot.getCount() <= secSlot.getMaxCount() && secondaryOutput.itemMatch(secSlot)) {
                    inv.getStack(2).increment(secondaryOutput.getCount());
                }
                trialChance--;
            }
        }
    }

    @Override
    public boolean fits(int width, int height) {
        return true;
    }

    @Override
    public ItemStack getOutput() {
        return mainOutput.getFirstStack();
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
        return Arrays.asList(mainOutput.getFirstStack(), secondaryOutput.getFirstStack());
    }

    public float getChance() {
        return chance;
    }

    public static class GrindingRecipeSerializer implements RecipeSerializer<GrindingRecipe> {

        @Override
        public GrindingRecipe read(Identifier id, JsonObject json) {
            IngredientStack input;
            OptionalStack output, secondary;
            float chance = 0;

            try {
                if(!(json.has("output") && json.has("input")))
                    throw new MalformedJsonException("Invalid Grinding Recipe Json");
                input = JsonUtils.ingredientFromJson(json.getAsJsonObject("input"));
                output = JsonUtils.optionalStackFromJson(json.getAsJsonObject("output"));
                secondary = JsonUtils.optionalStackFromJson(json.getAsJsonObject("secondary"));

                if(output.isEmpty())
                    return EMPTY;

                if(!secondary.isEmpty())
                    chance = json.get("chance").getAsFloat();

            } catch (Exception e) {
                if(e instanceof JsonSyntaxException)
                    return EMPTY;
                FFLog.error("Exception found while loading Grinding recipe json " + id.toString() + " ", e);
                return null;
            }
            return new GrindingRecipe(id, input, output, secondary, chance);
        }

        @Override
        public GrindingRecipe read(Identifier id, PacketByteBuf buf) {
            OptionalStack outputTop = OptionalStack.fromByteBuf(buf);
            OptionalStack outputBottom = OptionalStack.fromByteBuf(buf);
            IngredientStack input = IngredientStack.fromByteBuf(buf);
            float chance = buf.readFloat();
            return new GrindingRecipe(id, input, outputTop, outputBottom, chance);
        }

        @Override
        public void write(PacketByteBuf buf, GrindingRecipe recipe) {
            recipe.mainOutput.write(buf);
            recipe.secondaryOutput.write(buf);
            recipe.input.write(buf);
            buf.writeFloat(recipe.chance);
        }
    }
}
