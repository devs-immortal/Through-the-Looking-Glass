package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.util.ModifierProvider;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

public abstract class LookingGlassUpgradeableMachine extends LookingGlassMachine {

    private final double basePowerUsage;

    public LookingGlassUpgradeableMachine(BlockEntityType<?> type, MachineTier tier, int invSize, double baseMaxPower, double basePowerUsage) {
        super(type, tier, invSize + 4, baseMaxPower);
        this.basePowerUsage = basePowerUsage;
    }

    public double getPowerUsage() {
        double usage = basePowerUsage;

        List<Double> addMults = new ArrayList<>();
        double allMult = 0;

        for(int i = 1; i < 5; i++) {
            ItemStack stack = inventory.get(inventory.size() - i);
            ModifierProvider provider = (ModifierProvider) stack.getItem();
            if(provider.getMinMachineTier().tier <= machineTier.tier) {
                double effect = provider.getModifier(ModifierProvider.EffectType.POWER);
                switch (provider.getAdditivityType()) {
                    case ADD: usage += basePowerUsage * (effect - 1); break;
                    case ADD_MULT: addMults.add(effect);
                    case ALL_MULT: allMult += effect;
                }
            }
        }

        return usage;
    }

    @Override
    public double getEnergyCapacity() {
        return baseMaxPower;
    }
}
