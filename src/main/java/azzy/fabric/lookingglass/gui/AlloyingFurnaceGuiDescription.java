package azzy.fabric.lookingglass.gui;

import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.util.client.BackgroundType;
import io.github.cottonmc.cotton.gui.widget.WBar;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.Identifier;

public class AlloyingFurnaceGuiDescription extends UpgradeableMachineGuiDescription {

    public AlloyingFurnaceGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(LookingGlassGUIs.ALLOYING_FURNACE_HANDLER, syncId, 7, 4, 3, 2, playerInventory, context);
    }

    @Override
    protected void draw(WPlainPanel root) {
        root.add(WItemSlot.of(blockInventory, 0), 36, 28);
        root.add(WItemSlot.of(blockInventory, 1), 36, 46);
        root.add(new WItemSlot(blockInventory, 2, 1, 1, true), 99, 37);
        root.add(new WBar(new Identifier(LookingGlassCommon.MODID, "textures/gui/progress/electric_smelting_arrow.png"), new Identifier(LookingGlassCommon.MODID, "textures/gui/progress/electric_smelting_arrow_full.png"), 0, 1, WBar.Direction.RIGHT), 64, 39, 22, 15);
    }

    @Override
    protected BackgroundType getBackground() {
        return BackgroundTypes.DWARVEN;
    }
}
