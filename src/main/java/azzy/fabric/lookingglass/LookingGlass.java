package azzy.fabric.lookingglass;


import azzy.fabric.lookingglass.block.*;
import azzy.fabric.lookingglass.entity.ChunkLoaderEntity;
import azzy.fabric.lookingglass.entity.ProjectorEntity;
import azzy.fabric.lookingglass.entity.WormholeEntity;
import azzy.fabric.lookingglass.gui.ProjectorGUI;
import azzy.fabric.lookingglass.item.DataShardItem;
import azzy.fabric.lookingglass.item.WarcrimeItem;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import net.minecraft.util.registry.Registry;
import org.apache.logging.log4j.Logger;

import java.util.function.Supplier;

public class LookingGlass implements ModInitializer {
	public static final String MODID = "lookingglass";

	public static final Logger FFLog = LogManager.getLogger(MODID);

	private static final FabricBlockSettings BASIC_MACHINE = FabricBlockSettings.of(Material.METAL).lightLevel(7).nonOpaque().sounds(BlockSoundGroup.GLASS).breakByTool(FabricToolTags.PICKAXES, 2).strength(2f, 8f);

	public static final Block PROJECTORBLOCK = registerBlock("projector", new ProjectorBlock(BASIC_MACHINE), new Item.Settings().group(ItemGroup.DECORATIONS));
	public static final Block CHUNKLOADERBLOCK = registerBlock("chunkloader", new ChunkLoaderBlock(BASIC_MACHINE), new Item.Settings().group(ItemGroup.REDSTONE));
	public static final Block WORMHOLEBLOCK = registerBlock("wormhole", new WormholeBlock(BASIC_MACHINE), new Item.Settings().group(ItemGroup.REDSTONE));

	public static final Block BLOCK_TESSERACT_BLOCK = registerBlock("block_tesseract", new BlockTesseractBlock(BASIC_MACHINE), new Item.Settings().group(ItemGroup.REDSTONE));

	public static final Item DATA_SHARD = registerItem("data_shard", new DataShardItem(new Item.Settings().group(ItemGroup.REDSTONE)));
	public static final Item WARCRIME_ITEM = registerItem("warcrime_test_item", new WarcrimeItem(new Item.Settings().group(ItemGroup.REDSTONE)));
	public static final BlockEntityType<ProjectorEntity> PROJECTORENTITY = registerEntity("projector_entity", ProjectorEntity::new, PROJECTORBLOCK);
	public static final BlockEntityType<ChunkLoaderEntity> CHUNKLOADER_ENTITY = registerEntity("chunkloader_entity", ChunkLoaderEntity::new, CHUNKLOADERBLOCK);
	public static final BlockEntityType<WormholeEntity> WORMHOLE_ENTITY = registerEntity("wormhole_entity", WormholeEntity::new, WORMHOLEBLOCK);


	public static final Identifier STRING_TO_SERVER_PACKET = new Identifier(MODID, "stringtoserver");
	public static final Identifier DOUBLES_TO_SERVER_PACKET = new Identifier(MODID, "doubletoserver");

	@Override
	public void onInitialize() {
		FFLog.info("We Extra Utils 3 now!");

		ServerSidePacketRegistry.INSTANCE.register(STRING_TO_SERVER_PACKET, ((packetContext, packetByteBuf) -> {

			String url = packetByteBuf.readString(4096);
			BlockPos pos = packetByteBuf.readBlockPos();
			int index = packetByteBuf.readInt();
			World world = packetContext.getPlayer().getEntityWorld();

			packetContext.getTaskQueue().execute(() -> {
				if(world.isChunkLoaded(pos) && world.getBlockState(pos).getBlock() == PROJECTORBLOCK){
					ProjectorEntity projector = (ProjectorEntity) world.getBlockEntity(pos);
					assert projector != null;
					if(index == 0)
						projector.sign = url;
					else if(index == 2)
						projector.color = url;
					else
						projector.setUrl(url);
					projector.sync();
				}
			});
		}));

		ServerSidePacketRegistry.INSTANCE.register(DOUBLES_TO_SERVER_PACKET, ((packetContext, packetByteBuf) -> {

			int index = packetByteBuf.readInt();
			double value = packetByteBuf.readDouble();
			BlockPos pos = packetByteBuf.readBlockPos();
			World world = packetContext.getPlayer().getEntityWorld();

			packetContext.getTaskQueue().execute(() -> {
				if(world.isChunkLoaded(pos) && world.getBlockState(pos).getBlock() == PROJECTORBLOCK){
					ProjectorEntity projector = (ProjectorEntity) world.getBlockEntity(pos);
					assert projector != null;

					switch(index){
						case (1): projector.rotX = value; break;
						case (2): projector.rotY = value; break;
						case (3): projector.rotZ = value; break;
						case (4): projector.disX = value; break;
						case (5): projector.disY = value; break;
						case (6): projector.disZ = value; break;
						case (7): projector.scale = value; break;
					}
					projector.sync();
				}
			});
		}));

		ContainerProviderRegistry.INSTANCE.registerFactory(new Identifier(MODID, "projector_gui"), (syncID, id, player, buf) -> new ProjectorGUI(ScreenHandlerType.ANVIL, syncID, player.inventory, ScreenHandlerContext.create(player.world, buf.readBlockPos())));

	}

	private static Block registerBlock(String name, Block item, Item.Settings settings){
		Block block = Registry.register(Registry.BLOCK, new Identifier(MODID, name), item);
		Registry.register(Registry.ITEM, new Identifier(MODID, name), new BlockItem(block, settings));
		return block;
	}

	private static Item registerItem(String name, Item item) {
		return Registry.register(Registry.ITEM, new Identifier(MODID, name), item);
	}

	private static <T extends BlockEntity> BlockEntityType<T>  registerEntity(String name, Supplier<T> item, Block block){
		return Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, name), BlockEntityType.Builder.create(item, block).build(null));
	}
}
