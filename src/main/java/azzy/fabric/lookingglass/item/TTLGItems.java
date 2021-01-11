package azzy.fabric.lookingglass.item;

import azzy.fabric.lookingglass.block.NebulousSaltBlock;
import azzy.fabric.lookingglass.block.TTLGBlocks;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.MaterialColor;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.FoodComponent;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import static azzy.fabric.lookingglass.LookingGlassCommon.*;

@SuppressWarnings("unused")
public class TTLGItems {

    public static void init() {}

    //FoodComponents
    private static final FoodComponent steelhead = new FoodComponent.Builder().meat().snack().hunger(3).saturationModifier(1F).build();
    private static final FoodComponent shimmerfin = new FoodComponent.Builder().meat().hunger(8).saturationModifier(2F).alwaysEdible().statusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 600, 3), 1F).statusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 600, 1), 1F).statusEffect(new StatusEffectInstance(StatusEffects.RESISTANCE, 1200, 0), 1F).build();

    private static FabricItemSettings eldenmetalSettings() {
        return new FabricItemSettings().group(LOOKINGGLASS_ITEMS).fireproof().rarity(NULL_RARITY);
    }

    //Tools
    public static final Item DATA_SHARD = registerItem("data_shard", new DataShardItem(new Item.Settings().group(LOOKINGGLASS_ITEMS)));

    //Weapons
    public static final Item STEELHEAD_TROUT = registerItem("steelhead_trout", new FishWeaponItem(false, 8, new Item.Settings().rarity(Rarity.UNCOMMON).fireproof().food(steelhead).group(LOOKINGGLASS_ITEMS)));
    public static final Item PRISMATIC_SHIMMERFIN = registerItem("shimmerfin", new FishWeaponItem(true, 199, new Item.Settings().rarity(Rarity.EPIC).fireproof().food(shimmerfin).group(LOOKINGGLASS_ITEMS)));

    //Materials
    public static final Block NEBULOUS_SALTS = TTLGBlocks.registerBlock("nebulous_salts", new NebulousSaltBlock(FabricBlockSettings.of(Material.GLASS).sounds(BlockSoundGroup.GLASS).emissiveLighting((a, b, c) -> true).requiresTool().breakByTool(FabricToolTags.PICKAXES, 3).nonOpaque().lightLevel(7).postProcess((a, b, c) -> true).strength(20, 500)), new FabricItemSettings().fireproof().group(LOOKINGGLASS_ITEMS));
    public static final Item ELDENMETAL_NUGGET = registerItem("eldenmetal_drop", new Item(eldenmetalSettings()));
    public static final Item ELDENMETAL_INGOT = registerItem("eldenmetal_tear", new Item(eldenmetalSettings()));
    public static final Item ELDENMETAL_GEMSTONE = registerItem("eldenmetal_gem", new Item(eldenmetalSettings()));

    private static Item registerItem(String name, Item item) {
        return Registry.register(Registry.ITEM, new Identifier(MODID, name), item);
    }
}
