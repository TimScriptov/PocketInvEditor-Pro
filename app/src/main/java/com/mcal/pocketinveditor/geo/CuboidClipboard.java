package com.mcal.pocketinveditor.geo;

import com.mcal.pocketinveditor.util.Vector3f;

public class CuboidClipboard implements AreaBlockAccess, SizeLimitedArea {
    public static final int AIR = 0;
    public byte[] blocks;
    protected int height;
    protected int length;
    public byte[] metaData;
    protected int width;

    public CuboidClipboard(Vector3f size, byte[] blocks, byte[] data) {
        this.width = (int) size.getX();
        this.height = (int) size.getY();
        this.length = (int) size.getZ();
        this.blocks = blocks;
        this.metaData = data;
    }

    public CuboidClipboard(Vector3f size) {
        this(size, new byte[((int) ((size.getX() * size.getY()) * size.getZ()))], new byte[((int) ((size.getX() * size.getY()) * size.getZ()))]);
    }

    public int getWidth() {
        return this.width;
    }

    public int getHeight() {
        return this.height;
    }

    public int getLength() {
        return this.length;
    }

    public int getBlockTypeId(int x, int y, int z) {
        return this.blocks[getOffset(x, y, z)];
    }

    public void setBlockTypeId(int x, int y, int z, int type) {
        this.blocks[getOffset(x, y, z)] = (byte) type;
    }

    public int getBlockData(int x, int y, int z) {
        return this.metaData[getOffset(x, y, z)];
    }

    public void setBlockData(int x, int y, int z, int type) {
        this.metaData[getOffset(x, y, z)] = (byte) type;
    }

    public int getOffset(int x, int y, int z) {
        return (((this.width * y) * this.length) + (this.width * z)) + x;
    }

    public void place(AreaBlockAccess world, Vector3f startPoint, boolean noAir) {
        int beginX = (int) startPoint.getX();
        int beginY = (int) startPoint.getY();
        int beginZ = (int) startPoint.getZ();
        for (int x = 0; x < this.width; x++) {
            for (int z = 0; z < this.length; z++) {
                for (int y = 0; y < this.height; y++) {
                    int blockId = getBlockTypeId(x, y, z);
                    if (!noAir || blockId != 0) {
                        int data = getBlockData(x, y, z);
                        world.setBlockTypeId(beginX + x, beginY + y, beginZ + z, blockId);
                        world.setBlockData(beginX + x, beginY + y, beginZ + z, data);
                    }
                }
            }
        }
    }

    public void copy(AreaBlockAccess world, Vector3f startPoint) {
        int beginX = startPoint.getBlockX();
        int beginY = startPoint.getBlockY();
        int beginZ = startPoint.getBlockZ();
        for (int x = 0; x < this.width; x++) {
            for (int z = 0; z < this.length; z++) {
                for (int y = 0; y < this.height; y++) {
                    int blockId = world.getBlockTypeId(beginX + x, beginY + y, beginZ + z);
                    int data = world.getBlockData(beginX + x, beginY + y, beginZ + z);
                    setBlockTypeId(x, y, z, blockId);
                    setBlockData(x, y, z, data);
                }
            }
        }
    }
}
