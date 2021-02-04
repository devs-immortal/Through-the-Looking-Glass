package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.TTLGBlocks;
import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.energy.EnergyApi;
import dev.technici4n.fasttransferlib.api.energy.EnergyIo;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Set;

public class PowerPipeEntity extends BasePipeEntity<EnergyIo> implements EnergyIo {

    private final double maxPower, transferRate;
    private double power;

    public PowerPipeEntity(BlockEntityType type, double transferRate) {
        super(type, EnergyApi.SIDED);
        this.maxPower = transferRate * 4.0;
        this.transferRate = transferRate;
    }

    @Override
    public void performTransfers(Set<EnergyIo> participants) {
        EnergyIo[] io = participants.toArray(new EnergyIo[0]);
        List<EnergyIo> ioList = Arrays.asList(io);
        Collections.shuffle(Arrays.asList(io));
        io = ioList.toArray(new EnergyIo[0]);
        for (EnergyIo energyIo : io) {
            boolean pipe = energyIo instanceof PowerPipeEntity;
            if(power > 0 && energyIo.supportsInsertion()) {
                if(!(pipe && energyIo.getEnergy() > power)) {
                    double selfDrain = extract(transferRate, Simulation.SIMULATE);
                    double cap = energyIo.insert(selfDrain, Simulation.SIMULATE);
                    double transfer = selfDrain - cap;
                    power -= transfer;
                    energyIo.insert(transfer, Simulation.ACT);
                }
            }
            else if (!pipe && energyIo.getEnergy() > 0 && energyIo.supportsExtraction()) {
                double succ = energyIo.extract(transferRate, Simulation.SIMULATE);
                double selfCap = insert(succ, Simulation.SIMULATE);
                double transfer = succ - selfCap;
                power += transfer;
                energyIo.insert(transfer, Simulation.ACT);
            }
        }
        markDirty();
        if(!world.isClient()) {
            sync();
        }
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
        if(!world.isClient()) {
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
        if(!world.isClient()) {
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
        power = tag.getDouble("energy");
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
