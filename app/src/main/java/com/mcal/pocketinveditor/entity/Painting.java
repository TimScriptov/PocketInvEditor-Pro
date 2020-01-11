package com.mcal.pocketinveditor.entity;

import com.mcal.pocketinveditor.util.Vector3f;

public class Painting extends Entity {
    private String artType = "Alban";
    private Vector3f blockCoordinates = new Vector3f(0.0f, 0.0f, 0.0f);
    private byte direction = (byte) 0;

    public String getArt() {
        return artType;
    }

    public void setArt(String art) {
        this.artType = art;
    }

    public Vector3f getBlockCoordinates() {
        return blockCoordinates;
    }

    public byte getDirection() {
        return direction;
    }

    public void setDirection(byte dir) {
        this.direction = dir;
    }
}
