package azzy.fabric.lookingglass.entity.ai;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ai.goal.DiveJumpingGoal;
import net.minecraft.entity.passive.FishEntity;
import net.minecraft.fluid.FluidState;
import net.minecraft.sound.SoundEvents;
import net.minecraft.tag.FluidTags;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

public class FishJumpGoal extends DiveJumpingGoal {
    private static final int[] OFFSET_MULTIPLIERS = new int[]{0, 1, 4, 5, 6, 7};
    private final FishEntity fish;
    private final int chance;
    private boolean inWater;

    public FishJumpGoal(FishEntity fish, int chance) {
        this.fish = fish;
        this.chance = chance;
    }

    public boolean canStart() {
        if (this.fish.getRandom().nextInt(this.chance) != 0) {
            return false;
        } else {
            Direction direction = this.fish.getMovementDirection();
            int i = direction.getOffsetX();
            int j = direction.getOffsetZ();
            BlockPos blockPos = this.fish.getBlockPos();
            int[] var5 = OFFSET_MULTIPLIERS;
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
                int k = var5[var7];
                if (!this.isWater(blockPos, i, j, k) || !this.isAirAbove(blockPos, i, j, k)) {
                    return false;
                }
            }

            return true;
        }
    }

    private boolean isWater(BlockPos pos, int xOffset, int zOffset, int multiplier) {
        BlockPos blockPos = pos.add(xOffset * multiplier, 0, zOffset * multiplier);
        return this.fish.world.getFluidState(blockPos).isIn(FluidTags.WATER) && !this.fish.world.getBlockState(blockPos).getMaterial().blocksMovement();
    }

    private boolean isAirAbove(BlockPos pos, int xOffset, int zOffset, int multiplier) {
        return this.fish.world.getBlockState(pos.add(xOffset * multiplier, 1, zOffset * multiplier)).isAir() && this.fish.world.getBlockState(pos.add(xOffset * multiplier, 2, zOffset * multiplier)).isAir();
    }

    public boolean shouldContinue() {
        double d = this.fish.getVelocity().y;
        return (!(d * d < 0.029999999329447746D) || this.fish.pitch == 0.0F || !(Math.abs(this.fish.pitch) < 10.0F) || !this.fish.isTouchingWater()) && !this.fish.isOnGround();
    }

    public boolean canStop() {
        return false;
    }

    public void start() {
        Direction direction = this.fish.getMovementDirection();
        this.fish.setVelocity(this.fish.getVelocity().add((double)direction.getOffsetX() * 0.6D, 0.7D, (double)direction.getOffsetZ() * 0.6D));
        this.fish.getNavigation().stop();
    }

    public void stop() {
        this.fish.pitch = 0.0F;
    }

    public void tick() {
        boolean bl = this.inWater;
        if (!bl) {
            FluidState fluidState = this.fish.world.getFluidState(this.fish.getBlockPos());
            this.inWater = fluidState.isIn(FluidTags.WATER);
        }

        if (this.inWater && !bl) {
            this.fish.playSound(SoundEvents.ENTITY_DOLPHIN_JUMP, 1.0F, 1.0F);
        }

        Vec3d vec3d = this.fish.getVelocity();
        if (vec3d.y * vec3d.y < 0.029999999329447746D && this.fish.pitch != 0.0F) {
            this.fish.pitch = MathHelper.lerpAngle(this.fish.pitch, 0.0F, 0.2F);
        } else {
            double d = Math.sqrt(Entity.squaredHorizontalLength(vec3d));
            double e = Math.signum(-vec3d.y) * Math.acos(d / vec3d.length()) * 57.2957763671875D;
            this.fish.pitch = (float)e;
        }

    }
}
