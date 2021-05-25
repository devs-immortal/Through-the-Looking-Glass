package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.incubus_core.be.MovementSensitiveBlockEntity;
import azzy.fabric.lookingglass.item.DataShardItem;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static azzy.fabric.lookingglass.block.LookingGlassBlocks.WORMHOLE_ENTITY;

public class WormholeEntity extends BlockEntity implements Tickable, BlockEntityClientSerializable, MovementSensitiveBlockEntity {

    private BlockPos out = BlockPos.ORIGIN;
    private WormholeEntity cachedOut;
    private boolean valid;
    private HashMap<Entity, Long> receiverCooldown = new HashMap<>();
    private int onTicks;
    private final Consumer<Entity> teleporter = entity -> {
        if(world != null && entity != null && cachedOut != null) {
            cachedOut.notifyAssign(entity);
            entity.teleport(out.getX() + 0.5, out.getY() + 2, out.getZ() + 0.5);
            if(entity.hasPassengers() && entity.getPassengerList().get(0).getDimensions(EntityPose.STANDING).height > 1f)
                entity.getPassengerList().get(0).kill();
            world.playSound(null, pos, SoundEvents.BLOCK_SHROOMLIGHT_BREAK, SoundCategory.BLOCKS, 0.6f, 2f);
            world.playSound(null, out, SoundEvents.ENTITY_SHULKER_TELEPORT, SoundCategory.BLOCKS, 0.3f, 2f);
        }
    };

    public WormholeEntity() {
        super(WORMHOLE_ENTITY);
    }

    @Override
    public void tick() {
        if(world != null) {
            if(!world.isClient()) {
                if(!receiverCooldown.isEmpty() && world.getTime() % 2 == 0)
                    receiverCooldown = (HashMap<Entity, Long>) receiverCooldown.entrySet().stream().filter(entityLongEntry -> entityLongEntry.getValue() - 2 > 0).collect(Collectors.toMap(Map.Entry::getKey, cooldown -> cooldown.getValue() - 2));
                if(world.getTime() % 10 == 0) {
                    valid = world.isChunkLoaded(out.getX() >> 4, out.getZ() >> 4) && world.getBlockEntity(out) instanceof WormholeEntity;
                    if(valid && cachedOut == null) {
                        cachedOut = (WormholeEntity) world.getBlockEntity(out);
                    }
                    if(cachedOut != null && !out.equals(cachedOut.pos)){
                        valid = false;
                        cachedOut = null;
                    }
                    markDirty();
                    sync();
                }
                if(valid) {
                    getCollidingEntities();
                }
            }
            if(world.getTime() % 2 == 0) {
                if(valid) {
                    if(onTicks < 60)
                        onTicks++;
                }
                else {
                    if(onTicks > 0)
                        onTicks--;
                }
            }
        }
    }

    @Override
    public MovementSensitiveBlockEntity[] getObservers() {
        return new MovementSensitiveBlockEntity[]{ cachedOut };
    }

    @Override
    public void notifyObserver(BlockEntity movedEntity, BlockPos newPos) {
        if(movedEntity != null) {
            cachedOut = (WormholeEntity) movedEntity;
            out = newPos;
        }
    }

    @Override
    public void notifyMoved(BlockPos newPos) {}

    private void getCollidingEntities() {
        if(world != null && cachedOut != null && world.isChunkLoaded(cachedOut.pos)) {
            Box hitbox = new Box(pos.up(), pos.add(1, 3, 1));
            world.getEntitiesByType(EntityType.ITEM, hitbox, itemEntity -> itemEntity != null && (!receiverCooldown.containsKey(itemEntity) || receiverCooldown.get(itemEntity) == 0)).forEach( teleporter );
            world.getEntitiesByClass(Entity.class, new Box(pos.up(), pos.add(1, 3, 1)), entity -> entity != null&& (!receiverCooldown.containsKey(entity) || receiverCooldown.get(entity) == 0) && entity.getDimensions(EntityPose.STANDING).height <= 1f && !(entity instanceof ItemEntity)).forEach( teleporter );
        }
    }

    public void notifyAssign(Entity entity) {
        receiverCooldown.put(entity, 20L);
    }

    @SuppressWarnings("unchecked")
    public boolean tryAssign(ItemStack stack) {
        Optional<Long> pos = (Optional<Long>) DataShardItem.getData(stack, DataShardItem.DataType.POS);
        if(pos.isPresent() && pos.get() != 0){
            out = BlockPos.fromLong(pos.get());
            markDirty();
            return true;
        }
        return false;
    }

    public int getOnTicks() {
        return onTicks;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        tag.putLong("out", out.asLong());
        tag.putInt("ticks", onTicks);
        tag.putBoolean("valid", valid);
        return super.toTag(tag);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        out = BlockPos.fromLong(tag.getLong("out"));
        onTicks = tag.getInt("ticks");
        valid = tag.getBoolean("valid");
        super.fromTag(state, tag);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        tag.putInt("ticks", onTicks);
        tag.putBoolean("valid", valid);
        return tag;
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        onTicks = tag.getInt("ticks");
        valid = tag.getBoolean("valid");
    }
}
