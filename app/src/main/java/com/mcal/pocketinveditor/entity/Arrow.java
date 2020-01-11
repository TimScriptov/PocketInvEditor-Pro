package com.mcal.pocketinveditor.entity;

public class Arrow extends Projectile {
    private byte inData = (byte) 0;
    private boolean player = false;

    public byte getInData() {
        return inData;
    }

    public void setInData(byte inData) {
        this.inData = inData;
    }

    public boolean isShotByPlayer() {
        return player;
    }

    public void setShotByPlayer(boolean player) {
        this.player = player;
    }
}
