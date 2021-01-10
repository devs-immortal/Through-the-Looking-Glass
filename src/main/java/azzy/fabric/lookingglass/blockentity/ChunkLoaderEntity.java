package azzy.fabric.lookingglass.block.blockentity;

public interface ChunkLoaderEntity {

    void requestCheck();

    enum UnloadAction{
        NONE,
        FORCEUNLOAD,
        BREAKUNLOAD
    }
}
