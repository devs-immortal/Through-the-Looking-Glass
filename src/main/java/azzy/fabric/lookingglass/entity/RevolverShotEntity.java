package azzy.fabric.lookingglass.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.BlazeEntity;
import net.minecraft.entity.mob.PiglinEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.item.Item;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.world.World;

public class RevolverShotEntity extends ThrownItemEntity {

    public RevolverShotEntity(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    public RevolverShotEntity(World world, LivingEntity owner) {
        super(LookingGlassEntities.REVOLVER_SHOT_ENTITY_TYPE, owner, world);
    }

    public RevolverShotEntity(World world, double x, double y, double z) {
        super(LookingGlassEntities.REVOLVER_SHOT_ENTITY_TYPE, x, y, z, world);
    }


    @Override
    protected Item getDefaultItem(){
        return null;
    }

    protected void onEntityHit(EntityHitResult entityHitResult) { // called on entity hit.
        super.onEntityHit(entityHitResult);
        Entity entity = entityHitResult.getEntity(); // sets a new Entity instance as the EntityHitResult (victim)

        int i = 5; // Default Damage Value
        if (entity instanceof BlazeEntity) { i = 10;} //Deal more damage if entity hit is from the nether
        if (entity instanceof PiglinEntity) { i = 10;}
        //TODO add the rest of nether mobs

        entity.damage(DamageSource.thrownProjectile(this, this.getOwner()), (float)i); // deals damage

        if (entity instanceof LivingEntity) { // checks if entity is an instance of LivingEntity (meaning it is not a boat or minecart)
            ((LivingEntity) entity).addStatusEffect((new StatusEffectInstance(StatusEffects.BLINDNESS, 20 * 3, 0))); // applies a status effect
            ((LivingEntity) entity).addStatusEffect((new StatusEffectInstance(StatusEffects.SLOWNESS, 20 * 3, 2))); // applies a status effect
            entity.playSound(SoundEvents.AMBIENT_CAVE, 2F, 1F); // plays a sound for the entity hit only
        }

    }

    protected void onCollision(HitResult hitResult) { // called on collision with a block
        super.onCollision(hitResult);
        if (!this.world.isClient) { // checks if the world is client
            this.remove(RemovalReason.KILLED); // kills the projectile
        }

    }

}
