package azzy.fabric.lookingglass.item;

import azzy.fabric.lookingglass.LookingGlassCommon;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.ToolItem;
import net.minecraft.item.ToolMaterial;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

@SuppressWarnings("rawtypes")
public class GoldenLassoItem extends ToolItem {
    private static final String MOB_KEY = "MOB_KEY";
    private static final String MOB_TAG = "MOB_TAG";
    private static final String MOB_TYPE = "MOB_TYPE";

    public GoldenLassoItem(ToolMaterial toolMaterial, FabricItemSettings goldenLassoSettings) {
        super(toolMaterial, goldenLassoSettings);
    }

    /**
     * This method is called when the tool is used on a block.
     *
     * @param context Item Usage Context
     * @return ActionResult
     */
    public ActionResult useOnBlock(ItemUsageContext context) {
        World genericWorld = context.getWorld();
        // Spawn the mob above the clicked block position.
        BlockPos usedPosition = context.getBlockPos().up();

        CompoundTag tmpMobKey = context.getStack().getSubTag(MOB_KEY);
        System.out.println(tmpMobKey);

        // Don't bother playing on the client side.  We live on the server side.
        if (genericWorld.isClient)
            return ActionResult.PASS;

        ServerWorld world = (ServerWorld) genericWorld;
        ItemStack itemStack = context.getStack();
        CompoundTag stackTag = itemStack.getSubTag(MOB_KEY);

        if (stackTag == null) {
            return ActionResult.PASS;
        }

        CompoundTag mobTag = (CompoundTag) stackTag.get(MOB_TAG);
        String mobType = stackTag.getString(MOB_TYPE);
        Identifier mobTypeId = Identifier.tryParse(mobType);
        if (mobTypeId == null) {
            // Some issue with this mob.  Reset the lasso.
            LookingGlassCommon.FFLog.warn("Unable to spawn mob: '" + mobType + "'.");
            itemStack.removeSubTag(MOB_KEY);
            // TODO:  Reset the render for the lasso to make it empty again.
            return ActionResult.FAIL;
        }
        EntityType newMobEntityType = Registry.ENTITY_TYPE.get(mobTypeId);

        LivingEntity spawnedEntity = (LivingEntity) newMobEntityType.spawn(world, null, null, null, usedPosition, SpawnReason.COMMAND, false, false);

        if (spawnedEntity == null)
            return ActionResult.FAIL;

        spawnedEntity.fromTag(mobTag);

        // Remove the tag since we've successfully spawned the stored item away.
        itemStack.removeSubTag(MOB_KEY);

        return ActionResult.PASS;
    }

    /**
     * Called when the lasso is used on an entity.
     *
     * @param stack  The lasso (or a copy thereof, in creative).  For that reason, I won't be using this and instead, get the stack from the user object directly.
     * @param user   The user
     * @param entity The target entity
     * @param hand   The hand used
     * @return Result.
     */
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof PlayerEntity)
            return ActionResult.PASS;

        if (user.getEntityWorld().isClient)
            return ActionResult.PASS;

        // If we use the ItemStack that's been provided, the lasso won't work in creative.
        // Because, in creative mode, we get a copy of the itemstack that the user is wielding, not the actual itemstack.
        // To avoid this issue, I'm going to get the itemStack from user.getActiveItem()
        CompoundTag stackTag = stack.getOrCreateSubTag(MOB_KEY);
        CompoundTag mobTag = new CompoundTag();
        entity.saveSelfToTag(mobTag);
        EntityType entityType = entity.getType();
        Identifier entityId = Registry.ENTITY_TYPE.getId(entityType);
        stackTag.put(MOB_TAG, mobTag);
        stackTag.putString(MOB_TYPE, entityId.toString());

        entity.remove();
        // TODO:  Change the render for the lasso to make it loaded with the item.
        // TODO:  Change the lasso's notification to show the captured mob details (type, health, max health, etc.)
        return ActionResult.SUCCESS;
    }
}