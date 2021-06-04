package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.effects.FalsePlayerDamageSource;
import azzy.fabric.lookingglass.util.InventoryWrapper;
import com.mojang.authlib.GameProfile;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ToolItem;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.network.ServerPlayerInteractionManager;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.Box;

import java.lang.ref.WeakReference;

public class SpecialAnnulationCoreEntity extends BlockEntity implements InventoryWrapper {

    private final DefaultedList<ItemStack> inv = DefaultedList.ofSize(1, ItemStack.EMPTY);
    private WeakReference<ServerPlayerEntity> fakePlayer = null;

    public SpecialAnnulationCoreEntity() {
        super(LookingGlassBlocks.SPECIAL_ANNULATION_CORE_ENTITY);
    }

    public void setItem(ItemStack item) {
        inv.set(0, item);
    }

    public ItemStack getItem() {
        ItemStack item = inv.get(0);
        inv.set(0, ItemStack.EMPTY);
        return item;
    }

    public boolean isItemSet() {
        return inv.get(0) != ItemStack.EMPTY;
    }

    public void attack(LivingEntity target) {
        if(fakePlayer == null || fakePlayer.get() == null) {
            fakePlayer = new WeakReference<>(new ServerPlayerEntity(world.getServer(), (ServerWorld) world, new GameProfile(null, "iritat"), new ServerPlayerInteractionManager((ServerWorld) world)));
            fakePlayer.get().setInvulnerable(true);
            fakePlayer.get().setBoundingBox(new Box(0, 0, 0, 0, 0, 0));
        }

        ItemStack stack = inv.get(0);
        float multiplier = 1F;


        if(stack.getItem() instanceof ToolItem){
            float level = ((ToolItem) stack.getItem()).getMaterial().getMiningLevel();
            level = level == 0 ? 1.5F : level / 2F;
            multiplier = level * (stack.getItem().getEnchantability() / 2.5F) / 2F;
        }
        fakePlayer.get().setStackInHand(Hand.MAIN_HAND, stack);
        EnchantmentHelper.onTargetDamaged(fakePlayer.get(), target);
        target.damage(new FalsePlayerDamageSource("erasure", fakePlayer.get(), true, false, false), EnchantmentHelper.getAttackDamage(stack, target.getGroup()) * multiplier + 0.5F);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound tag) {
        Inventories.writeNbt(tag, inv);
        return super.writeNbt(tag);
    }

    @Override
    public void readNbt(BlockState state, NbtCompound tag) {
        Inventories.readNbt(tag, inv);
        super.readNbt(state, tag);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inv;
    }
}
