package azzy.fabric.lookingglass;


import azzy.fabric.lookingglass.block.TTLGBlocks;
import azzy.fabric.lookingglass.blockentity.ProjectorEntity;
import azzy.fabric.lookingglass.feature.TTLGConfiguredFeatures;
import azzy.fabric.lookingglass.gui.LookingGlassGUIs;
import azzy.fabric.lookingglass.gui.ProjectorGuiDescription;
import azzy.fabric.lookingglass.item.TTLGItems;
import azzy.fabric.lookingglass.util.datagen.Metadata;
import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.network.ServerSidePacketRegistry;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.SplittableRandom;

import static azzy.fabric.lookingglass.block.TTLGBlocks.PROJECTORBLOCK;

public class LookingGlassCommon implements ModInitializer {
	public static final String MODID = "lookingglass";

	public static final Metadata METADATA = new Metadata(MODID);

	public static final BlockSoundGroup ELDENMETAL = new BlockSoundGroup(1.0F, 1.0F, SoundEvents.BLOCK_RESPAWN_ANCHOR_SET_SPAWN, SoundEvents.BLOCK_SHROOMLIGHT_PLACE, SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundEvents.BLOCK_RESPAWN_ANCHOR_AMBIENT, SoundEvents.BLOCK_BEACON_DEACTIVATE);

	public static final Rarity NULL_RARITY = ClassTinkerers.getEnum(Rarity.class, "NULL");

	public static final Logger FFLog = LogManager.getLogger(MODID);
	public static final SplittableRandom RANDOM = new SplittableRandom();
	public static final ItemGroup LOOKINGGLASS_BLOCKS = FabricItemGroupBuilder.build(new Identifier(MODID, "blocks"), () -> new ItemStack(PROJECTORBLOCK));
	public static final ItemGroup LOOKINGGLASS_ITEMS = FabricItemGroupBuilder.build(new Identifier(MODID, "blocks"), () -> new ItemStack(TTLGItems.DATA_SHARD));

	public static final boolean DEV_ENV = FabricLoader.getInstance().isDevelopmentEnvironment();
	public static final boolean REGEN_RECIPES = false, REGEN_ITEMS = false, REGEN_BLOCKS = false, REGEN_LOOT = false;

	public static final Identifier STRING_TO_SERVER_PACKET = new Identifier(MODID, "stringtoserver");
	public static final Identifier DOUBLES_TO_SERVER_PACKET = new Identifier(MODID, "doubletoserver");
	public static final Identifier BLOCKPOS_TO_CLIENT_PACKET = new Identifier(MODID, "postoclient");

	@Override
	public void onInitialize() {
		FFLog.info(LookingGlassInit.BLESSED_CONST);

		TTLGBlocks.init();
		TTLGItems.init();
		TTLGConfiguredFeatures.init();
		TTLGConfiguredFeatures.Registrar.init();
		LookingGlassGUIs.initCommon();

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

		ContainerProviderRegistry.INSTANCE.registerFactory(new Identifier(MODID, "projector_gui"), (syncID, id, player, buf) -> new ProjectorGuiDescription(ScreenHandlerType.ANVIL, syncID, player.inventory, ScreenHandlerContext.create(player.world, buf.readBlockPos())));
	}
}
