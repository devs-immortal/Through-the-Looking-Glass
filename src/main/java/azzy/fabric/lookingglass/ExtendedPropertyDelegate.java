package azzy.fabric.lookingglass;

import net.minecraft.block.entity.BlockEntity;
import net.minecraft.screen.PropertyDelegate;

public interface ExtendedPropertyDelegate extends PropertyDelegate {
    public String getString(int index);
    public void setString(int index, String value);
    public double getDouble(int index);
    public void setDouble(int index, double value);
    public BlockEntity getEntity();
}
