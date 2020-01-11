package com.mcal.pocketinveditor.io.leveldb;

import com.litl.leveldb.DB;
import com.litl.leveldb.Iterator;
import com.litl.leveldb.WriteBatch;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import com.mcal.pocketinveditor.Level;
import com.mcal.pocketinveditor.entity.Entity;
import com.mcal.pocketinveditor.io.EntityDataConverter.EntityData;
import com.mcal.pocketinveditor.io.nbt.NBTConverter;
import com.mcal.pocketinveditor.tileentity.TileEntity;
import com.mcal.pocketinveditor.util.Vector3f;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

public class LevelDBConverter {
    private static final String LOCAL_PLAYER_KEY = "~local_player";

    private LevelDBConverter() {
    }

    public static DB openDatabase(File file) throws IOException {
        DB db;
        File lockFile = new File(file, "LOCK");
        int i = 0;
        while (i < 10) {
            try {
                db = new DB(file);
                db.open();
                return db;
            } catch (Exception ie) {
                ie.printStackTrace();
                try {
                    Thread.sleep(200);
                } catch (Exception e) {
                }
                i++;
            }
        }
        db = new DB(file);
        db.open();
        return db;
    }

    public static synchronized void readLevel(Level level, File file) throws IOException {
        synchronized (LevelDBConverter.class) {
            DB db = openDatabase(file);
            try {
                byte[] localPlayerBytes = db.get(bytes(LOCAL_PLAYER_KEY));
                if (localPlayerBytes != null) {
                    level.setPlayer(NBTConverter.readPlayer((CompoundTag) new NBTInputStream(new ByteArrayInputStream(localPlayerBytes), false, true).readTag()));
                }
            } finally {
                System.out.println("Closing db");
                db.close();
            }
        }
    }

    public static synchronized void writeLevel(Level level, File file) throws IOException {
        synchronized (LevelDBConverter.class) {
            DB db = openDatabase(file);
            try {
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                new NBTOutputStream(bos, false, true).writeTag(NBTConverter.writePlayer(level.getPlayer(), "", true));
                db.put(bytes(LOCAL_PLAYER_KEY), bos.toByteArray());
            } finally {
                System.out.println("Closing db");
                db.close();
            }
        }
    }

    public static synchronized EntityData readAllEntities(File file) throws IOException {
        EntityData entityData;
        synchronized (LevelDBConverter.class) {
            DB db = openDatabase(file);
            List<Entity> entityList = new ArrayList();
            List<TileEntity> tileEntityList = new ArrayList();
            Iterator iter = db.iterator();
            DBKey key = new DBKey();
            iter.seekToFirst();
            while (iter.isValid()) {
                key.fromBytes(iter.getKey());
                ByteArrayInputStream bis;
                NBTInputStream nis;
                if (key.getType() == 50) {
                    bis = new ByteArrayInputStream(iter.getValue());
                    nis = new NBTInputStream(bis, false, true);
                    while (bis.available() > 0) {
                        try {
                            Entity entity = NBTConverter.readSingleEntity((CompoundTag) nis.readTag());
                            if (entity == null) {
                                System.err.println("Not possible: null entity.");
                            } else {
                                entityList.add(entity);
                            }
                        } catch (EOFException eof) {
                            eof.printStackTrace();
                        } catch (Throwable th) {
                            System.out.println("Closing db");
                            db.close();
                        }
                    }
                    continue;
                } else if (key.getType() == 49) {
                    bis = new ByteArrayInputStream(iter.getValue());
                    nis = new NBTInputStream(bis, false, true);
                    while (bis.available() > 0) {
                        TileEntity entity2 = NBTConverter.readSingleTileEntity((CompoundTag) nis.readTag());
                        if (entity2 == null) {
                            System.err.println("Not possible: null tile entity.");
                        } else {
                            tileEntityList.add(entity2);
                        }
                    }
                }
                iter.next();
            }
            iter.close();
            System.out.println("Closing db");
            db.close();
            entityData = new EntityData(entityList, tileEntityList);
        }
        return entityData;
    }

