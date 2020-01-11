package com.mcal.pocketinveditor.io.nbt.entity;

import java.util.List;
import com.mcal.pocketinveditor.entity.LivingEntity;
import com.mcal.pocketinveditor.entity.PigZombie;
import org.spout.nbt.ShortTag;
import org.spout.nbt.Tag;

public class PigZombieEntityStore<T extends PigZombie> extends LivingEntityStore<T> {
    public void loadTag(T entity, Tag tag) {
        if (tag.getName().equals("Anger")) {
            entity.setAnger(((ShortTag) tag).getValue().shortValue());
        } else {
            super.loadTag(entity, tag);
        }
    }

    public List<Tag> save(T entity) {
        List<Tag> tags = super.save(entity);
        tags.add(new ShortTag("Anger", entity.getAnger()));
        return tags;
    }
}
