package azzy.fabric.lookingglass.blockentity;

import dev.technici4n.fasttransferlib.api.Simulation;
import dev.technici4n.fasttransferlib.api.energy.EnergyApi;
import dev.technici4n.fasttransferlib.api.energy.EnergyIo;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import java.util.*;

public abstract class LookingGlassMachine extends LookingGlassBE implements Tickable, EnergyIo, NamedScreenHandlerFactory {

    protected final MachineTier tier;
    protected double power, maxPower;

    public LookingGlassMachine(BlockEntityType<?> type, MachineTier tier, int invSize, double baseMaxPower) {
        super(type, invSize);
        this.tier = tier;
        this.maxPower = baseMaxPower;
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
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putDouble("power", power);
        tag.putDouble("maxPower", maxPower);
        return super.toTag(tag);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        power = tag.getDouble("power");
        maxPower = tag.getDouble("maxPower");
        super.fromTag(state, tag);
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        power = tag.getDouble("power");
        maxPower = tag.getDouble("maxPower");
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        tag.putDouble("power", power);
        tag.putDouble("maxPower", maxPower);
        return tag;
    }

    enum MachineTier {
        BASIC,
        ADVANCED,
        ELDEN,
        LUPREVAN,
        ENDGAME,
    }
}
