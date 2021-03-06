package azzy.fabric.lookingglass.block;

import net.minecraft.entity.Entity;

public interface ShapeContextMixinInterface {
    Entity getEntity();

    Entity setEntity(Entity entity);
}