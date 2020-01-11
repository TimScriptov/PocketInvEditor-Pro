package com.mcal.pocketinveditor.material;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Material {
    public static Map<MaterialKey, Material> materialMap = new HashMap();
    public static List<Material> materials;
    private short damage;
    private boolean damageable;
    private boolean hasSubtypes;
    private int id;
    private String name;

    public Material(int id, String name) {
        this(id, name, (short) 0, false);
    }

    public Material(int id, String name, short damage) {
        this(id, name, damage, true);
    }

    public Material(int id, String name, short damage, boolean hasSubtypes) {
        this.damageable = false;
        this.id = id;
        this.name = name;
        this.damage = damage;
        this.hasSubtypes = hasSubtypes;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public short getDamage() {
        return damage;
    }

    public boolean hasSubtypes() {
        return hasSubtypes;
    }

    public void setDamageable(boolean damageable) {
        this.damageable = damageable;
    }

    public boolean isDamageable() {
        return damageable;
    }

    public String toString() {
        return getName() + " : " + getId() + (damage != (short) 0 ? ":" + damage : "");
    }
}
