package com.mcal.pocketinveditor.io.nbt.entity;

import java.util.List;
import com.mcal.pocketinveditor.entity.Painting;
import com.mcal.pocketinveditor.util.Vector3f;
import org.spout.nbt.ByteTag;
import org.spout.nbt.IntTag;
import org.spout.nbt.StringTag;
import org.spout.nbt.Tag;

public class PaintingEntityStore<T extends Painting> extends EntityStore<T> {
    public void loadTag(T entity, Tag tag) {
        String name = tag.getName();
        if (name.equals("Dir")) {
            entity.setDirection(((ByteTag) tag).getValue().byteValue());
        } else if (name.equals("Motive")) {
            entity.setArt(((StringTag) tag).getValue());
        } else if (name.equals("TileX")) {
            entity.getBlockCoordinates().setX((float) ((IntTag) tag).getValue().intValue());
        } else if (name.equals("TileY")) {
            entity.getBlockCoordinates().setY((float) ((IntTag) tag).getValue().intValue());
        } else if (name.equals("TileZ")) {
            entity.getBlockCoordinates().setZ((float) ((IntTag) tag).getValue().intValue());
        } else {
            super.loadTag(entity, tag);
        }
    }

    public List<Tag> save(T entity) {
        List<Tag> tags = super.save(entity);
        tags.add(new ByteTag("Dir", entity.getDirection()));
        tags.add(new StringTag("Motive", entity.getArt()));
        Vector3f bc = entity.getBlockCoordinates();
        tags.add(new IntTag("TileX", bc.getBlockX()));
        tags.add(new IntTag("TileY", bc.getBlockY()));
        tags.add(new IntTag("TileZ", bc.getBlockZ()));
        return tags;
    }
}
