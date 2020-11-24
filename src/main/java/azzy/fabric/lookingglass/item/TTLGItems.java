package azzy.fabric.lookingglass.item;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import static azzy.fabric.lookingglass.LookingGlass.*;

public class TTLGItems {

    public static void init() {}

    private static FabricItemSettings eldenmetalSettings() {
        return new FabricItemSettings().group(LOOKINGGLASS_ITEMS).fireproof().rarity(NOLL);
    }

    public static final Item DATA_SHARD = registerItem("data_shard", new DataShardItem(new Item.Settings().group(LOOKINGGLASS_ITEMS)));
    public static final Item ELDENMETAL_NUGGET = registerItem("eldenmetal_drop", new Item(eldenmetalSettings()));
    public static final Item ELDENMETAL_INGOT = registerItem("eldenmetal_tear", new Item(eldenmetalSettings()));
    
    public static final Item ELDENMETAL_GEMSTONE = registerItem("eldenmetal_gem", new Item(eldenmetalSettings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(MODID, name), item);
    }
}
