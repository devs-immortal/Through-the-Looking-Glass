package azzy.fabric.lookingglass.gui;

import azzy.fabric.lookingglass.blockentity.ProjectorEntity;
import azzy.fabric.lookingglass.util.GeneralNetworking;
import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import io.github.cottonmc.cotton.gui.widget.data.Insets;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.text.LiteralText;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class NewProjectorGuiDescription extends SyncedGuiDescription {

    private final WTextField url = new WTextField().setMaxLength(512);
    private final WTextField disX = new WTextField().setMaxLength(4);
    private final WTextField disY = new WTextField().setMaxLength(4);
    private final WTextField disZ = new WTextField().setMaxLength(4);
    private final WTextField sign = new WTextField().setMaxLength(2048);
    private final WTextField color = new WTextField().setMaxLength(32);
    private final WTextField rotX = new WTextField().setMaxLength(7);
    private final WTextField rotY = new WTextField().setMaxLength(7);
    private final WTextField rotZ = new WTextField().setMaxLength(7);
    private final WTextField scale = new WTextField().setMaxLength(6);
    private BlockPos pos = null;

    public NewProjectorGuiDescription(int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(LookingGlassGUIs.PROJECTOR_HANDLER, syncId, playerInventory, getBlockInventory(context, 1), getBlockPropertyDelegate(context));

        WPlainPanel root = new WPlainPanel();
        root.setSize(162, 200);
        root.setInsets(Insets.ROOT_PANEL);
        setRootPanel(root);

        root.add(this.createPlayerInventoryPanel(), 1, 212);

        ProjectorEntity entity = (ProjectorEntity) world.getBlockEntity(new BlockPos(propertyDelegate.get(3), propertyDelegate.get(4), propertyDelegate.get(5)));

        if(entity == null)
            return;

        pos = entity.getPos();

        int state = entity.displayState;

        disX.setText(String.valueOf(entity.disX));
        disY.setText(String.valueOf(entity.disY));
        disZ.setText(String.valueOf(entity.disZ));
        sign.setText(entity.sign);
        color.setText(entity.color);
        rotX.setText(String.valueOf(entity.rotX));
        rotY.setText(String.valueOf(entity.rotY));
        rotZ.setText(String.valueOf(entity.rotZ));
        scale.setText(String.valueOf(entity.scale));

        root.add(new WText(new LiteralText("X")), 5, 76, 26, 10);
        root.add(new WText(new LiteralText("Y")), 5, 106, 26, 10);
        root.add(new WText(new LiteralText("Z")), 5, 136, 26, 10);

        root.add(new WText(new LiteralText("Offset")), 20, 55, 55, 10);
        root.add(new WText(new LiteralText("Rotation")), 90, 55, 55, 10);

        root.add(rotX, 90, 70, 55, 10);
        root.add(rotY, 90, 100, 55, 10);
        root.add(rotZ, 90, 130, 55, 10);
        root.add(disX, 20, 70, 55, 10);
        root.add(disY, 20, 100, 55, 10);
        root.add(disZ, 20, 130, 55, 10);

        root.add(scale, 20, state == 2 ? 185 : 165, 125, 40);

        if(state == 1 || state >= 3)
            root.add(new WItemSlot(blockInventory, 0, 1, 1, true), 70, 27);

        String label;

        switch(state){
            case(0): label = "Mode: Image Display"; break;
            case(1): label = "Mode: Item Display"; break;
            case(2): label = "Mode: Spectral Sign"; break;
            case(3): label = "Mode: Mob Renderer"; break;
            default: label = "Mode: Projector"; break;
        }

        WDynamicLabel stateLabel = new WDynamicLabel(() -> label);
        stateLabel.setSize(100, 20);
        stateLabel.setAlignment(HorizontalAlignment.CENTER);
        root.add(stateLabel, 72, 10);

        url.setSuggestion("URL");
        url.setSize(160, 5);
        url.setText(entity.url);

        sign.setSuggestion("Text");
        sign.setSize(160, 5);
        sign.setText(entity.sign);
        color.setSuggestion("Hex");

        if (state == 0) {
            root.add(url, 2, 23, 160, 5);
        }

        if(state == 2){
            root.add(sign, 2, 23, 160, 5);
            root.add(color, 53, 160, 60, 5);
        }
        root.validate(this);
    }

    @Override
    public void close(PlayerEntity player) {
        World world = player.getEntityWorld();

        if(pos != null) {
            if(world.isClient()) {
                try {
                    PacketByteBuf buf = PacketByteBufs.create();
                    buf.writeBlockPos(pos);
                    buf.writeDouble(Double.parseDouble(rotX.getText()));
                    buf.writeDouble(Double.parseDouble(rotY.getText()));
                    buf.writeDouble(Double.parseDouble(rotZ.getText()));
                    buf.writeDouble(Double.parseDouble(disX.getText()));
                    buf.writeDouble(Double.parseDouble(disY.getText()));
                    buf.writeDouble(Double.parseDouble(disZ.getText()));
                    buf.writeDouble(Double.parseDouble(scale.getText()));
                    buf.writeString(url.getText());
                    buf.writeString(sign.getText());
                    buf.writeString(color.getText());
                    ClientPlayNetworking.send(GeneralNetworking.PROJECTOR_SYNC_PACKET, buf);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

        super.close(player);
    }
}
