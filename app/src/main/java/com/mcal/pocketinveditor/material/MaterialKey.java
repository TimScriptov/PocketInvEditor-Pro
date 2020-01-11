package com.mcal.pocketinveditor.material;

public final class MaterialKey {
    public short damage;
    public short typeId;

    public MaterialKey(short typeId, short damage) {
        this.typeId = typeId;
        this.damage = damage;
    }

    public MaterialKey(MaterialKey other) {
        this(other.typeId, other.damage);
    }

    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof MaterialKey)) {
            return false;
        }
        MaterialKey another = (MaterialKey) other;
        if (another.typeId == typeId && another.damage == damage) {
            return true;
        }
        return false;
    }

    public int hashCode() {
        return ((typeId + 31) * 31) + damage;
    }

    public String toString() {
        return "MaterialKey[typeId=" + typeId + ";damage=" + damage + "]";
    }

    public static MaterialKey parse(String str, int radix) {
        String[] parts = str.split(":");
        if (parts.length == 0) {
            throw new IllegalArgumentException("Why is the string blank?!");
        } else if (parts.length == 1) {
            return new MaterialKey(Short.parseShort(parts[0], radix), (short) -1);
        } else {
            return new MaterialKey(Short.parseShort(parts[0], radix), Short.parseShort(parts[1], radix));
        }
    }
}
