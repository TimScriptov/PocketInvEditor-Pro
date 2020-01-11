package com.mcal.pocketinveditor.io.nbt.schematic;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import com.mcal.pocketinveditor.geo.CuboidClipboard;
import com.mcal.pocketinveditor.util.Vector3f;
import org.spout.nbt.ByteArrayTag;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.ListTag;
import org.spout.nbt.ShortTag;
import org.spout.nbt.StringTag;
import org.spout.nbt.Tag;
import org.spout.nbt.stream.NBTInputStream;
import org.spout.nbt.stream.NBTOutputStream;

public class SchematicIO {
    public static CuboidClipboard read(File file) throws IOException {
        NBTInputStream stream = new NBTInputStream(new FileInputStream(file));
        CompoundTag mainTag = (CompoundTag) stream.readTag();
        stream.close();
        int width = 0;
        int height = 0;
        int length = 0;
        byte[] blocks = null;
        byte[] data = null;
        for (Tag tag : mainTag.getValue()) {
            String tagName = tag.getName();
            if (tagName.equals("Width")) {
                width = ((ShortTag) tag).getValue().shortValue();
            } else if (tagName.equals("Height")) {
                height = ((ShortTag) tag).getValue().shortValue();
            } else if (tagName.equals("Length")) {
                length = ((ShortTag) tag).getValue().shortValue();
            } else if (tagName.equals("Materials")) {
                String materials = ((StringTag) tag).getValue();
            } else if (tagName.equals("Blocks")) {
                blocks = ((ByteArrayTag) tag).getValue();
            } else if (tagName.equals("Data")) {
                data = ((ByteArrayTag) tag).getValue();
            } else if (!(tagName.equals("Entities") || tagName.equals("TileEntities"))) {
                System.err.println("WTF: invalid tag name: " + tagName);
            }
        }
        return new CuboidClipboard(new Vector3f((float) width, (float) height, (float) length), blocks, data);
    }

    public static void save(CuboidClipboard clipboard, File file) throws IOException {
        List<Tag> tags = new ArrayList<>();
        tags.add(new ShortTag("Width", (short) clipboard.getWidth()));
        tags.add(new ShortTag("Height", (short) clipboard.getHeight()));
        tags.add(new ShortTag("Length", (short) clipboard.getLength()));
        tags.add(new StringTag("Materials", "Alpha"));
        tags.add(new ByteArrayTag("Blocks", clipboard.blocks));
        tags.add(new ByteArrayTag("Data", clipboard.metaData));
        tags.add(new ListTag("Entities", CompoundTag.class, Collections.EMPTY_LIST));
        tags.add(new ListTag("TileEntities", CompoundTag.class, Collections.EMPTY_LIST));
        CompoundTag mainTag = new CompoundTag("Schematic", tags);
        NBTOutputStream stream = new NBTOutputStream(new FileOutputStream(file));
        stream.writeTag(mainTag);
        stream.close();
    }
}
