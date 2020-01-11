package com.mcal.pocketinveditor.io.region;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.zip.DeflaterOutputStream;

public class RegionFile {
    public static final String ANVIL_EXTENSION = ".mca";
    static final int CHUNK_HEADER_SIZE = 5;
    public static final String MCREGION_EXTENSION = ".mcr";
    private static final int SECTOR_BYTES = 4096;
    private static final int SECTOR_INTS = 1024;
    private static final int VERSION_DEFLATE = 2;
    private static final int VERSION_GZIP = 1;
    private static final byte[] emptySector = new byte[SECTOR_BYTES];
    private final int[] chunkTimestamps = new int[SECTOR_INTS];
    private RandomAccessFile file;
    private final File fileName;
    private long lastModified = 0;
    private final int[] offsets = new int[SECTOR_INTS];
    private ArrayList<Boolean> sectorFree;
    private int sizeDelta;

    class ChunkBuffer extends ByteArrayOutputStream {
        private int x;
        private int z;

        public ChunkBuffer(int x, int z) {
            super(8096);
            this.x = x;
            this.z = z;
        }

        public void close() {
            RegionFile.this.write(x, z, buf, count);
        }
    }

    public RegionFile(File path) {
        fileName = path;
        debugln("REGION LOAD " + fileName);
        sizeDelta = 0;
        try {
            int i;
            if (path.exists()) {
                lastModified = path.lastModified();
            }
            file = new RandomAccessFile(path, "rw");
            if (this.file.length() < 4096) {
                for (i = 0; i < SECTOR_INTS; i += VERSION_GZIP) {
                    file.writeInt(0);
                }
                for (i = 0; i < SECTOR_INTS; i += VERSION_GZIP) {
                    file.writeInt(0);
                }
                sizeDelta += 8192;
            }
            if ((this.file.length() & 4095) != 0) {
                for (i = 0; ((long) i) < (file.length() & 4095); i += VERSION_GZIP) {
                    this.file.write(0);
                }
            }
            int nSectors = ((int) file.length()) / SECTOR_BYTES;
            sectorFree = new ArrayList<>(nSectors);
            for (i = 0; i < nSectors; i += VERSION_GZIP) {
                sectorFree.add(Boolean.valueOf(true));
            }
            sectorFree.set(0, Boolean.valueOf(false));
            this.file.seek(0);
            for (i = 0; i < SECTOR_INTS; i += VERSION_GZIP) {
                int offset = Integer.reverseBytes(file.readInt());
                this.offsets[i] = offset;
                if (offset != 0 && (offset >> 8) + (offset & 255) <= sectorFree.size()) {
                    for (int sectorNum = 0; sectorNum < (offset & 255); sectorNum += VERSION_GZIP) {
                        sectorFree.set((offset >> 8) + sectorNum, Boolean.valueOf(false));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long lastModified() {
        return lastModified;
    }

    public synchronized int getSizeDelta() {
        int ret;
        ret = sizeDelta;
        sizeDelta = 0;
        return ret;
    }

    private void debug(String in) {
    }

    private void debugln(String in) {
        debug(in + "\n");
    }

    private void debug(String mode, int x, int z, String in) {
        debug("REGION " + mode + " " + fileName.getName() + "[" + x + "," + z + "] = " + in);
    }

    private void debug(String mode, int x, int z, int count, String in) {
        debug("REGION " + mode + " " + fileName.getName() + "[" + x + "," + z + "] " + count + "B = " + in);
    }

    private void debugln(String mode, int x, int z, String in) {
        debug(mode, x, z, in + "\n");
    }

    public byte[] getChunkData(int x, int z) {
        if (outOfBounds(x, z)) {
            debugln("READ", x, z, "out of bounds");
            return null;
        }
        try {
            int offset = getOffset(x, z);
            if (offset == 0) {
                return null;
            }
            int sectorNumber = offset >> 8;
            int numSectors = offset & 255;
            if (sectorNumber + numSectors > sectorFree.size()) {
                debugln("READ", x, z, "invalid sector");
                return null;
            }
            this.file.seek((long) (sectorNumber * SECTOR_BYTES));
            debugln("READ", x, z, "location = " + Integer.toString(sectorNumber * SECTOR_BYTES, 16));
            int length = Integer.reverseBytes(file.readInt());
            if (length > numSectors * SECTOR_BYTES) {
                debugln("READ", x, z, "invalid length: " + length + " > 4096 * " + numSectors);
                return null;
            }
            byte[] data = new byte[(length - 1)];
            file.read(data);
            return data;
        } catch (IOException e) {
            debugln("READ", x, z, "exception");
            return null;
        }
    }

    public DataOutputStream getChunkDataOutputStream(int x, int z) {
        if (outOfBounds(x, z)) {
            return null;
        }
        return new DataOutputStream(new DeflaterOutputStream(new ChunkBuffer(x, z)));
    }

    public void write(int x, int z, byte[] data, int length) {
        try {
            int offset = getOffset(x, z);
            int sectorNumber = offset >> 8;
            int sectorsAllocated = offset & 255;
            int sectorsNeeded = ((length + CHUNK_HEADER_SIZE) / SECTOR_BYTES) + VERSION_GZIP;
            if (sectorsNeeded < 256) {
                if (sectorNumber == 0 || sectorsAllocated != sectorsNeeded) {
                    int i;
                    for (i = 0; i < sectorsAllocated; i += VERSION_GZIP) {
                        sectorFree.set(sectorNumber + i, Boolean.valueOf(true));
                    }
                    int runStart = sectorFree.indexOf(Boolean.valueOf(true));
                    int runLength = 0;
                    if (runStart != -1) {
                        for (i = runStart; i < sectorFree.size(); i += VERSION_GZIP) {
                            if (runLength != 0) {
                                if (sectorFree.get(i).booleanValue()) {
                                    runLength += VERSION_GZIP;
                                } else {
                                    runLength = 0;
                                }
                            } else if (sectorFree.get(i).booleanValue()) {
                                runStart = i;
                                runLength = VERSION_GZIP;
                            }
                            if (runLength >= sectorsNeeded) {
                                break;
                            }
                        }
                    }
                    if (runLength >= sectorsNeeded) {
                        debug("SAVE", x, z, length, "reuse");
                        sectorNumber = runStart;
                        setOffset(x, z, (sectorNumber << 8) | sectorsNeeded);
                        for (i = 0; i < sectorsNeeded; i += VERSION_GZIP) {
                            sectorFree.set(sectorNumber + i, Boolean.valueOf(false));
                        }
                        write(sectorNumber, data, length);
                        return;
                    }
                    debug("SAVE", x, z, length, "grow");
                    file.seek(file.length());
                    sectorNumber = sectorFree.size();
                    for (i = 0; i < sectorsNeeded; i += VERSION_GZIP) {
                        file.write(emptySector);
                        sectorFree.add(Boolean.valueOf(false));
                    }
                    sizeDelta += sectorsNeeded * SECTOR_BYTES;
                    write(sectorNumber, data, length);
                    setOffset(x, z, (sectorNumber << 8) | sectorsNeeded);
                    return;
                }
                debug("SAVE", x, z, length, "rewrite");
                write(sectorNumber, data, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void write(int sectorNumber, byte[] data, int length) throws IOException {
        debugln(" " + sectorNumber);
        file.seek((long) (sectorNumber * SECTOR_BYTES));
        file.writeInt(Integer.reverseBytes(length + VERSION_GZIP));
        file.write(data, 0, length);
    }

    private boolean outOfBounds(int x, int z) {
        return x < 0 || x >= 32 || z < 0 || z >= 32;
    }

    private int getOffset(int x, int z) {
        return offsets[(z * 32) + x];
    }

    public boolean hasChunk(int x, int z) {
        return getOffset(x, z) != 0;
    }

    private void setOffset(int x, int z, int offset) throws IOException {
        offsets[(z * 32) + x] = offset;
        file.seek((long) (((z * 32) + x) * 4));
        file.writeInt(Integer.reverseBytes(offset));
    }

    public void close() throws IOException {
        file.close();
    }
}
