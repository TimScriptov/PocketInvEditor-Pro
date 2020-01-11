package com.mcal.pocketinveditor.entity;

import java.util.ArrayList;
import java.util.List;
import com.mcal.pocketinveditor.util.Vector3f;

public class Entity {
    private short air = (short) 300;
    private List<Object> extras = new ArrayList<>();
    private float fallDistance;
    private short fire;
    private Vector3f location = new Vector3f(0.0f, 0.0f, 0.0f);
    private Vector3f motion = new Vector3f(0.0f, 0.0f, 0.0f);
    private boolean onGround = false;
    private float pitch;
    private Entity riding = null;
    private int typeId = 0;
    private float yaw;

    public Vector3f getLocation() {
        return location;
    }

    public void setLocation(Vector3f location) {
        this.location = location;
    }

    public float getPitch() {
        return pitch;
    }

    public void setPitch(float pitch) {
        this.pitch = pitch;
    }

    public float getYaw() {
        return yaw;
    }

    public void setYaw(float yaw) {
        this.yaw = yaw;
    }

    public Vector3f getVelocity() {
        return motion;
    }

    public void setVelocity(Vector3f velocity) {
        this.motion = velocity;
    }

    public float getFallDistance() {
        return fallDistance;
    }

    public void setFallDistance(float fallDistance) {
        this.fallDistance = fallDistance;
    }

    public short getFireTicks() {
        return fire;
    }

    public void setFireTicks(short fire) {
        this.fire = fire;
    }

    public short getAirTicks() {
        return air;
    }

    public void setAirTicks(short air) {
        this.air = air;
    }

    public boolean isOnGround() {
        return onGround;
    }

    public void setOnGround(boolean onGround) {
        this.onGround = onGround;
    }

    public int getEntityTypeId() {
        return typeId;
    }

    public void setEntityTypeId(int typeId) {
        this.typeId = typeId;
    }

    public Entity getRiding() {
        return riding;
    }

    public void setRiding(Entity riding) {
        this.riding = riding;
    }

    public EntityType getEntityType() {
        EntityType type = EntityType.getById(typeId);
        if (type == null) {
            return EntityType.UNKNOWN;
        }
        return type;
    }

    public List<Object> getExtraTags() {
        return extras;
    }
}
