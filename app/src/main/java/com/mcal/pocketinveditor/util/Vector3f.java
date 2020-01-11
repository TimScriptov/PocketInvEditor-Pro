package com.mcal.pocketinveditor.util;

public class Vector3f {
    public float x;
    public float y;
    public float z;

    public Vector3f() {
        this(0.0f, 0.0f, 0.0f);
    }

    public Vector3f(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public float getX() {
        return x;
    }

    public Vector3f setX(float x) {
        this.x = x;
        return this;
    }

    public float getY() {
        return y;
    }

    public Vector3f setY(float y) {
        this.y = y;
        return this;
    }

    public float getZ() {
        return z;
    }

    public Vector3f setZ(float z) {
        this.z = z;
        return this;
    }

    public int getBlockX() {
        return (int) x;
    }

    public int getBlockY() {
        return (int) y;
    }

    public int getBlockZ() {
        return (int) z;
    }

    public double distSquared(Vector3f other) {
        return (Math.pow(((double) other.x) - ((double) x), 2.0d) + Math.pow(((double) other.y) - ((double) y), 2.0d)) + Math.pow(((double) other.z) - ((double) z), 2.0d);
    }
}
