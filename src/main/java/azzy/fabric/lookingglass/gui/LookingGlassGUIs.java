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

    public static final ScreenHandlerType<CrateGuiDescription> CRATE_HANDLER = ScreenHandlerRegistry.registerSimple(new Identifier(MODID, "crate_gui"), (syncId, inventory) -> new CrateGuiDescription(syncId, inventory, ScreenHandlerContext.EMPTY));
    public static final ScreenHandlerType<NewProjectorGuiDescription> PROJECTOR_HANDLER = ScreenHandlerRegistry.registerExtended(new Identifier(MODID, "projector_gui"), (syncId, inventory, packetByteBuf) -> new NewProjectorGuiDescription(syncId, inventory, ScreenHandlerContext.create(inventory.player.world, packetByteBuf.readBlockPos())));


    public static void initCommon() {}

    @Environment(EnvType.CLIENT)
    public static void initClient() {
        ScreenRegistry.<CrateGuiDescription, CrateScreen>register(CRATE_HANDLER, (gui, inventory, title) -> new CrateScreen(gui, inventory.player, title));
        ScreenRegistry.<NewProjectorGuiDescription, NewProjectorScreen>register(PROJECTOR_HANDLER, (gui, inventory, title) -> new NewProjectorScreen(gui, inventory.player, title));
    }
}
