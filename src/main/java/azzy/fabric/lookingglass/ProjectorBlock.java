package azzy.fabric.lookingglass;

import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import static azzy.fabric.lookingglass.LookingGlass.MODID;

public class ProjectorBlock extends Block implements BlockEntityProvider {
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
                    case(0): label = "Image"; break;
                    case(1): label = "Item"; break;
                    case(2): label = "Sign"; break;
                    case(3): label = "Video (TBD)"; break;
                    case(4): label = "Player (TBD)"; break;
                }

                MinecraftClient.getInstance().player.sendMessage(new LiteralText("§aSwitched mode to: "+label));
            }
        }
        return ActionResult.SUCCESS;
    }

    @Override
    public boolean hasBlockEntity() {
        return true;
    }

    @Override
    public BlockEntity createBlockEntity(BlockView blockView){
        return new ProjectorEntity();
    }
}
