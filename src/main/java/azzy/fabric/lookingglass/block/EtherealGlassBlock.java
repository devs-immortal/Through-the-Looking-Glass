package azzy.fabric.lookingglass.block;

import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;

public class EtherealGlassBlock extends LookingGlassBlock {
    private final GlassBlockTypes glassBlockTypes;

    public EtherealGlassBlock(FabricBlockSettings fabricBlockSettings, GlassBlockTypes glassBlockTypes) {
        super(fabricBlockSettings, false);
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
        ShapeContextMixinInterface shapeContextInterface = (ShapeContextMixinInterface) context;
        Entity collidingEntity = shapeContextInterface.getEntity();
        if (collidingEntity != null)
            System.out.println("Entity class: '" + collidingEntity.getClass().getName() + "'");

        if (collidingEntity instanceof PlayerEntity)
            return VoxelShapes.empty();
        return super.getCollisionShape(state, blockView, pos, context);
    }

    @Override
    public int getOpacity(BlockState state, BlockView world, BlockPos pos) {
        return 0;
    }

    @Override
    public float getAmbientOcclusionLightLevel(BlockState state, BlockView world, BlockPos pos) {
        return 1;
    }
}
