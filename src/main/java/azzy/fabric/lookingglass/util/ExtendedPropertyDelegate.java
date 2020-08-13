package azzy.fabric.lookingglass.util;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.math.BlockPos;

public interface ExtendedPropertyDelegate extends PropertyDelegate {
    public String getString(int index);
    public void setString(int index, String value);
    public BlockPos getPos();
}
