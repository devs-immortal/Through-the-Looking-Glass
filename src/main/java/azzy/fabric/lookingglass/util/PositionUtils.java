package azzy.fabric.lookingglass.util;

import net.minecraft.util.math.BlockPos;

public class PositionUtils {

    public static int getSquaredDistance(BlockPos startPos, BlockPos endPos) {
        int deltaX = Math.abs(startPos.getX() - endPos.getX());
        int deltaY = Math.abs(startPos.getY() - endPos.getY());
        int deltaZ = Math.abs(startPos.getZ() - endPos.getZ());

        return (int) Math.sqrt((deltaX * deltaX) + (deltaY * deltaY) + (deltaZ * deltaZ));
    }
}
