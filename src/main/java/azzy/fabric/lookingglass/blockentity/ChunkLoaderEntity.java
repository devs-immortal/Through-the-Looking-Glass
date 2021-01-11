package azzy.fabric.lookingglass.blockentity;

import net.minecraft.util.math.ChunkPos;

import java.util.Set;

public interface ChunkLoaderEntity {

    void requestCheck();

    void setLoadedChunks(Set<ChunkPos> chunks);

    Set<ChunkPos> getLoadedChunks();

    enum LoadAction {
        LOAD,
        REFRESH,
        FORCEUNLOAD,
        BREAKUNLOAD
    }
}
