package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.screen.ScreenHandler;
import org.jetbrains.annotations.Nullable;

public class EnchanterEntity extends LookingGlassMachine {

    private final SuffuserEntity[] suffusers = new SuffuserEntity[4];
    private float totalEnchantingPower;
    private boolean active;

    public EnchanterEntity() {
        super(LookingGlassBlocks.ENCHANTER_ENTITY, MachineTier.ADVANCED, 2, 1000);
    }

    @Override
    public void tick() {

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
    public CompoundTag toTag(CompoundTag tag) {
        return super.toTag(tag);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        super.fromTag(state, tag);
    }
}
