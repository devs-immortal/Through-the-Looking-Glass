package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.*;
import azzy.fabric.lookingglass.util.LookingGlassSounds;
import azzy.fabric.lookingglass.util.datagen.BSJsonGen;
import azzy.fabric.lookingglass.util.datagen.LootGen;
import azzy.fabric.lookingglass.util.datagen.ModelJsonGen;
import azzy.fabric.lookingglass.util.datagen.RecipeJsonGen;
import dev.technici4n.fasttransferlib.api.energy.EnergyApi;
import dev.technici4n.fasttransferlib.api.energy.EnergyIo;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

import static azzy.fabric.lookingglass.LookingGlassCommon.*;

@SuppressWarnings("unused")
public class LookingGlassBlocks {

    private static FabricItemSettings basicItem() {
        return genericItem(Rarity.COMMON, 64);
    }

    private static FabricItemSettings genericItem(Rarity rarity, int maxCount) {
        return new FabricItemSettings().rarity(rarity).maxCount(maxCount).group(LOOKINGGLASS_BLOCKS);
    }

    private static FabricBlockSettings woodenMachine(int miningLevel) {
        return FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).breakByTool(FabricToolTags.AXES, miningLevel).requiresTool().strength(2f);
    }

    private static FabricBlockSettings basicMachine(BlockSoundGroup sounds) {
        return FabricBlockSettings.of(Material.STONE).sounds(sounds).breakByTool(FabricToolTags.PICKAXES, 2).requiresTool().strength(3.25f, 12f);
    }

    private static FabricBlockSettings dwarvenMachine() {
        return FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.NETHER_BRICKS).breakByTool(FabricToolTags.PICKAXES, 2).requiresTool().strength(3.25f, 12f);
    }

    private static FabricBlockSettings hardenedMachine() {
        return FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.ANCIENT_DEBRIS).breakByTool(FabricToolTags.PICKAXES, 3).requiresTool().strength(4f, 20f);
    }

    private static FabricBlockSettings paleMachine() {
        return FabricBlockSettings.of(Material.METAL).nonOpaque().sounds(BlockSoundGroup.GLASS).breakByTool(FabricToolTags.PICKAXES, 1).strength(1f, 8f);
    }
    private static FabricBlockSettings finisMachine() {
        return FabricBlockSettings.of(Material.AGGREGATE).sounds(LookingGlassSounds.FINIS).breakByTool(FabricToolTags.PICKAXES, 4).strength(5f, 10f);
    }
    private static FabricBlockSettings eldenMachine() {
        return FabricBlockSettings.of(Material.METAL).sounds(LookingGlassSounds.ELDENMETAL).breakByTool(FabricToolTags.PICKAXES, 4).requiresTool().strength(3f, 800f);
    }

    //  BLOCKS
    //Machines
    public static final Block PROJECTORBLOCK = registerBlock("projector", new ProjectorBlock(paleMachine().luminance(ignored -> 7)), basicItem());
    public static final Block CHUNKLOADERBLOCK = registerBlock("chunkloader", new ChunkAnchorBlock(paleMachine().luminance(ignored -> 7)), basicItem());
    public static final Block WORMHOLEBLOCK = registerBlock("wormhole", new WormholeBlock(paleMachine().luminance(ignored -> 5)), basicItem());
    public static final Block ADVANCED_VACUUM_HOPPER_BLOCK = registerBlock("advanced_vacuum_hopper", new VacuumHopperBlock(hardenedMachine(), 5, 7, 27), genericItem(Rarity.RARE, 64));
    public static final Block BLOCK_TESSERACT_BLOCK = registerBlock("block_tesseract", new BlockTesseractBlock(eldenMachine().nonOpaque()), genericItem(FINIS_RARITY, 64));
    public static final Block BLOCK_INDUCTOR_BLOCK = registerBlock("block_inductor", new BlockInductorBlock(basicMachine(BlockSoundGroup.STONE).nonOpaque().luminance(state -> state.get(AbstractInductorBlock.POWERED) ? 15 : 5)), basicItem());

    public static final Block DWARVEN_MACHINE_CORE = registerGeneratedBlock("dwarven_machine_core", new Block(dwarvenMachine()), null, null, basicItem(), SingletType.BLOCK);
    public static final Block POWERED_FURNACE_BLOCK = registerBlock("powered_furnace", new PoweredFurnaceBlock(dwarvenMachine()), basicItem());
    public static final Block ALLOY_FURNACE_BLOCK = registerBlock("alloy_furnace", new AlloyFurnaceBlock(dwarvenMachine()), basicItem());
    public static final Block GRINDER_BLOCK = registerBlock("grinder", new GrinderBlock(dwarvenMachine()), basicItem());
    public static final Block MIXER_BLOCK = registerBlock("mixer", new MixerBlock(dwarvenMachine()), basicItem());
    public static final Block VACUUM_HOPPER_BLOCK = registerBlock("vacuum_hopper", new VacuumHopperBlock(dwarvenMachine(), 20, 4, 9), basicItem());
    // Vector plates move entities around.
    public static final Block SLOW_VECTOR_PLATE_BLOCK = Registry.register(Registry.BLOCK, new Identifier(MODID, "slow_vector_plate"), new VectorPlateBlock(FabricBlockSettings.copyOf(Blocks.OBSIDIAN), 1));
    public static final Block NORMAL_VECTOR_PLATE_BLOCK = Registry.register(Registry.BLOCK, new Identifier(MODID, "normal_vector_plate"), new VectorPlateBlock(FabricBlockSettings.copyOf(Blocks.OBSIDIAN), 2));
    public static final Block FAST_VECTOR_PLATE_BLOCK = Registry.register(Registry.BLOCK, new Identifier(MODID, "fast_vector_plate"), new VectorPlateBlock(FabricBlockSettings.copyOf(Blocks.OBSIDIAN), 4));

    //Power
    public static final Block CREATIVE_ENERGY_SOURCE_BLOCK = registerBlock("creative_energy_source", new CreativeEnergySourceBlock(paleMachine().nonOpaque().luminance(7).emissiveLighting((state, world, pos) -> true)), genericItem(WORLDFORGE_RARITY, 1));
    public static final Block SILICON_CABLE_BLOCK = registerBlock("silicon_cable", new SiliconCableBlock(basicMachine(BlockSoundGroup.CHAIN).nonOpaque()), basicItem());
    public static final Block GUILDED_CABLE_BLOCK = registerBlock("guilded_cable", new GuildedCableBlock(basicMachine(BlockSoundGroup.CHAIN).nonOpaque()), genericItem(Rarity.UNCOMMON, 64));
    public static final Block ENCHANTED_CABLE_BLOCK = registerBlock("enchanted_cable", new EnchantedCableBlock(hardenedMachine().nonOpaque().sounds(BlockSoundGroup.CHAIN)), genericItem(Rarity.RARE, 64));
    public static final Block NULL_CABLE_BLOCK = registerBlock("null_cable", new NullCableBlock(eldenMachine().nonOpaque().sounds(BlockSoundGroup.CHAIN)), genericItem(ELDENMETAL_RARITY, 64));

    //Devices
    public static final Block FISH_BREEDER_BLOCK = registerBlock("fish_breeder", new FishBreederBlock(woodenMachine(1).nonOpaque().ticksRandomly()), basicItem());
    public static final Block CRATE_BLOCK = registerBlock("crate", new CrateBlock(woodenMachine(1)), basicItem());
    public static final Block WOODEN_SPIKE_BLOCK = Registry.register(Registry.BLOCK, new Identifier(MODID, "wooden_spike"), new SpikesBlock(FabricBlockSettings.copyOf(Blocks.OAK_WOOD)));
    public static final Block IRON_SPIKE_BLOCK = Registry.register(Registry.BLOCK, new Identifier(MODID, "iron_spike"), new SpikesBlock(FabricBlockSettings.copyOf(Blocks.IRON_BLOCK)));
    public static final Block DIAMOND_SPIKE_BLOCK = Registry.register(Registry.BLOCK, new Identifier(MODID, "diamond_spike"), new SpikesBlock(FabricBlockSettings.copyOf(Blocks.OBSIDIAN)));
    public static final Block NETHERITE_SPIKE_BLOCK = Registry.register(Registry.BLOCK, new Identifier(MODID, "netherite_spike"), new SpikesBlock(FabricBlockSettings.copyOf(Blocks.OBSIDIAN)));


    //Decorative
    public static final Block[] ADOBE_BRICK_SET = registerBuildingBlocks("adobe_bricks", FabricBlockSettings.copyOf(Blocks.BRICKS).materialColor(MaterialColor.DIRT), basicItem(), Items.AIR, false);
    public static final Block[] GOLD_BRICK_SET = registerBuildingBlocks("gold_bricks", FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).sounds(BlockSoundGroup.CHAIN), genericItem(Rarity.UNCOMMON, 64), Items.GOLD_INGOT, false);
    public static final Block[] SAND_BRICK_SET = registerBuildingBlocks("sand_bricks", FabricBlockSettings.copyOf(Blocks.SANDSTONE).sounds(BlockSoundGroup.SAND), basicItem(), Items.AIR, false);
    public static final Block[] SOULSAND_BRICK_SET = registerBuildingBlocks("soulsand_bricks", FabricBlockSettings.copyOf(Blocks.SANDSTONE).sounds(BlockSoundGroup.SOUL_SAND), basicItem(), Items.AIR, false);
    public static final Block WHITESTONE_BLOCK = registerGeneratedBlock("whitestone", new Block(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.BONE).materialColor(MaterialColor.WHITE)), null, null, basicItem(), SingletType.BLOCK);
    public static final Block[] WHISTONE_POLISHED = registerBuildingBlocks("polished_whitestone",FabricBlockSettings.copyOf(WHITESTONE_BLOCK), basicItem(), Items.AIR, false);
    public static final Block WHITESTONE_TILE = registerGeneratedBlock("whitestone_tile", new Block(FabricBlockSettings.copyOf(WHITESTONE_BLOCK)), null, null, basicItem(), SingletType.BLOCK);
    public static final Block[] WHITESTONE_BRICK_SET = registerBuildingBlocks("whitestone_bricks", FabricBlockSettings.copyOf(WHITESTONE_BLOCK), basicItem(), WHISTONE_POLISHED[0].asItem(), false);
    public static final Block[] WHITESTONE_LARGE_BRICK_SET = registerBuildingBlocks("large_whitestone_bricks", FabricBlockSettings.copyOf(WHITESTONE_BLOCK), basicItem(), WHITESTONE_TILE.asItem(), false);
    public static final Block[] BASALT_BRICK_SET = registerBuildingBlocks("basalt_bricks", FabricBlockSettings.copyOf(Blocks.POLISHED_BASALT), basicItem(), Items.POLISHED_BASALT, false);
    public static final Block HERRINGBONE_OAK_PLANKS = registerBlock("herringbone_oak_planks", new HerringboneWoodBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)), basicItem());
    public static final Block DARK_GLASS = registerGeneratedBlock("dark_glass", new EtherealGlassBlock(FabricBlockSettings.copyOf(Blocks.GLASS), GlassBlockTypes.DARK_GLASS), null, null, basicItem(), SingletType.BLOCK);
    public static final Block ETHEREAL_GLASS = registerGeneratedBlock("ethereal_glass", new EtherealGlassBlock(FabricBlockSettings.copyOf(Blocks.GLASS).blockVision((state, world, pos) -> false), GlassBlockTypes.ETHEREAL), null, null, basicItem(), SingletType.BLOCK);
    public static final Block DARK_ETHEREAL_GLASS = registerGeneratedBlock("dark_ethereal_glass", new EtherealGlassBlock(FabricBlockSettings.copyOf(Blocks.GLASS).blockVision((state, world, pos) -> false), GlassBlockTypes.DARK_ETHEREAL), null, null, basicItem(), SingletType.BLOCK);
    public static final Block REVERSE_ETHEREAL_GLASS = registerGeneratedBlock("reverse_ethereal_glass", new EtherealGlassBlock(FabricBlockSettings.copyOf(Blocks.GLASS).blockVision((state, world, pos) -> false), GlassBlockTypes.REVERSE_ETHEREAL), null, null, basicItem(), SingletType.BLOCK);
    public static final Block REVERSE_DARK_ETHEREAL_GLASS = registerGeneratedBlock("dark_reverse_ethereal_glass", new EtherealGlassBlock(FabricBlockSettings.copyOf(Blocks.GLASS).blockVision((state, world, pos) -> false), GlassBlockTypes.DARK_REVERSE_ETHEREAL), null, null, basicItem(), SingletType.BLOCK);
    public static final Block GHOST_GLASS = registerGeneratedBlock("ghost_glass", new EtherealGlassBlock(FabricBlockSettings.copyOf(Blocks.GLASS).blockVision((state, world, pos) -> false), GlassBlockTypes.GHOST_GLASS), null, null, basicItem(), SingletType.BLOCK);
    public static final Block DARK_GHOST_GLASS = registerGeneratedBlock("dark_ghost_glass", new EtherealGlassBlock(FabricBlockSettings.copyOf(Blocks.GLASS).blockVision((state, world, pos) -> false), GlassBlockTypes.DARK_GHOST_GLASS), null, null, basicItem(), SingletType.BLOCK);
    public static final Block GLOW_GLASS = registerGeneratedBlock("glow_glass", new EtherealGlassBlock(FabricBlockSettings.copyOf(Blocks.GLASS).lightLevel(15), GlassBlockTypes.GLOW_GLASS), null, null, basicItem(), SingletType.BLOCK);
    public static final Block RED_GLASS = registerGeneratedBlock("red_glass", new EtherealGlassBlock(FabricBlockSettings.copyOf(Blocks.GLASS), GlassBlockTypes.RED_GLASS), null, null, basicItem(), SingletType.BLOCK);

    //Cores
    public static final Block INTERMINAL_CORE = registerBlock("interminal_core", new InterminalCoreBlock(FabricBlockSettings.copyOf(Blocks.CRYING_OBSIDIAN)), basicItem().fireproof());
    public static final Block GROWTH_CORE_0 = registerBlock("growth_core_0", new GrowthCoreBlock(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).breakByTool(FabricToolTags.HOES, 0).sounds(BlockSoundGroup.FUNGUS), 1, 1, 0), basicItem().fireproof());
    public static final Block GROWTH_CORE_1 = registerBlock("growth_core_1", new GrowthCoreBlock(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).breakByTool(FabricToolTags.HOES, 0).sounds(BlockSoundGroup.FUNGUS), 2, 1, 0), basicItem().fireproof());
    public static final Block GROWTH_CORE_2 = registerBlock("growth_core_2", new GrowthCoreBlock(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).breakByTool(FabricToolTags.HOES, 0).sounds(BlockSoundGroup.FUNGUS), 3, 2, 0), basicItem().rarity(Rarity.UNCOMMON).fireproof());
    public static final Block GROWTH_CORE_3 = registerBlock("growth_core_3", new GrowthCoreBlock(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).breakByTool(FabricToolTags.HOES, 0).sounds(BlockSoundGroup.FUNGUS), 4, 2, 0), genericItem(Rarity.RARE, 64).fireproof());
    public static final Block GROWTH_CORE_4 = registerBlock("growth_core_4", new GrowthCoreBlock(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).breakByTool(FabricToolTags.HOES, 4).sounds(BlockSoundGroup.FUNGUS), 4, 3, 1), genericItem(Rarity.EPIC, 64).fireproof());
    public static final Block TRUE_GROWTH_CORE_0 = registerBlock("growth_core_5", new GrowthCoreBlock(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).breakByTool(FabricToolTags.HOES, 4).sounds(BlockSoundGroup.FUNGUS), 4, 4, 1), genericItem(LUPREVAN_RARITY, 64));
    public static final Block TRUE_GROWTH_CORE_1 = registerBlock("growth_core_6", new GrowthCoreBlock(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).breakByTool(FabricToolTags.HOES, 4).sounds(BlockSoundGroup.FUNGUS), 9, 4, 2), genericItem(LUPREVAN_RARITY, 64));
    public static final Block TRUE_GROWTH_CORE_2 = registerBlock("growth_core_7", new GrowthCoreBlock(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).breakByTool(FabricToolTags.HOES, 4).sounds(BlockSoundGroup.FUNGUS), 13, 5, 4), genericItem(LUPREVAN_RARITY, 64));
    public static final Block ANNULATION_CORE_0 = registerBlock("annulation_core_0", new AnnulationCoreBlock(FabricBlockSettings.copyOf(Blocks.STONE_BRICKS), 1, false, DamageSource.MAGIC), basicItem());
    public static final Block ANNULATION_CORE_1A = registerBlock("annulation_core_1a", new AnnulationCoreBlock(FabricBlockSettings.copyOf(Blocks.GILDED_BLACKSTONE), 2, true, DamageSource.OUT_OF_WORLD), genericItem(LUPREVAN_RARITY, 64));
    public static final Block ANNULATION_CORE_2B = registerBlock("annulation_core_2b", new SpecialAnnullationCoreBlock(eldenMachine()), genericItem(ELDENMETAL_RARITY, 64));

    //Misc
    public static final Block DWARVEN_STONE = registerGeneratedBlock("dwarven_stone", new Block(FabricBlockSettings.copyOf(Blocks.STONE).resistance(50).materialColor(DyeColor.GRAY).sounds(BlockSoundGroup.NETHER_BRICKS)), null, null, basicItem(), SingletType.BLOCK);
    public static final Block SILICON_BLOCK = registerGeneratedBlock("silicon_block", new Block(FabricBlockSettings.copyOf(Blocks.REDSTONE_BLOCK).materialColor(DyeColor.RED)), null, null, basicItem(), SingletType.BLOCK);
    public static final Block ROSE_GOLD_BLOCK = registerGeneratedBlock("rose_gold_block", new Block(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).materialColor(DyeColor.PINK)), null, null, basicItem(), SingletType.BLOCK);
    public static final Block PINK_GEL_BLOCK = registerGeneratedBlock("pink_gel_block", new Block(FabricBlockSettings.copyOf(Blocks.SLIME_BLOCK).materialColor(DyeColor.PINK)), null, null, genericItem(Rarity.UNCOMMON, 64), SingletType.BLOCK);
    public static final Block NEBULOUS_HALITE = registerBlock("nebulous_halite", new NebulousHaliteBlock(FabricBlockSettings.of(Material.GLASS).sounds(BlockSoundGroup.GLASS).emissiveLighting((a, b, c) -> true).requiresTool().breakByTool(FabricToolTags.PICKAXES, 3).nonOpaque().luminance(9).postProcess((a, b, c) -> true).strength(25, 500)), basicItem().fireproof().group(LOOKINGGLASS_BLOCKS));
    public static final Block NEBULOUS_SALTS = LookingGlassBlocks.registerBlock("nebulous_salts", new NebulousSaltBlock(FabricBlockSettings.of(Material.GLASS).sounds(BlockSoundGroup.GLASS).emissiveLighting((a, b, c) -> true).requiresTool().breakByTool(FabricToolTags.PICKAXES, 3).nonOpaque().luminance(7).postProcess((a, b, c) -> true).strength(20, 500)), basicItem().fireproof().group(LOOKINGGLASS_BLOCKS));
    public static final Block FINIS_BLOCK = LookingGlassBlocks.registerGeneratedBlock("finis_block", new Block(finisMachine()), null, null, genericItem(FINIS_RARITY, 64), SingletType.BLOCK);
    public static final Block ELDENMETAL_BLOCK = registerBlock("eldenmetal_block", new Block(FabricBlockSettings.copyOf(Blocks.OBSIDIAN).luminance(state -> 3).nonOpaque().sounds(LookingGlassSounds.ELDENMETAL)), basicItem().fireproof().rarity(ELDENMETAL_RARITY));
    public static final Block LUPREVAN_GLASS_BLOCK = registerGeneratedBlock("luprevan_glass_block", new Block(FabricBlockSettings.copyOf(Blocks.GLASS).strength(8, 500)), null, null, genericItem(LUPREVAN_RARITY, 64), SingletType.BLOCK);
    public static final Block SALMON_EGGS = registerBlock("salmon_egg", new SalmonEggBlock(FabricBlockSettings.copyOf(Blocks.TURTLE_EGG).sounds(BlockSoundGroup.HONEY).ticksRandomly()), basicItem());
    public static final Block ANGEL_BLOCK = Registry.register(Registry.BLOCK, new Identifier(MODID, "angel_block"), new AngelBlock(FabricBlockSettings.copyOf(Blocks.DIRT).sounds(BlockSoundGroup.SHROOMLIGHT).breakInstantly()));
    public static final Block CURSED_EARTH_BLOCK = Registry.register(Registry.BLOCK, new Identifier(MODID, "cursed_earth"), new CursedEarthBlock(FabricBlockSettings.copyOf(Blocks.GRASS_BLOCK).ticksRandomly()));

    //  BLOCK ENTITIES
    //Machines
    public static final BlockEntityType<ProjectorEntity> PROJECTORENTITY = registerEntity("projector_entity", ProjectorEntity::new, PROJECTORBLOCK);
    public static final BlockEntityType<ChunkAnchorEntity> CHUNKLOADER_ENTITY = registerEntity("chunkloader_entity", ChunkAnchorEntity::new, CHUNKLOADERBLOCK);
    public static final BlockEntityType<WormholeEntity> WORMHOLE_ENTITY = registerEntity("wormhole_entity", WormholeEntity::new, WORMHOLEBLOCK);
    public static final BlockEntityType<SpecialAnnulationCoreEntity> SPECIAL_ANNULATION_CORE_ENTITY = registerEntity("special_annulation_core_entity", SpecialAnnulationCoreEntity::new, ANNULATION_CORE_2B);
    public static final BlockEntityType<VacuumHopperEntity> VACUUM_HOPPER_ENTITY = registerEntity("vacuum_hopper_entity", () -> new VacuumHopperEntity(LookingGlassBlocks.VACUUM_HOPPER_ENTITY, 9, 20, 4), VACUUM_HOPPER_BLOCK);
    public static final BlockEntityType<VacuumHopperEntity> ADVANCED_VACUUM_HOPPER_ENTITY = registerEntity("advanced_vacuum_hopper_entity", () -> new VacuumHopperEntity(LookingGlassBlocks.ADVANCED_VACUUM_HOPPER_ENTITY, 27, 5, 8), ADVANCED_VACUUM_HOPPER_BLOCK);

    public static final BlockEntityType<PoweredFurnaceEntity> POWERED_FURNACE_ENTITY = registerEntity("powered_furnace_entity", PoweredFurnaceEntity::new, POWERED_FURNACE_BLOCK);
    public static final BlockEntityType<AlloyFurnaceEntity> ALLOY_FURNACE_ENTITY = registerEntity("alloy_furnace_entity", AlloyFurnaceEntity::new, ALLOY_FURNACE_BLOCK);
    public static final BlockEntityType<GrinderEntity> GRINDER_ENTITY = registerEntity("grinder_entity", GrinderEntity::new, GRINDER_BLOCK);
    public static final BlockEntityType<MixerEntity> MIXER_ENTITY = registerEntity("mixer_entity", MixerEntity::new, MIXER_BLOCK);

    //Power
    public static final BlockEntityType<CreativeEnergySourceEntity> CREATIVE_ENERGY_SOURCE_ENTITY = registerEntity("creative_energy_source_entity", CreativeEnergySourceEntity::new, CREATIVE_ENERGY_SOURCE_BLOCK);
    public static final BlockEntityType<SiliconCableEntity> SILICON_CABLE_ENTITY = registerEntity("silicon_cable_entity", SiliconCableEntity::new, SILICON_CABLE_BLOCK);
    public static final BlockEntityType<GuildedCableEntity> GUILDED_CABLE_ENTITY = registerEntity("guilded_cable_entity", GuildedCableEntity::new, GUILDED_CABLE_BLOCK);
    public static final BlockEntityType<EnchantedCableEntity> ENCHANTED_CABLE_ENTITY = registerEntity("enchanted_cable_entity", EnchantedCableEntity::new, ENCHANTED_CABLE_BLOCK);
    public static final BlockEntityType<NullCableEntity> NULL_CABLE_ENTITY = registerEntity("null_cable_entity", NullCableEntity::new, NULL_CABLE_BLOCK);

    //Devices
    public static final BlockEntityType<FishBreederEntity> FISH_BREEDER_ENTITY = registerEntity("fish_breeder_entity", FishBreederEntity::new, FISH_BREEDER_BLOCK);
    public static final BlockEntityType<CrateEntity> CRATE_ENTITY = registerEntity("crate_entity", CrateEntity::new, CRATE_BLOCK);

    //Tesseracts
    public static final BlockEntityType<BlockTesseractEntity> BLOCK_TESSERACT_ENTITY = registerEntity("block_tesseract", BlockTesseractEntity::new, BLOCK_TESSERACT_BLOCK);

    public static void init() {
        EnergyApi.SIDED.registerForBlockEntities((blockEntity, direction) -> {
            if(!((PowerPipeEntity) blockEntity).powered())
                return (EnergyIo) blockEntity;
            return null;
        }, SILICON_CABLE_ENTITY, GUILDED_CABLE_ENTITY, ENCHANTED_CABLE_ENTITY, NULL_CABLE_ENTITY);

        EnergyApi.SIDED.registerForBlockEntities((blockEntity, direction) -> (EnergyIo) blockEntity,
                CREATIVE_ENERGY_SOURCE_ENTITY, POWERED_FURNACE_ENTITY, ALLOY_FURNACE_ENTITY, GRINDER_ENTITY, MIXER_ENTITY);
    }

    public static Block registerBlock(String name, Block item, Item.Settings settings, boolean genLoot) {
        Identifier id =  new Identifier(MODID, name);
        Block block = Registry.register(Registry.BLOCK, id, item);
        Registry.register(Registry.ITEM, id, new BlockItem(block, settings));

        if(genLoot && DEV_ENV && REGEN_LOOT)
            LootGen.genSimpleBlockDropTable(METADATA, block);

        return block;
    }

    public static Block registerBlock(String name, Block item, Item.Settings settings) {
        return registerBlock(name, item, settings, true);
    }

    public static Block registerGeneratedBlock(String name, Block item, @Nullable Identifier parent, @Nullable Identifier texture, Item.Settings settings, SingletType type) {
        Identifier id =  new Identifier(MODID, name);
        Block block = Registry.register(Registry.BLOCK, id, item);
        Registry.register(Registry.ITEM, id, new BlockItem(block, settings));

        if(DEV_ENV) {
            if(REGEN_BLOCKS) {
                Identifier texId = texture == null ? new Identifier(MODID, "block/" + name) : texture;

                switch (type) {
                    case BLOCK:
                        BSJsonGen.genBlockBS(METADATA, id, "block/");
                        ModelJsonGen.genBlockJson(METADATA, texId, new Identifier(MODID, name), "");
                        break;
                    case SLAB:
                        BSJsonGen.genSlabBS(METADATA, id, Objects.requireNonNull(parent),"block/");
                        ModelJsonGen.genSlabJsons(METADATA, texId, new Identifier(MODID, name), "");
                        break;
                    case STAIRS:
                        BSJsonGen.genStairsBS(METADATA, id, "block/");
                        ModelJsonGen.genStairJsons(METADATA, texId, new Identifier(MODID, name), "");
                        break;
                    case PILLAR:
                        //BSJsonGen.genStairsBS(METADATA, id, "block/");
                        //ModelJsonGen.genStairJsons(METADATA, texId, new Identifier(MODID, name), "");
                        break;
                    case WALL:
                        BSJsonGen.genWallBS(METADATA, id, "block/");
                        ModelJsonGen.genWallJsons(METADATA, texId, new Identifier(MODID, name), "");
                        break;
                    case FENCE:
                        break;
                    default:
                }
            }
            if(REGEN_LOOT) {
                LootGen.genSimpleBlockDropTable(METADATA, block);
            }
        }

        return block;
    }

    public static Block[] registerBuildingBlocks(String baseName, FabricBlockSettings blockSettings, Item.Settings itemSettings, Item baseIngredient, boolean nines) {

        Block parent = registerBlock(baseName, new Block(blockSettings), itemSettings);

        Block[] blocks = new Block[]{
                parent,
                registerBlock(baseName + "_slab", new SlabBlock(blockSettings), itemSettings),
                registerBlock(baseName + "_stairs", new AltStairsBlock(parent, blockSettings), itemSettings),
                registerBlock(baseName + "_wall", new WallBlock(blockSettings), itemSettings)
        };

        if(DEV_ENV) {
            Identifier texId = new Identifier(MODID, "block/" + baseName);
            Identifier parentId = new Identifier(MODID, baseName);

            BSJsonGen.genBlockBS(METADATA, parentId, "block/");
            BSJsonGen.genSlabBS(METADATA, new Identifier(MODID, baseName + "_slab"), parentId, "block/");
            BSJsonGen.genStairsBS(METADATA, new Identifier(MODID, baseName + "_stairs"), "block/");
            BSJsonGen.genWallBS(METADATA, new Identifier(MODID, baseName + "_wall"), "block/");
            ModelJsonGen.genBlockJson(METADATA, texId, new Identifier(MODID, baseName), "");
            ModelJsonGen.genSlabJsons(METADATA, texId, new Identifier(MODID, baseName + "_slab"), "");
            ModelJsonGen.genStairJsons(METADATA, texId, new Identifier(MODID, baseName + "_stairs"), "");
            ModelJsonGen.genWallJsons(METADATA, texId, new Identifier(MODID, baseName + "_wall"), "");
        }

        if(baseIngredient != null && REGEN_RECIPES)
            registerBuildingRecipes(baseName, blocks, baseIngredient, nines);

        return blocks;
    }

    public static void registerBuildingRecipes(String baseName, Block[] blocks, Item baseIngredient, boolean nines) {
        if(baseIngredient != Items.AIR)  {
            if(nines) {
                RecipeJsonGen.gen3x3Recipe(METADATA, baseName, baseIngredient, blocks[0].asItem(), 9);
            }
            else {
                RecipeJsonGen.gen2x2Recipe(METADATA, baseName, baseIngredient, blocks[0].asItem(), 4);
            }
        }
        RecipeJsonGen.genSlabRecipe(METADATA, baseName + "_slabs", blocks[0].asItem(), blocks[1].asItem(), 6);
        RecipeJsonGen.genStairsRecipe(METADATA, baseName + "_stairs", blocks[0].asItem(), blocks[2].asItem(), 8);
        RecipeJsonGen.genWallRecipe(METADATA, baseName + "_walls", blocks[0].asItem(), blocks[3].asItem(), 6);
    }

    private static <T extends BlockEntity> BlockEntityType<T> registerEntity(String name, Supplier<T> item, Block block){
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, name), BlockEntityType.Builder.create(item, block).build(null));
    }

    enum SingletType {
        BLOCK,
        SLAB,
        STAIRS,
        PILLAR,
        WALL,
        FENCE,
        NONE
    }

    private enum WallType {
        WALL,
        FENCE,
        NONE
    }
}
