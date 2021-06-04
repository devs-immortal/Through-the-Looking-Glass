package azzy.fabric.lookingglass.recipe;

import azzy.fabric.incubus_core.recipe.IngredientStack;
import azzy.fabric.lookingglass.block.BlockInductorBlock;
import azzy.fabric.lookingglass.block.LookingGlassBlocks;
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
import java.util.stream.Collectors;

import static azzy.fabric.lookingglass.LookingGlassCommon.FFLog;

public class InductionRecipe implements LookingGlassRecipe<BlockInductorBlock> {

    private final Block[] inputs;
    private final ItemStack output;
    private final Identifier id;

    public InductionRecipe(Identifier id, Block[] inputs, ItemStack output) {
        this.inputs = inputs;
        this.output = output;
        this.id = id;
    }

    @Override
    public boolean matches(BlockInductorBlock inv, World world) {
        return Arrays.stream(inputs).allMatch(inv.tempBlockHolder::contains);
    }

    @Override
    public boolean isEmpty() {
        return false;
    }

    @Override
    public ItemStack craft(BlockInductorBlock inv) {
        return ItemStack.EMPTY;
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
    public ItemStack createIcon() {
        return new ItemStack(LookingGlassBlocks.BLOCK_INDUCTOR_BLOCK);
    }

    @Override
    public DefaultedList<ItemStack> getRemainingStacks(BlockInductorBlock inventory) {
        return DefaultedList.of();
    }

    @Override
    public Identifier getId() {
        return id;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return LookingGlassRecipes.INDUCTION_SERIALIZER;
    }

    @Override
    public LookingGlassRecipeType<?> getType() {
        return LookingGlassRecipes.INDUCTION_RECIPE;
    }

    @Override
    public List<IngredientStack> getInputs() {
        return Arrays.stream(inputs).map(Ingredient::ofItems).map(IngredientStack::of).collect(Collectors.toList());
    }

    @Override
    public List<ItemStack> getOutputs() {
        return Collections.singletonList(getOutput());
    }

    public static class InductionRecipeSerializer implements RecipeSerializer<InductionRecipe> {

        @Override
        public InductionRecipe read(Identifier id, JsonObject json) {

            List<Block> inputs = new ArrayList<>();
            Item output;
            int count;

            try {
                if(!(json.has("inputs") && json.has("output")))
                    throw new MalformedJsonException("Invalid Induction Recipe Json");

                json.getAsJsonArray("inputs").forEach( jsonElement ->
                        inputs.add(Registry.BLOCK.get(Identifier.tryParse(jsonElement.getAsString())))
                );
                output = Registry.ITEM.get(Identifier.tryParse(json.get("output").getAsString()));
                count = json.has("count") ? json.get("count").getAsInt() : 1;
            } catch (Exception e) {
                FFLog.error("Exception found while loading Induction recipe json " + id.toString() + " ", e);
                return null;
            }

            return new InductionRecipe(id, inputs.toArray(new Block[0]), new ItemStack(output, count));
        }

        @Override
        public InductionRecipe read(Identifier id, PacketByteBuf buf) {
            ItemStack output = buf.readItemStack();
            int length = buf.readInt();
            Block[] inputs = new Block[length];
            while (length > 0) {
                inputs[length - 1] = Registry.BLOCK.get(buf.readIdentifier());
                length--;
            }

            return new InductionRecipe(id, inputs, output);
        }

        @Override
        public void write(PacketByteBuf buf, InductionRecipe recipe) {
            buf.writeItemStack(recipe.output);
            buf.writeInt(recipe.inputs.length);
            Arrays.stream(recipe.inputs).forEach(block -> buf.writeIdentifier(Registry.BLOCK.getId(block)));
        }
    }
}
