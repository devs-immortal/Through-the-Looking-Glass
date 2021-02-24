package azzy.fabric.lookingglass.gui;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;

public class CrateGuiDescription extends SyncedGuiDescription {

    public CrateGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(LookingGlassGUIs.CRATE_HANDLER, syncId, playerInventory, getBlockInventory(context, 117), getBlockPropertyDelegate(context));

        WPlainPanel root = new WPlainPanel();
        root.setSize(234, 162);
        setRootPanel(root);
        WItemSlot itemSlot;
        int s = 0;
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 13; j++) {
                itemSlot = WItemSlot.of(blockInventory, s);
                root.add(itemSlot, j * 18, 13 + i * 18);
                s++;
            }
        }

        root.add(this.createPlayerInventoryPanel(), 36, 178);
        root.validate(this);
    }

    @Override
    public void close(PlayerEntity player) {
        world.playSound(player, player.getBlockPos(), SoundEvents.BLOCK_FENCE_GATE_CLOSE, SoundCategory.BLOCKS, 1F, 0.9F);
        super.close(player);
    }
}
