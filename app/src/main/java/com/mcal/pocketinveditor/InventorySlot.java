package com.mcal.pocketinveditor;

public class InventorySlot {
    private ItemStack contents;
    private byte slot;

    public InventorySlot(byte slot, ItemStack contents) {
        this.slot = slot;
        this.contents = contents;
    }

    public byte getSlot() {
        return slot;
    }

    public void setSlot(byte slot) {
        this.slot = slot;
    }

    public ItemStack getContents() {
        return contents;
    }

    public void setContents(ItemStack contents) {
        this.contents = contents;
    }

    public String toString() {
        return "Type: " + contents.getTypeId() + "; Damage: " + contents.getDurability();
    }
}
