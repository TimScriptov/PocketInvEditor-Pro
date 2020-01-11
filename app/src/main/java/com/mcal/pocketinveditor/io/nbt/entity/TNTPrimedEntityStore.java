package com.mcal.pocketinveditor.io.nbt.entity;

import java.util.List;
import com.mcal.pocketinveditor.entity.TNTPrimed;
import org.spout.nbt.ByteTag;
import org.spout.nbt.Tag;

public class TNTPrimedEntityStore<T extends TNTPrimed> extends EntityStore<T> {
    public void loadTag(T entity, Tag tag) {
        if (tag.getName().equals("Fuse")) {
            entity.setFuseTicks(((ByteTag) tag).getValue().byteValue());
        } else {
            super.loadTag(entity, tag);
        }
    }

    public List<Tag> save(T entity) {
        List<Tag> tags = super.save(entity);
        tags.add(new ByteTag("Fuse", entity.getFuseTicks()));
        return tags;
    }
}
