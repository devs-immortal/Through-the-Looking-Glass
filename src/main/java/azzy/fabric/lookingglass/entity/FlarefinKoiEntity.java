package azzy.fabric.lookingglass.entity;

import azzy.fabric.lookingglass.effects.PiercingEntityDamageSource;
import azzy.fabric.lookingglass.entity.ai.DynamicAttackGoal;
import azzy.fabric.lookingglass.entity.ai.FishJumpGoal;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MovementType;
import net.minecraft.entity.ai.goal.FollowTargetGoal;
import net.minecraft.entity.ai.goal.MoveIntoWaterGoal;
import net.minecraft.entity.ai.goal.RevengeGoal;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.WitherEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.mob.*;
import net.minecraft.entity.passive.CodEntity;
import net.minecraft.entity.passive.DolphinEntity;
import net.minecraft.entity.passive.SquidEntity;
import net.minecraft.entity.passive.TurtleEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public class FlarefinKoiEntity extends CodEntity implements Angerable {

    private UUID angerTarget;
    private static final UUID ATTACKING_SPEED_BOOST_ID = UUID.fromString("49452A49-7EC5-45BB-B886-3B90B23A1718");
    private static final EntityAttributeModifier ATTACKING_SPEED_BOOST = new EntityAttributeModifier(ATTACKING_SPEED_BOOST_ID, "Attacking speed boost", 0.15D, EntityAttributeModifier.Operation.ADDITION);
    private int angerTime;

    public FlarefinKoiEntity(EntityType<FlarefinKoiEntity> type, World world) {
        super(type, world);
    }

    public static DefaultAttributeContainer.Builder createFlarefinAttributes() {
        return MobEntity.createMobAttributes().add(EntityAttributes.GENERIC_MAX_HEALTH, 16.0D).add(EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.25).add(EntityAttributes.GENERIC_ATTACK_DAMAGE, 2).add(EntityAttributes.GENERIC_ATTACK_KNOCKBACK, 0).add(EntityAttributes.GENERIC_ARMOR, 2);
    }

    @Override
    public void onBubbleColumnCollision(boolean drag) {
        if(drag)
            if(world.isClient) {
                this.applyStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 5), null);
            }
    }

    @Override
    public boolean isInvulnerableTo(DamageSource damageSource) {
        if(super.isInvulnerableTo(damageSource))
            return true;

        return damageSource.isFire() || damageSource == DamageSource.HOT_FLOOR;
    }


    @Override
    public boolean tryAttack(Entity target) {
        target.timeUntilRegen = 0;
        float f = (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (target instanceof LivingEntity) {
            f += EnchantmentHelper.getAttackDamage(this.getMainHandStack(), ((LivingEntity)target).getGroup());
        }

        boolean bl = target.damage(new PiercingEntityDamageSource("flarefin", this, false), f);
        boolean alt = target.damage(new PiercingEntityDamageSource("flarefin", this, true), f);

        if(alt)
            target.setOnFireFor(3);

        if(!bl)
            bl = alt;
        if (bl) {
            this.dealDamage(this, target);
            this.onAttacking(target);
        }

        return bl;
    }

    @Override
    public int getLimitPerChunk() {
        return 40;
    }

    @Override
    protected void initGoals() {
        super.initGoals();
        this.goalSelector.add(-2, new RevengeGoal(this).setGroupRevenge());
        this.goalSelector.add(-1, new MoveIntoWaterGoal(this));
        this.goalSelector.add(-1, new DynamicAttackGoal(this, 0.8, 0.1));
        this.targetSelector.add(0, new FollowTargetGoal<>(this, PlayerEntity.class, 0, true, true, this::shouldAngerAt));
        this.goalSelector.add(1, new FollowTargetGoal<>(this, ElderGuardianEntity.class, true));
        this.goalSelector.add(1, new FollowTargetGoal<>(this, DrownedEntity.class, true));
        this.goalSelector.add(2, new FollowTargetGoal<>(this, WitherEntity.class, true));
        this.goalSelector.add(2, new FollowTargetGoal<>(this, GuardianEntity.class, true));
        this.goalSelector.add(3, new FollowTargetGoal<>(this, DolphinEntity.class, true));
        this.goalSelector.add(3, new FollowTargetGoal<>(this, TurtleEntity.class, true));
        this.goalSelector.add(4, new FollowTargetGoal<>(this, SquidEntity.class, true));
        this.goalSelector.add(4, new FishJumpGoal(this, 20));
    }

    @Override
    public boolean cannotDespawn() {
        return super.cannotDespawn();
    }

    @Override
    protected void mobTick() {
        EntityAttributeInstance entityAttributeInstance = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        if(entityAttributeInstance != null) {
            if(getTarget() != null && !entityAttributeInstance.hasModifier(ATTACKING_SPEED_BOOST)) {
                entityAttributeInstance.addTemporaryModifier(ATTACKING_SPEED_BOOST);
            } else if(getTarget() == null) {
                entityAttributeInstance.removeModifier(ATTACKING_SPEED_BOOST);
            }
        }
        tickAngerLogic((ServerWorld) world, true);
        super.mobTick();
    }

    @Override
    public void travel(Vec3d movementInput) {
        if (this.canMoveVoluntarily() && this.isTouchingWater()) {
            this.updateVelocity(this.getMovementSpeed(), movementInput);
            this.move(MovementType.SELF, this.getVelocity());
            this.setVelocity(this.getVelocity().multiply(0.9D));
            if (this.getTarget() == null) {
                this.setVelocity(this.getVelocity().add(0.0D, -0.005D, 0.0D));
            }
        } else {
            super.travel(movementInput);
        }
    }

    @Override
    public int getAngerTime() {
        return angerTime;
    }

    @Override
    public void setAngerTime(int ticks) {
        this.angerTime = ticks;
    }

    @Nullable
    @Override
    public UUID getAngryAt() {
        return angerTarget;
    }

    @Override
    public void setAngryAt(@Nullable UUID uuid) {
        angerTarget = uuid;
    }

    @Override
    public void chooseRandomAngerTime() {
        angerTime = random.nextInt(20000) + 20000;
    }
}
