package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.render.TesseractRenderable;
import azzy.fabric.lookingglass.util.machine.BlockEntityMover;
import net.fabricmc.fabric.api.block.entity.BlockEntityClientSerializable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import org.apache.commons.lang3.tuple.Triple;

import java.util.Optional;

public class BlockTesseractEntity extends BlockEntity implements BlockEntityClientSerializable, TesseractRenderable {

    private static final ItemStack core = new ItemStack(Items.ENDER_EYE);
    private BlockPos movePos;

    public BlockTesseractEntity() {
        super(LookingGlassBlocks.BLOCK_TESSERACT_ENTITY);
    }

    public void moveBlock(Direction direction) {
        if(movePos != null) {
            BlockState moveState = world.getBlockState(movePos);
            BlockPos oldPos = movePos;
            BlockPos target = movePos.offset(direction);
            if(world.isAir(movePos)) {
                ((ServerWorld) world).spawnParticles(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, oldPos.getX() + 0.5, oldPos.getY() + 0.5, oldPos.getZ() + 0.5, 1, 0, 0, 0, 0);
                ((ServerWorld) world).spawnParticles(ParticleTypes.DRAGON_BREATH, movePos.getX() + 0.5, movePos.getY() + 0.5, movePos.getZ() + 0.5, 10 + world.getRandom().nextInt(10), 0, 0, 0, 0.04);
                movePos = null;
                sync();
                return;
            }
            BlockState targetState = world.getBlockState(target);
            Block moveBlock = moveState.getBlock();
            float targetHardness = targetState.getHardness(world, target);
            float moveHardness = moveState.getHardness(world, movePos);
            PushAction action = PushAction.NONE;

            //Do nothing if hardness is the same
            if(world.isAir(target) && !(moveHardness < 0F)) {
                action = PushAction.MOVE;
            }
            else if(moveBlock == LookingGlassBlocks.INTERMINAL_CORE || targetState.isOf(LookingGlassBlocks.INTERMINAL_CORE) || moveHardness < 0F) {
                action = PushAction.TRANSFER;
            }
            else if(moveBlock == LookingGlassBlocks.ANNULATION_CORE_1A || (moveHardness > targetHardness && targetHardness > 0f)) {
                action = PushAction.TARBREAK;
            }
            else if(targetHardness < 0F || moveHardness < targetHardness) {
                action = PushAction.SELFBREAK;
            }
            switch (action) {
                case TRANSFER: {
                    movePos = movePos.offset(direction);
                    ((ServerWorld) world).spawnParticles(ParticleTypes.DRAGON_BREATH, target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, 10 + world.getRandom().nextInt(10), 0, 0, 0, 0.08);
                    sync();
                    return;
                }
                case MOVE: {
                    BlockEntity entity = world.getBlockEntity(movePos);
                    if(entity != null) {
                        BlockEntityMover.directEntityMove(world, movePos, target);
                    }
                    else {
                        world.setBlockState(target, world.getBlockState(movePos));
                        world.setBlockState(movePos, Blocks.AIR.getDefaultState());
                    }
                    break;
                }
                case TARBREAK:{
                    world.breakBlock(target, true);
                    BlockEntity entity = world.getBlockEntity(movePos);
                    if(entity != null) {
                        BlockEntityMover.directEntityMove(world, movePos, target);
                    }
                    else {
                        world.setBlockState(target, world.getBlockState(movePos));
                        world.setBlockState(movePos, Blocks.AIR.getDefaultState());
                    }
                    break;
                }
                case SELFBREAK: {
                    world.breakBlock(movePos, true);
                }
                case NONE: return;
            }
            ((ServerWorld) world).spawnParticles(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, oldPos.getX() + 0.5, oldPos.getY() + 0.5, oldPos.getZ() + 0.5, 1, 0, 0, 0, 0);
            ((ServerWorld) world).spawnParticles(ParticleTypes.DRAGON_BREATH, target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, 10 + world.getRandom().nextInt(10), 0, 0, 0, 0.08);
            movePos = movePos.offset(direction);
            sync();
            return;
        }
        sync();
    }

    public boolean setTarget(Optional<Long> encodedPos) {
        if(encodedPos.isPresent() && encodedPos.get() != 0L) {
            BlockPos pos = BlockPos.fromLong(encodedPos.get());
            if(pos != this.pos && World.isInBuildLimit(pos)) {
                movePos = pos;
                return true;
            }
        }
        return false;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if(movePos != null)
            tag.putLong("target", movePos.asLong());
        return super.toTag(tag);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        if(tag.contains("target"))
            movePos = BlockPos.fromLong(tag.getLong("target"));
        super.fromTag(state, tag);
    }

    @Override
    public boolean shouldRenderCore() {
        return movePos != null;
    }

    @Override
    public boolean shouldRender() {
        return true;
    }

    @Override
    public ItemStack getCoreItem() {
        return core;
    }

    @Override
    public Triple<Integer, Integer, Integer> getColor() {
        return Triple.of(255, 255, 255);
    }

    @Override
    public CompoundTag toClientTag(CompoundTag tag) {
        if(movePos != null)
            tag.putLong("target", movePos.asLong());
        return tag;
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        if(tag.contains("target"))
            movePos = BlockPos.fromLong(tag.getLong("target"));
    }

    static {
        core.addEnchantment(Enchantments.SHARPNESS, 1);
    }

    private enum PushAction {
        NONE,
        TRANSFER,
        MOVE,
        TARBREAK,
        SELFBREAK
    }
}
