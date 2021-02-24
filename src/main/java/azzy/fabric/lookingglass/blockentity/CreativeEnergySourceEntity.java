package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.LookingGlassBlock;
import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.energy.EnergyIo;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;

public class CreativeEnergySourceEntity extends BlockEntity implements EnergyIo {

    public static final double CREATIVE_LUMEN_CHARGE = Double.POSITIVE_INFINITY;
    public static final ItemStack REDSPEEN = new ItemStack(Items.REDSTONE_BLOCK);

    public CreativeEnergySourceEntity() {
        super(LookingGlassBlocks.CREATIVE_ENERGY_SOURCE_ENTITY);
    }

    @Override
    public double getEnergy() {
        return CREATIVE_LUMEN_CHARGE;
    }

    @Override
    public double extract(double maxAmount, Simulation simulation) {
        return maxAmount;
    }

    @Override
    public boolean supportsExtraction() {
        return !getCachedState().get(LookingGlassBlock.POWERED);
    }
}
