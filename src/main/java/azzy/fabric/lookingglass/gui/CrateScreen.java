package azzy.fabric.lookingglass.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;

public class CrateScreen extends CottonInventoryScreen<CrateGuiDescription> {

    public CrateScreen(CrateGuiDescription container, PlayerEntity player) {
        super(container, player);
    }

}