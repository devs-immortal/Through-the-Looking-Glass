package azzy.fabric.lookingglass.blockentity;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.energy.EnergyApi;
import dev.technici4n.fasttransferlib.api.energy.EnergyIo;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.Direction;

import java.util.*;
import java.util.stream.Stream;

public class PowerPipeEntity extends BasePipeEntity<EnergyIo> implements EnergyIo {

    private final Direction[] directions = Direction.values();
    private final double maxPower, transferRate;
    private double power;
    private boolean ticked;

    public PowerPipeEntity(BlockEntityType type, double transferRate) {
        super(type, EnergyApi.SIDED, true);
        this.maxPower = transferRate * 4.0;
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

        //Taken from Techreborn's CableBlockEntity, some of it at least

        List<PowerPipeEntity> cables = new ArrayList<>();

        if(!ticked) {
            cables.addAll(getNeighbours());
        }

        if(cables.size() > 1) {
            double totalPower = cables.stream().mapToDouble(EnergyIo::getEnergy).sum();
            double distributedpower = totalPower / cables.size();

            cables.forEach(cable -> cable.setEnergy(distributedpower));
        }

        markDirty();
        if(!world.isClient()) {
            sync();
        }

        ticked = false;
    }

    public double getTransferRate() {
        return transferRate;
    }

    public Set<PowerPipeEntity> getNeighbours() {
        Set<PowerPipeEntity> neighbours = new HashSet<>();
        Queue<PowerPipeEntity> next = new LinkedList<>();
        next.add(this);
        while (!next.isEmpty()) {
            PowerPipeEntity cable = next.poll();
            if(cable.getType() == getType()) {
                Arrays.stream(directions)
                        .map(direction -> EnergyApi.SIDED.get(world, cable.pos.offset(direction), direction.getOpposite()))
                        .filter(energyIo -> energyIo instanceof PowerPipeEntity && !neighbours.contains(energyIo))
                        .forEach(wire -> next.add(((PowerPipeEntity) wire).markTicked()));
                neighbours.add(cable);
            }
        }
        return neighbours;
    }

    public PowerPipeEntity markTicked() {
        this.ticked = true;
        return this;
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
