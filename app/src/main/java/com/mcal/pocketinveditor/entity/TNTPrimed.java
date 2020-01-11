package com.mcal.pocketinveditor.entity;

public class TNTPrimed extends Entity {
    private byte fuse = (byte) 0;

    public byte getFuseTicks() {
        return fuse;
    }

    public void setFuseTicks(byte fuse) {
        this.fuse = fuse;
    }
}
