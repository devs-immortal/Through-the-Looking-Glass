package azzy.fabric.lookingglass.util.json;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.Identifier;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class JsonUtils {

    private static final JsonParser PARSER = new JsonParser();

    public static JsonObject fromInputStream(InputStream in) {
        return PARSER.parse(new JsonReader(new InputStreamReader(in, StandardCharsets.UTF_8))).getAsJsonObject();
    }

    public static ItemStack stackFromJson(JsonObject element) {
        Item item = Registry.ITEM.get(Identifier.tryParse(element.get("item").getAsString()));
        int count = element.has("count") ? element.get("count").getAsInt() : 1;
        return item != Items.AIR ? new ItemStack(item, count) : ItemStack.EMPTY;
    }

    public static DefaultedList<Ingredient> ingredientsFromJson(JsonArray elements, int size) {
         DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(size, Ingredient.EMPTY);
        for (int i = 0; i < elements.size() && i < size; i++) {
            ingredients.set(i, Ingredient.fromJson(elements.get(i)));
        }
        return ingredients;
    }
}
