package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.PowerConduitEntity;
import dev.technici4n.fasttransferlib.api.energy.EnergyApi;
import dev.technici4n.fasttransferlib.api.energy.EnergyIo;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;

public class PowerConduitBlock extends PipeBlock {

    public final double transferRate;
    private final BiFunction<BlockPos, BlockState, PowerConduitEntity> powerConduitEntitySupplier;

    public PowerConduitBlock(Settings settings, double transferRate, BiFunction<BlockPos, BlockState, PowerConduitEntity> powerConduitEntitySupplier) {
        super(settings, true);
        this.transferRate = transferRate;
        this.powerConduitEntitySupplier = powerConduitEntitySupplier;
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return PowerConduitEntity::tick;
    }

    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
        BlockEntity entity = world.getBlockEntity(pos);
        if(entity instanceof PowerConduitEntity) {
            ((PowerConduitEntity) entity).initialize(world);
        }
        super.onPlaced(world, pos, state, placer, itemStack);
    }

    @Override
    public BlockState checkConnections(World world, BlockPos pos, BlockState cable) {
        BlockState state = cable.getBlock().getDefaultState();
        List<Direction> connections = new ArrayList<>();
        for (Direction direction : Direction.values()) {
            BlockPos offPos = pos.offset(direction);
            EnergyIo io = EnergyApi.SIDED.find(world, offPos, direction.getOpposite());
            if(io instanceof PowerConduitEntity) {
                if(((PowerConduitEntity) io).getCachedState().isOf(this)) {
                    connections.add(direction);
                }
            }
            else {
                if(!state.get(POWERED) && io != null && (io.supportsInsertion() || io.supportsExtraction())) {
                    connections.add(direction);
                }
            }
        }
        if(connections.size() == 2 && connections.get(0).getOpposite() == connections.get(1)) {
            state = state.with(AXIS, connections.get(0).getAxis()).with(CENTER, false);
        }
        else if(connections.size() != 0) {
            for (Direction connection : connections) {
                state = state.with(FACING.get(connection), true);
            }
        }
        return state.with(WATERLOGGED, cable.get(WATERLOGGED));
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return powerConduitEntitySupplier.apply(pos, state);
    }
}
