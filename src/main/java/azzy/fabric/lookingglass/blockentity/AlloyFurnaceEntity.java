package azzy.fabric.lookingglass.blockentity;

import azzy.fabric.lookingglass.block.LookingGlassBlocks;
import azzy.fabric.lookingglass.gui.AlloyingFurnaceGuiDescription;
import azzy.fabric.lookingglass.recipe.AlloyingRecipe;
import azzy.fabric.lookingglass.recipe.LookingGlassRecipes;
import io.github.cottonmc.cotton.gui.PropertyDelegateHolder;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

import static net.minecraft.state.property.Properties.LIT;

public class AlloyFurnaceEntity extends LookingGlassUpgradeableMachine implements PropertyDelegateHolder {

    private AlloyingRecipe trackedRecipe;
    private int progress;

    public AlloyFurnaceEntity(BlockPos pos, BlockState state) {
        super(LookingGlassBlocks.ALLOY_FURNACE_ENTITY, pos, state, null, MachineTier.BASIC, 3, 200, 1000, 2);
    }

    @Override
    public void tick() {
        if(!world.isClient()) {
            if(trackedRecipe == null) {
                Optional<AlloyingRecipe> smeltable = world.getRecipeManager().getFirstMatch(LookingGlassRecipes.ALLOYING_RECIPE, this, world);
                smeltable.ifPresent(smeltingRecipe -> trackedRecipe = smeltingRecipe);
                tickRecipeProgression();
            }
            else {
                if(trackedRecipe.matches(this, world)) {
                    tickRecipeProgression();
                    if(!getCachedState().get(LIT))
                        world.setBlockState(pos, getCachedState().with(LIT, true));
                }
                else {
                    trackedRecipe = null;
                    progress = 0;
                    if(getCachedState().get(LIT))
                        world.setBlockState(pos, getCachedState().with(LIT, false));
                }
            }
            attemptPowerDraw();
            markDirty();
            sync();
        }
    }

    private void tickRecipeProgression() {
        if(trackedRecipe != null) {
            if(progress >= getProcessTime()) {
                trackedRecipe.craft(this);
                progress = 0;
                sync();
            }
            else {
                double drain = getPowerUsage();
                if(power - drain > 0) {
                    power -= drain;
                    progress++;
                }
                else if(progress > 0) {
                    progress--;
                }
            }
        }
    }

    @Override
    public @Nullable ScreenHandler createMenu(int syncId, PlayerInventory inv, PlayerEntity player) {
        return new AlloyingFurnaceGuiDescription(syncId, inv, ScreenHandlerContext.create(world, pos));
    }

    @Override
    public PropertyDelegate getPropertyDelegate() {
        return delegate;
    }

    private final PropertyDelegate delegate = new PropertyDelegate() {
        @Override
        public int get(int index) {
            switch (index) {
                case 0: return progress;
                case 1: return getProcessTime();
                case 2: return (int) Math.round(power);
                case 3: return (int) baseMaxPower;
            }
            return 0;
        }

        @Override
        public void set(int index, int value) {
        }

        @Override
        public int size() {
            return 4;
        }
    };

}
