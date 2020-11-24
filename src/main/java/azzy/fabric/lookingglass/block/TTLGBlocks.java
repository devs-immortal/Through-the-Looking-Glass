package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.LookingGlass;
import azzy.fabric.lookingglass.entity.ChunkLoaderEntity;
import azzy.fabric.lookingglass.entity.ProjectorEntity;
import azzy.fabric.lookingglass.entity.WormholeEntity;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.registry.Registry;

import java.util.function.Supplier;

import static azzy.fabric.lookingglass.LookingGlass.*;

public class TTLGBlocks {

    private static FabricItemSettings basicMachineItem() {
        return new FabricItemSettings().group(LOOKINGGLASS_BLOCKS);
    }

    private static FabricBlockSettings basicMachine() {
        return FabricBlockSettings.of(Material.METAL).lightLevel(7).nonOpaque().sounds(BlockSoundGroup.GLASS).breakByTool(FabricToolTags.PICKAXES, 2).strength(2f, 8f);
    }

    public static void init() {}

    //Machines
    public static final Block PROJECTORBLOCK = registerBlock("projector", new ProjectorBlock(basicMachine()), basicMachineItem());
    public static final Block CHUNKLOADERBLOCK = registerBlock("chunkloader", new ChunkLoaderBlock(basicMachine()), basicMachineItem());
    public static final Block WORMHOLEBLOCK = registerBlock("wormhole", new WormholeBlock(basicMachine()), basicMachineItem());
    public static final Block BLOCK_TESSERACT_BLOCK = registerBlock("block_tesseract", new BlockTesseractBlock(basicMachine()), basicMachineItem());
    public static final Block BLOCK_INDUCTOR_BLOCK = registerBlock("block_inductor", new BlockInductorBlock(basicMachine()), basicMachineItem());

    //Misc
    public static final Block ELDENMETAL_BLOCK = registerBlock("eldenmetal_block", new Block(FabricBlockSettings.copyOf(Blocks.OBSIDIAN).luminance(a -> 3).nonOpaque().sounds(LookingGlass.ELDENMETAL)), basicMachineItem().fireproof().rarity(NOLL));

    //BEs
    public static final BlockEntityType<ProjectorEntity> PROJECTORENTITY = registerEntity("projector_entity", ProjectorEntity::new, PROJECTORBLOCK);
    public static final BlockEntityType<ChunkLoaderEntity> CHUNKLOADER_ENTITY = registerEntity("chunkloader_entity", ChunkLoaderEntity::new, CHUNKLOADERBLOCK);
    public static final BlockEntityType<WormholeEntity> WORMHOLE_ENTITY = registerEntity("wormhole_entity", WormholeEntity::new, WORMHOLEBLOCK);

    private static Block registerBlock(String name, Block item, Item.Settings settings){
        Block block = Registry.register(Registry.BLOCK, new Identifier(MODID, name), item);
        Registry.register(Registry.ITEM, new Identifier(MODID, name), new BlockItem(block, settings));
        return block;
    }

    private static <T extends BlockEntity> BlockEntityType<T> registerEntity(String name, Supplier<T> item, Block block){
        return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, name), BlockEntityType.Builder.create(item, block).build(null));
    }
}
