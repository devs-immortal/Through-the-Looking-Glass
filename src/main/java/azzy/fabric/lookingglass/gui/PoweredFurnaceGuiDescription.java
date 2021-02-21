package azzy.fabric.lookingglass.gui;

import azzy.fabric.lookingglass.LookingGlassCommon;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.client.BackgroundPainter;
import io.github.cottonmc.cotton.gui.widget.WBar;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class PoweredFurnaceGuiDescription extends SyncedGuiDescription {

    public PoweredFurnaceGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(LookingGlassGUIs.POWERED_FURNACE_HANDLER, syncId, playerInventory, getBlockInventory(context, 2), getBlockPropertyDelegate(context, 4));

        WPlainPanel root = new WPlainPanel();
        root.setSize(162, 54);
        setRootPanel(root);

        root.add(WItemSlot.of(blockInventory, 0), 45, 30);
        root.add(new WItemSlot(blockInventory, 1, 1, 1, true), 108, 31);

        root.add(new WBar(new Identifier(LookingGlassCommon.MODID, "textures/gui/progress/electric_smelting_arrow.png"), new Identifier(LookingGlassCommon.MODID, "textures/gui/progress/electric_smelting_arrow_full.png"), 0, 1, WBar.Direction.RIGHT), 72, 32, 22, 15);

        root.add(new WBar(new Identifier(LookingGlassCommon.MODID, "textures/gui/energy/small_energy_bar_dwarven.png"), new Identifier(LookingGlassCommon.MODID, "textures/gui/energy/small_energy_bar_dwarven_full.png"), 2, 3), 9, 21, 16, 32);

        root.add(this.createPlayerInventoryPanel(), 0, 66);
        root.validate(this);
    }

    @Override
    public void addPainters() {
        if (this.rootPanel != null && !this.fullscreen) {
            this.rootPanel.setBackgroundPainter(BackgroundTypes.DWARVEN);
        }
    }
}
