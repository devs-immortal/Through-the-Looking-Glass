package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.AnnulationCoreBlock;
import azzy.fabric.lookingglass.block.TTLGBlocks;
import azzy.fabric.lookingglass.render.TesseractRenderable;
import azzy.fabric.lookingglass.util.BlockEntityMover;
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
    private BlockPos receiver;

    public BlockTesseractEntity() {
        super(TTLGBlocks.BLOCK_TESSERACT_ENTITY);
    }

    public boolean moveBlock(Direction direction) {
        if(receiver != null) {
            BlockState receiverState = world.getBlockState(receiver);
            BlockPos oldPos = receiver;
            BlockPos target = receiver.offset(direction);
            if(world.isAir(receiver)) {
                ((ServerWorld) world).spawnParticles(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, oldPos.getX() + 0.5, oldPos.getY() + 0.5, oldPos.getZ() + 0.5, 1, 0, 0, 0, 0);
                ((ServerWorld) world).spawnParticles(ParticleTypes.DRAGON_BREATH, receiver.getX() + 0.5, receiver.getY() + 0.5, receiver.getZ() + 0.5, 10 + world.getRandom().nextInt(10), 0, 0, 0, 0.04);
                receiver = null;
                sync();
                return false;
            }
            if(!receiverState.isOf(TTLGBlocks.INTERMINAL_CORE)) {
                BlockState targetState = world.getBlockState(target);
                Block receiverBlock = receiverState.getBlock();
                float targetHardness = targetState.getHardness(world, target);
                byte flags = (byte) ((receiverBlock instanceof AnnulationCoreBlock && ((AnnulationCoreBlock) receiverBlock).canBreakAll()) ? 2 : targetHardness < 0.0F ? 0 : receiverBlock instanceof AnnulationCoreBlock ? 2 : receiverState.getHardness(world, receiver) < targetHardness ? 1 : receiverState.getHardness(world, receiver) > targetHardness ? 2 : 3);
                if (flags == 3) {
                    receiver = receiver.offset(direction);
                    sync();
                    return false;
                }
                else if(flags == 2) {
                    world.breakBlock(target, true);
                    if(world.getBlockEntity(receiver) != null) {
                        BlockEntityMover.tryMoveEntity(world, receiver, direction);
                    }
                    else {
                        BlockPos newPos = receiver.offset(direction);
                        world.setBlockState(newPos, receiverState);
                        world.setBlockState(receiver, Blocks.AIR.getDefaultState());
                    }
                }
                else {
                    world.breakBlock(receiver, true);
                    sync();
                    return true;
                }
                ((ServerWorld) world).spawnParticles(ParticleTypes.DRIPPING_OBSIDIAN_TEAR, oldPos.getX() + 0.5, oldPos.getY() + 0.5, oldPos.getZ() + 0.5, 1, 0, 0, 0, 0);
                ((ServerWorld) world).spawnParticles(ParticleTypes.DRAGON_BREATH, target.getX() + 0.5, target.getY() + 0.5, target.getZ() + 0.5, 10 + world.getRandom().nextInt(10), 0, 0, 0, 0.08);
            }
            receiver = receiver.offset(direction);
            sync();
            return true;
        }
        sync();
        return false;
    }

    public boolean setTarget(Optional<Long> encodedPos) {
        if(encodedPos.isPresent() && encodedPos.get() != 0L) {
            BlockPos pos = BlockPos.fromLong(encodedPos.get());
            if(pos != this.pos && World.isInBuildLimit(pos)) {
                receiver = pos;
                return true;
            }
        }
        return false;
    }

    @Override
    public CompoundTag toTag(CompoundTag tag) {
        if(receiver != null)
            tag.putLong("target", receiver.asLong());
        return super.toTag(tag);
    }

    @Override
    public void fromTag(BlockState state, CompoundTag tag) {
        if(tag.contains("target"))
            receiver = BlockPos.fromLong(tag.getLong("target"));
        super.fromTag(state, tag);
    }

    @Override
    public boolean shouldRenderCore() {
        return receiver != null;
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
        if(receiver != null)
            tag.putLong("target", receiver.asLong());
        return tag;
    }

    @Override
    public void fromClientTag(CompoundTag tag) {
        if(tag.contains("target"))
            receiver = BlockPos.fromLong(tag.getLong("target"));
    }

    static {
        core.addEnchantment(Enchantments.SHARPNESS, 1);
    }
}
