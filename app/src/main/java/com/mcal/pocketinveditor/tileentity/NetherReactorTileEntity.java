package com.mcal.pocketinveditor.tileentity;

public class NetherReactorTileEntity extends TileEntity {
    private boolean hasFinished = false;
    private boolean isInitialized = false;
    private short progress = (short) 0;

    public boolean isInitialized() {
        return isInitialized;
    }

    public void setInitialized(boolean initialized) {
        this.isInitialized = initialized;
    }

    public short getProgress() {
        return progress;
    }

    public void setProgress(short progress) {
        this.progress = progress;
    }

    public boolean hasFinished() {
        return hasFinished;
    }

    public void setFinished(boolean finished) {
        this.hasFinished = finished;
    }
}
