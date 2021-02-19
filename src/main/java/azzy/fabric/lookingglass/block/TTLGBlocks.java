package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.blockentity.*;
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
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
import java.util.function.Supplier;

import static azzy.fabric.lookingglass.LookingGlassCommon.*;

@SuppressWarnings("unused")
public class TTLGBlocks {

    private static FabricItemSettings basicMachineItem() {
        return new FabricItemSettings().group(LOOKINGGLASS_BLOCKS);
    }

    private static FabricItemSettings midtierMachineItem() {
        return new FabricItemSettings().group(LOOKINGGLASS_BLOCKS).rarity(Rarity.UNCOMMON);
    }
    private static FabricItemSettings advancedMachineItem() {
        return new FabricItemSettings().group(LOOKINGGLASS_BLOCKS).rarity(Rarity.RARE);
    }
    private static FabricItemSettings trueMachineItem() {
        return new FabricItemSettings().group(LOOKINGGLASS_BLOCKS).rarity(Rarity.EPIC).fireproof();
    }

    private static FabricBlockSettings woodenMachine(int miningLevel) {
        return FabricBlockSettings.of(Material.WOOD).sounds(BlockSoundGroup.WOOD).breakByTool(FabricToolTags.AXES, miningLevel).requiresTool().strength(2f);
    }

