package azzy.fabric.lookingglass.item;

import azzy.fabric.lookingglass.util.datagen.ModelJsonGen;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import static azzy.fabric.lookingglass.LookingGlassCommon.*;

@SuppressWarnings("unused")
public class TTLGItems {

    public static void init() {}

    //FoodComponents
    private static final FoodComponent badNoms = new FoodComponent.Builder().alwaysEdible().hunger(2).statusEffect(new StatusEffectInstance(StatusEffects.POISON, 100), 1F).build();
    private static final FoodComponent steelhead = new FoodComponent.Builder().meat().snack().hunger(3).saturationModifier(1F).build();
    private static final FoodComponent shimmerfin = new FoodComponent.Builder().meat().hunger(8).saturationModifier(2F).alwaysEdible().statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 600, 3), 1F).statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 600, 1), 1F).statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1200, 0), 1F).build();

    private static FabricItemSettings defaultSettings() {
        return new FabricItemSettings().group(LOOKINGGLASS_ITEMS);
    }

    private static FabricItemSettings eldenmetalSettings() {
        return new FabricItemSettings().group(LOOKINGGLASS_ITEMS).fireproof().rarity(ELDENMETAL_RARITY);
    }

    //Tools
    public static final Item DATA_SHARD = registerItem("data_shard", new DataShardItem(defaultSettings()));
    public static final Item ENERGY_PROBE = registerItem("energy_probe", new EnergyProbeItem(defaultSettings()));

    //Weapons
    public static final Item STEELHEAD_TROUT = registerItem("steelhead_trout", new FishWeaponItem(false, 8, defaultSettings().rarity(Rarity.UNCOMMON).fireproof().food(steelhead)));
    public static final Item PRISMATIC_SHIMMERFIN = registerItem("shimmerfin", new FishWeaponItem(true, 199, defaultSettings().rarity(Rarity.EPIC).fireproof().food(shimmerfin)));

    //Materials
    public static final Item FISH_FEED = registerItem("fish_feed", new Item(defaultSettings().food(badNoms)));
    public static final Item DWARVEN_CLAY = registerGeneratedItem("dwarven_clay", new Item(defaultSettings()));
    public static final Item ELDENMETAL_NUGGET = registerItem("eldenmetal_drop", new Item(eldenmetalSettings()));
    public static final Item ELDENMETAL_INGOT = registerItem("eldenmetal_tear", new Item(eldenmetalSettings()));
    public static final Item ELDENMETAL_GEMSTONE = registerItem("eldenmetal_gem", new Item(eldenmetalSettings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(MODID, name), item);
    }

    private static Item registerGeneratedItem(String name, Item item) {
        Identifier id = new Identifier(MODID, name);
        if(REGEN_ITEMS)
            ModelJsonGen.genItemJson(METADATA, id);
        return Registry.register(Registry.ITEM, id, item);
    }
}
