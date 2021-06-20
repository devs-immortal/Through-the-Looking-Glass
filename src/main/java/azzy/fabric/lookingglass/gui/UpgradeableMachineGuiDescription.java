package azzy.fabric.lookingglass.gui;

import azzy.fabric.lookingglass.util.client.BackgroundType;
import azzy.fabric.lookingglass.util.machine.ModifierProvider;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.WBar;
import io.github.cottonmc.cotton.gui.widget.WItemSlot;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

import static azzy.fabric.lookingglass.LookingGlassCommon.MODID;

public abstract class UpgradeableMachineGuiDescription extends SyncedGuiDescription {

    public static final Identifier LUMEN_BAR_FULL = new Identifier(MODID, "textures/gui/energy/lumen_bar_overlay.png");

    private final WPlainPanel root;
    private final int powerIndex;

    public UpgradeableMachineGuiDescription(ScreenHandlerType<?> type, int syncId, int invSize, int delegateSize, int upgradeIndex, int powerIndex, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(type, syncId, playerInventory, getBlockInventory(context, invSize), getBlockPropertyDelegate(context, delegateSize));

        this.powerIndex = powerIndex;
        root = new WPlainPanel();
        root.setSize(162, 72);
        root.setInsets(Insets.ROOT_PANEL);
        setRootPanel(root);

        draw(root);

        for(int i = 0; i < 4; i++) {
            WItemSlot slot = WItemSlot.of(blockInventory, upgradeIndex + i);
            slot.setFilter(stack -> stack.getItem() instanceof ModifierProvider && ((ModifierProvider) stack.getItem()).canEquip(this));
            root.add(slot, 135, 18 * (i + 1) - 9);
        }

        root.add(this.createPlayerInventoryPanel(), 0, 81);
        root.validate(this);
    }

    protected abstract void draw(WPlainPanel root);

    @Environment(EnvType.CLIENT)
    protected abstract BackgroundType getBackground();

    @Override
    public void addPainters() {
        if (this.rootPanel != null && !this.fullscreen) {
            this.rootPanel.setBackgroundPainter(getBackground().painter);
            root.add(new WBar(getBackground().lumenTexture, LUMEN_BAR_FULL, powerIndex, powerIndex + 1), 10, 20, 14, 50);
        }
    }
}
