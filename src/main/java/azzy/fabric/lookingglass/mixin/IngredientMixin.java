package azzy.fabric.lookingglass.mixin;

import azzy.fabric.lookingglass.util.MatchingStackAccessor;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.stream.Stream;

@Mixin(Ingredient.class)
public abstract class IngredientMixin implements MatchingStackAccessor {

    @Shadow protected abstract void cacheMatchingStacks();

    @Shadow private ItemStack[] matchingStacks;

    @Override
    public ItemStack[] getMatchingStacks() {
        cacheMatchingStacks();
        return matchingStacks;
    }
}
