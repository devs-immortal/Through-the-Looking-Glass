package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.util.ExtendedPropertyDelegate;
import azzy.fabric.lookingglass.util.InventoryWrapper;
import com.mojang.authlib.GameProfile;
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import sun.misc.Unsafe;

import java.lang.reflect.Field;
import java.util.UUID;

import static azzy.fabric.lookingglass.block.TTLGBlocks.PROJECTORENTITY;


public class ProjectorEntity extends BlockEntity implements BlockEntityClientSerializable, InventoryWrapper, PropertyDelegateHolder, Tickable {

    public int displayState;
    public double rotY, rotX, rotZ, disY, disX, disZ, scale;
    public String sign, url, color;
    public DefaultedList<ItemStack> inventory;
    public PlayerEntity player;

    public ProjectorEntity() {
        super(PROJECTORENTITY);
        inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
        displayState = 0;
        sign = "";
        url = "";
        color = "0xffffff";
        disY = 1;
        scale = 1;
    }

    @Override
    public void tick() {
        if(displayState == 4 && !world.isClient()) {
            ItemStack stack = inventory.get(0);
            if(stack.getOrCreateTag().contains("data")) {
                CompoundTag tag = stack.getSubTag("data");
                if(tag.contains("uuid")) {
                    UUID id = tag.getUuid("uuid");
                    if(player == null || !player.getUuid().equals(id)) {
                        player = ((ServerWorld) world).getServer().getPlayerManager().getPlayer(id);
                    }
                }
            }
            sync();
        }
    }

    @Override
    public void sync() {
        BlockEntityClientSerializable.super.sync();
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Inventories.toTag(tag, inventory);

        tag.putDouble("rotX", rotX);
        tag.putDouble("rotY", rotY);
        tag.putDouble("rotZ", rotZ);

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

        rotX = tag.getDouble("rotX");
        rotY = tag.getDouble("rotY");
        rotZ = tag.getDouble("rotZ");

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
        Inventories.toTag(compoundTag, inventory);
        compoundTag.putDouble("rotX", rotX);
        compoundTag.putDouble("rotY", rotY);
        compoundTag.putDouble("rotZ", rotZ);

        compoundTag.putDouble("disX", disX);
        compoundTag.putDouble("disY", disY);
        compoundTag.putDouble("disZ", disZ);
        compoundTag.putDouble("scale", scale);

        compoundTag.putInt("state", displayState);
        compoundTag.putString("sign", sign);
        compoundTag.putString("image", url);
        if(player != null && displayState == 4 && inventory.get(0).getOrCreateTag().contains("data") && inventory.get(0).getSubTag("data").contains("uuid")) {
            compoundTag.putUuid("uuid", inventory.get(0).getSubTag("data").getUuid("uuid"));
            compoundTag.put("player", player.toTag(new CompoundTag()));
        }
        return compoundTag;
    }

    @Override
    public double getSquaredRenderDistance() {
        return 2048D;
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        Inventories.fromTag(compoundTag, inventory);
        rotX = compoundTag.getDouble("rotX");
        rotY = compoundTag.getDouble("rotY");
        rotZ = compoundTag.getDouble("rotZ");

        disX = compoundTag.getDouble("disX");
        disY = compoundTag.getDouble("disY");
        disZ = compoundTag.getDouble("disZ");
        scale = compoundTag.getInt("scale");

        displayState = compoundTag.getInt("state");
        sign = compoundTag.getString("sign");
        url = compoundTag.getString("image");
        if(displayState == 4) {
            //try {
            //    Field field = Unsafe.class.getDeclaredField("theUnsafe");
            //    field.setAccessible(true);
            //    Unsafe unsafe = (Unsafe) field.get(null);
            //    player = (PlayerEntity) unsafe.allocateInstance(ClientPlayerEntity.class);
            //} catch (NoSuchFieldException | IllegalAccessException | InstantiationException e) {
            //    e.printStackTrace();
            //}
            //MinecraftClient.getInstance().getCurrentServerEntry().playerListSummary.forEach(a -> LookingGlassCommon.FFLog.error(a.asString()));
            if(compoundTag.contains("uuid")) {
                if(player == null || !player.getUuid().equals(compoundTag.getUuid("uuid"))) {
                    player = new OtherClientPlayerEntity((ClientWorld) world, new GameProfile(compoundTag.getUuid("uuid"), ""));
                }
                if(!player.isAlive() || !player.getUuid().equals(MinecraftClient.getInstance().player.getUuid())) {
                    player.fromTag((CompoundTag) compoundTag.get("player"));
                    player.yaw = 0;
                    player.pitch = 0;
                    player.refreshPositionAndAngles(player.getBlockPos(), 0, 0);
                }
            }
        }
    }

    private final ExtendedPropertyDelegate referenceHolder = new ExtendedPropertyDelegate() {

        @Override
        public double getDouble(int index) {
            switch(index){
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
        public void setDouble(int index, double value) {
            if(world.isClient()){
                PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
                packet.writeInt(index).writeDouble(value);
                packet.writeBlockPos(pos);
                ClientSidePacketRegistry.INSTANCE.sendToServer(LookingGlassCommon.DOUBLES_TO_SERVER_PACKET, packet);
            }

            switch(index){
                case (1): rotX = value; break;
                case (2): rotY = value; break;
                case (3): rotZ = value; break;
                case (4): disX = value; break;
                case (5): disY = value; break;
                case (6): disZ = value; break;
                case (7): scale = value; break;
            }
        }

        @Override
        public int get(int index) {
            switch(index){
                case (0): return displayState;
                case (1): return Registry.ITEM.getRawId(inventory.get(0).getItem());
                case (2): return inventory.get(0).getCount();
            }
            return -1;
        }
        @Override
        public void set(int index, int value) {
            if (index == 0) {
                displayState = value;
            }
        }

        @Override
        public String getString(int index) {
            switch(index){
                case (0): return sign;
                case (1): return url;
                case (2): return color;
            }
            return null;
        }

        @Override
        public void setString(int index, String value) {

            if(world.isClient()){
                PacketByteBuf packet = new PacketByteBuf(Unpooled.buffer());
                packet.writeString(value).writeBlockPos(pos).writeInt(index);
                ClientSidePacketRegistry.INSTANCE.sendToServer(LookingGlassCommon.STRING_TO_SERVER_PACKET, packet);
            }

            switch(index){
                case (0): sign = value; break;
                case (1): url = value; break;
                case (2): color = value; break;
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
            return 10;
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