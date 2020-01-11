package com.mcal.pocketinveditor.geo;

public interface AreaChunkAccess extends AreaBlockAccess {
    Chunk getChunk(int x, int z);
}
