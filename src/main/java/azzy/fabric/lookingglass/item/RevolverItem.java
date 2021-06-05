package azzy.fabric.lookingglass.item;

import azzy.fabric.lookingglass.entity.RevolverShotEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;


public class RevolverItem extends Item {


    public RevolverItem(Settings settings) {
        super(settings);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack itemStack = user.getStackInHand(hand); // creates a new ItemStack instance of the user's itemStack in-hand
        world.playSound(null, user.getX(), user.getY(), user.getZ(), SoundEvents.ENTITY_FIREWORK_ROCKET_SHOOT, SoundCategory.NEUTRAL, 0.5F, 1F); // Play a sound when used

        if (!world.isClient) {
            RevolverShotEntity shotEntity = new RevolverShotEntity(world, user);
            shotEntity.setItem(itemStack);
            shotEntity.setProperties(user, user.getPitch(), user.getYaw(), 0.0F, 50F, 0F);
            world.spawnEntity(shotEntity); // spawns entity
        }
        return TypedActionResult.success(itemStack, world.isClient());
    }
}

