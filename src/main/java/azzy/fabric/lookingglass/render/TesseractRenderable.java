package azzy.fabric.lookingglass.blockentity;

import net.minecraft.item.ItemStack;
import org.apache.commons.lang3.tuple.Triple;

public interface TesseractRenderable {

    boolean shouldRenderCore();
    boolean shouldRender();

    ItemStack getCoreItem();

    Triple<Float, Float, Float> getColor();
}
