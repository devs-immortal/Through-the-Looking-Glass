package azzy.fabric.lookingglass.util;

import net.minecraft.network.PacketByteBuf;

import java.util.ArrayList;
import java.util.List;

public class BufUtils {

    public static List<IngredientStack> ingredientsFromBuf(PacketByteBuf buf) {
        int size = buf.readInt();
        List<IngredientStack> ingredients = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            ingredients.set(i, IngredientStack.fromByteBuf(buf));
        }
        return ingredients;
    }

    public static void ingredientsToBuf(List<IngredientStack> ingredients, PacketByteBuf buf) {
        buf.writeInt(ingredients.size());
        ingredients.forEach(ingredient -> ingredient.write(buf));
    }
}
