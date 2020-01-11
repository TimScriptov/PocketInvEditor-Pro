package com.mcal.pocketinveditor.entity;

public class FallingBlock extends Entity {
    private byte blockData;
    private int blockId = 12;
    private int time;

    public int getBlockId() {
        return blockId;
    }

    public void setBlockId(int blockId) {
        this.blockId = blockId;
    }

    public byte getBlockData() {
        return blockData;
    }

    public void setBlockData(byte blockData) {
        this.blockData = blockData;
    }

	/** Ticks that this block has lived */
    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }
}
