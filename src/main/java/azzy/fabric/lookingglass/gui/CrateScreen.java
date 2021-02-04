package azzy.fabric.lookingglass.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class CrateScreen extends CottonInventoryScreen<CrateGuiDescription> {

    public CrateScreen(CrateGuiDescription container, PlayerEntity player, Text title) {
        super(container, player, title);
    }

}
