package azzy.fabric.lookingglass.util;

import com.google.gson.JsonObject;
import net.minecraft.entity.EntityType;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static azzy.fabric.lookingglass.LookingGlassCommon.FFLog;

public class LookingGlassJsonManager {

    private static final Set<EntityType<?>> GOLDEN_LASSO_ENTITIES_WHITELIST = new HashSet<>();
    private static final Set<EntityType<?>> CURSED_LASSO_ENTITIES_WHITELIST = new HashSet<>();
    private static final Set<EntityType<?>> GOLDEN_LASSO_ENTITIES_BLACKLIST = new HashSet<>();
    private static final Set<EntityType<?>> CURSED_LASSO_ENTITIES_BLACKLIST = new HashSet<>();

    public static void load(ResourceManager manager) {
        GOLDEN_LASSO_ENTITIES_WHITELIST.clear();
        CURSED_LASSO_ENTITIES_WHITELIST.clear();
        GOLDEN_LASSO_ENTITIES_BLACKLIST.clear();
        CURSED_LASSO_ENTITIES_BLACKLIST.clear();

        manager.findResources("lookingglass/item_config", path -> path.endsWith(".json")).forEach(identifier -> loadItemConfigs(manager, identifier));
    }

    public static boolean canLassoCapture(EntityType<?> entityType, boolean cursed) {
        if(cursed) {
            return CURSED_LASSO_ENTITIES_WHITELIST.contains(entityType) || (CURSED_LASSO_ENTITIES_WHITELIST.isEmpty() && !CURSED_LASSO_ENTITIES_BLACKLIST.contains(entityType));
        }
        return GOLDEN_LASSO_ENTITIES_WHITELIST.contains(entityType) || (GOLDEN_LASSO_ENTITIES_WHITELIST.isEmpty() && !GOLDEN_LASSO_ENTITIES_BLACKLIST.contains(entityType));
    }

    public static void loadItemConfigs(ResourceManager manager, Identifier identifier) {
        ItemConfigType configType = null;
        try(InputStream stream = manager.getResource(identifier).getInputStream()) {
            JsonObject configJson = JsonUtils.fromInputStream(stream);

            configType = ItemConfigType.valueOf(configJson.get("type").getAsString());
            configType.processor.accept(configJson, configType);
        } catch (Exception e) {
            FFLog.error("Error found while loading item config json " + identifier.toString() + "of type " + (configType != null ? configType.toString() : "WARNING: INVALID CONFIG TYPE"), e);
        }
    }

    private static void clearItemConfigs(ItemConfigType type) {
        switch (type) {
            case GOLDEN_LASSO: {
                GOLDEN_LASSO_ENTITIES_BLACKLIST.clear();
                GOLDEN_LASSO_ENTITIES_WHITELIST.clear();
                break;
            }
            case CURSED_LASSO: {
                CURSED_LASSO_ENTITIES_BLACKLIST.clear();
                CURSED_LASSO_ENTITIES_WHITELIST.clear();
                break;
            }
        }
    }

    private enum ItemConfigType {
        GOLDEN_LASSO(LookingGlassJsonManager::loadLassoConfigs),
        CURSED_LASSO(LookingGlassJsonManager::loadLassoConfigs);

        private final BiConsumer<JsonObject, ItemConfigType> processor;

        ItemConfigType(BiConsumer<JsonObject, ItemConfigType> processor) {
            this.processor = processor;
        }
    }

    private static void loadLassoConfigs(JsonObject parent, ItemConfigType configType) {
        if(parent.has("whitelist")) {
            parent.getAsJsonArray("whitelist").forEach(jsonElement -> {
                EntityType<?> type = Registry.ENTITY_TYPE.get(Identifier.tryParse(jsonElement.getAsString()));
                switch (configType) {
                    case GOLDEN_LASSO: GOLDEN_LASSO_ENTITIES_WHITELIST.add(type); break;
                    case CURSED_LASSO: CURSED_LASSO_ENTITIES_WHITELIST.add(type); break;
                }
            });
        }
        else {
            parent.getAsJsonArray("blacklist").forEach(jsonElement -> {
                EntityType<?> type = Registry.ENTITY_TYPE.get(Identifier.tryParse(jsonElement.getAsString()));
                switch (configType) {
                    case GOLDEN_LASSO: GOLDEN_LASSO_ENTITIES_BLACKLIST.add(type); break;
                    case CURSED_LASSO: CURSED_LASSO_ENTITIES_BLACKLIST.add(type); break;
                }
            });
        }
    }
}
