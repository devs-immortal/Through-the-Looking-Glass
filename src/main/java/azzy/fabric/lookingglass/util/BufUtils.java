package azzy.fabric.lookingglass.util;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.util.collection.DefaultedList;

public class BufUtils {

    public static DefaultedList<Ingredient> ingredientsFromBuf(PacketByteBuf buf) {
        DefaultedList<Ingredient> ingredients = DefaultedList.ofSize(buf.readInt(), Ingredient.EMPTY);
        for (int i = 0; i < ingredients.size(); i++) {
            ingredients.set(i, Ingredient.fromPacket(buf));
        }
        return ingredients;
    }

    public static void ingredientsToBuf(DefaultedList<Ingredient> ingredients, PacketByteBuf buf) {
        buf.writeInt(ingredients.size());
        ingredients.forEach(ingredient -> ingredient.write(buf));
    }
}
