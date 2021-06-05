package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class EnchanterEntity extends LookingGlassMachine {

    private final SuffuserEntity[] suffusers = new SuffuserEntity[4];
    private float totalEnchantingPower;
    private boolean active;

    public EnchanterEntity(BlockPos pos, BlockState state) {
        super(LookingGlassBlocks.ENCHANTER_ENTITY, pos, state, MachineTier.ADVANCED, 2, 1000);
    }

    @Override
    public void notifyRedstoneActivation() {
        super.notifyRedstoneActivation();
    }

    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return null;
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        return super.writeNbt(tag);
    }

    @Override
    public void readNbt(NbtCompound tag) {
        super.readNbt(tag);
    }
}
