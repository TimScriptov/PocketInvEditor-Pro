package com.mcal.pocketinveditor;

import java.util.ArrayList;
import java.util.List;

public class ItemStack {
    private int amount;
    private short durability;
    private List<Object> extraTags;
    private short id;

    public ItemStack(short id, short durability, int amount) {
        extraTags = new ArrayList<>();
        this.id = id;
        this.durability = durability;
        this.amount = amount;
    }

    public ItemStack(ItemStack other) {
        this(other.getTypeId(), other.getDurability(), other.getAmount());
    }

    public short getTypeId() {
        return id;
    }

    public void setTypeId(short id) {
        this.id = id;
    }

    public short getDurability() {
        return durability;
    }

    public void setDurability(short durability) {
        this.durability = durability;
    }

    public int getAmount() {
        return amount;
    }

    public void setAmount(int amount) {
        this.amount = amount;
    }

    public String toString() {
        return "ItemStack: type=" + getTypeId() + ", durability=" + getDurability() + ", amount=" + getAmount();
    }

    public List<Object> getExtraTags() {
        return extraTags;
    }
}
