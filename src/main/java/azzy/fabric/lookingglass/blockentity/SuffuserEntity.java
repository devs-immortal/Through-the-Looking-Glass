package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.util.json.LookingGlassJsonManager;
import dev.technici4n.fasttransferlib.api.energy.EnergyIo;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

public class SuffuserEntity extends LookingGlassUpgradeableMachine implements EnergyIo, LookingGlassTickable {

    private int range;

    public SuffuserEntity(BlockPos pos, BlockState state) {
        super(LookingGlassBlocks.SUFFUSER_ENTITY, pos, state, null, MachineTier.ADVANCED, 1, 1, 5000, 1);
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return null;
    }

    @Override
    public void tick() {
    }

    public int getEnchantingPower() {
        if(world != null) {
            int radious = (int) (range * 0.75);
            return BlockPos.stream(pos.add(-radious, -radious, -radious), pos.add(radious + 1, radious + 1, radious + 1))
                    .mapToInt(pos -> {
                        if(!world.isAir(pos)) {
                            return LookingGlassJsonManager.BLOCK_ENCHANTING_POWER.getOrDefault(world.getBlockState(pos).getBlock(), 0);
                        }
                        return 0;
                    })
                    .sum();
        }
        return 0;
    }

    public void setRange(int range) {
        this.range = range;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        tag.putInt("range", range);
        return super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        range = tag.getInt("range");
        super.readNbt(tag);
    }

    @Override
    public NbtCompound toClientTag(NbtCompound tag) {
        tag.putInt("range", range);
        return super.toClientTag(tag);
    }

    @Override
    public void fromClientTag(NbtCompound tag) {
        range = tag.getInt("range");
        super.fromClientTag(tag);
    }
}