    private static FabricBlockSettings stoneMachine() {
        return FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).breakByTool(FabricToolTags.PICKAXES, 2).requiresTool().strength(3.25f, 12f);
    }

    private static FabricBlockSettings hardenedMachine() {
        return FabricBlockSettings.of(Material.METAL).sounds(BlockSoundGroup.ANCIENT_DEBRIS).breakByTool(FabricToolTags.PICKAXES, 3).requiresTool().strength(4f, 20f);
    }

    private static FabricBlockSettings paleMachine() {
        return FabricBlockSettings.of(Material.METAL).nonOpaque().sounds(BlockSoundGroup.GLASS).breakByTool(FabricToolTags.PICKAXES, 1).strength(1f, 8f);
    }
    private static FabricBlockSettings eldenMachine() {
        return FabricBlockSettings.of(Material.STRUCTURE_VOID).sounds(ELDENMETAL).breakByTool(FabricToolTags.PICKAXES, 4).requiresTool().strength(3f, 800f);
    }

    //  BLOCKS
    //Machines
    public static final Block PROJECTORBLOCK = registerBlock("projector", new ProjectorBlock(paleMachine().luminance(ignored -> 7)), basicMachineItem());
    public static final Block CHUNKLOADERBLOCK = registerBlock("chunkloader", new ChunkAnchorBlock(paleMachine().luminance(ignored -> 7)), basicMachineItem());
    public static final Block WORMHOLEBLOCK = registerBlock("wormhole", new WormholeBlock(paleMachine().luminance(ignored -> 5)), basicMachineItem());
    public static final Block VACUUM_HOPPER_BLOCK = registerBlock("vacuum_hopper", new VacuumHopperBlock(stoneMachine(), 20, 4, 9), basicMachineItem());
    public static final Block ADVANCED_VACUUM_HOPPER_BLOCK = registerBlock("advanced_vacuum_hopper", new VacuumHopperBlock(hardenedMachine(), 5, 7, 27), advancedMachineItem());
    public static final Block BLOCK_TESSERACT_BLOCK = registerBlock("block_tesseract", new BlockTesseractBlock(eldenMachine().nonOpaque()), trueMachineItem());
    public static final Block BLOCK_INDUCTOR_BLOCK = registerBlock("block_inductor", new BlockInductorBlock(stoneMachine().nonOpaque().luminance(state -> state.get(AbstractInductorBlock.POWERED) ? 15 : 5)), basicMachineItem());

    //Power
    public static final Block CREATIVE_ENERGY_SOURCE_BLOCK = registerBlock("creative_energy_source", new CreativeEnergySourceBlock(paleMachine().nonOpaque().luminance(7).emissiveLighting((state, world, pos) -> true)), trueMachineItem());
    public static final Block SILICON_CABLE_BLOCK = registerBlock("silicon_cable", new SiliconCableBlock(stoneMachine().nonOpaque().sounds(BlockSoundGroup.CHAIN)), basicMachineItem());
    public static final Block GUILDED_CABLE_BLOCK = registerBlock("guilded_cable", new GuildedCableBlock(stoneMachine().nonOpaque().sounds(BlockSoundGroup.CHAIN)), midtierMachineItem());
    public static final Block ENCHANTED_CABLE_BLOCK = registerBlock("enchanted_cable", new EnchantedCableBlock(hardenedMachine().nonOpaque().sounds(BlockSoundGroup.CHAIN)), advancedMachineItem());
    public static final Block NULL_CABLE_BLOCK = registerBlock("null_cable", new NullCableBlock(eldenMachine().nonOpaque().sounds(BlockSoundGroup.CHAIN)), trueMachineItem());

    //Devices
    public static final Block FISH_BREEDER_BLOCK = registerBlock("fish_breeder", new FishBreederBlock(woodenMachine(1).nonOpaque().ticksRandomly()), basicMachineItem());
    public static final Block CRATE_BLOCK = registerBlock("crate", new CrateBlock(woodenMachine(1)), basicMachineItem());

    //Decorative
    public static final Block[] ADOBE_BRICK_SET = registerBuildingBlocks("adobe_bricks", FabricBlockSettings.copyOf(Blocks.BRICKS).materialColor(MaterialColor.DIRT), basicMachineItem(), Items.AIR, false);
    public static final Block[] GOLD_BRICK_SET = registerBuildingBlocks("gold_bricks", FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).sounds(BlockSoundGroup.CHAIN), midtierMachineItem(), Items.GOLD_INGOT, false);
    public static final Block[] SAND_BRICK_SET = registerBuildingBlocks("sand_bricks", FabricBlockSettings.copyOf(Blocks.SANDSTONE).sounds(BlockSoundGroup.SAND), basicMachineItem(), Items.AIR, false);
    public static final Block[] SOULSAND_BRICK_SET = registerBuildingBlocks("soulsand_bricks", FabricBlockSettings.copyOf(Blocks.SANDSTONE).sounds(BlockSoundGroup.SOUL_SAND), basicMachineItem(), Items.AIR, false);
    public static final Block WHITESTONE_BLOCK = registerGeneratedBlock("whitestone", new Block(FabricBlockSettings.copyOf(Blocks.STONE).sounds(BlockSoundGroup.BONE).materialColor(MaterialColor.WHITE)), null, null, basicMachineItem(), SingletType.BLOCK);
    public static final Block[] WHISTONE_POLISHED = registerBuildingBlocks("polished_whitestone",FabricBlockSettings.copyOf(WHITESTONE_BLOCK), basicMachineItem(), Items.AIR, false);
    public static final Block WHITESTONE_TILE = registerGeneratedBlock("whitestone_tile", new Block(FabricBlockSettings.copyOf(WHITESTONE_BLOCK)), null, null, basicMachineItem(), SingletType.BLOCK);
    public static final Block[] WHITESTONE_BRICK_SET = registerBuildingBlocks("whitestone_bricks", FabricBlockSettings.copyOf(WHITESTONE_BLOCK), basicMachineItem(), WHISTONE_POLISHED[0].asItem(), false);
    public static final Block[] WHITESTONE_LARGE_BRICK_SET = registerBuildingBlocks("large_whitestone_bricks", FabricBlockSettings.copyOf(WHITESTONE_BLOCK), basicMachineItem(), WHITESTONE_TILE.asItem(), false);
    public static final Block[] BASALT_BRICK_SET = registerBuildingBlocks("basalt_bricks", FabricBlockSettings.copyOf(Blocks.POLISHED_BASALT), basicMachineItem(), Items.POLISHED_BASALT, false);
    public static final Block HERRINGBONE_OAK_PLANKS = registerBlock("herringbone_oak_planks", new HerringboneWoodBlock(FabricBlockSettings.copyOf(Blocks.OAK_PLANKS)), basicMachineItem());

    //Cores
    public static final Block INTERMINAL_CORE = registerBlock("interminal_core", new InterminalCoreBlock(FabricBlockSettings.copyOf(Blocks.CRYING_OBSIDIAN)), basicMachineItem().fireproof());
    public static final Block GROWTH_CORE_0 = registerBlock("growth_core_0", new GrowthCoreBlock(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).breakByTool(FabricToolTags.HOES, 0).sounds(BlockSoundGroup.FUNGUS), 1, 1, 0), basicMachineItem().fireproof());
    public static final Block GROWTH_CORE_1 = registerBlock("growth_core_1", new GrowthCoreBlock(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).breakByTool(FabricToolTags.HOES, 0).sounds(BlockSoundGroup.FUNGUS), 2, 1, 0), basicMachineItem().fireproof());
    public static final Block GROWTH_CORE_2 = registerBlock("growth_core_2", new GrowthCoreBlock(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).breakByTool(FabricToolTags.HOES, 0).sounds(BlockSoundGroup.FUNGUS), 3, 2, 0), basicMachineItem().fireproof());
    public static final Block GROWTH_CORE_3 = registerBlock("growth_core_3", new GrowthCoreBlock(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).breakByTool(FabricToolTags.HOES, 0).sounds(BlockSoundGroup.FUNGUS), 4, 2, 0), basicMachineItem().fireproof());
    public static final Block GROWTH_CORE_4 = registerBlock("growth_core_4", new GrowthCoreBlock(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).breakByTool(FabricToolTags.HOES, 4).sounds(BlockSoundGroup.FUNGUS), 4, 3, 1), midtierMachineItem().fireproof());
    public static final Block TRUE_GROWTH_CORE_0 = registerBlock("growth_core_5", new GrowthCoreBlock(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).breakByTool(FabricToolTags.HOES, 4).sounds(BlockSoundGroup.FUNGUS), 4, 4, 1), advancedMachineItem().fireproof());
    public static final Block TRUE_GROWTH_CORE_1 = registerBlock("growth_core_6", new GrowthCoreBlock(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).breakByTool(FabricToolTags.HOES, 4).sounds(BlockSoundGroup.FUNGUS), 9, 4, 2), advancedMachineItem().fireproof());
    public static final Block TRUE_GROWTH_CORE_2 = registerBlock("growth_core_7", new GrowthCoreBlock(FabricBlockSettings.copyOf(Blocks.GOLD_BLOCK).breakByTool(FabricToolTags.HOES, 4).sounds(BlockSoundGroup.FUNGUS), 13, 5, 4), trueMachineItem());
    public static final Block ANNULATION_CORE_0 = registerBlock("annulation_core_0", new AnnulationCoreBlock(FabricBlockSettings.copyOf(Blocks.STONE_BRICKS), 1, false, DamageSource.MAGIC), basicMachineItem());
    public static final Block ANNULATION_CORE_1A = registerBlock("annulation_core_1a", new AnnulationCoreBlock(FabricBlockSettings.copyOf(Blocks.GILDED_BLACKSTONE), 2, true, DamageSource.OUT_OF_WORLD), advancedMachineItem());
    public static final Block ANNULATION_CORE_2B = registerBlock("annulation_core_2b", new SpecialAnnullationCoreBlock(eldenMachine()), trueMachineItem());

    //Misc
    public static final Block NEBULOUS_HALITE = registerBlock("nebulous_halite", new NebulousHaliteBlock(FabricBlockSettings.of(Material.GLASS).sounds(BlockSoundGroup.GLASS).emissiveLighting((a, b, c) -> true).requiresTool().breakByTool(FabricToolTags.PICKAXES, 3).nonOpaque().luminance(9).postProcess((a, b, c) -> true).strength(25, 500)), new FabricItemSettings().fireproof().group(LOOKINGGLASS_BLOCKS));
    public static final Block NEBULOUS_SALTS = TTLGBlocks.registerBlock("nebulous_salts", new NebulousSaltBlock(FabricBlockSettings.of(Material.GLASS).sounds(BlockSoundGroup.GLASS).emissiveLighting((a, b, c) -> true).requiresTool().breakByTool(FabricToolTags.PICKAXES, 3).nonOpaque().luminance(7).postProcess((a, b, c) -> true).strength(20, 500)), new FabricItemSettings().fireproof().group(LOOKINGGLASS_BLOCKS));
    public static final Block ELDENMETAL_BLOCK = registerBlock("eldenmetal_block", new Block(FabricBlockSettings.copyOf(Blocks.OBSIDIAN).luminance(state -> 3).nonOpaque().sounds(LookingGlassCommon.ELDENMETAL)), basicMachineItem().fireproof().rarity(ELDENMETAL_RARITY));
    public static final Block SALMON_EGGS = registerBlock("salmon_egg", new SalmonEggBlock(FabricBlockSettings.copyOf(Blocks.TURTLE_EGG).sounds(BlockSoundGroup.HONEY).ticksRandomly()), basicMachineItem());

    //  BLOCK ENTITIES
    //Machines
    public static final BlockEntityType<ProjectorEntity> PROJECTORENTITY = registerEntity("projector_entity", ProjectorEntity::new, PROJECTORBLOCK);
    public static final BlockEntityType<ChunkAnchorEntity> CHUNKLOADER_ENTITY = registerEntity("chunkloader_entity", ChunkAnchorEntity::new, CHUNKLOADERBLOCK);
    public static final BlockEntityType<WormholeEntity> WORMHOLE_ENTITY = registerEntity("wormhole_entity", WormholeEntity::new, WORMHOLEBLOCK);
    public static final BlockEntityType<SpecialAnnulationCoreEntity> SPECIAL_ANNULATION_CORE_ENTITY = registerEntity("special_annulation_core_entity", SpecialAnnulationCoreEntity::new, ANNULATION_CORE_2B);
    public static final BlockEntityType<VacuumHopperEntity> VACUUM_HOPPER_ENTITY = registerEntity("vacuum_hopper_entity", () -> new VacuumHopperEntity(TTLGBlocks.VACUUM_HOPPER_ENTITY, 9, 20, 4), VACUUM_HOPPER_BLOCK);
    public static final BlockEntityType<VacuumHopperEntity> ADVANCED_VACUUM_HOPPER_ENTITY = registerEntity("advanced_vacuum_hopper_entity", () -> new VacuumHopperEntity(TTLGBlocks.ADVANCED_VACUUM_HOPPER_ENTITY, 27, 5, 8), ADVANCED_VACUUM_HOPPER_BLOCK);

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

        EnergyApi.SIDED.registerForBlockEntities((blockEntity, direction) -> (EnergyIo) blockEntity, CREATIVE_ENERGY_SOURCE_ENTITY);
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
