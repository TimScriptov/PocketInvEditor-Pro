package com.mcal.pocketinveditor.io.nbt.entity;

import java.util.ArrayList;
import java.util.List;
import com.mcal.pocketinveditor.entity.Entity;
import com.mcal.pocketinveditor.io.nbt.NBTConverter;
import org.spout.nbt.ByteTag;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.FloatTag;
import org.spout.nbt.IntTag;
import org.spout.nbt.ListTag;
import org.spout.nbt.ShortTag;
import org.spout.nbt.Tag;

public class EntityStore<T extends Entity> {
    public void load(T entity, CompoundTag tag) {
        for (Tag t : tag.getValue()) {
            loadTag(entity, t);
        }
    }

    public void loadTag(T entity, Tag tag) {
        boolean z = false;
        String name = tag.getName();
        if (name.equals("Pos")) {
            entity.setLocation(NBTConverter.readVector((ListTag) tag));
        } else if (name.equals("Motion")) {
            entity.setVelocity(NBTConverter.readVector((ListTag) tag));
        } else if (name.equals("Air")) {
            entity.setAirTicks(((ShortTag) tag).getValue().shortValue());
        } else if (name.equals("Fire")) {
            entity.setFireTicks(((ShortTag) tag).getValue().shortValue());
        } else if (name.equals("FallDistance")) {
            entity.setFallDistance(((FloatTag) tag).getValue().floatValue());
        } else if (name.equals("Riding")) {
            entity.setRiding(NBTConverter.readSingleEntity((CompoundTag) tag));
        } else if (name.equals("Rotation")) {
            List<FloatTag> rotationTags = ((ListTag) tag).getValue();
            entity.setYaw(rotationTags.get(0).getValue().floatValue());
            entity.setPitch(rotationTags.get(1).getValue().floatValue());
        } else if (name.equals("OnGround")) {
            if (((ByteTag) tag).getValue().byteValue() > (byte) 0) {
                z = true;
            }
            entity.setOnGround(z);
        } else if (!name.equals("id")) {
            System.err.println("Unknown tag " + name + " for entity " + entity.getClass().getSimpleName() + " : " + tag);
            entity.getExtraTags().add(tag);
        }
    }

    public List<Tag> save(T entity) {
        List<Tag> tags = new ArrayList<>();
        tags.add(new ShortTag("Air", entity.getAirTicks()));
        tags.add(new FloatTag("FallDistance", entity.getFallDistance()));
        tags.add(new ShortTag("Fire", entity.getFireTicks()));
        tags.add(NBTConverter.writeVector(entity.getVelocity(), "Motion"));
        tags.add(NBTConverter.writeVector(entity.getLocation(), "Pos"));
        if (entity.getRiding() != null) {
            tags.add(NBTConverter.writeEntity(entity.getRiding(), "Riding"));
        }
        List<FloatTag> rotationTags = new ArrayList<>(2);
        rotationTags.add(new FloatTag("", entity.getYaw()));
        rotationTags.add(new FloatTag("", entity.getPitch()));
        tags.add(new ListTag("Rotation", FloatTag.class, rotationTags));
        tags.add(new ByteTag("OnGround", entity.isOnGround() ? (byte) 1 : (byte) 0));
        tags.add(new IntTag("id", entity.getEntityTypeId()));
        
        return tags;
    }
}
