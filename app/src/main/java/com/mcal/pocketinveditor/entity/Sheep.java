package com.mcal.pocketinveditor.entity;

public class Sheep extends Animal {
    private byte color = (byte) 0;
    private boolean sheared = false;

    public boolean isSheared() {
        return sheared;
    }

    public void setSheared(boolean sheared) {
        this.sheared = sheared;
    }

    public byte getColor() {
        return color;
    }

    public void setColor(byte color) {
        this.color = color;
    }

    public int getMaxHealth() {
        return 8;
    }
}
