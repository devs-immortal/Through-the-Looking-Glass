package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.util.ShapeContextMixinInterface;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.AbstractGlassBlock;
import net.minecraft.block.BlockState;
import net.minecraft.block.EntityShapeContext;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

import java.util.Optional;

public class EtherealGlassBlock extends AbstractGlassBlock {
    public GlassBlockTypes glassBlockTypes;

    public EtherealGlassBlock(FabricBlockSettings fabricBlockSettings, GlassBlockTypes glassBlockTypes) {
        super(fabricBlockSettings);
        this.glassBlockTypes = glassBlockTypes;
    }

    /**
     * Make the block an empty block
     *
     * @param state     Block State
     * @param blockView Block View
     * @param pos       Block Position
     * @param context   Shape Context
     * @return Empty VoxelShape
     */
    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView blockView, BlockPos pos, ShapeContext context) {
        if(context instanceof EntityShapeContext) {
            Optional<Entity> optional = ((EntityShapeContext) context).getEntity();

            if(optional.isEmpty())
                return super.getCollisionShape(state, blockView, pos, context);

            Entity collidingEntity = optional.get();

            // Ghost glass allows everyone to pass through.
            if (GlassBlockTypes.GHOST_GLASS.equals(glassBlockTypes) || GlassBlockTypes.DARK_GHOST_GLASS.equals(glassBlockTypes))
                return VoxelShapes.empty();

            if (collidingEntity instanceof PlayerEntity) {
                // If the glass is ethereal type glass, it allows players to pass through.
                if (GlassBlockTypes.ETHEREAL.equals(glassBlockTypes) || GlassBlockTypes.DARK_ETHEREAL.equals(glassBlockTypes))
                    return VoxelShapes.empty();
            } else {
                // The entity is not a player entity.  Allow non-players to pass through for reverse ethereal glass types.
                if (GlassBlockTypes.REVERSE_ETHEREAL.equals(glassBlockTypes) || GlassBlockTypes.DARK_REVERSE_ETHEREAL.equals(glassBlockTypes))
                    return VoxelShapes.empty();
            }
        }

        return super.getCollisionShape(state, blockView, pos, context);
    }

    @Override
    public int getOpacity(BlockState state, BlockView blockView, BlockPos pos) {
        // Opaque to dark variants only.
        if (GlassBlockTypes.DARK_GLASS.equals(glassBlockTypes) || GlassBlockTypes.DARK_GHOST_GLASS.equals(glassBlockTypes) || GlassBlockTypes.DARK_ETHEREAL.equals(glassBlockTypes) || GlassBlockTypes.DARK_REVERSE_ETHEREAL.equals(glassBlockTypes))
            return blockView.getMaxLightLevel();

        return super.getOpacity(state, blockView, pos);
    }

    @Override
    public boolean emitsRedstonePower(BlockState state) {
        // Redstone glass emits redstone power.  Others don't.
        return GlassBlockTypes.RED_GLASS.equals(glassBlockTypes);
    }

    @Override
    public int getWeakRedstonePower(BlockState state, BlockView world, BlockPos pos, Direction direction) {
        // Redstone glass emits redstone power.  Others don't.
        if (GlassBlockTypes.RED_GLASS.equals(glassBlockTypes))
            return 15;

        return super.getWeakRedstonePower(state, world, pos, direction);
    }

    @Environment(EnvType.CLIENT)
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1.0F;
    }

    @Override
    public boolean isTranslucent(BlockState state, BlockView world, BlockPos pos) {
        // Ghost glass, ethereal glass, reverse_etheral glass, glow glass and red glass are translucent.
        return GlassBlockTypes.ETHEREAL.equals(glassBlockTypes) || GlassBlockTypes.REVERSE_ETHEREAL.equals(glassBlockTypes) || GlassBlockTypes.GHOST_GLASS.equals(glassBlockTypes) || GlassBlockTypes.GLOW_GLASS.equals(glassBlockTypes) || GlassBlockTypes.RED_GLASS.equals(glassBlockTypes);
    }
}
