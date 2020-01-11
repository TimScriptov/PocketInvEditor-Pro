package com.mcal.pocketinveditor.io.nbt.tileentity;

import java.util.List;
import com.mcal.pocketinveditor.tileentity.MobSpawnerTileEntity;
import org.spout.nbt.IntTag;
import org.spout.nbt.ShortTag;
import org.spout.nbt.Tag;

public class MobSpawnerTileEntityStore<T extends MobSpawnerTileEntity> extends TileEntityStore<T> {
    public void loadTag(T entity, Tag tag) {
        String name = tag.getName();
        if (name.equals("Delay")) {
            entity.setDelay(((ShortTag) tag).getValue().shortValue());
        } else if (name.equals("EntityId")) {
            entity.setEntityId(((IntTag) tag).getValue().intValue());
        } else if (name.equals("MaxNearbyEntities")) {
            entity.setMaxNearbyEntities(((ShortTag) tag).getValue().shortValue());
        } else if (name.equals("MaxSpawnDelay")) {
            entity.setMaxSpawnDelay(((ShortTag) tag).getValue().shortValue());
        } else if (name.equals("MinSpawnDelay")) {
            entity.setMinSpawnDelay(((ShortTag) tag).getValue().shortValue());
        } else if (name.equals("RequiredPlayerRange")) {
            entity.setRequiredPlayerRange(((ShortTag) tag).getValue().shortValue());
        } else if (name.equals("SpawnCount")) {
            entity.setSpawnCount(((ShortTag) tag).getValue().shortValue());
        } else if (name.equals("SpawnRange")) {
            entity.setSpawnRange(((ShortTag) tag).getValue().shortValue());
        } else {
            super.loadTag(entity, tag);
        }
    }

    public List<Tag> save(T entity) {
        List<Tag> tags = super.save(entity);
        tags.add(new ShortTag("Delay", entity.getDelay()));
        tags.add(new IntTag("EntityId", entity.getEntityId()));
        tags.add(new ShortTag("MaxNearbyEntities", entity.getMaxNearbyEntities()));
        tags.add(new ShortTag("MaxSpawnDelay", entity.getMaxSpawnDelay()));
        tags.add(new ShortTag("MinSpawnDelay", entity.getMinSpawnDelay()));
        tags.add(new ShortTag("RequiredPlayerRange", entity.getRequiredPlayerRange()));
        tags.add(new ShortTag("SpawnCount", entity.getSpawnCount()));
        tags.add(new ShortTag("SpawnRange", entity.getSpawnRange()));
        return tags;
    }
}
