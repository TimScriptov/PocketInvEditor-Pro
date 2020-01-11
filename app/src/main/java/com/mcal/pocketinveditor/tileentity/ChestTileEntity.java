package com.mcal.pocketinveditor.tileentity;

public class ChestTileEntity extends ContainerTileEntity {
    private int pairx = -65535;
    private int pairz = -65535;

    public void setPairX(int pairx) {
        this.pairx = pairx;
    }

    public int getPairX() {
        return pairx;
    }

    public void setPairZ(int pairz) {
        this.pairz = pairz;
    }

    public int getPairZ() {
        return pairz;
    }
}
