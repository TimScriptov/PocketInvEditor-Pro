package com.mcal.pocketinveditor.io.nbt.entity;

import java.util.List;
import com.mcal.pocketinveditor.entity.Arrow;
import com.mcal.pocketinveditor.entity.Projectile;
import org.spout.nbt.ByteTag;
import org.spout.nbt.Tag;

public class ArrowEntityStore<T extends Arrow> extends ProjectileEntityStore<T> {
    public void loadTag(T entity, Tag tag) {
        String name = tag.getName();
        if (name.equals("inData")) {
            entity.setInData(((ByteTag) tag).getValue().byteValue());
        } else if (name.equals("player")) {
            entity.setShotByPlayer(((ByteTag) tag).getValue().byteValue() != (byte) 0);
        } else {
            super.loadTag(entity, tag);
        }
    }

    public List<Tag> save(T entity) {
        List<Tag> tags = super.save(entity);
        tags.add(new ByteTag("inData", entity.getInData()));
        tags.add(new ByteTag("player", entity.isShotByPlayer() ? (byte) 1 : (byte) 0));
        return tags;
    }
}
