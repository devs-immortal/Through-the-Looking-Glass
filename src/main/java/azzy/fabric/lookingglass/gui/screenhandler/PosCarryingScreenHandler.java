package azzy.fabric.lookingglass.gui.screenhandler;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.inventory.SimpleInventory;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

public class PosCarryingScreenHandler extends ScreenHandler {

    private BlockPos pos;
    private final Inventory inventory;

    protected PosCarryingScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, PacketByteBuf buf) {
        this(type, syncId, new SimpleInventory(1));
        pos = buf.readBlockPos();
    }

    protected PosCarryingScreenHandler(@Nullable ScreenHandlerType<?> type, int syncId, Inventory inventory) {
        super(type, syncId);
        this.inventory = inventory;

        pos = BlockPos.ORIGIN;
    }

    public BlockPos getPos() {
        return pos;
    }

    @Override
    public boolean canUse(PlayerEntity player) {
        return inventory.canPlayerUse(player);
    }
}
