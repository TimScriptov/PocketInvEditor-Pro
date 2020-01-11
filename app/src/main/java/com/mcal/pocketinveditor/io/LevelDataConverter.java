package com.mcal.pocketinveditor.io;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import com.mcal.pocketinveditor.Level;
import com.mcal.pocketinveditor.io.leveldb.LevelDBConverter;
import com.mcal.pocketinveditor.io.nbt.NBTConverter;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

public final class LevelDataConverter {
    public static final byte[] header = new byte[]{(byte) 4, (byte) 0, (byte) 0, (byte) 0};

    public static Level read(File file) throws IOException {
        BufferedInputStream is = new BufferedInputStream(new FileInputStream(file));
        is.skip(8);
        Level level = NBTConverter.readLevel((CompoundTag) new NBTInputStream(is, false, true).readTag());
        is.close();
        File dbFile = new File(file.getParentFile(), "db");
        if (dbFile.exists()) {
            LevelDBConverter.readLevel(level, dbFile);
        }
        return level;
    }

    public static void write(Level level, File file) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        new NBTOutputStream(bos, false, true).writeTag(NBTConverter.writeLevel(level));
        DataOutputStream dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
        int length = bos.size();
        dos.write(header);
        dos.writeInt(Integer.reverseBytes(length));
        bos.writeTo(dos);
        dos.close();
        File dbFile = new File(file.getParentFile(), "db");
        if (dbFile.exists()) {
            LevelDBConverter.writeLevel(level, dbFile);
        }
    }

    public static void main(String[] args) throws Exception {
        Level level = read(new File(args[0]));
        System.out.println(level);
        write(level, new File(args[1]));
    }
}
