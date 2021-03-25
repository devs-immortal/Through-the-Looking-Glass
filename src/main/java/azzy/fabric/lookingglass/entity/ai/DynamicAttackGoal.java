package azzy.fabric.lookingglass.entity.ai;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.entity.mob.MobEntity;

import java.util.EnumSet;

public class DynamicAttackGoal extends Goal {

    private final MobEntity mob;
    private LivingEntity target;
    private int cooldown;
    private final double baseSpeed, variance;
    double speed;

    public DynamicAttackGoal(MobEntity mob, double speed, double variance) {
        this.mob = mob;
        this.setControls(EnumSet.of(Control.MOVE, Control.LOOK));
        this.baseSpeed = speed;
        this.variance = variance;
    }

    public boolean canStart() {
        LivingEntity livingEntity = this.mob.getTarget();
        if (livingEntity == null) {
            return false;
        } else {
            this.target = livingEntity;
            return true;
        }
    }

    public boolean shouldContinue() {
        if (!this.target.isAlive()) {
            return false;
        } else if (this.mob.squaredDistanceTo(this.target) > 225.0D) {
            return false;
        } else {
            return !this.mob.getNavigation().isIdle() || this.canStart();
        }
    }

    public void stop() {
        this.target = null;
        this.mob.getNavigation().stop();
    }

    @Override
    public void start() {
        super.start();
        speed = baseSpeed + (mob.getRandom().nextDouble() * (variance * 2)) - variance;
    }

    public void tick() {
        this.mob.getLookControl().lookAt(this.target, 30.0F, 30.0F);
        double size = this.mob.getWidth() * 2.0F * this.mob.getWidth() * 2.0F;
        double distance = this.mob.squaredDistanceTo(this.target.getX(), this.target.getY(), this.target.getZ());
        double effectiveSpeed = speed;

        if (distance > size && distance < 16.0D) {
            effectiveSpeed *= 1.5;
        } else if (distance < 100.0D) {
            effectiveSpeed *= 2;
        }

        this.mob.getNavigation().startMovingTo(this.target, effectiveSpeed);
        this.cooldown = Math.max(this.cooldown - 1, 0);
        if (!(distance > size)) {
            if (this.cooldown <= 0) {
                this.cooldown = 3;
                this.mob.tryAttack(this.target);
            }
        }
    }
}
