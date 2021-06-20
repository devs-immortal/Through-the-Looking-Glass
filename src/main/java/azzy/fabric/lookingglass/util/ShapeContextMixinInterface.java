package azzy.fabric.lookingglass.util;

import net.minecraft.entity.Entity;

public interface ShapeContextMixinInterface {
    Entity getCachedEntity();

    Entity setEntity(Entity entity);
}
