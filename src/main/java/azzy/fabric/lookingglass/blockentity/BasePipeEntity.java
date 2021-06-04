package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.block.LookingGlassBlock;
import azzy.fabric.lookingglass.block.PipeBlock;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import java.util.*;

public abstract class BasePipeEntity<T> extends BlockEntity implements Tickable, BlockEntityClientSerializable, RedstoneReactiveEntity {

    private final BlockApiLookup<T, Direction> lookup;
    private final boolean strict;
    protected final int offset = LookingGlassCommon.RANDOM.nextInt(10);
    protected boolean straight = false;
    protected final Set<Direction> IO = new HashSet<>();
    protected final Direction[] directions = Direction.values();
    protected boolean ticked;

    public BasePipeEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, BlockApiLookup<T, Direction> lookup, boolean strict) {
        super(type, pos, state);
        this.lookup = lookup;
        this.strict = strict;
    }

    @Override
    public void tick() {
        if(world != null && !world.isClient()) {
            if(IO.isEmpty())
                revalidateConnections();
            Set<T> ioSet = checkConnections();
            connectionTest();
            revalidateConnections();
            if(!ioSet.isEmpty())
                performTransfers(ioSet);
        }
    }

    public Set<T> checkConnections() {

        if(world == null)
            return Collections.emptySet();

        Set<T> ioSet = new HashSet<>();
        for(Direction direction : Direction.values()){
            BlockEntity entity = world.getBlockEntity(pos.offset(direction));
            if(entity instanceof BasePipeEntity && strict) {
                BlockEntityType<?> type = entity.getType();
                if (type == getType()) {
                    T io = lookup.find(world, pos.offset(direction), direction.getOpposite());
                    if (io != null) {
                        if (!getCachedState().get(PipeBlock.getFACING().get(direction))) {
                            world.setBlockState(pos, world.getBlockState(pos).with(PipeBlock.getFACING().get(direction), true));
                            IO.add(direction);
                        }
                        ioSet.add(io);
                        continue;
                    }
                }
                world.setBlockState(pos, world.getBlockState(pos).with(PipeBlock.getFACING().get(direction), false));
                getIO().remove(direction);
            }
            else {
                T io = lookup.find(world, pos.offset(direction), direction.getOpposite());
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

    @SuppressWarnings({"unchecked", "rawtypes"})
    public Set<BasePipeEntity<T>> getNeighbours() {
        Set<BasePipeEntity<T>> neighbours = new HashSet<>();
        Queue<BasePipeEntity<T>> next = new LinkedList<>();
        next.add(this);
        while (!next.isEmpty()) {
            BasePipeEntity<T> cable = next.poll();
            if(cable.getType() == getType()) {
                Arrays.stream(directions)
                        .map(direction -> lookup.find(world, cable.pos.offset(direction), direction.getOpposite()))
                        .filter(energyIo -> energyIo instanceof BasePipeEntity && !neighbours.contains(energyIo))
                        .forEach(wire -> next.add(((BasePipeEntity) wire).markTicked()));
                neighbours.add(cable);
            }
        }
        return neighbours;
    }

    public BasePipeEntity<T> markTicked() {
        this.ticked = true;
        return this;
    }

    public abstract void performTransfers(Set<T> participants);

    @Override
    public boolean powered() {
        return getCachedState().get(LookingGlassBlock.POWERED);
    }

    public Set<Direction> getIO() {
        return IO;
    }

    protected void connectionTest(){
        if(world != null) {
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
    }

    protected void revalidateConnections(){
        if(world != null) {
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
}
