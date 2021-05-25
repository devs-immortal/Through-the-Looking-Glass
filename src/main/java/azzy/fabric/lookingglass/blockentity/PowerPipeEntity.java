package azzy.fabric.lookingglass.blockentity;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.energy.EnergyApi;
import dev.technici4n.fasttransferlib.api.energy.EnergyIo;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class PowerPipeEntity extends BasePipeEntity<EnergyIo> implements EnergyIo {

    private final double maxPower, transferRate;
    private double power;


    public PowerPipeEntity(BlockEntityType type, double transferRate) {
        super(type, EnergyApi.SIDED, true);
        this.maxPower = transferRate * 2.0;
        this.transferRate = transferRate;
    }

    @Override
    public void performTransfers(Set<EnergyIo> participants) {
        for (EnergyIo energyIo : participants.toArray(new EnergyIo[0])) {
            if(energyIo instanceof PowerPipeEntity) {
                continue;
            }
            if(power > 0 && energyIo.supportsInsertion()) {
                double selfDrain = extract(transferRate, Simulation.SIMULATE);
                double cap = energyIo.insert(selfDrain, Simulation.SIMULATE);
                double transfer = selfDrain - cap;
                power -= transfer;
                energyIo.insert(transfer, Simulation.ACT);
            }
            else if (energyIo.getEnergy() > 0 && energyIo.supportsExtraction()) {
                double succ = energyIo.extract(transferRate, Simulation.SIMULATE);
                double selfCap = insert(succ, Simulation.SIMULATE);
                double transfer = succ - selfCap;
                power += transfer;
                energyIo.insert(transfer, Simulation.ACT);
            }
        }

        List<PowerPipeEntity> cables = new ArrayList<>();

        if(!ticked) {
            getNeighbours().forEach(wire -> cables.add((PowerPipeEntity) wire));
        }

        if(cables.size() > 1) {
            double totalPower = cables.stream().mapToDouble(EnergyIo::getEnergy).sum();
            double distributedPower = totalPower / cables.size();

            cables.forEach(cable -> cable.setEnergy(distributedPower));
        }

        markDirty();
        if(world != null && !world.isClient()) {
            sync();
        }

        ticked = false;
    }

    public double getTransferRate() {
        return transferRate;
    }

    private void setEnergy(double power) {
        this.power = power;
    }

    @Override
    public double getEnergyCapacity() {
        return maxPower;
    }

    @Override
    public double getEnergy() {
        return power;
    }

    @Override
    public boolean supportsInsertion() {
        return true;
    }

    @Override
    public boolean supportsExtraction() {
        return true;
    }

    @Override
    public double insert(double amount, Simulation simulation) {
        if (simulation == Simulation.SIMULATE) {
            return Math.max(0, amount - (Math.min(maxPower - power, transferRate)));
        }
        double remnant = Math.max(0, amount - Math.min(maxPower - power, transferRate));
        power =  power + (amount - remnant);
        markDirty();
        if(world != null && !world.isClient()) {
            sync();
        }
        return remnant;
    }

    @Override
    public double extract(double maxAmount, Simulation simulation) {
        if(simulation == Simulation.SIMULATE) {
            return Math.min(power, maxAmount);
        }
        double drain = Math.min(power, maxAmount);
        power -= drain;
        markDirty();
        if(world != null && !world.isClient()) {
            sync();
        }
        return drain;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putDouble("power", power);
        return super.toTag(tag);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        power = tag.getDouble("power");
        super.fromTag(state, tag);
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        power = tag.getDouble("power");
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        tag.putDouble("power", power);
        return tag;
    }
}
