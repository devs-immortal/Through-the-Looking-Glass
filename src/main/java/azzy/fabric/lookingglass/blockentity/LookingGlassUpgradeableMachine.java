package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.util.machine.ModifierProvider;
import azzy.fabric.lookingglass.util.machine.RecipeConversionProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import org.jetbrains.annotations.Nullable;

public abstract class LookingGlassUpgradeableMachine extends LookingGlassMachine {

    private final double basePowerUsage;
    private final int baseProcessTime;
    private final RecipeType<?> recipeType;

    public LookingGlassUpgradeableMachine(BlockEntityType<?> type, BlockPos pos, BlockState state, RecipeType<?> recipeType, MachineTier tier, int invSize, int baseProcessTime, double baseMaxPower, double basePowerUsage) {
        super(type, pos, state, tier, invSize + 4, baseMaxPower);
        this.basePowerUsage = basePowerUsage;
        this.baseProcessTime = baseProcessTime;
        this.recipeType = recipeType;
    }

    public RecipeType getRecipeType() {
        for(int i = 1; i < 5; i++) {
            Item upgrade = inventory.get(inventory.size() - i).getItem();
            if(upgrade instanceof RecipeConversionProvider)
                return ((RecipeConversionProvider) upgrade).getRecipeType();
        }
        return recipeType;
    }

    public double getPowerUsage() {
        double usage = basePowerUsage;

        double addMult = 1;
        double allMult = 1;

        for(int i = 1; i < 5; i++) {
            ItemStack stack = inventory.get(inventory.size() - i);
            if(stack.getItem() == Items.AIR)
                continue;
            ModifierProvider provider = (ModifierProvider) stack.getItem();
            if(provider.getMinMachineTier().tier <= machineTier.tier) {
                double effect = provider.getModifier(ModifierProvider.EffectType.POWER);
                switch (provider.getAdditivityType()) {
                    case ADD: usage += basePowerUsage * (effect - 1); break;
                    case ADD_MULT: addMult += effect; break;
                    case EXP: allMult += effect;
                }
            }
        }
        usage *= addMult;
        usage = Math.pow(usage, allMult);
        return usage;
    }

    @Override
    public double getEnergyCapacity() {
        double storage = baseMaxPower;

        double addMult = 1;
        double allMult = 1;

        for(int i = 1; i < 5; i++) {
            ItemStack stack = inventory.get(inventory.size() - i);
            if(stack.getItem() == Items.AIR)
                continue;
            ModifierProvider provider = (ModifierProvider) stack.getItem();
            if(provider.getMinMachineTier().tier <= machineTier.tier) {
                double effect = provider.getModifier(ModifierProvider.EffectType.STORE);
                switch (provider.getAdditivityType()) {
                    case ADD:
                    case ADD_MULT: addMult += effect; break;
                    case EXP: allMult += effect;
                }
            }
        }
        storage *= addMult;
        storage = Math.pow(storage, allMult);

        if(power > storage) {
            power = storage;
            if(!world.isClient())
                sync();
            markDirty();
        }

        return storage;
    }

    public int getProcessTime() {
        int time = baseProcessTime;

        double addMult = 1;
        double allMult = 1;

        for(int i = 1; i < 5; i++) {
            ItemStack stack = inventory.get(inventory.size() - i);
            if(stack.getItem() == Items.AIR)
                continue;
            ModifierProvider provider = (ModifierProvider) stack.getItem();
            if(provider.getMinMachineTier().tier <= machineTier.tier) {
                double effect = provider.getModifier(ModifierProvider.EffectType.SPEED);
                switch (provider.getAdditivityType()) {
                    case ADD: time += baseProcessTime * (effect - 1); break;
                    case ADD_MULT: addMult += effect; break;
                    case EXP: allMult += effect;
                }
            }
        }
        time *= addMult;
        time = (int) Math.pow(time, allMult);
        return time <= 0 ? 1 : time;
    }

    @Override
    public boolean canInsert(int slot, ItemStack stack, @Nullable Direction dir) {
        return slot < inventory.size() - 4;
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return slot < inventory.size() - 4;
    }

    @Override
    public int[] getAvailableSlots(Direction side) {
        int[] result = new int[inventory.size() - 4];
        for (int i = 0; i < result.length; i++) {
            result[i] = i;
        }
        return result;
    }
}
