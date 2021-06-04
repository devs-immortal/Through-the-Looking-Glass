package azzy.fabric.lookingglass;


import azzy.fabric.incubus_core.datagen.Metadata;
import azzy.fabric.lookingglass.biome.LookingGlassBiomes;
import azzy.fabric.lookingglass.biome.LookingGlassConfiguredFeatures;
import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.effects.LookingGlassEffects;
import azzy.fabric.lookingglass.entity.LookingGlassEntities;
import azzy.fabric.lookingglass.gui.LookingGlassGUIs;
import azzy.fabric.lookingglass.item.LookingGlassItems;
import azzy.fabric.lookingglass.recipe.LookingGlassCompat;
import azzy.fabric.lookingglass.recipe.LookingGlassRecipes;
import azzy.fabric.lookingglass.util.GeneralNetworking;
import azzy.fabric.lookingglass.util.json.LookingGlassJsonManager;
import com.chocohead.mm.api.ClassTinkerers;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.fabricmc.fabric.impl.resource.loader.ResourceManagerHelperImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.SplittableRandom;

import static azzy.fabric.lookingglass.block.LookingGlassBlocks.PROJECTORBLOCK;

public class LookingGlassCommon implements ModInitializer {
	public static final String MODID = "lookingglass";

	public static final Metadata METADATA = new Metadata(MODID);

	public static final Rarity FINIS_RARITY = ClassTinkerers.getEnum(Rarity.class, "FINIS");
	public static final Rarity ELDENMETAL_RARITY = ClassTinkerers.getEnum(Rarity.class, "NULL");
	public static final Rarity LUPREVAN_RARITY = ClassTinkerers.getEnum(Rarity.class, "DAWN");
	public static final Rarity WORLDFORGE_RARITY = ClassTinkerers.getEnum(Rarity.class, "TERMINUS");

	public static final Logger FFLog = LogManager.getLogger(MODID);
	public static final SplittableRandom RANDOM = new SplittableRandom();
	public static final ItemGroup LOOKINGGLASS_BLOCKS = FabricItemGroupBuilder.build(new Identifier(MODID, "looking_blocks"), () -> new ItemStack(PROJECTORBLOCK));
	public static final ItemGroup LOOKINGGLASS_ITEMS = FabricItemGroupBuilder.build(new Identifier(MODID, "looking_items"), () -> new ItemStack(LookingGlassItems.DATA_SHARD));

	public static final boolean DEV_ENV = FabricLoader.getInstance().isDevelopmentEnvironment();
	public static final boolean REGEN_RECIPES = true, REGEN_ITEMS = true, REGEN_BLOCKS = true, REGEN_LOOT = true;

	@Override
	public void onInitialize() {
		if(!DEV_ENV)
			FFLog.info(LookingGlassInit.BLESSED_CONST);

		LookingGlassBlocks.init();
		LookingGlassItems.init();
		LookingGlassEffects.init();
		LookingGlassConfiguredFeatures.init();
		LookingGlassConfiguredFeatures.Registrar.init();
		LookingGlassEntities.init();
		LookingGlassBiomes.init();
		LookingGlassGUIs.initCommon();
		LookingGlassRecipes.init();
		LookingGlassCompat.init();
		GeneralNetworking.init();

		ResourceManagerHelperImpl.get(ResourceType.SERVER_DATA).registerReloadListener(new SimpleSynchronousResourceReloadListener() {
			private final Identifier id = new Identifier(MODID, "config");

			@Override
			public Identifier getFabricId() {
				return id;
			}

			@Override
			public void reload(ResourceManager manager) {
				LookingGlassJsonManager.load(manager);
			}
		});
	}
}
