package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.effects.FalsePlayerDamageSource;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

public class DiamondSpikeBlock extends LookingGlassBlock {
    private WeakReference<ServerPlayerEntity> fakePlayer = null;
    private ServerPlayerEntity fakePlayerEntity = null;

    public DiamondSpikeBlock(FabricBlockSettings settings) {
        super(settings, false);
    }

    /**
     * I want mobs to be able to spawn in this block, as if this block is like grass.
     *
     * @return true
     */
    @Override
    public boolean canMobSpawnInside() {
        return true;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return VoxelShapes.empty();
    }

    @Override
    public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
        return 0;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient)
            return;

        if ((entity instanceof PlayerEntity) || !(entity instanceof LivingEntity))
            return;

        if (world.getServer() == null)
            return;

        if ((fakePlayer == null) || (fakePlayerEntity == null)) {
            fakePlayer = new WeakReference<>(new ServerPlayerEntity(world.getServer(), (ServerWorld) world, new GameProfile(null, "iritat"), new ServerPlayerInteractionManager((ServerWorld) world)));
            fakePlayerEntity = fakePlayer.get();

            if (fakePlayerEntity == null) {
                LookingGlassCommon.FFLog.warn("Error instantiating fake player for diamond spikes.  Defaulting to magic damage.  Kindly report to mod author(s).");
                entity.damage(DamageSource.MAGIC, 7);
                return;
            }

            fakePlayerEntity.setInvulnerable(true);
            fakePlayerEntity.setBoundingBox(new Box(0, 0, 0, 0, 0, 0));
        }

        entity.damage(new FalsePlayerDamageSource("diamond_spike", fakePlayerEntity, true, false, false), 7);
    }

    /**
     * This method can potentially be used to propagate enchantments to the block from the item, and use it to calculate damage.
     * It's theoretically possible to store the ItemStack within the block and pass it to a false player to wield and use for damage calculations too.
     *
     * @param world     World
     * @param pos       Block Position
     * @param state     Block State
     * @param placer    Placing User
     * @param itemStack The Item being placed.
     */
    @Override
    public void onPlaced(World world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack itemStack) {
//        ListTag enchantments = itemStack.getEnchantments();
//        Block placedBlock = state.getBlock();
    }
}