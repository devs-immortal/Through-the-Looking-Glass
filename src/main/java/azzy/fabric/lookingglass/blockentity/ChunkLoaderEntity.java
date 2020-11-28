package azzy.fabric.lookingglass.blockentity;

public interface ChunkLoaderEntity {

    void requestCheck();

    enum UnloadAction{
        NONE,
        FORCEUNLOAD,
        BREAKUNLOAD
    }
}
