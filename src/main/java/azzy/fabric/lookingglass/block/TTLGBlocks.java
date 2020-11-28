package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.blockentity.BlockTesseractEntity;
import azzy.fabric.lookingglass.blockentity.ChunkAnchorEntity;
import azzy.fabric.lookingglass.blockentity.ProjectorEntity;
import azzy.fabric.lookingglass.blockentity.WormholeEntity;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

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

    private static FabricBlockSettings paleMachine() {
        return FabricBlockSettings.of(Material.METAL).nonOpaque().sounds(BlockSoundGroup.GLASS).breakByTool(FabricToolTags.PICKAXES, 1).strength(1f, 8f);
    }
    private static FabricBlockSettings stoneMachine() {
        return FabricBlockSettings.of(Material.STONE).sounds(BlockSoundGroup.STONE).breakByTool(FabricToolTags.PICKAXES, 2).requiresTool().strength(2.25f, 12f);
    }
    private static FabricBlockSettings eldenMachine() {
        return FabricBlockSettings.of(Material.STRUCTURE_VOID).sounds(ELDENMETAL).breakByTool(FabricToolTags.PICKAXES, 4).requiresTool().strength(5f, 800f);
    }

    public static void init() {}

    //  BLOCKS
    //Machines
    public static final Block PROJECTORBLOCK = registerBlock("projector", new ProjectorBlock(paleMachine().luminance(ignored -> 7)), basicMachineItem());
    public static final Block CHUNKLOADERBLOCK = registerBlock("chunkloader", new ChunkAnchorBlock(paleMachine().luminance(ignored -> 7)), basicMachineItem());
    public static final Block WORMHOLEBLOCK = registerBlock("wormhole", new WormholeBlock(paleMachine().luminance(ignored -> 5)), basicMachineItem());
    public static final Block BLOCK_TESSERACT_BLOCK = registerBlock("block_tesseract", new BlockTesseractBlock(paleMachine()), basicMachineItem());
    public static final Block BLOCK_INDUCTOR_BLOCK = registerBlock("block_inductor", new BlockInductorBlock(stoneMachine().nonOpaque().luminance(state -> state.get(AbstractInductorBlock.POWERED) ? 15 : 5)), basicMachineItem());

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
    public static final Block ANNULATION_CORE_1A = registerBlock("annulation_core_1a", new AnnulationCoreBlock(FabricBlockSettings.copyOf(Blocks.STONE_BRICKS), 2, true, DamageSource.OUT_OF_WORLD), advancedMachineItem());
    public static final Block ANNULATION_CORE_1B = registerBlock("annulation_core_1b", new AnnulationCoreBlock(FabricBlockSettings.copyOf(Blocks.STONE_BRICKS), 10, false, DamageSource.MAGIC), midtierMachineItem());
    public static final Block ANNULATION_CORE_2B = registerBlock("annulation_core_2b", new SpecialAnnullationCoreBlock(eldenMachine()), trueMachineItem());

    //Misc
    public static final Block ELDENMETAL_BLOCK = registerBlock("eldenmetal_block", new Block(FabricBlockSettings.copyOf(Blocks.OBSIDIAN).luminance(state -> 3).nonOpaque().sounds(LookingGlassCommon.ELDENMETAL)), basicMachineItem().fireproof().rarity(NULL_RARITY));

    //  BLOCK ENTITIES
    //Machines
    public static final BlockEntityType<ProjectorEntity> PROJECTORENTITY = registerEntity("projector_entity", ProjectorEntity::new, PROJECTORBLOCK);
    public static final BlockEntityType<ChunkAnchorEntity> CHUNKLOADER_ENTITY = registerEntity("chunkloader_entity", ChunkAnchorEntity::new, CHUNKLOADERBLOCK);
    public static final BlockEntityType<WormholeEntity> WORMHOLE_ENTITY = registerEntity("wormhole_entity", WormholeEntity::new, WORMHOLEBLOCK);

    //Tesseracts
    public static final BlockEntityType<BlockTesseractEntity> BLOCK_TESSERACT_ENTITY = registerEntity("block_tesseract", BlockTesseractEntity::new, BLOCK_TESSERACT_BLOCK);


    private static Block registerBlock(String name, Block item, Item.Settings settings){
        Block block = Registry.register(Registry.BLOCK, new Identifier(MODID, name), item);
        Registry.register(Registry.ITEM, new Identifier(MODID, name), new BlockItem(block, settings));
        return block;
    }

    private static <T extends BlockEntity> BlockEntityType<T> registerEntity(String name, Supplier<T> item, Block block){
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, name), BlockEntityType.Builder.create(item, block).build(null));
    }
}
