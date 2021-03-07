package azzy.fabric.lookingglass.util;

import net.minecraft.entity.Entity;

public interface ShapeContextMixinInterface {
    Entity getEntity();

    Entity setEntity(Entity entity);
}