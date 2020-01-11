package com.mcal.pocketinveditor.io.nbt.entity;

import java.util.List;
import com.mcal.pocketinveditor.entity.Item;
import com.mcal.pocketinveditor.io.nbt.NBTConverter;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.ShortTag;
import org.spout.nbt.Tag;

public class ItemEntityStore<T extends Item> extends EntityStore<T> {
    public void loadTag(T entity, Tag tag) {
        if (tag.getName().equals("Age")) {
            entity.setAge(((ShortTag) tag).getValue().shortValue());
        } else if (tag.getName().equals("Health")) {
            entity.setHealth(((ShortTag) tag).getValue().shortValue());
        } else if (tag.getName().equals("Item")) {
            entity.setItemStack(NBTConverter.readItemStack((CompoundTag) tag));
        } else {
            super.loadTag(entity, tag);
        }
    }

    public List<Tag> save(T entity) {
        List<Tag> tags = super.save(entity);
        tags.add(new ShortTag("Age", entity.getAge()));
        tags.add(new ShortTag("Health", entity.getHealth()));
        tags.add(NBTConverter.writeItemStack(entity.getItemStack(), "Item"));
        return tags;
    }
}
