package com.mcal.pocketinveditor.entity;

public class PigZombie extends Monster {
    private short anger = (short) 0;

    public int getMaxHealth() {
        return 20;
    }

    public short getAnger() {
        return anger;
    }

    public void setAnger(short anger) {
        this.anger = anger;
    }
}
