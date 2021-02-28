package azzy.fabric.lookingglass.item;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.blockentity.LookingGlassMachine;
import azzy.fabric.lookingglass.gui.PoweredFurnaceGuiDescription;
import azzy.fabric.lookingglass.recipe.LookingGlassRecipes;
import azzy.fabric.lookingglass.util.ModifierProvider;
import azzy.fabric.lookingglass.util.datagen.ModelJsonGen;
import dev.emi.trinkets.api.SlotGroups;
import dev.emi.trinkets.api.Slots;
import dev.emi.trinkets.api.TrinketSlots;
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
public class LookingGlassItems {

    //FoodComponents
    private static final FoodComponent BAD_NOMS = new FoodComponent.Builder().alwaysEdible().hunger(2).statusEffect(new StatusEffectInstance(StatusEffects.POISON, 100), 1F).build();
    private static final FoodComponent STEELHEAD = new FoodComponent.Builder().meat().snack().hunger(3).saturationModifier(1F).build();
    private static final FoodComponent SHIMMERFIN = new FoodComponent.Builder().meat().hunger(8).saturationModifier(2F).alwaysEdible().statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 600, 3), 1F).statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 600, 1), 1F).statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1200, 0), 1F).build();

    private static FabricItemSettings defaultSettings() {
        return new FabricItemSettings().group(LOOKINGGLASS_ITEMS);
    }

    public static void init() {
        TrinketSlots.addSlot(SlotGroups.OFFHAND, Slots.RING, new Identifier("trinkets", "textures/item/empty_trinket_slot_ring.png"));
    }

    private static FabricItemSettings genericSettings(Rarity rarity) {
        return new FabricItemSettings().group(LOOKINGGLASS_ITEMS).rarity(rarity);
    }

    private static FabricItemSettings eldenmetalSettings() {
        return new FabricItemSettings().group(LOOKINGGLASS_ITEMS).fireproof().rarity(ELDENMETAL_RARITY);
    }

    //Tools
    public static final Item DATA_SHARD = registerItem("data_shard", new DataShardItem(defaultSettings().maxCount(16)));
    public static final Item ENERGY_PROBE = registerItem("energy_probe", new EnergyProbeItem(defaultSettings().maxCount(1)));
    public static final Item BASE_RING = registerGeneratedItem("ring", new Item(defaultSettings().rarity(Rarity.UNCOMMON).maxCount(8)));
    public static final Item SIMPLE_ANGEL_RING = registerItem("simple_angel_ring", new SimpleAngelRingItem(new FabricItemSettings().group(LOOKINGGLASS_ITEMS).rarity(FINIS_RARITY).maxCount(1)));
    public static final Item ADVANCED_ANGEL_RING = registerItem("advanced_angel_ring", new AdvancedAngelRingItem(new FabricItemSettings().group(LOOKINGGLASS_ITEMS).rarity(LUPREVAN_RARITY).fireproof().maxCount(1)));

    //Upgrades
    public static final Item ROSE_CHIPSET = registerGeneratedItem("rose_chipset", new Item(defaultSettings()));
    public static final Item BASIC_SPEED_UPGRADE = registerGeneratedItem("basic_speed_upgrade", new GenericUpgradeItem(defaultSettings(), 1.2, 0, 0.95, ModifierProvider.AdditivityType.ADD, ModifierProvider.AdditivityType.ADD, LookingGlassMachine.MachineTier.BASIC));
    public static final Item BASIC_ENERGY_STORAGE_UPGRADE = registerGeneratedItem("basic_energy_storage_upgrade", new GenericUpgradeItem(defaultSettings(), 1, 0.5, 1, ModifierProvider.AdditivityType.ADD, ModifierProvider.AdditivityType.ADD, LookingGlassMachine.MachineTier.BASIC));
    public static final Item BASIC_ENERGY_USAGE_UPGRADE = registerGeneratedItem("basic_energy_usage_upgrade", new GenericUpgradeItem(defaultSettings(), 0.9, 0, 1.05, ModifierProvider.AdditivityType.ADD, ModifierProvider.AdditivityType.ADD, LookingGlassMachine.MachineTier.BASIC));
    public static final Item FREEZER_UPGRADE_ITEM = registerGeneratedItem("freezer_upgrade", new RecipeConvertingUpgradeItem<>(defaultSettings(), LookingGlassRecipes.FREEZING_RECIPE, PoweredFurnaceGuiDescription.class, 2.0, 0, 0.5, ModifierProvider.AdditivityType.ADD, ModifierProvider.AdditivityType.ADD));

    //Materials
    public static final Item FISH_FEED = registerItem("fish_feed", new Item(defaultSettings().food(BAD_NOMS)));
    public static final Item DWARVEN_CLAY = registerGeneratedItem("dwarven_clay", new Item(defaultSettings()));
    public static final Item RED_SILICON_INGOT = registerGeneratedItem("red_silicon_ingot", new Item(defaultSettings()));
    public static final Item ROSE_GOLD_INGOT = registerGeneratedItem("rose_gold_ingot", new Item(defaultSettings()));
    public static final Item ROSE_GOLD_NUGGET = registerGeneratedItem("rose_gold_nugget", new Item(defaultSettings()));
    public static final Item CELESTIAL_AMALGAM = registerGeneratedItem("celestial_amalgam", new Item(genericSettings(FINIS_RARITY)));
    public static final Item FINIS_INGOT = registerGeneratedItem("finis_ingot", new Item(genericSettings(FINIS_RARITY)));
    public static final Item FINIS_NUGGET = registerGeneratedItem("finis_nugget", new Item(genericSettings(FINIS_RARITY)));
    public static final Item ELDENMETAL_INGOT = registerItem("eldenmetal_tear", new Item(eldenmetalSettings()));
    public static final Item ELDENMETAL_NUGGET = registerItem("eldenmetal_drop", new Item(eldenmetalSettings()));
    public static final Item ELDENMETAL_GEMSTONE = registerItem("eldenmetal_gem", new Item(eldenmetalSettings()));

    //Weapons
    public static final Item STEELHEAD_TROUT = registerItem("steelhead_trout", new FishWeaponItem(false, 8, defaultSettings().rarity(Rarity.UNCOMMON).fireproof().food(STEELHEAD)));
    public static final Item PRISMATIC_SHIMMERFIN = registerItem("shimmerfin", new FishWeaponItem(true, 199, defaultSettings().rarity(WORLDFORGE_RARITY).fireproof().food(SHIMMERFIN)));

    //Registry shenanigans
    public static final Item ANGEL_BLOCK = registerItem("angel_block", new AngelBlockItem(LookingGlassBlocks.ANGEL_BLOCK, new FabricItemSettings().group(LOOKINGGLASS_BLOCKS).fireproof()));
    public static final Item CURSED_EARTH_BLOCK = registerItem("cursed_earth", new CursedEarthBlockItem(LookingGlassBlocks.CURSED_EARTH_BLOCK, new FabricItemSettings().group(LOOKINGGLASS_BLOCKS)));
    public static final Item WOODEN_SPIKE_BLOCK = registerItem("wooden_spike", new WoodenSpikeBlockItem(LookingGlassBlocks.WOODEN_SPIKE_BLOCK, new FabricItemSettings().group(LOOKINGGLASS_BLOCKS)));
    public static final Item IRON_SPIKE_BLOCK = registerItem("iron_spike", new IronSpikeBlockItem(LookingGlassBlocks.IRON_SPIKE_BLOCK, new FabricItemSettings().group(LOOKINGGLASS_BLOCKS)));
    public static final Item DIAMOND_SPIKE_BLOCK = registerItem("diamond_spike", new DiamondSpikeBlockItem(LookingGlassBlocks.DIAMOND_SPIKE_BLOCK, new FabricItemSettings().group(LOOKINGGLASS_BLOCKS)));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(MODID, name), item);
    }

    private static Item registerGeneratedItem(String name, Item item) {
        Identifier id = new Identifier(MODID, name);
        if (REGEN_ITEMS)
            ModelJsonGen.genItemJson(METADATA, id);
        return Registry.register(Registry.ITEM, id, item);
    }
}