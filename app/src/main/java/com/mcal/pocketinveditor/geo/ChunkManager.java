package com.mcal.pocketinveditor.geo;

import com.litl.leveldb.DB;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import com.mcal.pocketinveditor.geo.Chunk.Key;
import com.mcal.pocketinveditor.io.leveldb.LevelDBConverter;

public class ChunkManager implements AreaChunkAccess {
    public static final int WORLD_HEIGHT = 128;
    public static final int WORLD_LENGTH = 256;
    public static final int WORLD_WIDTH = 256;
    public static CuboidRegion worldRegion = new CuboidRegion(0, 0, 0, WORLD_WIDTH, WORLD_HEIGHT, WORLD_WIDTH);
    protected Map<Key, Chunk> chunks = new HashMap<>();
    private DB db;
    protected File dbFile;
    private Chunk lastChunk = null;
    private Key lastKey = null;

    public ChunkManager(File dbFile) {
        this.dbFile = dbFile;
        openDatabase();
    }

    public void openDatabase() {
        if (this.db == null) {
            try {
                this.db = LevelDBConverter.openDatabase(this.dbFile);
            } catch (IOException ie) {
                throw new RuntimeException(ie);
            }
        }
    }

    public void closeDatabase() {
        this.db.close();
        this.db = null;
    }

    public Chunk getChunk(int x, int z) {
        if (this.lastKey != null && this.lastKey.getX() == x && this.lastKey.getZ() == z) {
            return this.lastChunk;
        }
        Key key;
        if (this.lastKey == null) {
            key = new Key(x, z);
            this.lastKey = key;
        } else {
            key = this.lastKey;
            key.setX(x);
            key.setZ(z);
        }
        Chunk chunk = (Chunk) this.chunks.get(key);
        if (chunk == null) {
            chunk = loadChunk(key);
        }
        this.lastChunk = chunk;
        return chunk;
    }

    public Chunk loadChunk(Key key) {
        Chunk chunk = new Chunk(key.getX(), key.getZ());
        byte[] data = LevelDBConverter.getChunkData(this.db, key.getX(), key.getZ());
        if (data != null) {
            chunk.loadFromByteArray(data);
        } else {
            System.err.println("No chunk at:" + key.getX() + ":" + key.getZ());
        }
        this.chunks.put(new Key(key), chunk);
        return chunk;
    }

    public int getBlockTypeId(int x, int y, int z) {
        if (y >= WORLD_HEIGHT || y < 0) {
            return 0;
        }
        return getChunk(x >> 4, z >> 4).getBlockTypeId(x & 15, y, z & 15);
    }

    public int getBlockData(int x, int y, int z) {
        if (y >= WORLD_HEIGHT || y < 0) {
            return 0;
        }
        return getChunk(x >> 4, z >> 4).getBlockData(x & 15, y, z & 15);
    }

    public void setBlockTypeId(int x, int y, int z, int type) {
        if (y < WORLD_HEIGHT && y >= 0) {
            getChunk(x >> 4, z >> 4).setBlockTypeId(x & 15, y, z & 15, type);
        }
    }

    public void setBlockData(int x, int y, int z, int data) {
        if (y < WORLD_HEIGHT && y >= 0) {
            getChunk(x >> 4, z >> 4).setBlockData(x & 15, y, z & 15, data);
        }
    }

    public int getHighestBlockYAt(int x, int z) {
        return getChunk(x >> 4, z >> 4).getHighestBlockYAt(x & 15, z & 15);
    }

    public int saveAll() {
        int savedCount = 0;
        for (Entry<Key, Chunk> entry : this.chunks.entrySet()) {
            Key key = (Key) entry.getKey();
            Chunk value = (Chunk) entry.getValue();
            if (key.getX() != value.x || key.getZ() != value.z) {
                throw new AssertionError("WTF: key x = " + key.getX() + " z = " + key.getZ() + " chunk x=" + value.x + " chunk z=" + value.z);
            } else if (value.needsSaving) {
                saveChunk(value);
                savedCount++;
            }
        }
        return savedCount;
    }

    protected void saveChunk(Chunk chunk) {
        LevelDBConverter.writeChunkData(this.db, chunk.x, chunk.z, chunk.saveToByteArray());
    }

    public void unloadChunks(boolean saveFirst) {
        if (saveFirst) {
            saveAll();
        }
        this.chunks.clear();
    }

    public void close() throws IOException {
        closeDatabase();
    }
}
