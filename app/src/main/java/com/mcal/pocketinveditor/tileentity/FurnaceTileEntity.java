package com.mcal.pocketinveditor.tileentity;

public class FurnaceTileEntity extends ContainerTileEntity {
    private short burnTime = (short) 0;
    private short cookTime = (short) 0;

    public short getBurnTime() {
        return burnTime;
    }

    public void setBurnTime(short burnTime) {
        this.burnTime = burnTime;
    }

    public short getCookTime() {
        return cookTime;
    }

    public void setCookTime(short cookTime) {
        this.cookTime = cookTime;
    }

    public int getContainerSize() {
        return 3;
    }
}
