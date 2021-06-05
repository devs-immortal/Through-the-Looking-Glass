package azzy.fabric.lookingglass.item;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.client.resource.language.I18n;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

public class DataShardItem extends Item {
    public DataShardItem(Settings settings) {
        super(settings);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        BlockPos pos = context.getBlockPos();
        World world = context.getWorld();
        if(context.getPlayer().isSneaking()) {
            clearNBT(context.getStack());
            NbtCompound tag = context.getStack().getOrCreateSubTag("data");
            tag.putLong("pos", pos.asLong());
            tag.putString("id", Registry.BLOCK.getId(world.getBlockState(pos).getBlock()).toString());
            context.getPlayer().getItemCooldownManager().set(this, 5);
            world.playSound(null, context.getPlayer().getBlockPos(), SoundEvents.BLOCK_BELL_USE, SoundCategory.PLAYERS, 0.5F, 2F);
            if(!world.isClient()) {
                world.playSound(pos.getX(), pos.getY(), pos.getZ(), SoundEvents.BLOCK_RESPAWN_ANCHOR_CHARGE, SoundCategory.PLAYERS, 1f, 1f, true);
            }
        }
        return super.useOnBlock(context);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        HitResult result = user.raycast(5 + user.getAttributeValue(ReachEntityAttributes.REACH), 1f, false);
        if(result.getType() == HitResult.Type.MISS) {
            clearNBT(user.getStackInHand(hand));
            if(user.isSneaking()) {
                world.playSoundFromEntity(null, user, SoundEvents.BLOCK_BELL_RESONATE, SoundCategory.PLAYERS, 1F, 2F);
            }
            else {
                user.damage(DamageSource.MAGIC, 1F);
                user.getStackInHand(hand).getOrCreateSubTag("data").putUuid("uuid", user.getUuid());
            }
            user.getItemCooldownManager().set(this, 5);
        }
        return super.use(world, user, hand);
    }

    @SuppressWarnings("unchecked")
    public static <T> Optional<T> getData(ItemStack stack, DataType type) {
        if(stack.getOrCreateTag().contains("data")) {
            NbtCompound data = stack.getSubTag("data");
            Optional<?> result = switch (type) {
                case POS -> Optional.of(data.getLong("pos"));
                case ID -> Optional.of(data.getString("id"));
                case ENTITY -> Optional.of(data.getString("entity"));
                case ENTITYID -> Optional.of(data.getInt("entity_id"));
                case UUID -> Optional.of(data.getUuid("uuid"));
                default -> Optional.empty();
            };
            return (Optional<T>) result;
        }
        return Optional.empty();
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        Text text = new LiteralText("§8VVVVVV");
        List<Text> secText = new ArrayList<>();
        if(stack.getOrCreateTag().contains("data")) {
            NbtCompound data = stack.getSubTag("data");
            if(data.contains("pos")) {
                text = new LiteralText("§b" + BlockPos.fromLong(data.getLong("pos")).toShortString());
                if(data.contains("id")) {
                    secText.add(new LiteralText("§o§3" + I18n.translate(Registry.BLOCK.get(Identifier.tryParse(data.getString("id"))).getTranslationKey())));
                }
            }
            else if(data.contains("uuid")) {
                PlayerEntity playerEntity = world.getPlayerByUuid(data.getUuid("uuid"));
                if(playerEntity != null) {
                    text = new LiteralText("§b" + playerEntity.getEntityName());
                    secText.add(new LiteralText("§c" + playerEntity.getHealth() + " / " + playerEntity.getMaxHealth() + " HP"));
                    secText.add(new LiteralText("§d" + playerEntity.getArmor() + " def"));
                    secText.add(new LiteralText("§9" + I18n.translate(playerEntity.getStackInHand(Hand.MAIN_HAND).getTranslationKey())));
                    secText.add(new LiteralText("§3" + playerEntity.getBlockPos().toShortString()));
                }
                else {
                    text = new LiteralText("§l§cnull");
                }
            }
            else if(data.contains("entity_id")) {
                Iterator<Entity> entities = ((ClientWorld) world).getEntities().iterator();
                Entity entity = null;
                while (entities.hasNext()) {
                    Entity temp = entities.next();
                    if(temp.getUuid().equals(data.getUuid("entity_id"))) {
                        entity = temp;
                        break;
                    }
                }
                if(entity != null) {
                    text = new LiteralText("§b" + entity.getEntityName());
                    if(entity.isLiving()) {
                        secText.add(new LiteralText("§c" + ((LivingEntity) entity).getHealth() + " / " + ((LivingEntity) entity).getMaxHealth() + " HP"));
                        secText.add(new LiteralText("§d" + ((LivingEntity) entity).getArmor() + " def"));
                        secText.add(new LiteralText("§9" + I18n.translate(((LivingEntity) entity).getStackInHand(Hand.MAIN_HAND).getTranslationKey())));
                    }
                    secText.add(new LiteralText("§3" + entity.getBlockPos().toShortString()));
                }
                else {
                    text = new LiteralText("§l§cnull");
                }
            }
        }
        tooltip.add(text);
        tooltip.addAll(secText);
    }

    @Override
    public void inventoryTick(ItemStack stack, World world, Entity entity, int slot, boolean selected) {
        if(!world.isClient() && selected && stack.getOrCreateTag().contains("data") && stack.getSubTag("data").contains("entity_id")) {
            Entity target = ((ServerWorld) world).getEntity(stack.getSubTag("data").getUuid("entity_id"));
            if(target instanceof LivingEntity)
                ((LivingEntity) target).addStatusEffect(new StatusEffectInstance(StatusEffects.GLOWING, 5));
        }
        super.inventoryTick(stack, world, entity, slot, selected);
    }

    @Override
    public boolean hasGlint(ItemStack stack) {
        return stack.hasTag();
    }

    private void clearNBT(ItemStack stack) {
        stack.removeSubTag("data");
    }

    @Override
    public boolean postHit(ItemStack stack, LivingEntity target, LivingEntity attacker) {
        clearNBT(stack);
        NbtCompound tag = stack.getOrCreateSubTag("data");
        if(target instanceof PlayerEntity) {
            tag.putUuid("uuid", target.getUuid());
        }
        else {
            tag.putUuid("entity_id", target.getUuid());
            tag.putString("entity", Registry.ENTITY_TYPE.getId(target.getType()).toString());
        }
        return super.postHit(stack, target, attacker);
    }

    public enum DataType {
        POS,
        ID,
        ENTITY,
        ENTITYID,
        UUID,
        INV
    }
}
