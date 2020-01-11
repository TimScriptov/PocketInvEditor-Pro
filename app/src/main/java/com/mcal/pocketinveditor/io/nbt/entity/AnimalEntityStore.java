package com.mcal.pocketinveditor.io.nbt.entity;

import java.util.List;
import com.mcal.pocketinveditor.entity.Animal;
import com.mcal.pocketinveditor.entity.LivingEntity;
import org.spout.nbt.IntTag;
import org.spout.nbt.Tag;

public class AnimalEntityStore<T extends Animal> extends LivingEntityStore<T> {
    public void loadTag(T entity, Tag tag) {
        if (tag.getName().equals("Age")) {
            entity.setAge(((IntTag) tag).getValue().intValue());
        } else if (tag.getName().equals("InLove")) {
            entity.setInLove(((IntTag) tag).getValue().intValue());
        } else {
            super.loadTag(entity, tag);
        }
    }

    public List<Tag> save(T entity) {
        List<Tag> tags = super.save(entity);
        tags.add(new IntTag("Age", entity.getAge()));
        tags.add(new IntTag("InLove", entity.getInLove()));
        return tags;
    }
}
