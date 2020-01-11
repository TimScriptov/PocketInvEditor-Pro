package com.mcal.pocketinveditor.material;

public class MaterialCount {
    public int count;
    public MaterialKey key;

    public MaterialCount(MaterialKey key, int count) {
        this.key = key;
        this.count = count;
    }

    public String toString() {
        return "[" + key.typeId + ":" + key.damage + "]: " + count;
    }
}
