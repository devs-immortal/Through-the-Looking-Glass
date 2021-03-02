package azzy.fabric.lookingglass.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class GrinderScreen extends CottonInventoryScreen<GrinderGuiDescription> {

    public GrinderScreen(GrinderGuiDescription description, PlayerEntity player, Text title) {
        super(description, player, title);
    }
}
