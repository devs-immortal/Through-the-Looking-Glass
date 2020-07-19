package azzy.fabric.lookingglass;

import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.registry.Registry;

import java.util.concurrent.atomic.AtomicInteger;

import static azzy.fabric.lookingglass.LookingGlass.FFLog;
import static azzy.fabric.lookingglass.LookingGlass.PROJECTORENTITY;

public class ProjectorEntity extends BlockEntity implements BlockEntityClientSerializable, InventoryWrapper, PropertyDelegateHolder, Tickable {

    public int displayState;
    public int rotY, rotX, rotZ, oneSided;
    public double disY, disX, disZ, scale;
    public String sign;
    public String url;
    public DefaultedList<ItemStack> inventory;

    public ProjectorEntity() {
        super(PROJECTORENTITY);
        inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
        displayState = 0;
        sign = "";
        url = "";
        disX = 0.5;
        disY = 10.5;
        disZ = 0.5;
        scale = 10;
    }

    @Override
    public void tick() {
        markDirty();
        if(world.getTime() % 100 == 0)
            FFLog.error(url);
        if(!inventory.get(0).isEmpty()) {
            url = getName(inventory.get(0));
            FFLog.error(getName(inventory.get(0)));
        }
        if(inventory.isEmpty())
            url = "";
    }

    private String getName(ItemStack item){
        if(item.hasCustomName()){
                return item.getName().asString();
        }
        return "";
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Inventories.toTag(tag, inventory);

        tag.putInt("rotX", rotX);
        tag.putInt("rotY", rotY);
        tag.putInt("rotZ", rotZ);

        tag.putDouble("disX", disX);
        tag.putDouble("disY", disY);
        tag.putDouble("disZ", disZ);
        tag.putDouble("scale", scale);

        tag.putString("sign", sign);
        tag.putString("image", url);

        tag.putInt("state", displayState);
        return super.toTag(tag);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        Inventories.fromTag(tag, inventory);

        rotX = tag.getInt("rotX");
        rotY = tag.getInt("rotY");
        rotZ = tag.getInt("rotZ");

        disX = tag.getDouble("disX");
        disY = tag.getDouble("disY");
        disZ = tag.getDouble("disZ");
        scale = tag.getDouble("scale");

        sign = tag.getString("sign");
        url = tag.getString("image");

        displayState = tag.getInt("state");
        super.fromTag(state, tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        compoundTag.putInt("rotX", rotX);
        compoundTag.putInt("rotY", rotY);
        compoundTag.putInt("rotZ", rotZ);

        compoundTag.putDouble("disX", disX);
        compoundTag.putDouble("disY", disY);
        compoundTag.putDouble("disZ", disZ);
        compoundTag.putDouble("scale", scale);

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

        disX = compoundTag.getDouble("disX");
        disY = compoundTag.getDouble("disY");
        disZ = compoundTag.getDouble("disZ");
        scale = compoundTag.getDouble("scale");

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
                case(3): return rotX;
                case(4): return rotY;
                case(5): return rotZ;
            }
            return -1;
        }
        @Override
        public void set(int index, int value) {
            switch(index){
                case(0): displayState = value; break;
                case(1): rotX = value; break;
                case(2): rotY = value; break;
                case(3): rotZ = value; break;
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
                case(0): sign = value; break;
                case(1): PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
                packet.writeString(value, 128).writeBlockPos(pos);
                ClientSidePacketRegistry.INSTANCE.sendToServer(LookingGlass.STRING_TO_SERVER_PACKET, packet);
                //    url = value;
                break;
            }
        }

        @Override
        public double getDouble(int index) {
            switch(index){
                case(0): return disX;
                case(1): return disY;
                case(2): return disZ;
            }
            return -1;
        }

        @Override
        public void setDouble(int index, double value) {
            switch(index){
                case(0): disX = value; break;
                case(1): disY = value; break;
                case(2): disZ = value; break;
            }
        }

        @Override
        public BlockEntity getEntity() {
            return getThis();
        }

        @Override
        public int size() {
            return 5;
        }
    };

    private BlockEntity getThis(){
        return this;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public PropertyDelegate getPropertyDelegate() {
        return referenceHolder;
    }
}