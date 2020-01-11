package com.mcal.pocketinveditor.entity;

import com.mcal.pocketinveditor.ItemStack;

public class Item extends Entity {
    private short age = (short) 0;
    private short health = (short) 5;
    private ItemStack stack;

    public ItemStack getItemStack() {
        if (stack == null) {
            stack = new ItemStack((short) 0, (short) 0, 0);
        }
        return stack;
    }

    public void setItemStack(ItemStack stack) {
        this.stack = stack;
    }

    public short getAge() {
        return age;
    }

    public void setAge(short age) {
        this.age = age;
    }

    public short getHealth() {
        return health;
    }

    public void setHealth(short health) {
        this.health = health;
    }
}
