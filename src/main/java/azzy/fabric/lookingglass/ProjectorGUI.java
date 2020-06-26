package azzy.fabric.lookingglass;

import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WDynamicLabel;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.data.Alignment;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.container.BlockContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;

public class ProjectorGUI extends CottonCraftingController {

    private WPlainPanel root = new WPlainPanel();
    private ExtendedPropertyDelegate delegate;
    private WDynamicLabel stateLabel;
    private int state;
    private String label;

    public ProjectorGUI(RecipeType<?> recipeType, int syncId, PlayerInventory playerInventory, BlockContext context) {
        super(recipeType, syncId, playerInventory, getBlockInventory(context), getBlockPropertyDelegate(context));
        delegate = (ExtendedPropertyDelegate) getPropertyDelegate();
        state = delegate.get(0);

        root.setSize(162, 120);
        setRootPanel(root);
        root.add(this.createPlayerInventoryPanel(), 1, 120+12);
        root.add(new WLabel("Inventory", 16776693), 1, 120);

        switch(state){
            case(0): label = "Image"; break;
            case(1): label = "Item"; break;
            case(2): label = "Sign"; break;
            case(3): label = "Video (TBD)"; break;
            case(4): label = "Player (TBD)"; break;
        }

        if(world.isClient) {
            stateLabel = new WDynamicLabel(() -> I18n.translate("label.lookingglass.mode", label));
            stateLabel.setSize(100, 20);
            stateLabel.setAlignment(Alignment.CENTER);

            root.add(stateLabel, 72, 10);
            if (state == 0) {

            }
        }
        root.validate(this);
    }

    private void onStateChange(){
        if(state < 4){
            delegate.set(0, state+1);
            return;
        }
        delegate.set(0, 0);
    }

}
