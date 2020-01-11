package com.mcal.pocketinveditor.io.nbt.entity;

import java.util.List;
import com.mcal.pocketinveditor.entity.Enderman;
import com.mcal.pocketinveditor.entity.LivingEntity;
import org.spout.nbt.ShortTag;
import org.spout.nbt.Tag;

public class EndermanEntityStore<T extends Enderman> extends LivingEntityStore<T> {
    public void loadTag(T entity, Tag tag) {
        if (tag.getName().equals("carried")) {
            entity.setCarried(((ShortTag) tag).getValue().shortValue());
        } else if (tag.getName().equals("carriedData")) {
            entity.setCarriedData(((ShortTag) tag).getValue().shortValue());
        } else {
            super.loadTag(entity, tag);
        }
    }

    public List<Tag> save(T entity) {
        List<Tag> tags = super.save(entity);
        tags.add(new ShortTag("carried", entity.getCarried()));
        tags.add(new ShortTag("carriedData", entity.getCarriedData()));
        return tags;
    }
}
