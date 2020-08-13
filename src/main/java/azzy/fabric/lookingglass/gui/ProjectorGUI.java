package azzy.fabric.lookingglass.gui;

import azzy.fabric.lookingglass.util.ExtendedPropertyDelegate;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.Axis;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.TranslatableText;


public class ProjectorGUI extends SyncedGuiDescription {

    private WPlainPanel root = new WPlainPanel();
    private ExtendedPropertyDelegate delegate;
    private WDynamicLabel stateLabel;
    private WTextField url = new WTextField().setMaxLength(512);
    private WTextField disX = new WTextField().setMaxLength(4);
    private WTextField disY = new WTextField().setMaxLength(4);
    private WTextField disZ = new WTextField().setMaxLength(4);
    private WTextField sign = new WTextField().setMaxLength(2048);
    private WTextField color = new WTextField().setMaxLength(32);
    private WSlider rotX = new WSlider(0, 360, Axis.HORIZONTAL);
    private WSlider rotY = new WSlider(0, 360, Axis.HORIZONTAL);
    private WSlider rotZ = new WSlider(0, 360, Axis.HORIZONTAL);
    private WSlider scale = new WSlider(1, 200, Axis.HORIZONTAL);

    private final int state;
    private String label;

    public ProjectorGUI(ScreenHandlerType recipeType, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(recipeType, syncId, playerInventory, getBlockInventory(context), getBlockPropertyDelegate(context));
        delegate = (ExtendedPropertyDelegate) getPropertyDelegate();
        state = delegate.get(0);

        root.setSize(162, 200);
        setRootPanel(root);
        root.add(this.createPlayerInventoryPanel(), 1, 212);

        disX.setText(String.valueOf(delegate.get(6)));
        disY.setText(String.valueOf(delegate.get(7)));
        disZ.setText(String.valueOf(delegate.get(8)));
        sign.setText(delegate.getString(0));
        color.setText(delegate.getString(2));
        rotX.setValue(delegate.get(3));
        rotY.setValue(delegate.get(4));
        rotZ.setValue(delegate.get(5));
        scale.setValue(delegate.get(9));

        root.add(new WText(new LiteralText("X")), 5, 66, 26, 10);
        root.add(new WText(new LiteralText("Y")), 5, 96, 26, 10);
        root.add(new WText(new LiteralText("Z")), 5, 126, 26, 10);

        root.add(rotX, 55, 65, 90, 10);
        root.add(rotY, 55, 95, 90, 10);
        root.add(rotZ, 55, 125, 90, 10);
        root.add(disX, 20, 60, 26, 10);
        root.add(disY, 20, 90, 26, 10);
        root.add(disZ, 20, 120, 26, 10);

        root.add(scale, 21, 160, 125, 40);


        switch(state){
            case(0): label = "Image"; break;
            case(1): label = "Item"; break;
            case(2): label = "Sign"; break;
            case(3): label = "Video (TBD)"; break;
            case(4): label = "Player (TBD)"; break;
        }
        if(state == 1)
            root.add(new WItemSlot(blockInventory, 0, 1, 1, true), 70, 27);


        if(world.isClient) {
            stateLabel = new WDynamicLabel(() -> I18n.translate("label.lookingglass.mode", label));
            stateLabel.setSize(100, 20);
            stateLabel.setAlignment(HorizontalAlignment.CENTER);

            root.add(stateLabel, 72, 10);
            if (state == 0) {
                url.setSuggestion("URL");
                url.setSize(160, 5);
                url.setText(delegate.getString(1));
                root.add(url, 2, 23, 160, 5);
            }

            if(state == 2){
                sign.setSuggestion("Message");
                sign.setSize(160, 5);
                sign.setText(delegate.getString(0));
                color.setSuggestion("Hex color");
                root.add(sign, 2, 23, 160, 5);
                root.add(color, 53, 150, 60, 5);
            }
        }
        root.validate(this);
    }

    @Override
    public void close(PlayerEntity player) {

        if(!world.isClient())
            super.close(player);

        delegate.set(1, rotX.getValue());
        delegate.set(2, rotY.getValue());
        delegate.set(3, rotZ.getValue());
        try{
            delegate.set(4, Integer.parseInt(disX.getText()));
            delegate.set(5, Integer.parseInt(disY.getText()));
            delegate.set(6, Integer.parseInt(disZ.getText()));
        } catch (NumberFormatException e){
            MinecraftClient.getInstance().player.sendSystemMessage(new TranslatableText("label.lookingglass.wrong"), null);
        }
        delegate.set(7, scale.getValue());

        if(state == 0)
            delegate.setString(1, url.getText());

        else if(state == 2) {
            delegate.setString(0, sign.getText());
            delegate.setString(2, color.getText());
        }

        super.close(player);
    }

}
