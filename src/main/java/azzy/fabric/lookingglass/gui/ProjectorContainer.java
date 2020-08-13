package azzy.fabric.lookingglass.gui;

import azzy.fabric.lookingglass.gui.ProjectorGUI;
import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;

public class ProjectorContainer extends CottonInventoryScreen<ProjectorGUI> {

    public ProjectorContainer(ProjectorGUI container, PlayerEntity player) {
        super(container, player);
    }

}
