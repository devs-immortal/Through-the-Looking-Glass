package azzy.fabric.lookingglass.util.client;

import azzy.fabric.lookingglass.LookingGlassCommon;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

@Environment(EnvType.CLIENT)
public class BackgroundType {

    public final BackgroundPainter painter;
    public final Identifier lumenTexture;

    public BackgroundType(BackgroundPainter painter, String lumenTexture) {
        this.painter = painter;
        this.lumenTexture = new Identifier(LookingGlassCommon.MODID, lumenTexture);
    }
}
