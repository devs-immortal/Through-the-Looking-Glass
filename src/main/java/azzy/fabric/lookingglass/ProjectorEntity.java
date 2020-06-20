package azzy.fabric.lookingglass;

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.container.PropertyDelegate;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.DefaultedList;
import net.minecraft.util.registry.Registry;

import static azzy.fabric.lookingglass.LookingGlass.PROJECTORENTITY;

public class ProjectorEntity extends BlockEntity implements BlockEntityClientSerializable, InventoryWrapper, PropertyDelegateHolder {

    public int displayState;
    int rotY, rotX, rotZ;
    public String sign;
    public String url;
    public DefaultedList<ItemStack> inventory;

    public ProjectorEntity() {
        super(PROJECTORENTITY);
        inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
        displayState = 0;
        sign = "";
        url = "";
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Inventories.toTag(tag, inventory);

        tag.putInt("rotx", rotX);
        tag.putInt("roty", rotY);
        tag.putInt("rotz", rotZ);
        tag.putString("sign", sign);
        tag.putString("image", url);
        tag.putInt("state", displayState);
        return super.toTag(tag);
    }

    @Override
    public void fromTag(CompoundTag tag) {
        Inventories.fromTag(tag, inventory);

        rotX = tag.getInt("rotX");
        rotY = tag.getInt("rotY");
        rotZ = tag.getInt("rotZ");
        sign = tag.getString("sign");
        url = tag.getString("image");
        displayState = tag.getInt("state");
        super.fromTag(tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        compoundTag.putInt("rotx", rotX);
        compoundTag.putInt("roty", rotY);
        compoundTag.putInt("rotz", rotZ);
        compoundTag.putInt("state", displayState);
        compoundTag.putString("sign", sign);
        compoundTag.putString("image", url);
        return compoundTag;
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        rotX = compoundTag.getInt("rotX");
        rotY = compoundTag.getInt("rotY");
        rotZ = compoundTag.getInt("rotZ");
        displayState = compoundTag.getInt("state");
        sign = compoundTag.getString("sign");
        url = compoundTag.getString("image");
    }

    private ExtendedPropertyDelegate referenceHolder = new ExtendedPropertyDelegate() {
        @Override
        public int get(int index) {
            switch(index){
                case(0): return displayState;
                case(1): return Registry.ITEM.getRawId(inventory.get(0).getItem());
                case(2): return inventory.get(0).getCount();
            }
            return -1;
        }
        @Override
        public void set(int index, int value) {
            switch(index){
                case(0): displayState = value;
                case(1): rotX = value;
                case(2): rotY = value;
                case(3): rotZ = value;
            }
        }

        @Override
        public String getString(int index) {
            switch(index){
                case(0): return sign;
                case(1): return url;
            }
            return null;
        }

        @Override
        public void setString(int index, String value) {
            switch(index){
                case(0): sign = value;
                case(1): url = value;
            }
        }

        @Override
        public int size() {
            return 3;
        }
    };

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public PropertyDelegate getPropertyDelegate() {
        return referenceHolder;
    }
}