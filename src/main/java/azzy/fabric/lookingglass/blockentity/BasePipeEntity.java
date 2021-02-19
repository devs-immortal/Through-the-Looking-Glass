package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.block.LookingGlassBlock;
import azzy.fabric.lookingglass.block.PipeBlock;
import dev.technici4n.fasttransferlib.api.fluid.FluidApi;
import dev.technici4n.fasttransferlib.api.fluid.FluidIo;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import java.util.HashSet;
import java.util.Set;

public abstract class BasePipeEntity<T> extends BlockEntity implements Tickable, BlockEntityClientSerializable, RedstoneReactiveEntity {

    private final BlockApiLookup<T, Direction> lookup;
    private final boolean strict;
    protected final int offset = LookingGlassCommon.RANDOM.nextInt(10);
    protected boolean straight = false;
    protected final Set<Direction> IO = new HashSet<>();

    public BasePipeEntity(BlockEntityType<?> type, BlockApiLookup<T, Direction> lookup, boolean strict) {
        super(type);
        this.lookup = lookup;
        this.strict = strict;
    }

    @Override
    public void tick() {
        if(IO.isEmpty())
            revalidateConnections();
        if((world.getTime() + offset) % 10 == 0){
            Set<T> ioSet = checkConnections();
            connectionTest();
            revalidateConnections();
                if(!ioSet.isEmpty());
                performTransfers(ioSet);
        }
    }

    public Set<T> checkConnections() {
        Set<T> ioSet = new HashSet<>();
        for(Direction direction : Direction.values()){
            BlockEntity entity = world.getBlockEntity(pos.offset(direction));
            if(entity instanceof BasePipeEntity && strict) {
                if(entity != null) {
                    BlockEntityType<?> type = entity.getType();
                    if (type == getType()) {
                        T io = lookup.get(world, pos.offset(direction), direction.getOpposite());
                        if (io != null) {
                            if (!getCachedState().get(PipeBlock.getFACING().get(direction))) {
                                world.setBlockState(pos, world.getBlockState(pos).with(PipeBlock.getFACING().get(direction), true));
                                IO.add(direction);
                            }
                            ioSet.add(io);
                            continue;
                        }
                    }
                }
                world.setBlockState(pos, world.getBlockState(pos).with(PipeBlock.getFACING().get(direction), false));
                getIO().remove(direction);
            }
            else {
                T io = lookup.get(world, pos.offset(direction), direction.getOpposite());
                if(io != null){
                    if(!getCachedState().get(PipeBlock.getFACING().get(direction))) {
                        world.setBlockState(pos, world.getBlockState(pos).with(PipeBlock.getFACING().get(direction), true));
                        IO.add(direction);
                    }
                    ioSet.add(io);
                }
                else {
                    world.setBlockState(pos, world.getBlockState(pos).with(PipeBlock.getFACING().get(direction), false));
                    getIO().remove(direction);
                }
            }
        }
        return ioSet;
    }

    public abstract void performTransfers(Set<T> participants);

    public boolean isStraight() {
        return straight;
    }

    @Override
    public boolean powered() {
        return getCachedState().get(LookingGlassBlock.POWERED);
    }

    public boolean waterlogged() {
        return getCachedState().get(LookingGlassBlock.WATERLOGGED);
    }

    public Set<Direction> getIO() {
        return IO;
    }

    protected void connectionTest(){
        BlockState state = this.world.getBlockState(this.pos);
        Direction valid = null, valid2 = null;
        for(Direction direction : Direction.values()){
            if(valid == null && state.get(PipeBlock.getFACING().get(direction)) && state.get(PipeBlock.getFACING().get(direction.getOpposite()))){
                valid = direction;
                valid2 = direction.getOpposite();
                continue;
            }
            if(state.get(PipeBlock.getFACING().get(direction)) && (direction != valid && direction != valid2)) {
                this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(PipeBlock.CENTER, true), 3);
                return;
            }
        }
        if(valid != null && valid2 != null)
            this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(PipeBlock.CENTER, false).with(PipeBlock.DIRECTION, valid));
    }

    protected void revalidateConnections(){
        IO.clear();
        straight = false;
        BlockState state = world.getBlockState(pos);
        if(!state.get(PipeBlock.CENTER)){
            Direction facing = state.get(PipeBlock.DIRECTION);
            IO.add(facing);
            IO.add(facing.getOpposite());
            straight = true;
        }
        for(Direction direction : Direction.values()){
            if(state.get(PipeBlock.getFACING().get(direction)))
                IO.add(direction);
        }
    }
}
