package azzy.fabric.lookingglass;

import io.github.cottonmc.cotton.gui.SyncedGuiDescription;
import io.github.cottonmc.cotton.gui.widget.*;
import io.github.cottonmc.cotton.gui.widget.data.HorizontalAlignment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

import java.util.function.Consumer;

public class ProjectorGUI extends SyncedGuiDescription {

    private WPlainPanel root = new WPlainPanel();
    private ExtendedPropertyDelegate delegate;
    private WDynamicLabel stateLabel;
    private WTextField url = new WTextField();
    private int state;
    private String label;

    public ProjectorGUI(ScreenHandlerType recipeType, int syncId, PlayerInventory playerInventory, ScreenHandlerContext context) {
        super(recipeType, syncId, playerInventory, getBlockInventory(context), getBlockPropertyDelegate(context));
        delegate = (ExtendedPropertyDelegate) getPropertyDelegate();
        state = delegate.get(0);

        root.setSize(162, 120);
        setRootPanel(root);
        root.add(this.createPlayerInventoryPanel(), 1, 120+12);

        switch(state){
            case(0): label = "Image"; break;
            case(1): label = "Item"; break;
            case(2): label = "Sign"; break;
            case(3): label = "Video (TBD)"; break;
            case(4): label = "Player (TBD)"; break;
        }

        if(state == 0){
            url.setText(delegate.getString(1));
            root.add(url, 30, 40, 80, 10);
            root.add(new WItemSlot(blockInventory, 0, 1, 1, false), 60, 60);
        }

        if(world.isClient) {
            stateLabel = new WDynamicLabel(() -> I18n.translate("label.lookingglass.mode", label));
            stateLabel.setSize(100, 20);
            stateLabel.setAlignment(HorizontalAlignment.CENTER);

            root.add(stateLabel, 72, 10);
            if (state == 0) {

            }
        }
        root.validate(this);
    }

    @Override
    public void close(PlayerEntity player) {
        delegate.setString(1, url.getText());
        super.close(player);
    }

    private void onStateChange(){
        if(state < 4){
            delegate.set(0, state+1);
            return;
        }
        delegate.set(0, 0);
    }

}
