package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.LookingGlassBlock;
import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Tickable;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import java.util.List;

public class VacuumHopperEntity extends LookingGlassBE implements Tickable {

    private final int delay, radius;
    private final boolean advanced = this.getType() == LookingGlassBlocks.ADVANCED_VACUUM_HOPPER_ENTITY;

    public VacuumHopperEntity(BlockEntityType<?> type, int invSize, int delay, int radious) {
        super(type, invSize);
        this.radius = radious;
        this.delay = delay;
    }

    @Override
    public void tick() {
        if(!world.isClient() && !getCachedState().get(LookingGlassBlock.POWERED) && (world.getTime() + offset) % delay == 0) {
            List<ItemEntity> itemEntities = world.getEntitiesByType(EntityType.ITEM, new Box(pos.add(-radius, -radius, -radius), pos.add(radius + 1, radius + 1, radius + 1)), ignored -> true);
            entityCheck:
                for (ItemEntity itemEntity : itemEntities) {
                    ItemStack stack = itemEntity.getStack();
                    if(inventory.isEmpty()) {
                        inventory.set(0, stack);
                        genStreak(itemEntity.getPos());
                        itemEntity.kill();
                        continue;
                    }
                    for (int i = 0; i < inventory.size(); i++) {
                        if(inventory.get(i).isEmpty()) {
                            inventory.set(i, stack);
                            genStreak(itemEntity.getPos());
                            itemEntity.kill();
                            continue entityCheck;
                        }
                    }
                    int slot = getItemSlot(stack);
                    if(slot != -1) {
                        ItemStack invStack = getStack(slot);
                        int change = Math.min(invStack.getMaxCount() - invStack.getCount(), stack.getCount());
                        getStack(slot).increment(change);
                        stack.decrement(change);
                        genStreak(itemEntity.getPos());
                    }
                }
        }
    }

    private void genStreak(Vec3d target) {
        Vec3d startPos = Vec3d.of(pos).add(0.5, 0.5, 0.5);
        double lenX = startPos.x - target.x;
        double lenY = startPos.y - target.y;
        double lenZ = startPos.z - target.z;
        double allLen = startPos.squaredDistanceTo(target);
        for (int i = 0; i < allLen; i++) {
            ((ServerWorld) world).spawnParticles(advanced ?  ParticleTypes.ENCHANTED_HIT : ParticleTypes.WITCH, startPos.x - ((lenX / allLen) * i), startPos.y - ((lenY / allLen) * i),startPos.z - ((lenZ / allLen) * i), 1, 0, 0, 0, 0);
        }
    }

    @Override
    public boolean canExtract(int slot, ItemStack stack, Direction dir) {
        return true;
    }
}
