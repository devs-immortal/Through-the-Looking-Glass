package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.ProjectorEntity;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import static azzy.fabric.lookingglass.LookingGlassCommon.MODID;

public class ProjectorBlock extends Block implements BlockEntityProvider {

    private final VoxelShape shape = VoxelShapes.cuboid(0, 0, 0, 1, 0.375, 1);

    public ProjectorBlock(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ProjectorEntity entity = (ProjectorEntity) world.getBlockEntity(pos);
        if(!world.isClient) {
            if(!player.isInSneakingPose())
            ContainerProviderRegistry.INSTANCE.openContainer(new Identifier(MODID, "projector_gui"), player, (packetByteBuf -> packetByteBuf.writeBlockPos(pos)));
        }
        if(player.isInSneakingPose()){
            if(entity.displayState < 4)
                entity.displayState++;
            else
                entity.displayState = 0;
            if(world.isClient()) {
                String label = "";
                switch(entity.displayState){
                    case(0): label = I18n.translate("label.lookingglass.glass.image_switch", label); break;
                    case(1): label = I18n.translate("label.lookingglass.glass.item_switch", label); break;
                    case(2): label = I18n.translate("label.lookingglass.glass.sign_switch");; break;
                    case(3): label = I18n.translate("label.lookingglass.glass.mob_switch");; break;
                    case(4): label = I18n.translate("label.lookingglass.glass.player_switch"); break;
                }

                MinecraftClient.getInstance().player.sendSystemMessage(new LiteralText(label), null);
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return shape;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        return shape;
    }

    @Override
    public void onStateReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean moved) {
        if (state.getBlock() != newState.getBlock()) {
            BlockEntity blockEntity = world.getBlockEntity(pos);
            if (blockEntity != null) {
                ItemScatterer.spawn(world, pos, (Inventory) blockEntity);
                world.updateComparators(pos, this);
            }
            super.onStateReplaced(state, world, pos, newState, moved);
        }
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView){
        return new ProjectorEntity();
    }
}
