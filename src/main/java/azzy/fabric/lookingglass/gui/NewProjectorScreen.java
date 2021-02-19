package azzy.fabric.lookingglass.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.Text;

public class NewProjectorScreen extends CottonInventoryScreen<NewProjectorGuiDescription> {

    public NewProjectorScreen(NewProjectorGuiDescription description, PlayerEntity player, Text title) {
        super(description, player, title);
    }
}
