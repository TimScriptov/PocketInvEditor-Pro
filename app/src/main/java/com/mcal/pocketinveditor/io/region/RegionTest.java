package com.mcal.pocketinveditor.io.region;

import java.io.File;
import com.mcal.pocketinveditor.geo.Chunk;
import com.mcal.pocketinveditor.geo.ChunkManager;

public class RegionTest {
    public static void main(String[] args) {
        try {
            int x, z;
            ChunkManager mgr = new ChunkManager(new File(args[0]));
            for (x = 0; x < 16; x++) {
                for (z = 0; z < 16; z++) {
                    Chunk chunk = mgr.getChunk(x, z);
                    System.out.println("Chunk " + x + "," + z);
                    System.out.println("DIAMONDS: " + chunk.countDiamonds());
                    if (chunk.dirtyTableIsReallyGross()) {
                        System.out.println("Chunk " + x + "," + z + " has been modified.");
                    }
                }
            }
            for (x = 0; x < 16; x++) {
                for (z = 0; z < 16; z++) {
                    if (mgr.getChunk(x, z).dirtyTableIsReallyGross()) {
                        System.out.print("*");
                    } else {
                        System.out.print("_");
                    }
                }
                System.out.println("|");
            }
            for (x = 0; x < 16; x++) {
                for (int y = 0; y < ChunkManager.WORLD_HEIGHT; y++) {
                    for (z = 0; z < 16; z++) {
                        mgr.getChunk(2, 12).setBlockTypeId(x, y, z, 10);
                    }
                }
            }
            System.out.println("Saving chunks...");
            System.out.println(mgr.saveAll() + " chunks saved");
            mgr.unloadChunks(false);
            mgr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
