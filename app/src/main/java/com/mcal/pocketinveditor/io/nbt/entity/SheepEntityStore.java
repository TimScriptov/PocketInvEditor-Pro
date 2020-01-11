package com.mcal.pocketinveditor.io.nbt.entity;

import java.util.List;
import com.mcal.pocketinveditor.entity.Animal;
import com.mcal.pocketinveditor.entity.Sheep;
import org.spout.nbt.ByteTag;
import org.spout.nbt.Tag;

public class SheepEntityStore<T extends Sheep> extends AnimalEntityStore<T> {
    public void loadTag(T entity, Tag tag) {
        if (tag.getName().equals("Color")) {
            entity.setColor(((ByteTag) tag).getValue().byteValue());
        } else if (tag.getName().equals("Sheared")) {
            entity.setSheared(((ByteTag) tag).getValue().byteValue() > (byte) 0);
        } else {
            super.loadTag(entity, tag);
        }
    }

    public List<Tag> save(T entity) {
        List<Tag> tags = super.save(entity);
        tags.add(new ByteTag("Color", entity.getColor()));
        tags.add(new ByteTag("Sheared", entity.isSheared() ? (byte) 1 : (byte) 0));
        return tags;
    }
}
