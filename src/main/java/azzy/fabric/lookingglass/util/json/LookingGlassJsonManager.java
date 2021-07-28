package azzy.fabric.lookingglass.util.json;

import azzy.fabric.incubus_core.json.JsonUtils;
import com.google.gson.JsonObject;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.apache.commons.lang3.StringUtils;

import java.io.InputStream;
import java.util.*;
import java.util.function.BiConsumer;

import static azzy.fabric.lookingglass.LookingGlassCommon.FFLog;

public class LookingGlassJsonManager {

    private static final Set<EntityType<?>> GOLDEN_LASSO_BLACKLIST = new HashSet<>();
    private static final Set<EntityType<?>> CURSED_LASSO_BLACKLIST = new HashSet<>();
    private static boolean goldenLassoOverride, cursedLassoOverride;
    private static final Map<String, Integer> SPIKE_DAMAGE_LIST = new HashMap<>();
    public static final Map<Block, Integer> BLOCK_ENCHANTING_POWER = new HashMap<>();
    private static boolean blockEnchantingPowerOverride;

    public static void load(ResourceManager manager) {
        GOLDEN_LASSO_BLACKLIST.clear();
        CURSED_LASSO_BLACKLIST.clear();
        SPIKE_DAMAGE_LIST.clear();

        goldenLassoOverride = false;
        cursedLassoOverride = false;
        blockEnchantingPowerOverride = false;

        manager.findResources("lookingglass/config", path -> path.endsWith(".json")).forEach(identifier -> loadConfigs(manager, identifier));
    }

    public static void loadConfigs(ResourceManager manager, Identifier identifier) {
        ConfigType configType = null;
        try (InputStream stream = manager.getResource(identifier).getInputStream()) {
            JsonObject configJson = JsonUtils.fromInputStream(stream);

            configType = ConfigType.valueOf(configJson.get("type").getAsString());
            configType.processor.accept(configJson, configType);
        } catch (Exception e) {
            FFLog.error("Error found while loading item config json " + identifier.toString() + "of type " + (configType != null ? configType.toString() : "WARNING: INVALID CONFIG TYPE"), e);
        }
    }

    private static void loadSuffuserConfigs(JsonObject parent, ConfigType configType) {
        if(blockEnchantingPowerOverride)
            return;
        if(parent.get("replace").getAsBoolean()) {
            blockEnchantingPowerOverride = true;
            BLOCK_ENCHANTING_POWER.clear();
        }
        parent.getAsJsonArray("entries").forEach(jsonElement -> {
            Block block = Registry.BLOCK.get(Identifier.tryParse(jsonElement.getAsJsonObject().get("block").getAsString()));
            BLOCK_ENCHANTING_POWER.put(block, jsonElement.getAsJsonObject().get("power").getAsInt());
        });
    }

    private static void loadLassoConfigs(JsonObject parent, ConfigType configType) {
        boolean golden = configType == ConfigType.GOLDEN_LASSO;
        if(golden && goldenLassoOverride || !golden && cursedLassoOverride)
            return;
        if(parent.get("replace").getAsBoolean()) {
            if(golden) {
                goldenLassoOverride = true;
                GOLDEN_LASSO_BLACKLIST.clear();
            }
            else {
                cursedLassoOverride = true;
                CURSED_LASSO_BLACKLIST.clear();
            }
        }
        parent.getAsJsonArray("blacklist").forEach(jsonElement -> {
            EntityType<?> type = Registry.ENTITY_TYPE.get(Identifier.tryParse(jsonElement.getAsString()));
            if(golden)
                GOLDEN_LASSO_BLACKLIST.add(type);
            else
                CURSED_LASSO_BLACKLIST.add(type);
        });
    }

    public static boolean canLassoCapture(EntityType<?> entityType, boolean cursed) {
        if (cursed) {
            return !CURSED_LASSO_BLACKLIST.contains(entityType);
        }
        return !GOLDEN_LASSO_BLACKLIST.contains(entityType);
    }

    public static List<ItemStack> addItemToList(List<ItemStack> inputList, String itemId, int count) {
        if (StringUtils.isBlank(itemId))
            return inputList;
        if (inputList == null)
            return null;

        Item item = Registry.ITEM.get(Identifier.tryParse(itemId));
        ItemStack itemStack = new ItemStack(item);
        itemStack.setCount(count);
        inputList.add(itemStack);

        return inputList;
    }

    private enum ConfigType {
        GOLDEN_LASSO(LookingGlassJsonManager::loadLassoConfigs),
        CURSED_LASSO(LookingGlassJsonManager::loadLassoConfigs),
        SUFFUSER_BOOSTING_BLOCKS(LookingGlassJsonManager::loadSuffuserConfigs);

        private final BiConsumer<JsonObject, ConfigType> processor;

        ConfigType(BiConsumer<JsonObject, ConfigType> processor) {
            this.processor = processor;
        }
    }
}
