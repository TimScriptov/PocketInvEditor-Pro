package com.mcal.pocketinveditor.io.nbt.entity;

import java.util.List;
import com.mcal.pocketinveditor.entity.LivingEntity;
import com.mcal.pocketinveditor.entity.Monster;
import org.spout.nbt.ByteTag;
import org.spout.nbt.ShortTag;
import org.spout.nbt.Tag;

public class LivingEntityStore<T extends LivingEntity> extends EntityStore<T> {
    public void loadTag(T entity, Tag tag) {
        boolean z = true;
        if (tag.getName().equals("AttackTime")) {
            entity.setAttackTime(((ShortTag) tag).getValue().shortValue());
        } else if (tag.getName().equals("DeathTime")) {
            entity.setDeathTime(((ShortTag) tag).getValue().shortValue());
        } else if (tag.getName().equals("Health")) {
            entity.setHealth(((ShortTag) tag).getValue().shortValue());
        } else if (tag.getName().equals("HurtTime")) {
            entity.setHurtTime(((ShortTag) tag).getValue().shortValue());
        } else if (tag.getName().equals("SpawnedByNight")) {
            if (entity instanceof Monster) {
                Monster monster = (Monster) entity;
                if (((ByteTag) tag).getValue().byteValue() == (byte) 0) {
                    z = false;
                }
                monster.setSpawnedAtNight(z);
            }
        } else if (tag.getName().equals("Persistent")) {
            if (((ByteTag) tag).getValue().byteValue() == (byte) 0) {
                z = false;
            }
            entity.setPersistent(z);
        } else {
            super.loadTag(entity, tag);
        }
    }

    public List<Tag> save(T entity) {
        byte b = (byte) 1;
        List<Tag> tags = super.save(entity);
        tags.add(new ShortTag("AttackTime", entity.getAttackTime()));
        tags.add(new ShortTag("DeathTime", entity.getDeathTime()));
        tags.add(new ShortTag("Health", entity.getHealth()));
        tags.add(new ShortTag("HurtTime", entity.getHurtTime()));
        if (entity instanceof Monster) {
            byte b2;
            String str = "SpawnedByNight";
            if (((Monster) entity).isSpawnedAtNight()) {
                b2 = (byte) 1;
            } else {
                b2 = (byte) 0;
            }
            tags.add(new ByteTag(str, b2));
        }
        String str2 = "Persistent";
        if (!entity.isPersistent()) {
            b = (byte) 0;
        }
        tags.add(new ByteTag(str2, b));
        return tags;
    }
}
