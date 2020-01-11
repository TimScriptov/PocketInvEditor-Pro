package com.mcal.pocketinveditor.geo;

import com.mcal.pocketinveditor.util.Vector3f;

public class CuboidRegion {
    public int height;
    public int length;
    public int width;
    public int x;
    public int y;
    public int z;

    public CuboidRegion(int x, int y, int z, int width, int height, int length) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
        this.length = length;
    }

    public CuboidRegion(Vector3f pos, Vector3f size) {
        this((int) pos.getX(), (int) pos.getY(), (int) pos.getZ(), (int) size.getX(), (int) size.getY(), (int) size.getZ());
    }

    public CuboidRegion(CuboidRegion other) {
        this(other.x, other.y, other.z, other.width, other.height, other.length);
    }

    public boolean contains(CuboidRegion other) {
        return other.x >= this.x && other.y >= this.y && other.z >= this.z && other.x + other.width <= this.x + this.width && other.y + other.height <= this.y + this.height && other.z + other.length <= this.z + this.length;
    }

    public CuboidRegion createIntersection(CuboidRegion other) {
        int iSX;
        int iSY;
        int iSZ;
        int iX = this.x > other.x ? this.x : other.x;
        int iY = this.y > other.y ? this.y : other.y;
        int iZ = this.z > other.z ? this.z : other.z;
        int tSX = this.x + this.width;
        int tSY = this.y + this.height;
        int tSZ = this.z + this.length;
        int oSX = other.x + other.width;
        int oSY = other.y + other.height;
        int oSZ = other.z + other.length;
        if (tSX < oSX) {
            iSX = tSX;
        } else {
            iSX = oSX;
        }
        if (tSY < oSY) {
            iSY = tSY;
        } else {
            iSY = oSY;
        }
        if (tSZ < oSZ) {
            iSZ = tSZ;
        } else {
            iSZ = oSZ;
        }
        return new CuboidRegion(iX, iY, iZ, iSX - iX, iSY - iY, iSZ - iZ);
    }

    public boolean isValid() {
        return this.width >= 0 && this.height >= 0 && this.length >= 0;
    }

    public Vector3f getSize() {
        return new Vector3f((float) this.width, (float) this.height, (float) this.length);
    }

    public Vector3f getPosition() {
        return new Vector3f((float) this.x, (float) this.y, (float) this.z);
    }

    public int getBlockCount() {
        return (this.width * this.height) * this.length;
    }

    public static CuboidRegion fromPoints(Vector3f pos1, Vector3f pos2) {
        int minX = (int) (pos1.getX() < pos2.getX() ? pos1.getX() : pos2.getX());
        int minY = (int) (pos1.getY() < pos2.getY() ? pos1.getY() : pos2.getY());
        int minZ = (int) (pos1.getZ() < pos2.getZ() ? pos1.getZ() : pos2.getZ());
        return new CuboidRegion(minX, minY, minZ, (((int) (pos1.getX() >= pos2.getX() ? pos1.getX() : pos2.getX())) - minX) + 1, (((int) (pos1.getY() >= pos2.getY() ? pos1.getY() : pos2.getY())) - minY) + 1, (((int) (pos1.getZ() >= pos2.getZ() ? pos1.getZ() : pos2.getZ())) - minZ) + 1);
    }
}
