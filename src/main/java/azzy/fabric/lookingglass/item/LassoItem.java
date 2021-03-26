package azzy.fabric.lookingglass.item;

import azzy.fabric.lookingglass.LookingGlassCommon;
import azzy.fabric.lookingglass.util.json.LookingGlassJsonManager;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.LiteralText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import static azzy.fabric.lookingglass.LookingGlassConstants.*;

@SuppressWarnings("rawtypes")
public class LassoItem extends Item {
    private static final String MOB_KEY = "MOB_KEY";
    private static final String MOB_TYPE = "MOB_TYPE";
    private static final String MOB_HEALTH = "MOB_HEALTH";
    private static final String MOB_MAX_HEALTH = "MOB_MAX_HEALTH";

    private final boolean isCursed;

    public LassoItem(FabricItemSettings lassoSettings, boolean isCursed) {
        super(lassoSettings);
        this.isCursed = isCursed;
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

        // Don't bother playing on the client side.  We live on the server side.
        if (genericWorld.isClient)
            return ActionResult.PASS;

        if (genericWorld.getDifficulty() == Difficulty.PEACEFUL) {
            if (context.getPlayer() == null)
                return ActionResult.FAIL;

            context.getPlayer().sendMessage(new TranslatableText(ITEM_LOOKINGGLASS_GOLDENLASSO_PEACEFUL), true);
            return ActionResult.FAIL;
        }

        ServerWorld world = (ServerWorld) genericWorld;
        ItemStack itemStack = context.getStack();
        CompoundTag stackTag = itemStack.getSubTag(MOB_KEY);

        if (stackTag == null) {
            return ActionResult.PASS;
        }

        String mobType = stackTag.getString(MOB_TYPE);
        Identifier mobTypeId = Identifier.tryParse(mobType);
        if (mobTypeId == null) {
            // Some issue with this mob.  Reset the lasso.
            LookingGlassCommon.FFLog.warn("Unable to spawn mob: '" + mobType + "'.");
            itemStack.removeSubTag(MOB_KEY);
            return ActionResult.FAIL;
        }
        EntityType newMobEntityType = Registry.ENTITY_TYPE.get(mobTypeId);

        LivingEntity spawnedEntity = (LivingEntity) newMobEntityType.spawn(world, null, null, null, usedPosition, SpawnReason.COMMAND, false, false);

        if (spawnedEntity == null)
            return ActionResult.FAIL;

        // Remove the tag since we've successfully spawned the stored item away.
        itemStack.removeSubTag(MOB_KEY);

        return ActionResult.PASS;
    }

    /**
     * Called when the lasso is used on an entity.
     *
     * @param stack  The lasso
     * @param user   The user
     * @param entity The target entity
     * @param hand   The hand used
     * @return Result.
     */
    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        if (entity instanceof PlayerEntity)
            return ActionResult.PASS;

        // We don't mess with dead things.  This also fixes a possible bug where user has a lasso on main and offhand and uses
        // on an entity.
        if (!entity.isAlive())
            return ActionResult.PASS;

        if (!LookingGlassJsonManager.canLassoCapture(entity.getType(), isCursed)) {
            // The entity has been blacklisted OR not whitelisted.
            user.sendMessage(new TranslatableText(ITEM_LOOKINGGLASS_GOLDENLASSO_TABOO_ENTITY), true);
            return ActionResult.PASS;
        }

        // Golden lasso doesn't capture hostile entities.
        // Cursed lasso captures everything.
        if ((!isCursed) && (entity instanceof HostileEntity)) {
            // On the client side, send a notification message about the faux pas.
            if (user.getEntityWorld().isClient)
                user.sendMessage(new TranslatableText(ITEM_LOOKINGGLASS_GOLDENLASSO_HOSTILE_MOB), true);
            return ActionResult.PASS;
        }

        if (user.getEntityWorld().isClient)
            return ActionResult.PASS;

        // If we use the ItemStack that's been provided, the lasso won't work in creative.
        // Because, in creative mode, we get a copy of the itemstack that the user is wielding, not the actual itemstack.
        CompoundTag stackTag = stack.getOrCreateSubTag(MOB_KEY);
        EntityType entityType = entity.getType();
        Identifier entityId = Registry.ENTITY_TYPE.getId(entityType);
        float currentHealth = entity.getHealth();
        float maxHealth = entity.getMaxHealth();
        stackTag.putString(MOB_TYPE, entityId.toString());
        DecimalFormat decimalFormat = new DecimalFormat();
        decimalFormat.setMaximumFractionDigits(2);
        stackTag.putString(MOB_HEALTH, decimalFormat.format(currentHealth));
        stackTag.putString(MOB_MAX_HEALTH, decimalFormat.format(maxHealth));

        entity.remove();
        return ActionResult.SUCCESS;
    }

    /**
     * This method is called when the user mouse-overs the item.
     *
     * @param stack   The lasso item
     * @param world   The world
     * @param tooltip The tooltip text
     * @param context The tool tip context
     */
    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context) {
        CompoundTag stackTag = stack.getSubTag(MOB_KEY);
        if (stackTag == null)
            return;

        String entityTypeStr = stackTag.getString(MOB_TYPE);
        Identifier identifier = Identifier.tryParse(entityTypeStr);
        if (identifier == null)
            return;
        EntityType entityType = Registry.ENTITY_TYPE.get(identifier);
        String mobHealth = stackTag.getString(MOB_HEALTH);
        String mobMaxHealth = stackTag.getString(MOB_MAX_HEALTH);

        // Display the name and health of the caught entity.
        List<Text> displayText = new ArrayList<>();
        displayText.add(entityType.getName());
        Text text = new LiteralText("§3§oHealth: " + mobHealth + "/" + mobMaxHealth);
        displayText.add(text);

        tooltip.addAll(displayText);
    }

    /**
     * Determines whether or not the lasso should glow, this is determined by whether or not it contains an entity.
     *
     * @param stack The lasso
     * @return Should the lasso shine
     */
    @Override
    public boolean hasGlint(ItemStack stack) {
        return stack.getOrCreateTag().contains(MOB_KEY);
    }
}
