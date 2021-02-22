package azzy.fabric.lookingglass.item;

import azzy.fabric.lookingglass.blockentity.LookingGlassMachine;
import azzy.fabric.lookingglass.gui.UpgradeableMachineGuiDescription;
import azzy.fabric.lookingglass.util.ModifierProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;

public class GenericUpgradeItem extends Item implements ModifierProvider<BlockEntity> {

    private final LookingGlassMachine.MachineTier machineTier;
    private final AdditivityType additivity;
    private final double basePowerEff, baseStorageEff, baseSpeedEff;
    private final boolean exponential;

    public GenericUpgradeItem(Settings settings, double basePowerEff, double baseStorageEff, double baseSpeedEff, boolean exponential, AdditivityType additivity, LookingGlassMachine.MachineTier minMachineTier) {
        super(settings);
        this.basePowerEff = basePowerEff;
        this.baseStorageEff = baseStorageEff;
        this.baseSpeedEff = baseSpeedEff;
        this.exponential = exponential;
        this.machineTier = minMachineTier;
        this.additivity = additivity;
    }

    @Override
    public double getModifier(EffectType type) {
        switch (type) {
            case POWER: return baseStorageEff * tier;
            case SPEED: return baseSpeedEff * tier;
            case STORE: return exponential ? Math.pow(basePowerEff, tier) : basePowerEff * tier;
            default: return 1;
        }
    }

    @Override
    public AdditivityType getAdditivityType() {
        return additivity;
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
