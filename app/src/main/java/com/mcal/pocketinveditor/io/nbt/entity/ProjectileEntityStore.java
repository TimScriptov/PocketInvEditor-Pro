package com.mcal.pocketinveditor.io.nbt.entity;

import java.util.List;
import com.mcal.pocketinveditor.entity.Projectile;
import org.spout.nbt.ByteTag;
import org.spout.nbt.ShortTag;
import org.spout.nbt.Tag;

public class ProjectileEntityStore<T extends Projectile> extends EntityStore<T> {
    public void loadTag(T entity, Tag tag) {
        String name = tag.getName();
        if (name.equals("inGround")) {
            boolean z;
            if (((ByteTag) tag).getValue().byteValue() != (byte) 0) {
                z = true;
            } else {
                z = false;
            }
            entity.setInGround(z);
        } else if (name.equals("inTile")) {
            entity.setInBlock(((ByteTag) tag).getValue().byteValue());
        } else if (name.equals("shake")) {
            entity.setShake(((ByteTag) tag).getValue().byteValue());
        } else if (name.equals("xTile")) {
            entity.setXTile(((ShortTag) tag).getValue().shortValue());
        } else if (name.equals("yTile")) {
            entity.setYTile(((ShortTag) tag).getValue().shortValue());
        } else if (name.equals("zTile")) {
            entity.setZTile(((ShortTag) tag).getValue().shortValue());
        } else {
            super.loadTag(entity, tag);
        }
    }

    public List<Tag> save(T entity) {
        List<Tag> tags = super.save(entity);
        tags.add(new ByteTag("inGround", entity.isInGround() ? (byte) 1 : (byte) 0));
        tags.add(new ByteTag("inTile", entity.getInBlock()));
        tags.add(new ByteTag("shake", entity.getShake()));
        tags.add(new ShortTag("xTile", entity.getXTile()));
        tags.add(new ShortTag("yTile", entity.getYTile()));
        tags.add(new ShortTag("zTile", entity.getZTile()));
        return tags;
    }
}
