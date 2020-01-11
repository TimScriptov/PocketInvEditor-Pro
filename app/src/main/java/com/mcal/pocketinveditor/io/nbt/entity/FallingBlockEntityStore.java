package com.mcal.pocketinveditor.io.nbt.entity;

import java.util.List;
import com.mcal.pocketinveditor.entity.FallingBlock;
import org.spout.nbt.ByteTag;
import org.spout.nbt.Tag;

public class FallingBlockEntityStore<T extends FallingBlock> extends EntityStore<T> {
    public void loadTag(T entity, Tag tag) {
        String name = tag.getName();
        if (name.equals("Tile")) {
            entity.setBlockId(((ByteTag) tag).getValue().byteValue() & 255);
        } else if (name.equals("Data")) {
            entity.setBlockData(((ByteTag) tag).getValue().byteValue());
        } else if (name.equals("Time")) {
            entity.setTime(((ByteTag) tag).getValue().byteValue() & 255);
        } else {
            super.loadTag(entity, tag);
        }
    }

    public List<Tag> save(T entity) {
        List<Tag> tags = super.save(entity);
        tags.add(new ByteTag("Data", entity.getBlockData()));
        tags.add(new ByteTag("Tile", (byte) entity.getBlockId()));
        tags.add(new ByteTag("Time", (byte) entity.getTime()));
        return tags;
    }
}
