package azzy.fabric.lookingglass.gui;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;

public class ProjectorContainer extends CottonInventoryScreen<ProjectorGuiDescription> {

    public ProjectorContainer(ProjectorGuiDescription container, PlayerEntity player) {
        super(container, player);
    }

}
