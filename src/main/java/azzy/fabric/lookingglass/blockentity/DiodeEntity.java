package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.DiodeBlock;
import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.property.Properties;
import net.minecraft.util.Tickable;

public class DiodeEntity extends BlockEntity implements Tickable {

    private int timer, power;

    public DiodeEntity() {
        super(LookingGlassBlocks.DIODE_ENTITY);
    }

    @Override
    public void tick() {
        if (world != null && !world.isClient()) {
            if (timer == 0) {
                if (power != 0) {
                    power = 0;
                    world.setBlockState(pos, getCachedState().with(Properties.POWERED, false));
                    onRedstoneUpdate();
                    world.updateNeighbors(pos, getCachedState().getBlock());
                }
                markDirty();
            } else
                timer--;
        }
    }

    public void onRedstoneUpdate() {
        if(world != null && !world.isClient()) {
            BlockState state = getCachedState();
            int curPower = world.getReceivedStrongRedstonePower(pos.down());
            boolean powered = state.get(Properties.POWERED);
            int length = state.get(DiodeBlock.EXTENSION);
            if(!powered && curPower > 0 || curPower >= power) {
                switch (length) {
                    case 0: timer = 2; break;
                    case 1: timer = 10; break;
                    case 2: timer = 20; break;
                    case 3: timer = 100; break;
                    case 4: timer = 200; break;
                    default: timer = 0;
                }
                if(curPower > power) {
                    power = curPower;
                    world.updateNeighbors(pos, state.getBlock());
                }
                if(!powered && power > 0) {
                    world.setBlockState(pos, state.with(Properties.POWERED, true));
                }
                markDirty();
            }
        }
    }

    public void updateTimer(int length) {
        switch (length) {
            case 0: timer = 2; break;
            case 1: timer = 10; break;
            case 2: timer = 20; break;
            case 3: timer = 100; break;
            case 4: timer = 200; break;
            default: timer = 0;
        }
        markDirty();
    }

    public int getRedstonePower() {
        return power;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        return super.writeNbt(tag);
    }

    @Override
    public void readNbt(BlockState state, NbtCompound tag) {
        super.readNbt(state, tag);
    }
}
