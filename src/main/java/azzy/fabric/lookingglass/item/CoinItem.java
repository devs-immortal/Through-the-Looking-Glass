package azzy.fabric.lookingglass.item;

import azzy.fabric.lookingglass.entity.TossedCoinEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class CoinItem extends Item{

    public CoinItem(Item.Settings settings) { super(settings); }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.getItemCooldownManager().set(this, 0); // Cooldown to use again, not sure what the unit is though
        ItemStack itemStack = user.getStackInHand(hand); // creates a new ItemStack instance of the user's itemStack in-hand
        world.playSound(user, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_EXPERIENCE_ORB_PICKUP, SoundCategory.NEUTRAL, 0.5F, 1F); // Play a sound when used

        if (!world.isClient) {
            TossedCoinEntity shotEntity = new TossedCoinEntity(world, user);
            shotEntity.setItem(itemStack);
            shotEntity.setProperties(user, user.pitch, user.yaw, 0.0F, 1F, 1F);
            world.spawnEntity(shotEntity); // spawns entity
        }
        return TypedActionResult.success(itemStack, world.isClient());
    }
}
