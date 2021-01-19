package azzy.fabric.lookingglass.gui;

import azzy.fabric.lookingglass.block.TTLGBlocks;
import azzy.fabric.lookingglass.blockentity.CrateEntity;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;

public class CrateGuiDescription extends SyncedGuiDescription {

    private final WPlainPanel root = new WPlainPanel();

    public CrateGuiDescription(ScreenHandlerType recipeType, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(recipeType, syncId, playerInventory, getBlockInventory(context, 117), getBlockPropertyDelegate(context));

        root.setSize(234, 162);
        setRootPanel(root);
        root.add(this.createPlayerInventoryPanel(), 36, 178);
        root.add(new WLabel(I18n.translate(TTLGBlocks.CRATE_BLOCK.getTranslationKey())), 0, 0);
        WItemSlot itemSlot;
        int s = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 13; j++) {
                itemSlot = WItemSlot.of(blockInventory, s);
                root.add(itemSlot, j * 18, 13 + i * 18);
                s++;
            }
        }
    }
}
