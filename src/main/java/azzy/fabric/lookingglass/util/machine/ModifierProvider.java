package azzy.fabric.lookingglass.util.machine;

import azzy.fabric.lookingglass.blockentity.LookingGlassMachine;
import azzy.fabric.lookingglass.gui.UpgradeableMachineGuiDescription;

public interface ModifierProvider {

    double getModifier(EffectType type);

    AdditivityType getAdditivityType();

    AdditivityType getSpeedAdditivityType();

    boolean canEquip(UpgradeableMachineGuiDescription gui);

    LookingGlassMachine.MachineTier getMinMachineTier();


    enum EffectType {
        SPEED,
        POWER,
        STORE
    }
    enum AdditivityType {
        ADD,
        ADD_MULT,
        EXP
    }
}
