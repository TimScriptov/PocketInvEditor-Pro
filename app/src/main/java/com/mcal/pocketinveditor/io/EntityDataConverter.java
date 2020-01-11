package com.mcal.pocketinveditor.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import com.mcal.pocketinveditor.entity.Entity;
import com.mcal.pocketinveditor.io.nbt.NBTConverter;
import com.mcal.pocketinveditor.tileentity.TileEntity;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

public final class EntityDataConverter {
    public static final byte[] header = new byte[]{(byte) 69, (byte) 78, (byte) 84, (byte) 0, (byte) 1, (byte) 0, (byte) 0, (byte) 0};

    public static class EntityData {
        public List<Entity> entities;
        public List<TileEntity> tileEntities;

        public EntityData(List<Entity> entities, List<TileEntity> tileEntities) {
            this.entities = entities;
            this.tileEntities = tileEntities;
        }
    }

    public static EntityData read(File file) throws IOException {
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
        is.skip(12);
        EntityData eDat = NBTConverter.readEntities((CompoundTag) new NBTInputStream(is, false, true).readTag());
        is.close();
        return eDat;
    }

    public static void write(List<Entity> entitiesList, List<TileEntity> tileEntitiesList, File file) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        new NBTOutputStream(bos, false, true).writeTag(NBTConverter.writeEntities(entitiesList, tileEntitiesList));
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        int length = bos.size();
        dos.write(header);
        dos.writeInt(Integer.reverseBytes(length));
        bos.writeTo(dos);
        dos.close();
    }

    public static void main(String[] args) throws Exception {
        EntityData entities = read(new File(args[0]));
        System.out.println(entities);
        write(entities.entities, entities.tileEntities, new File(args[1]));
    }
}
