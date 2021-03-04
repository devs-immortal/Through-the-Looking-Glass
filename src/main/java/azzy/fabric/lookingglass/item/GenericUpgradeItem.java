package azzy.fabric.lookingglass.item;

import azzy.fabric.lookingglass.blockentity.LookingGlassMachine;
import azzy.fabric.lookingglass.gui.UpgradeableMachineGuiDescription;
import azzy.fabric.lookingglass.util.machine.ModifierProvider;
import net.minecraft.item.Item;

public class GenericUpgradeItem extends Item implements ModifierProvider {

    private final LookingGlassMachine.MachineTier machineTier;
    private final AdditivityType additivity, speedAdd;
    private final double basePowerEff, baseStorageEff, baseSpeedEff;

    public GenericUpgradeItem(Settings settings, double basePowerEff, double baseStorageEff, double baseSpeedEff, AdditivityType additivity, AdditivityType speedAdd, LookingGlassMachine.MachineTier minMachineTier) {
        super(settings);
        this.basePowerEff = basePowerEff;
        this.baseStorageEff = baseStorageEff;
        this.baseSpeedEff = baseSpeedEff;
        this.machineTier = minMachineTier;
        this.additivity = additivity;
        this.speedAdd = speedAdd;
    }

    @Override
    public double getModifier(EffectType type) {
        switch (type) {
            case POWER: return basePowerEff;
            case SPEED: return baseSpeedEff;
            case STORE: return baseStorageEff;
            default: return 1;
        }
    }

    @Override
    public AdditivityType getAdditivityType() {
        return additivity;
    }

    @Override
    public AdditivityType getSpeedAdditivityType() {
        return speedAdd;
    }

    @Override
    public boolean canEquip(UpgradeableMachineGuiDescription gui) {
        return true;
    }

    @Override
    public LookingGlassMachine.MachineTier getMinMachineTier() {
        return machineTier;
    }
}
