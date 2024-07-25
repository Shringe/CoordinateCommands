package com.github.coordinatecommands;

import net.minecraft.client.MinecraftClient;
import net.minecraft.util.math.BlockPos;

public class CoordinateHelper {
    private BlockPos lastPosition;
    private BlockPos currentPosition;
    public BlockPos point1;
    public BlockPos point2;

    public BlockPos getLastPosition() {
        return lastPosition;
    }

    public BlockPos getCurrentPosition() {
        return currentPosition;
    }

    public void setNewPosition(BlockPos newPosition) {
        this.lastPosition = currentPosition;
        this.currentPosition = newPosition;
    }

    public void setPlayerPosition() {
        setNewPosition(getPlayerPosition());
    }

    public BlockPos getPlayerPosition() {
        return MinecraftClient.getInstance().player.getBlockPos();
    }

    public double pointDistance() {
        return straightLineDistance(point1, point2);
    }

    public double newDistance(BlockPos newPosition) {
        return straightLineDistance(lastPosition, currentPosition);
    }

    public void clearPoints() {
        this.point1 = null;
        this.point2 = null;
    }

    public static double straightLineDistance(BlockPos p1, BlockPos p2) {
        return Math.sqrt(p1.getSquaredDistance(p2));
    }
}
