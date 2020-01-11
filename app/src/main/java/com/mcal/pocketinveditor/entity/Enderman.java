package com.mcal.pocketinveditor.entity;

public class Enderman extends Monster {
    private short carried = (short) 0;
    private short carriedData = (short) 0;

    public short getCarried() {
        return carried;
    }

    public void setCarried(short carried) {
        this.carried = carried;
    }

    public short getCarriedData() {
        return carriedData;
    }

    public void setCarriedData(short carriedData) {
        this.carriedData = carriedData;
    }

    public int getMaxHealth() {
        return 40;
    }
}
