package azzy.fabric.lookingglass.block;

import azzy.fabric.lookingglass.blockentity.ProjectorEntity;
import azzy.fabric.lookingglass.util.GeneralNetworking;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.server.network.ServerPlayerEntity;
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

public class ProjectorBlock extends LookingGlassBlock implements BlockEntityProvider {

    private final VoxelShape shape = VoxelShapes.cuboid(0, 0, 0, 1, 0.375, 1);

    public ProjectorBlock(Settings settings) {
        super(settings, true);
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit) {
        ProjectorEntity entity = (ProjectorEntity) world.getBlockEntity(pos);
        if(entity != null) {
            if(!player.isInSneakingPose())
                player.openHandledScreen(state.createScreenHandlerFactory(world, pos));
            if(!world.isClient) {
                if(player.isInSneakingPose()) {
                    if(entity.displayState < 4)
                        entity.displayState++;
                    else
                        entity.displayState = 0;

                    ServerPlayNetworking.send((ServerPlayerEntity) player, GeneralNetworking.TRANSLATEABLE_PACKET, PacketByteBufs.create().writeString(getClientMessage(entity)));
                }
                entity.sync();
            }
        }
        return ActionResult.SUCCESS;
    }

    public String getClientMessage(ProjectorEntity entity) {
        switch(entity.displayState){
            case(0): return "label.lookingglass.glass.image_switch";
            case(1): return "label.lookingglass.glass.item_switch";
            case(2): return "label.lookingglass.glass.sign_switch";
            case(3): return "label.lookingglass.glass.mob_switch";
            case(4): return "label.lookingglass.glass.player_switch";
            default:return "";
        }
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
    public BlockEntity createBlockEntity(BlockView blockView){
        return new ProjectorEntity();
    }
}
