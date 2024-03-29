package azzy.fabric.lookingglass.item;

import azzy.fabric.lookingglass.blockentity.LookingGlassUpgradeableMachine;
import azzy.fabric.lookingglass.blockentity.PowerConduitEntity;
import azzy.fabric.lookingglass.blockentity.SuffuserEntity;
import azzy.fabric.lookingglass.transport.PowerNetwork;
import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import dev.technici4n.fasttransferlib.api.energy.EnergyApi;
import dev.technici4n.fasttransferlib.api.energy.EnergyIo;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class EnergyProbeItem extends Item {

    public EnergyProbeItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        EnergyIo io = EnergyApi.SIDED.find(world, pos, context.getSide());
        ItemStack stack = context.getStack();
        if(io != null && !world.isClient()) {
            String message = "";
            if(stack.getOrCreateTag().contains("mode") && stack.getOrCreateTag().getBoolean("mode")) {
                if(io instanceof LookingGlassUpgradeableMachine) {
                    if(io instanceof SuffuserEntity) {
                        message = "enchanting power - " + ((SuffuserEntity) io).getEnchantingPower();
                    } else {
                        message = "process time - " + ((LookingGlassUpgradeableMachine) io).getProcessTime() + " ticks";
                    }
                }
                else if(io instanceof PowerConduitEntity) {
                    Optional<PowerNetwork> parent = ((PowerConduitEntity) io).getParentNetwork();
                    message += "network id - " + parent.map(network -> network.networkId.toString()).orElse("NONE") + " - network size - " + parent.map(PowerNetwork::getConnectedCables).orElse(-1);
                }
            }
            else {
                double energy = io.getEnergy();
                double max = io.getEnergyCapacity();
                if (Double.isFinite(energy)) {
                    message = Math.round(energy) + " Lu | max " + Math.round(max) + " Lu";
                }
                else {
                    message = energy + " Lu";
                }
                if(io instanceof LookingGlassUpgradeableMachine) {

                    message += " | drain " + String.format("%.1f", ((LookingGlassUpgradeableMachine) io).getPowerUsage()) + " Lu";
                }
                else if(io instanceof PowerConduitEntity) {
                    message += " | trans " + ((PowerConduitEntity) io).transferRate + " Lu";
                }
            }
            context.getPlayer().sendMessage(new LiteralText(message), true);
            return ActionResult.SUCCESS;
        }
        return super.useOnBlock(context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        HitResult result = user.raycast(5 + user.getAttributeValue(ReachEntityAttributes.REACH), 1f, false);
        if(result.getType() == HitResult.Type.MISS && user.isSneaking()) {
            NbtCompound tag = user.getStackInHand(hand).getOrCreateTag();
            if(!tag.contains("mode"))
                tag.putBoolean("mode", true);
            tag.putBoolean("mode", !tag.getBoolean("mode"));
            world.playSoundFromEntity(null, user, SoundEvents.BLOCK_BELL_RESONATE, SoundCategory.PLAYERS, 1F, 1.5F);
            return TypedActionResult.success(user.getStackInHand(hand));
        }
        return super.use(world, user, hand);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return stack.getOrCreateTag().contains("mode") && stack.getOrCreateTag().getBoolean("mode");
    }
}
