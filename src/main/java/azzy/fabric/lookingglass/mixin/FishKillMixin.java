package azzy.fabric.lookingglass.mixin;


import azzy.fabric.lookingglass.effects.FishDamageSource;
import azzy.fabric.lookingglass.item.FishWeaponItem;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityGroup;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.boss.dragon.EnderDragonPart;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.decoration.ArmorStandEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.SwordItem;
import net.minecraft.network.packet.s2c.play.EntityVelocityUpdateS2CPacket;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.stat.Stats;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Iterator;
import java.util.List;

@Mixin(PlayerEntity.class)
public abstract class FishKillMixin extends LivingEntity {

    @Shadow @Final public PlayerInventory inventory;

    protected FishKillMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = {"attack"}, at = @At("HEAD"), cancellable = true)
    public void attack(Entity target, CallbackInfo ci) {
        if(!target.handleAttack(this) && inventory.getMainHandStack().getItem() instanceof FishWeaponItem) {
            FishWeaponItem fish = (FishWeaponItem) inventory.getMainHandStack().getItem();
            if(target.isAttackable() || fish.isDivine()) {

                float f = (float)this.getAttributeValue(EntityAttributes.GENERIC_ATTACK_DAMAGE);
                float h;
                if (target instanceof LivingEntity) {
                    h = EnchantmentHelper.getAttackDamage(this.getMainHandStack(), ((LivingEntity)target).getGroup());
                } else {
                    h = EnchantmentHelper.getAttackDamage(this.getMainHandStack(), EntityGroup.DEFAULT);
                }

                float i = this.getAttackCooldownProgress(0.5F);
                f *= 0.2F + i * i * 0.8F;
                h *= i;
                this.resetLastAttackedTicks();
                if (f > 0.0F || h > 0.0F) {
                    boolean bl = i > 0.9F;
                    boolean bl2 = false;
                    int j = EnchantmentHelper.getKnockback(this);
                    if (this.isSprinting() && bl) {
                        this.world.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_KNOCKBACK, this.getSoundCategory(), 1.0F, 1.0F);
                        ++j;
                        bl2 = true;
                    }

                    boolean bl3 = bl && this.fallDistance > 0.0F && !this.onGround && !this.isClimbing() && !this.isTouchingWater() && !this.hasStatusEffect(StatusEffects.BLINDNESS) && !this.hasVehicle() && target instanceof LivingEntity;
                    bl3 = bl3 && !this.isSprinting();
                    if (bl3) {
                        f *= 1.5F;
                    }

                    f += h;
                    boolean bl4 = false;
                    double d = (double)(this.horizontalSpeed - this.prevHorizontalSpeed);
                    if (bl && !bl3 && !bl2 && this.onGround && d < (double)this.getMovementSpeed()) {
                        bl4 = true;
                    }

                    float k = 0.0F;
                    boolean bl5 = false;
                    int l = EnchantmentHelper.getFireAspect(this);
                    if (target instanceof LivingEntity) {
                        k = ((LivingEntity)target).getHealth();
                        if (l > 0 && !target.isOnFire()) {
                            bl5 = true;
                            target.setOnFireFor(1);
                        }
                    }

                    Vec3d vec3d = target.getVelocity();
                    boolean bl6 = target.damage(new FishDamageSource(fish.isDivine()), f);
                    if (bl6) {
                        if (j > 0) {
                            if (target instanceof LivingEntity) {
                                ((LivingEntity)target).takeKnockback((float)j * 0.5F, (double) MathHelper.sin(this.yaw * 0.017453292F), (double)(-MathHelper.cos(this.yaw * 0.017453292F)));
                            } else {
                                target.addVelocity((double)(-MathHelper.sin(this.yaw * 0.017453292F) * (float)j * 0.5F), 0.1D, (double)(MathHelper.cos(this.yaw * 0.017453292F) * (float)j * 0.5F));
                            }

                            this.setVelocity(this.getVelocity().multiply(0.6D, 1.0D, 0.6D));
                            this.setSprinting(false);
                        }

                        if (bl4) {
                            float m = 1.0F + EnchantmentHelper.getSweepingMultiplier(this) * f;
                            List<LivingEntity> list = this.world.getNonSpectatingEntities(LivingEntity.class, target.getBoundingBox().expand(1.0D, 0.25D, 1.0D));
                            Iterator var19 = list.iterator();

                            label166:
                            while(true) {
                                LivingEntity livingEntity;
                                do {
                                    do {
                                        do {
                                            do {
                                                if (!var19.hasNext()) {
                                                    this.world.playSound((PlayerEntity)null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_PLAYER_ATTACK_SWEEP, this.getSoundCategory(), 1.0F, 1.0F);
                                                    this.spawnSweepAttackParticles();
                                                    break label166;
                                                }

                                                livingEntity = (LivingEntity)var19.next();
                                            } while(livingEntity == this);
                                        } while(livingEntity == target);
                                    } while(this.isTeammate(livingEntity));
                                } while(livingEntity instanceof ArmorStandEntity && ((ArmorStandEntity)livingEntity).isMarker());

                                if (this.squaredDistanceTo(livingEntity) < 9.0D) {
                                    livingEntity.takeKnockback(0.4F, (double)MathHelper.sin(this.yaw * 0.017453292F), (double)(-MathHelper.cos(this.yaw * 0.017453292F)));
                                    livingEntity.damage(new FishDamageSource(fish.isDivine()), m);
                                }
                            }
                        }

                        if (target instanceof ServerPlayerEntity && target.velocityModified) {
                            ((ServerPlayerEntity)target).networkHandler.sendPacket(new EntityVelocityUpdateS2CPacket(target));
                            target.velocityModified = false;
                            target.setVelocity(vec3d);
                        }

                        if (bl3) {
                            this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_SALMON_FLOP, this.getSoundCategory(), 1.0F, 1.0F);
                            this.addCritParticles(target);
                        }

                        if (!bl3 && !bl4) {
                            if (bl) {
                                this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_COD_FLOP, this.getSoundCategory(), 1.0F, 1.0F);
                            } else {
                                this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_TROPICAL_FISH_FLOP, this.getSoundCategory(), 1.0F, 1.0F);
                            }
                        }

                        if (h > 0.0F) {
                            this.addEnchantedHitParticles(target);
                        }

                        this.onAttacking(target);
                    } else {
                        this.world.playSound(null, this.getX(), this.getY(), this.getZ(), SoundEvents.ENTITY_FISH_SWIM, this.getSoundCategory(), 1.0F, 1.0F);
                        if (bl5) {
                            target.extinguish();
                        }
                    }
                }
            }
            ci.cancel();
        }
    }

    @Shadow
    public abstract float getAttackCooldownProgress(float v);

    @Shadow
    public abstract void resetLastAttackedTicks();

    @Shadow
    public abstract void spawnSweepAttackParticles();

    @Shadow
    public abstract void addEnchantedHitParticles(Entity target);

    @Shadow
    public abstract void addCritParticles(Entity target);
}
