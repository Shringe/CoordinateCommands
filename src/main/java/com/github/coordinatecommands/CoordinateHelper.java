package com.github.coordinatecommands;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.util.math.BlockPos;

public class DistanceHelper {
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

    public void setCurrentPosition(BlockPos currentPosition) {
        this.lastPosition = ;
        this.currentPosition = currentPosition;
    }

    public double pointDistance() {
        return point1.getSquaredDistance(point2);
    }

    public double newDistance(BlockPos newPosition) {
        return lastPosition.getSquaredDistance(newPosition);
    }

    public void getNewPosition() {
        ClientPlayerEntity player = MinecraftClient.getInstance().player;

        lastPosition = currentPosition;
        currentPosition = player.getBlockPos();
    }

}
