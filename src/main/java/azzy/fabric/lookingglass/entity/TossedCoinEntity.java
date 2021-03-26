package azzy.fabric.lookingglass.entity;

import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import azzy.fabric.lookingglass.LookingGlassClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.network.Packet;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;


public class TossedCoinEntity extends ThrownItemEntity {

    public TossedCoinEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public TossedCoinEntity(World world, LivingEntity owner) {
        super(LookingGlassEntities.TOSSED_COIN_ENTITY_TYPE, owner, world);
    }

    public TossedCoinEntity(World world, double x, double y, double z) {
        super(LookingGlassEntities.TOSSED_COIN_ENTITY_TYPE, x, y, z, world);
    }


    @Override
    protected Item getDefaultItem(){
        return null;
    }

    protected void onEntityHit(EntityHitResult entityHitResult) { // called on entity hit.
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity(); // sets a new Entity instance as the EntityHitResult (victim)

        if (entity instanceof RevolverShotEntity) {entity.playSound(SoundEvents.ENTITY_GENERIC_EXPLODE, 4F, 1F);
            //TODO Add behavior for shot ricochette

        }

        if (entity instanceof LivingEntity) {
            float i = 1;
            entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), (float)i); // Deal a smidgen of damage on a hit
        }
    }

    protected void onCollision(HitResult hitResult) { // called on collision with a block
        super.onCollision(hitResult);
        if (!this.world.isClient) { // checks if the world is client
            this.remove(); // kills the projectile
        }


    }

    @Override
    public Packet createSpawnPacket() {
        return EntitySpawnPacket.create(this, LookingGlassClient.PacketID);
    }

}
