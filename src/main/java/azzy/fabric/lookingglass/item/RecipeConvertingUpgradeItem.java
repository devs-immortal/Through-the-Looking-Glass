package azzy.fabric.lookingglass.item;

import azzy.fabric.lookingglass.blockentity.LookingGlassMachine;
import azzy.fabric.lookingglass.gui.PoweredFurnaceGuiDescription;
import azzy.fabric.lookingglass.gui.UpgradeableMachineGuiDescription;
import azzy.fabric.lookingglass.recipe.LookingGlassRecipe;
import azzy.fabric.lookingglass.util.RecipeConversionProvider;
import net.minecraft.recipe.RecipeType;

public class RecipeConvertingUpgradeItem<T extends UpgradeableMachineGuiDescription> extends GenericUpgradeItem implements RecipeConversionProvider {

    private final RecipeType<?> recipeType;
    private final Class<T> guiType;

    public RecipeConvertingUpgradeItem(Settings settings, RecipeType<?> recipeType, Class<T> guiType, double basePowerEff, double baseStorageEff, double baseSpeedEff, AdditivityType additivity, AdditivityType speedAdd) {
        super(settings, basePowerEff, baseStorageEff, baseSpeedEff, additivity, speedAdd, LookingGlassMachine.MachineTier.BASIC);
        this.recipeType = recipeType;
        this.guiType = guiType;
    }

    @Override
    public boolean canEquip(UpgradeableMachineGuiDescription gui) {
        return guiType.isInstance(gui);
    }

    @Override
    public RecipeType<?> getRecipeType() {
        return recipeType;
    }
}
