package azzy.fabric.lookingglass.util;

import net.minecraft.network.PacketByteBuf;

public interface Syncable {

    void sync(PacketByteBuf packet);
}
