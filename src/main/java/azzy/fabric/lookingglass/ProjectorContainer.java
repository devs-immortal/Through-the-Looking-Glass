package azzy.fabric.lookingglass;

import io.github.cottonmc.cotton.gui.client.CottonInventoryScreen;
import net.minecraft.entity.player.PlayerEntity;

public class ProjectorContainer extends CottonInventoryScreen<ProjectorGUI> {

    public ProjectorContainer(ProjectorGUI container, PlayerEntity player) {
        super(container, player);
    }

}
