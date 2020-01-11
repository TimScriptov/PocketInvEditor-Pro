package com.mcal.pocketinveditor.tileentity;

import java.util.Arrays;

public class SignTileEntity extends TileEntity {
    public static final int NUM_LINES = 4;
    private String[] lines;

    public SignTileEntity() {
        String[] strArr = new String[NUM_LINES];
        strArr[0] = "";
        strArr[1] = "";
        strArr[2] = "";
        strArr[3] = "";
        this.lines = strArr;
    }

    public String getLine(int index) {
        return lines[index];
    }

    public void setLine(int index, String newLine) {
        this.lines[index] = newLine;
    }

    public String[] getLines() {
        return lines;
    }

    public String toString() {
        return super.toString() + Arrays.asList(lines).toString();
    }
}
