package com.mcal.pocketinveditor.io.leveldb;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class DBKey {
    public static final int CHUNK = 48;
    public static final int ENTITY = 50;
    public static final int PLACEHOLDER = 118;
    public static final int TILE_ENTITY = 49;
    private int type;
    private int x;
    private int z;

    public DBKey() {
        this(0, 0, 0);
    }

    public DBKey(int x, int z, int type) {
        this.x = x;
        this.z = z;
        this.type = type;
    }

    public DBKey(DBKey other) {
        this(other.x, other.z, other.type);
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    public int getType() {
        return this.type;
    }

    public DBKey setX(int x) {
        this.x = x;
        return this;
    }

    public DBKey setZ(int z) {
        this.z = z;
        return this;
    }

    public DBKey setType(int type) {
        this.type = type;
        return this;
    }

    public byte[] toBytes() {
        try {
            ByteArrayOutputStream bos = new ByteArrayOutputStream(9);
            DataOutputStream dos = new DataOutputStream(bos);
            dos.writeInt(Integer.reverseBytes(this.x));
            dos.writeInt(Integer.reverseBytes(this.z));
            dos.writeByte(this.type);
            return bos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void fromBytes(byte[] bytes) {
        if (bytes.length <= 8) {
            this.type = 0;
            return;
        }
        this.x = ((bytes[0] | (bytes[1] << 8)) | (bytes[2] << 16)) | (bytes[3] << 24);
        this.z = ((bytes[4] | (bytes[5] << 8)) | (bytes[6] << 16)) | (bytes[7] << 24);
        this.type = bytes[8] & 255;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof DBKey)) {
            return false;
        }
        DBKey key = (DBKey) obj;
        if (key.x == this.x && key.z == this.z && key.type == this.type) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((((this.x + 31) * 31) + this.z) * 31) + this.type;
    }

    public String toString() {
        return getClass().getSimpleName() + ": " + this.x + "_" + this.z + "_" + this.type;
    }
}
