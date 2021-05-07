package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.util.json.LookingGlassJsonManager;
import dev.technici4n.fasttransferlib.api.energy.EnergyIo;
import it.unimi.dsi.fastutil.objects.ObjectList;
import it.unimi.dsi.fastutil.objects.ObjectLists;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.recipe.RecipeType;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class SuffuserEntity extends LookingGlassUpgradeableMachine implements EnergyIo {

    private int range;
    private int enchantingPower;

    public SuffuserEntity() {
        super(LookingGlassBlocks.SUFFUSER_ENTITY, null, MachineTier.ADVANCED, 1, 1, 5000, 1);
    }


    @Nullable
    @Override
    public ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return null;
    }

    public void updateEnchantingPower() {
        enchantingPower = 0;
        if(world != null) {
            int radious = (int) (range * 0.75);
            BlockPos.iterate(pos.add(-radious, -radious, -radious), pos.add(radious + 1, radious + 1, radious + 1)).forEach(pos -> {
                if(!world.isAir(pos)) {
                    enchantingPower += LookingGlassJsonManager.BLOCK_ENCHANTING_POWER.getOrDefault(world.getBlockState(pos).getBlock(), 0);
                }
            });
        }
    }

    public int getEnchantingPower() {
        return enchantingPower;
    }

    public void setRange(int range) {
        this.range = range;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putInt("range", range);
        return super.toTag(tag);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        range = tag.getInt("range");
        updateEnchantingPower();
        super.fromTag(state, tag);
    }

    @Override
    public void tick() {}
}
