package com.mcal.pocketinveditor.tileentity;

import com.mcal.pocketinveditor.entity.EntityType;

public class MobSpawnerTileEntity extends TileEntity {
    private short delay = (short) 20;
    private int entityId = 0;
    private short maxNearbyEntities = (short) 6;
    private short maxSpawnDelay = (short) 200;
    private short minSpawnDelay = (short) 200;
    private short requiredPlayerRange = (short) 16;
    private short spawnCount = (short) 4;
    private short spawnRange = (short) 4;

    public short getDelay() {
        return delay;
    }

    public void setDelay(short delay) {
        this.delay = delay;
    }

    public int getEntityId() {
        return entityId;
    }

    public void setEntityId(int entityId) {
        this.entityId = entityId;
    }

    public short getMaxNearbyEntities() {
        return maxNearbyEntities;
    }

    public void setMaxNearbyEntities(short maxNearbyEntities) {
        this.maxNearbyEntities = maxNearbyEntities;
    }

    public short getMaxSpawnDelay() {
        return maxSpawnDelay;
    }

    public void setMaxSpawnDelay(short maxSpawnDelay) {
        this.maxSpawnDelay = maxSpawnDelay;
    }

    public short getMinSpawnDelay() {
        return minSpawnDelay;
    }

    public void setMinSpawnDelay(short minSpawnDelay) {
        this.minSpawnDelay = minSpawnDelay;
    }

    public short getRequiredPlayerRange() {
        return requiredPlayerRange;
    }

    public void setRequiredPlayerRange(short requiredPlayerRange) {
        this.requiredPlayerRange = requiredPlayerRange;
    }

    public short getSpawnCount() {
        return spawnCount;
    }

    public void setSpawnCount(short spawnCount) {
        this.spawnCount = spawnCount;
    }

    public short getSpawnRange() {
        return spawnRange;
    }

    public void setSpawnRange(short spawnRange) {
        this.spawnRange = spawnRange;
    }

    public String toString() {
        Class clazz = EntityType.getById(entityId).getEntityClass();
        if (clazz == null) {
            return super.toString();
        }
        return super.toString() + ": " + clazz.getSimpleName();
    }
}
