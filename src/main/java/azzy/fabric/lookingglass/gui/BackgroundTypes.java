package azzy.fabric.lookingglass.gui;

import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.util.client.BackgroundType;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import net.minecraft.util.Identifier;

public class BackgroundTypes {

    public static final BackgroundType DWARVEN = new BackgroundType(BackgroundPainter.createNinePatch(new Identifier(LookingGlassCommon.MODID, "textures/gui/background/dwarven.png"), 8), "textures/gui/energy/lumen_bar_dwarven.png");
}
