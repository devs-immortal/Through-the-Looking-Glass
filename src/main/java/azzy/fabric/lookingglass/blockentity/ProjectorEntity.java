package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.gui.CrateGuiDescription;
import azzy.fabric.lookingglass.gui.NewProjectorGuiDescription;
import azzy.fabric.lookingglass.util.GeneralNetworking;
import azzy.fabric.lookingglass.util.InventoryWrapper;
import azzy.fabric.lookingglass.util.Syncable;
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import io.netty.buffer.Unpooled;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.fabricmc.fabric.api.network.ClientSidePacketRegistry;
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import org.jetbrains.annotations.Nullable;

import static azzy.fabric.lookingglass.block.TTLGBlocks.PROJECTORENTITY;


public class ProjectorEntity extends BlockEntity implements BlockEntityClientSerializable, InventoryWrapper, PropertyDelegateHolder, ExtendedScreenHandlerFactory, Syncable {

    public int displayState;
    public double rotY, rotX, rotZ, disY, disX, disZ, scale;
    public String sign, url, color;
    public DefaultedList<ItemStack> inventory;

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
    }

    private final PropertyDelegate referenceHolder = new PropertyDelegate() {

        @Override
        public int get(int index) {
            switch(index){
                case (0): return displayState;
                case (1): return Registry.ITEM.getRawId(inventory.get(0).getItem());
                case (2): return inventory.get(0).getCount();
                case (3): return pos.getX();
                case (4): return pos.getY();
                case (5): return pos.getZ();
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
        public int size() {
            return 6;
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

    @Override
    public Text getDisplayName() {
        return new TranslatableText(getCachedState().getBlock().getTranslationKey());
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new NewProjectorGuiDescription(syncId, inv, ScreenHandlerContext.create(world, pos));
    }

    @Override
    public void writeScreenOpeningData(ServerPlayerEntity serverPlayerEntity, PacketByteBuf packetByteBuf) {
        packetByteBuf.writeBlockPos(pos);
    }

    @Override
    public void sync(PacketByteBuf packet) {












        sync();
    }
}