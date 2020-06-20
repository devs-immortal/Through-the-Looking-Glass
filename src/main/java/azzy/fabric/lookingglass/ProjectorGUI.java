package azzy.fabric.lookingglass;

import io.github.cottonmc.cotton.gui.CottonCraftingController;
import io.github.cottonmc.cotton.gui.widget.WButton;
import io.github.cottonmc.cotton.gui.widget.WLabel;
import io.github.cottonmc.cotton.gui.widget.WPlainPanel;
import io.github.cottonmc.cotton.gui.widget.data.Alignment;
import net.minecraft.container.BlockContext;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.recipe.RecipeType;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;

public class ProjectorGUI extends CottonCraftingController {

    private WPlainPanel root = new WPlainPanel();
    private ExtendedPropertyDelegate delegate;
    private int state;

    public ProjectorGUI(RecipeType<?> recipeType, int syncId, PlayerInventory playerInventory, BlockContext context) {
        super(recipeType, syncId, playerInventory, getBlockInventory(context), getBlockPropertyDelegate(context));
        delegate = (ExtendedPropertyDelegate) getPropertyDelegate();
        state = delegate.get(0);

        root.setSize(162, 120);
        setRootPanel(root);
        root.add(this.createPlayerInventoryPanel(), 1, 120+12);
        root.add(new WLabel("Inventory", 16776693), 1, 120);
        WButton stateSelector = new WButton();
        stateSelector.setAlignment(Alignment.CENTER);
        stateSelector.setLabel(new LiteralText(""+state));
        root.add(stateSelector, 35, 100);
        stateSelector.setSize(100, 20);
        stateSelector.setOnClick(this::onStateChange);
        if(state == 0){

        }
        root.validate(this);
    }

    private void onStateChange(){
        if(state < 3){
            delegate.set(0, state+1);
            return;
        }
        delegate.set(0, 0);
    }

}
