package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.util.InventoryWrapper;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Tickable;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static azzy.fabric.lookingglass.block.LookingGlassBlocks.CHUNKLOADER_ENTITY;

public class ChunkAnchorEntity extends BlockEntity implements ChunkLoaderEntity, BlockEntityClientSerializable, InventoryWrapper, Tickable {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private Set<ChunkPos> loadedChunks = new HashSet<>();
    private boolean check = true;
    private BlockPos lastPos;
    private int lastRadius;

    public ChunkAnchorEntity() {
        super(CHUNKLOADER_ENTITY);
        lastRadius = 0;
        lastPos = pos;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void tick() {
        if(world.isClient())
            return;
        if(!inventory.isEmpty()) {
            int currentRadius = inventory.get(0).getCount();
            if(currentRadius != lastRadius || pos != lastPos || check){
                lastRadius = currentRadius;
                LoadAction action = currentRadius == 0 ? LoadAction.BREAKUNLOAD : LoadAction.FORCEUNLOAD;
                recalcChunks(lastRadius, (ServerWorld) world, action);
                if(currentRadius != 0)
                    recalcChunks(currentRadius, (ServerWorld) world, pos != lastPos ? LoadAction.REFRESH : LoadAction.LOAD);
                lastPos = pos;
                check = false;
                markDirty();
            }
        }
        else if(lastRadius != 0){
            lastRadius = 0;
        }
    }

    public void recalcChunks(int radius, ServerWorld world, LoadAction action) {

        ChunkPos curPos = world.getChunk(pos).getPos();

        if(action == LoadAction.BREAKUNLOAD) {
            for (ChunkPos chunk : loadedChunks) {
                if(!checkForLoaders(chunk))
                    world.setChunkForced(chunk.x, chunk.z, false);
            }
            loadedChunks.clear();
        }
        else if(action == LoadAction.FORCEUNLOAD) {
            for (ChunkPos chunk : loadedChunks) {
                if(chunk != world.getChunk(pos).getPos()) {
                    if(!checkForLoaders(chunk))
                        world.setChunkForced(chunk.x, chunk.z, false);
                }
            }
            loadedChunks.removeIf(chunk -> chunk != curPos);
        }
        else {
            List<ChunkPos> chunks = new LinkedList<>();
            int cornerX = curPos.x - radius;
            int cornerZ = curPos.z - radius;

            int box = (radius * 2) + 1;
            for(int i = 0; i < box; i++){
                for(int j = 0; j < box; j++){
                    chunks.add(new ChunkPos(cornerX + i, cornerZ + j));
                }
            }

            if(action == LoadAction.LOAD) {
                for(ChunkPos chunkPos : chunks) {
                    int chunkX = chunkPos.x;
                    int chunkZ = chunkPos.z;
                    boolean loaded = world.getForcedChunks().contains(chunkPos.toLong());
                    if(loaded && chunkPos == curPos && !checkForLoaders(chunkPos)) {
                        loadedChunks.add(chunkPos);
                        //loadedChunks++;
                    }
                    else if(!loaded) {
                        loadedChunks.add(chunkPos);
                        world.setChunkForced(chunkX, chunkZ, true);
                        //loadedChunks++;
                    }
                }
            }
            else {
                for(ChunkPos chunkPos : chunks) {
                    int chunkX = chunkPos.x;
                    int chunkZ = chunkPos.z;
                    loadedChunks.add(chunkPos);
                    world.setChunkForced(chunkX, chunkZ, true);
                    //loadedChunks++;
                }
            }
        }
    }

    private boolean checkForLoaders(ChunkPos chunkPos) {
        Set<BlockPos> blockEntities = world.getChunk(chunkPos.x, chunkPos.z).getBlockEntityPositions();
        boolean hasLoaders = false;
        for(BlockPos blockPos : blockEntities){
            if(blockPos == pos)
                continue;
            BlockEntity entity = world.getBlockEntity(blockPos);
            if(entity instanceof ChunkLoaderEntity && entity != this){
                world.getBlockTickScheduler().schedule(blockPos, world.getBlockState(blockPos).getBlock(), 1);
                hasLoaders = true;
            }
        }
        return hasLoaders;
    }

    @Override
    public int getMaxCountPerStack() {
        return 16;
    }

    @Override
    public double getSquaredRenderDistance() {
        return 128;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        Inventories.toTag(tag, inventory);
        tag.putBoolean("check", check);
        tag.putInt("radius", lastRadius);
        tag.putLong("lastPos", lastPos.asLong());
        return super.toTag(tag);
    }

    public void requestCheck() {
        this.check = true;
    }

    @Override
    public void setLoadedChunks(Set<ChunkPos> chunks) {
        loadedChunks = chunks;
    }

    @Override
    public Set<ChunkPos> getLoadedChunks() {
        return loadedChunks;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        Inventories.fromTag(tag, inventory);
        check = tag.getBoolean("check");
        lastRadius = tag.getInt("radius");
        lastPos = BlockPos.fromLong(tag.getLong("lastPos"));
        super.fromTag(state, tag);
    }

    @Override
    public void fromClientTag(CompoundTag compoundTag) {
        Inventories.fromTag(compoundTag, inventory);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag compoundTag) {
        Inventories.toTag(compoundTag, inventory);
        return compoundTag;
    }
}
