package azzy.fabric.lookingglass.gui;

import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.util.client.BackgroundType;
import io.github.cottonmc.cotton.gui.widget.WBar;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.Identifier;

public class MixerGuiDescription extends UpgradeableMachineGuiDescription {

    public MixerGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(LookingGlassGUIs.MIXER_HANDLER, syncId, 9, 4, 5, 2, playerInventory, context);
    }

    @Override
    protected void draw(WPlainPanel root) {
        root.add(WItemSlot.of(blockInventory, 0), 28, 28);
        root.add(WItemSlot.of(blockInventory, 1), 46, 28);
        root.add(WItemSlot.of(blockInventory, 2), 28, 46);
        root.add(WItemSlot.of(blockInventory, 3), 46, 46);

        root.add(new WItemSlot(blockInventory, 4, 1, 1, true), 108, 37);
        root.add(new WBar(new Identifier(LookingGlassCommon.MODID, "textures/gui/progress/electric_smelting_arrow.png"), new Identifier(LookingGlassCommon.MODID, "textures/gui/progress/electric_smelting_arrow_full.png"), 0, 1, WBar.Direction.RIGHT), 73, 39, 22, 15);
    }

    @Override
    protected BackgroundType getBackground() {
        return BackgroundTypes.DWARVEN;
    }
}
