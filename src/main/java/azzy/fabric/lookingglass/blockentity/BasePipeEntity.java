package azzy.fabric.lookingglass.blockentity;

import dev.technici4n.fasttransferlib.api.fluid.FluidApi;
import dev.technici4n.fasttransferlib.api.fluid.FluidIo;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Direction;

import java.util.HashSet;
import java.util.Set;

public class PowerPipeEntity extends BlockEntity implements Tickable {

    protected final Set<Direction> IO = new HashSet<>();

    public PowerPipeEntity(BlockEntityType<?> type) {
        super(type);
    }

    @Override
    public void tick() {
        if(this.IO.isEmpty())
            revalidateConnections();
        if((this.world.getTime() + this.delay) % 10 == 0){
            for(Direction direction : Direction.values()){
                FluidIo fluidIo = FluidApi.SIDED.get(this.world, this.pos.offset(direction), direction.getOpposite());
                if(fluidIo != null){
                    if(!getCachedState().get(MultiFacingBlock.getFACING().get(direction))) {
                        this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(MultiFacingBlock.getFACING().get(direction), true));
                        this.IO.add(direction);
                    }
                }
                else {
                    this.world.setBlockState(this.pos, this.world.getBlockState(this.pos).with(MultiFacingBlock.getFACING().get(direction), false));
                    getIO().remove(direction);
                }
            }
            connectionTest();
            revalidateConnections();
        }
    }
}
