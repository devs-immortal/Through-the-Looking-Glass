package azzy.fabric.lookingglass.gui;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.screenhandler.v1.ScreenRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

import static azzy.fabric.lookingglass.LookingGlassCommon.MODID;


public class LookingGlassGUIs {

    //Pale Machines
    public static final ScreenHandlerType<NewProjectorGuiDescription> PROJECTOR_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier(MODID, "projector_gui"), (syncId, inventory, packetByteBuf) -> new NewProjectorGuiDescription(syncId, inventory, ScreenHandlerContext.create(inventory.player.world, packetByteBuf.readBlockPos())));

    //Dwarven Machines
    public static final ScreenHandlerType<PoweredFurnaceGuiDescription> POWERED_FURNACE_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(MODID, "powered_furnace_gui"), (syncId, inventory) -> new PoweredFurnaceGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY));
    public static final ScreenHandlerType<AlloyingFurnaceGuiDescription> ALLOYING_FURNACE_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(MODID, "alloying_furnace_gui"), (syncId, inventory) -> new AlloyingFurnaceGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY));
    public static final ScreenHandlerType<GrinderGuiDescription> GRINDER_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(MODID, "grinder_gui"), (syncId, inventory) -> new GrinderGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY));
    public static final ScreenHandlerType<MixerGuiDescription> MIXER_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(MODID, "mixer_gui"), (syncId, inventory) -> new MixerGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY));

    //Devices
    public static final ScreenHandlerType<CrateGuiDescription> CRATE_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(MODID, "crate_gui"), (syncId, inventory) -> new CrateGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY));

    public static void initCommon() {}

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        ScreenRegistry.<CrateGuiDescription, CrateScreen>register(CRATE_HANDLER, (gui, inventory, title) -> new CrateScreen(gui, inventory.player, title));
        ScreenRegistry.<NewProjectorGuiDescription, NewProjectorScreen>register(PROJECTOR_HANDLER, (gui, inventory, title) -> new NewProjectorScreen(gui, inventory.player, title));
        ScreenRegistry.<PoweredFurnaceGuiDescription, PoweredFurnaceScreen>register(POWERED_FURNACE_HANDLER, (gui, inventory, title) -> new PoweredFurnaceScreen(gui, inventory.player, title));
        ScreenRegistry.<AlloyingFurnaceGuiDescription, AlloyingFurnaceScreen>register(ALLOYING_FURNACE_HANDLER, (gui, inventory, title) -> new AlloyingFurnaceScreen(gui, inventory.player, title));
        ScreenRegistry.<GrinderGuiDescription, GrinderScreen>register(GRINDER_HANDLER, (gui, inventory, title) -> new GrinderScreen(gui, inventory.player, title));
        ScreenRegistry.<MixerGuiDescription, MixerScreen>register(MIXER_HANDLER, (gui, inventory, title) -> new MixerScreen(gui, inventory.player, title));
    }
}
