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
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.MutableText;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;

import java.util.concurrent.atomic.AtomicInteger;

import static azzy.fabric.lookingglass.LookingGlass.FFLog;
import static azzy.fabric.lookingglass.LookingGlass.PROJECTORENTITY;

public class ProjectorEntity extends BlockEntity implements BlockEntityClientSerializable, InventoryWrapper, PropertyDelegateHolder, Tickable {

    public int displayState;
    public int rotY, rotX, rotZ, disY, disX, disZ, scale;
    private boolean syncNeeded;
    public String sign;
    public String url;
    public DefaultedList<ItemStack> inventory;

    public ProjectorEntity() {
        super(PROJECTORENTITY);
        inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
        displayState = 0;
        sign = "";
        url = "";
        disX = 1;
        disY = 1;
        disZ = 1;
        scale = 1;
    }

    @Override
    public void tick() {
    }

    @Override
    public void sync() {
        BlockEntityClientSerializable.super.sync();
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Inventories.toTag(tag, inventory);

        tag.putInt("rotX", rotX);
        tag.putInt("rotY", rotY);
        tag.putInt("rotZ", rotZ);

        tag.putInt("disX", disX);
        tag.putInt("disY", disY);
        tag.putInt("disZ", disZ);
        tag.putInt("scale", scale);

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

        disX = tag.getInt("disX");
        disY = tag.getInt("disY");
        disZ = tag.getInt("disZ");
        scale = tag.getInt("scale");

        sign = tag.getString("sign");
        url = tag.getString("image");

        displayState = tag.getInt("state");
        super.fromTag(state, tag);
    }

    public void requestSync(){
        syncNeeded = true;
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        Inventories.toTag(compoundTag, inventory);
        compoundTag.putInt("rotX", rotX);
        compoundTag.putInt("rotY", rotY);
        compoundTag.putInt("rotZ", rotZ);

        compoundTag.putInt("disX", disX);
        compoundTag.putInt("disY", disY);
        compoundTag.putInt("disZ", disZ);
        compoundTag.putInt("scale", scale);

        compoundTag.putInt("state", displayState);
        compoundTag.putString("sign", sign);
        compoundTag.putString("image", url);
        return compoundTag;
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        Inventories.fromTag(compoundTag, inventory);
        rotX = compoundTag.getInt("rotX");
        rotY = compoundTag.getInt("rotY");
        rotZ = compoundTag.getInt("rotZ");

        disX = compoundTag.getInt("disX");
        disY = compoundTag.getInt("disY");
        disZ = compoundTag.getInt("disZ");
        scale = compoundTag.getInt("scale");

        displayState = compoundTag.getInt("state");
        sign = compoundTag.getString("sign");
        url = compoundTag.getString("image");
    }

    private ExtendedPropertyDelegate referenceHolder = new ExtendedPropertyDelegate() {
        @Override
        public int get(int index) {
            switch(index){
                case (0): return displayState;
                case (1): return Registry.ITEM.getRawId(inventory.get(0).getItem());
                case (2): return inventory.get(0).getCount();
                case (3): return rotX;
                case (4): return rotY;
                case (5): return rotZ;
                case (6): return disX;
                case (7): return disY;
                case (8): return disZ;
                case (9): return scale;
            }
            return -1;
        }
        @Override
        public void set(int index, int value) {

            PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
            packet.writeInt(index).writeInt(value);
            packet.writeBlockPos(pos);
            ClientSidePacketRegistry.INSTANCE.sendToServer(LookingGlass.INTS_TO_SERVER_PACKET, packet);

            switch(index){
                case (0): displayState = value; break;
                case (1): rotX = value; break;
                case (2): rotY = value; break;
                case (3): rotZ = value; break;
                case (4): disX = value; break;
                case (5): disY = value; break;
                case (6): disZ = value; break;
                case (7): scale = value; break;
            }
            if(!world.isClient)
                sync();
        }

        @Override
        public String getString(int index) {
            switch(index){
                case (0): return sign;
                case (1): return url;
            }
            return null;
        }

        @Override
        public void setString(int index, String value) {

            if(world.isClient()){
                PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
                packet.writeString(value).writeBlockPos(pos).writeInt(index);
                ClientSidePacketRegistry.INSTANCE.sendToServer(LookingGlass.STRING_TO_SERVER_PACKET, packet);
            }

            switch(index){
                case (0): sign = value; break;
                case (1): url = value; break;
            }

            if(!world.isClient)
                sync();
        }

        @Override
        public BlockPos getPos() {
            return pos;
        }

        @Override
        public int size() {
            return 9;
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