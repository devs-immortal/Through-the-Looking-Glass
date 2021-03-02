package azzy.fabric.lookingglass.gui;

import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.util.client.BackgroundType;
import io.github.cottonmc.cotton.gui.widget.WBar;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.Identifier;

public class GrinderGuiDescription extends UpgradeableMachineGuiDescription {

    public GrinderGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(LookingGlassGUIs.GRINDER_HANDLER, syncId, 7, 4, 3, 2, playerInventory, context);
    }

    @Override
    protected void draw(WPlainPanel root) {
        root.add(WItemSlot.of(blockInventory, 0), 37, 37);
        root.add(WItemSlot.of(blockInventory, 1), 99, 28);
        root.add(WItemSlot.of(blockInventory, 2), 99, 46);
        root.add(new WBar(new Identifier(LookingGlassCommon.MODID, "textures/gui/progress/electric_smelting_arrow.png"), new Identifier(LookingGlassCommon.MODID, "textures/gui/progress/electric_smelting_arrow_full.png"), 0, 1, WBar.Direction.RIGHT), 66, 39, 22, 15);
    }

    @Override
    protected BackgroundType getBackground() {
        return BackgroundTypes.DWARVEN;
    }
}
