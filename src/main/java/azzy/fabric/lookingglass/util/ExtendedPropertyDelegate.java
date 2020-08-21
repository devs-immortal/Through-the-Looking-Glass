package azzy.fabric.lookingglass.util;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.math.BlockPos;

public interface ExtendedPropertyDelegate extends PropertyDelegate {
    String getString(int index);
    void setString(int index, String value);
    double getDouble(int index);
    void setDouble(int index, double value);
    BlockPos getPos();
}
