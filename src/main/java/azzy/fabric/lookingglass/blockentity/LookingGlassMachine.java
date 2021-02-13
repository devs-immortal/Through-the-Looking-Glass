package azzy.fabric.lookingglass.blockentity;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.energy.EnergyApi;
import dev.technici4n.fasttransferlib.api.energy.EnergyIo;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import java.util.*;

public abstract class LookingGlassMachine extends LookingGlassBE implements Tickable, EnergyIo {

    protected final MachineTier tier;
    protected double power, maxPower;

    public LookingGlassMachine(BlockEntityType<?> type, MachineTier tier, int invSize, double baseMaxPower) {
        super(type, invSize);
        this.tier = tier;
    }


    public void attemptPowerDraw() {
        List<EnergyIo> io = new ArrayList<>();
        for(Direction direction : Direction.values()) {
            EnergyIo capacitor = EnergyApi.SIDED.get(world, pos.offset(direction), direction.getOpposite());
            if(capacitor != null && capacitor.supportsExtraction())
                io.add(capacitor);
        }
        Collections.shuffle(io);
        for (EnergyIo energyIo : io) {
            if (energyIo.getEnergy() > 0 && energyIo.supportsExtraction()) {
                double succ = energyIo.extract(maxPower, Simulation.SIMULATE);
                double selfCap = insert(succ, Simulation.SIMULATE);
                double transfer = succ - selfCap;
                power += transfer;
                energyIo.insert(transfer, Simulation.ACT);
            }
        }
        markDirty();
    }

    @Override
    public double insert(double amount, Simulation simulation) {
        if (simulation == Simulation.SIMULATE) {
            return Math.max(0, amount - (maxPower - power));
        }
        double remnant = Math.max(0, amount - (maxPower - power));
        power =  power + (amount - remnant);
        markDirty();
        return remnant;
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

    enum MachineTier {
        BASIC,
        ADVANCED,
        ENDGAME,
        QUARTZ,
        ELDEN,
        SPECIAL
    }
}
