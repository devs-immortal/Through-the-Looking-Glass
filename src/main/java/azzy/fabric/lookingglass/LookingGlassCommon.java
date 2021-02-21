package azzy.fabric.lookingglass;


import azzy.fabric.lookingglass.block.TTLGBlocks;
import azzy.fabric.lookingglass.blockentity.ProjectorEntity;
import azzy.fabric.lookingglass.feature.TTLGConfiguredFeatures;
import azzy.fabric.lookingglass.gui.LookingGlassGUIs;
import azzy.fabric.lookingglass.item.TTLGItems;
import azzy.fabric.lookingglass.util.GeneralNetworking;
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

	public static final Rarity FINIS_RARITY = ClassTinkerers.getEnum(Rarity.class, "FINIS");
	public static final Rarity ELDENMETAL_RARITY = ClassTinkerers.getEnum(Rarity.class, "NULL");
	public static final Rarity LUPREVAN_RARITY = ClassTinkerers.getEnum(Rarity.class, "DAWN");
	public static final Rarity WORLDFORGE_RARITY = ClassTinkerers.getEnum(Rarity.class, "TERMINUS");

	public static final Logger FFLog = LogManager.getLogger(MODID);
	public static final SplittableRandom RANDOM = new SplittableRandom();
	public static final ItemGroup LOOKINGGLASS_BLOCKS = FabricItemGroupBuilder.build(new Identifier(MODID, "looking_blocks"), () -> new ItemStack(PROJECTORBLOCK));
	public static final ItemGroup LOOKINGGLASS_ITEMS = FabricItemGroupBuilder.build(new Identifier(MODID, "looking_items"), () -> new ItemStack(TTLGItems.DATA_SHARD));

	public static final boolean DEV_ENV = FabricLoader.getInstance().isDevelopmentEnvironment();
	public static final boolean REGEN_RECIPES = false, REGEN_ITEMS = false, REGEN_BLOCKS = false, REGEN_LOOT = false;

	@Override
	public void onInitialize() {
		FFLog.info(LookingGlassInit.BLESSED_CONST);

		TTLGBlocks.init();
		TTLGItems.init();
		TTLGConfiguredFeatures.init();
		TTLGConfiguredFeatures.Registrar.init();
		LookingGlassGUIs.initCommon();
		GeneralNetworking.init();
	}
}
