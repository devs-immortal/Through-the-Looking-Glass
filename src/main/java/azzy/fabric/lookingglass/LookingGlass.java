package azzy.fabric.lookingglass;


import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.block.FabricBlockSettings;
import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.RunArgs;
import net.minecraft.container.BlockContext;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import org.apache.logging.log4j.LogManager;
import net.minecraft.util.registry.Registry;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;

public class LookingGlass implements ModInitializer {
	public static final String MODID = "lookingglass";

	public static final Logger FFLog = LogManager.getLogger(MODID);

	public static final Block PROJECTORBLOCK = new ProjectorBlock(FabricBlockSettings.of(Material.METAL).lightLevel(6).nonOpaque().resistance(1f).sounds(BlockSoundGroup.GLASS).build());
	public static final BlockEntityType<ProjectorEntity> PROJECTORENTITY = BlockEntityType.Builder.create(ProjectorEntity::new, PROJECTORBLOCK).build(null);

	@Override
	public void onInitialize() {
		FFLog.info("Eye see you");



		Registry.register(Registry.BLOCK,new Identifier(MODID, "projector"), PROJECTORBLOCK);
		Registry.register(Registry.ITEM, new Identifier(MODID, "projector"), new BlockItem(PROJECTORBLOCK, new Item.Settings().group(ItemGroup.DECORATIONS).rarity(Rarity.RARE)));
		Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(MODID, "projector_entity"), PROJECTORENTITY);
		ContainerProviderRegistry.INSTANCE.registerFactory(new Identifier(MODID, "projector_gui"), (syncID, id, player, buf) -> new ProjectorGUI(RecipeType.SMELTING, syncID, player.inventory, BlockContext.create(player.world, buf.readBlockPos())));



		//Registries

	}
	public void onInitializing(){
	}
}
