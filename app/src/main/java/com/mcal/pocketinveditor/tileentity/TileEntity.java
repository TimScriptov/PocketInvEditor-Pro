package com.mcal.pocketinveditor.tileentity;

import java.util.ArrayList;
import java.util.List;
import com.mcal.pocketinveditor.util.Vector3f;

public class TileEntity {
    private List<Object> extras = new ArrayList<>();
    private String id = null;
    private int x = 0;
    private int y = 0;
    private int z = 0;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getZ() {
        return z;
    }

    public void setZ(int z) {
        this.z = z;
    }

    public double distanceSquaredTo(Vector3f other) {
        return (Math.pow((double) (other.x - ((float) x)), 2.0d) + Math.pow((double) (other.y - ((float) y)), 2.0d)) + Math.pow((double) (other.z - ((float) z)), 2.0d);
    }

    public List<Object> getExtraTags() {
        return extras;
    }

    public String toString() {
        return id + ": X: " + x + " Y: " + y + " Z: " + z;
    }
}
