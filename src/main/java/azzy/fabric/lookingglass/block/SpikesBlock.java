package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.effects.FalsePlayerDamageSource;
import com.mojang.authlib.GameProfile;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.lang.ref.WeakReference;

public class SpikesBlock extends LookingGlassBlock {
    private static final VoxelShape SHAPE = Block.createCuboidShape(0, 0, 0, 16, 2, 16);
    private WeakReference<ServerPlayerEntity> fakePlayer = null;
    private ServerPlayerEntity fakePlayerEntity = null;
    private final int damage;

    public SpikesBlock(FabricBlockSettings settings, int damage) {
        super(settings, false);
        this.damage = damage;
//        BlockState defaultState = getStateManager().getDefaultState();
//        setDefaultState(defaultState.with(VECTOR, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
    }

    /**
     * I shouldn't need this, but since azzy's code in LookingGlassBlock is kinda bugged right now, I'll have this code.
     *
     * @param ctx Placement Context
     * @return Block State
     */
    @Override
    public @Nullable BlockState getPlacementState(ItemPlacementContext ctx) {
        return super.getPlacementState(ctx);
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
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return SHAPE;
    }

    @Override
    public VoxelShape getCullingShape(BlockState state, BlockView world, BlockPos pos) {
        return VoxelShapes.empty();
    }

    @Override
    public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
        return 0;
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1;
    }

    @Override
    public void onEntityCollision(BlockState state, World world, BlockPos pos, Entity entity) {
        if (world.isClient)
            return;

        // Don't hurt players.  Don't hurt items.
        if ((entity instanceof PlayerEntity) || !(entity instanceof LivingEntity))
            return;

        // Just a NPE avoidance check.
        if (world.getServer() == null)
            return;

        if (this == LookingGlassBlocks.WOODEN_SPIKE_BLOCK) {
            // Wooden spikes cause magic damage, but only until the monster gets to 1 health (half heart)
            float health = ((LivingEntity) entity).getHealth();
            if (health > damage)
                entity.damage(DamageSource.MAGIC, damage);
            else
                // If the entity has less than "damage" health, just set it to 1.  Shouldn't happen since wooden spikes only do 1 damage right now.
                entity.damage(DamageSource.MAGIC, (health - 1));
        } else if (this == LookingGlassBlocks.IRON_SPIKE_BLOCK) {
            // Iron spikes cause magic damage - 4 for now.
            entity.damage(DamageSource.MAGIC, damage);
        } else if ((this == LookingGlassBlocks.DIAMOND_SPIKE_BLOCK) || (this == LookingGlassBlocks.NETHERITE_SPIKE_BLOCK)) {
            // Diamond spikes and above cause player damage.
            if ((fakePlayer == null) || (fakePlayerEntity == null)) {
                fakePlayer = new WeakReference<>(new ServerPlayerEntity(world.getServer(), (ServerWorld) world, new GameProfile(null, "TTLG_Spike_Player"), new ServerPlayerInteractionManager((ServerWorld) world)));
                fakePlayerEntity = fakePlayer.get();

                if (fakePlayerEntity == null) {
                    LookingGlassCommon.FFLog.warn("Error instantiating fake player for diamond spikes.  Defaulting to magic damage.  Kindly report to mod author(s).");
                    entity.damage(DamageSource.MAGIC, damage);
                    return;
                }

                fakePlayerEntity.setInvulnerable(true);
                fakePlayerEntity.setBoundingBox(new Box(0, 0, 0, 0, 0, 0));
            }

            entity.damage(new FalsePlayerDamageSource("spikes", fakePlayerEntity, true, false, false), damage);

/*
            // Here is the vector portion of the codebase.
            int vectorState = getStateManager().getDefaultState().get(VECTOR);
            switch(vectorState) {
                case 1:
                    // Slow speed
                    // TODO:  Add direction to the vector plate, store it as a property and use it to add to the velocity.
                    entity.addVelocity(2, 0, 2);
                    break;
                case 2:
                    // Medium speed
                    // TODO:  Add direction to the vector plate, store it as a property and use it to add to the velocity.
                    entity.addVelocity(4, 0, 4);
                    break;
                case 3:
                    // Fast speed
                    // TODO:  Add direction to the vector plate, store it as a property and use it to add to the velocity.
                    entity.addVelocity(8, 0, 8);
                    break;
                default:
                    // Do nothing.
            }
*/
        }
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
