package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.util.InventoryWrapper;
import azzy.fabric.lookingglass.blockentity.ChunkLoaderEntity.UnloadAction;
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

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static azzy.fabric.lookingglass.block.TTLGBlocks.CHUNKLOADER_ENTITY;

public class ChunkAnchorEntity extends BlockEntity implements ChunkLoaderEntity, BlockEntityClientSerializable, InventoryWrapper, Tickable {

    private final DefaultedList<ItemStack> inventory = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private boolean check = true;
    private int lastRadius;

    public ChunkAnchorEntity() {
        super(CHUNKLOADER_ENTITY);
        lastRadius = 0;
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    @Override
    public void tick() {
        if(world.isClient())
            return;
        if(!inventory.isEmpty()){
            int currentRadius = inventory.get(0).getCount();
            if((currentRadius != lastRadius) || check){
                lastRadius = currentRadius;
                UnloadAction action = currentRadius == 0 ? UnloadAction.BREAKUNLOAD : UnloadAction.FORCEUNLOAD;
                recalcChunks(lastRadius, (ServerWorld) world, action);
                if(currentRadius != 0)
                    recalcChunks(currentRadius, (ServerWorld) world, UnloadAction.NONE);
                check = false;
                markDirty();
            }
        }
        else if(lastRadius != 0){
            lastRadius = 0;
        }
    }

    public int recalcChunks(int radius, ServerWorld world, UnloadAction action){
        List<ChunkPos> chunks = new LinkedList<>();
        ChunkPos currentChunk = world.getChunk(pos).getPos();
        int cornerX = currentChunk.x - radius;
        int cornerZ = currentChunk.z - radius;
        int loadedChunks = 0;

        int box = (radius * 2) + 1;
        for(int i = 0; i < box; i++){
            for(int j = 0; j < box; j++){
                chunks.add(new ChunkPos(cornerX + i, cornerZ + j));
            }
        }

        for(ChunkPos chunkPos : chunks){
            int chunkX = chunkPos.x;
            int chunkZ = chunkPos.z;
            boolean loaded = world.getForcedChunks().contains(chunkPos.toLong());
            if(action == UnloadAction.FORCEUNLOAD && chunkPos != world.getChunk(pos).getPos() && !checkForLoaders(chunkX, chunkZ)){
                world.setChunkForced(chunkX, chunkZ, false);
            }
            else if(action == UnloadAction.BREAKUNLOAD && !checkForLoaders(chunkX, chunkZ)){
                world.setChunkForced(chunkX, chunkZ, false);
            }
            else if(!loaded){
                world.setChunkForced(chunkX, chunkZ, true);
                loadedChunks++;
            }
        }
        return loadedChunks;
    }

    private boolean checkForLoaders(int chunkX, int chunkZ){
        Set<BlockPos> blockEntities = world.getChunk(chunkX, chunkZ).getBlockEntityPositions();
        boolean hasLoaders = false;
        for(BlockPos blockPos : blockEntities){
            if(blockPos == pos)
                continue;
            BlockEntity entity = world.getBlockEntity(blockPos);
            if(entity instanceof ChunkLoaderEntity){
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
        return super.toTag(tag);
    }

    public void requestCheck() {
        this.check = true;
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        Inventories.fromTag(tag, inventory);
        check = tag.getBoolean("check");
        lastRadius = tag.getInt("radius");
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
