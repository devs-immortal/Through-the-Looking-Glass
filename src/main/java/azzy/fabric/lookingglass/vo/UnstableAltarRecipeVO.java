package azzy.fabric.lookingglass.vo;

import net.minecraft.item.ItemStack;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class UnstableAltarRecipeVO {
    public List<ItemStack> inputList = new ArrayList<>();
    public List<ItemStack> outputList = new ArrayList<>();
    public int instability;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UnstableAltarRecipeVO that = (UnstableAltarRecipeVO) o;
        return Objects.equals(inputList, that.inputList);
    }

    /**
     * This method generates a unique recipe id based on the input ingredients.
     * Please note that changing the order of ingredients WILL change the hashcode.
     * But inherently, I want the infusion crafting to be shapeless since it's based on real-world crafting.
     *
     * @return SHA1 Hash
     */
    public String generateKey() {
        // SHA1 is more than enough for our needs.
        return DigestUtils.sha1Hex(inputList.toString());
    }
}