    public static synchronized void writeAllEntities(List<Entity> entitiesList, File file) throws IOException {
        synchronized (LevelDBConverter.class) {
            DB db = openDatabase(file);
            try {
                System.out.println("starting to write entities");
                Map<DBKey, ByteArrayOutputStream> allEntitiesMap = new HashMap();
                DBKey temp = new DBKey();
                temp.setType(50);
                for (Entity e : entitiesList) {
                    CompoundTag tag = NBTConverter.writeEntity(e);
                    Vector3f pos = e.getLocation();
                    temp.setX(((int) pos.getX()) >> 4).setZ(((int) pos.getZ()) >> 4);
                    ByteArrayOutputStream bos = (ByteArrayOutputStream) allEntitiesMap.get(temp);
                    if (bos == null) {
                        bos = new ByteArrayOutputStream();
                        allEntitiesMap.put(new DBKey(temp), bos);
                    }
                    new NBTOutputStream(bos, false, true).writeTag(tag);
                }
                Iterator iter = db.iterator();
                WriteBatch batch = new WriteBatch();
                iter.seekToFirst();
                while (iter.isValid()) {
                    temp.fromBytes(iter.getKey());
                    if (temp.getType() == 50 && !allEntitiesMap.containsKey(temp)) {
                        batch.delete(ByteBuffer.wrap(temp.toBytes()));
                    }
                    iter.next();
                }
                iter.close();
                db.write(batch);
                batch.clear();
                for (Entry<DBKey, ByteArrayOutputStream> pear : allEntitiesMap.entrySet()) {
                    batch.put(ByteBuffer.wrap(((DBKey) pear.getKey()).toBytes()), ByteBuffer.wrap(((ByteArrayOutputStream) pear.getValue()).toByteArray()));
                }
                System.out.println("Writing the batch into the DB");
                db.write(batch);
                batch.close();
                System.out.println("Closing db");
                db.close();
            } catch (Throwable th) {
                System.out.println("Closing db");
                db.close();
            }
        }
    }

    public static synchronized void writeAllTileEntities(List<TileEntity> entitiesList, File file) throws IOException {
        synchronized (LevelDBConverter.class) {
            DB db = openDatabase(file);
            try {
                System.out.println("starting to write tile entities");
                Map<DBKey, ByteArrayOutputStream> allEntitiesMap = new HashMap();
                DBKey temp = new DBKey();
                temp.setType(49);
                for (TileEntity e : entitiesList) {
                    CompoundTag tag = NBTConverter.writeTileEntity(e);
                    temp.setX(e.getX() >> 4).setZ(e.getZ() >> 4);
                    ByteArrayOutputStream bos = (ByteArrayOutputStream) allEntitiesMap.get(temp);
                    if (bos == null) {
                        bos = new ByteArrayOutputStream();
                        allEntitiesMap.put(new DBKey(temp), bos);
                    }
                    new NBTOutputStream(bos, false, true).writeTag(tag);
                }
                Iterator iter = db.iterator();
                WriteBatch batch = new WriteBatch();
                iter.seekToFirst();
                while (iter.isValid()) {
                    temp.fromBytes(iter.getKey());
                    if (temp.getType() == 49 && !allEntitiesMap.containsKey(temp)) {
                        batch.delete(ByteBuffer.wrap(temp.toBytes()));
                    }
                    iter.next();
                }
                iter.close();
                db.write(batch);
                batch.clear();
                for (Entry<DBKey, ByteArrayOutputStream> pear : allEntitiesMap.entrySet()) {
                    batch.put(ByteBuffer.wrap(((DBKey) pear.getKey()).toBytes()), ByteBuffer.wrap(((ByteArrayOutputStream) pear.getValue()).toByteArray()));
                }
                System.out.println("Writing the batch into the DB");
                db.write(batch);
                batch.close();
            } finally {
                System.out.println("Closing db");
                db.close();
            }
        }
    }

    public static byte[] getChunkData(DB db, int x, int z) {
        return db.get(new DBKey(x, z, 48).toBytes());
    }

    public static void writeChunkData(DB db, int x, int z, byte[] data) {
        db.put(new DBKey(x, z, 48).toBytes(), data);
    }

    private static byte[] bytes(String str) {
        return str.getBytes(Charset.forName("utf-8"));
    }
}
