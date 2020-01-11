package com.mcal.pocketinveditor;

import com.mcal.pocketinveditor.entity.Entity;
import com.mcal.pocketinveditor.entity.Player;
import com.mcal.pocketinveditor.tileentity.TileEntity;
import java.util.ArrayList;
import java.util.List;

public class Level {
    private int dayCycleStopTime = -1;
    private int dimension = 0;
    private List<Entity> entities;
    private List<Object> extras = new ArrayList<>();
    private int gameType;
    private int generator = 0;
    private long lastPlayed;
    private String levelName;
    private int platform;
    private Player player;
    private long randomSeed;
    private long sizeOnDisk;
    private boolean spawnMobs = true;
    private int spawnX;
    private int spawnY;
    private int spawnZ;
    private int storageVersion;
    private List<TileEntity> tileEntities;
    private long time;

    public int getGameType() {
        return gameType;
    }

    public void setGameType(int gameType) {
        this.gameType = gameType;
    }

    public long getLastPlayed() {
        return lastPlayed;
    }

    public void setLastPlayed(long lastPlayed) {
        this.lastPlayed = lastPlayed;
    }

    public String getLevelName() {
        return levelName;
    }

    public void setLevelName(String levelName) {
        this.levelName = levelName;
    }

    public int getPlatform() {
        return platform;
    }

    public void setPlatform(int platform) {
        this.platform = platform;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public long getRandomSeed() {
        return randomSeed;
    }

    public void setRandomSeed(long randomSeed) {
        this.randomSeed = randomSeed;
    }

    public long getSizeOnDisk() {
        return sizeOnDisk;
    }

    public void setSizeOnDisk(long sizeOnDisk) {
        this.sizeOnDisk = sizeOnDisk;
    }

    public int getSpawnX() {
        return spawnX;
    }

    public void setSpawnX(int spawnX) {
        this.spawnX = spawnX;
    }

    public int getSpawnY() {
        return spawnY;
    }

    public void setSpawnY(int spawnY) {
        this.spawnY = spawnY;
    }

    public int getSpawnZ() {
        return spawnZ;
    }

    public void setSpawnZ(int spawnZ) {
        this.spawnZ = spawnZ;
    }

    public int getStorageVersion() {
        return storageVersion;
    }

    public void setStorageVersion(int storageVersion) {
        this.storageVersion = storageVersion;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public int getDayCycleStopTime() {
        return dayCycleStopTime;
    }

    public void setDayCycleStopTime(int dayCycleStopTime) {
        this.dayCycleStopTime = dayCycleStopTime;
    }

    public boolean getSpawnMobs() {
        return spawnMobs;
    }

    public void setSpawnMobs(boolean spawnMobs) {
        this.spawnMobs = spawnMobs;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void setEntities(List<Entity> entities) {
        this.entities = entities;
    }

    public List<TileEntity> getTileEntities() {
        return tileEntities;
    }

    public void setTileEntities(List<TileEntity> tileEntities) {
        this.tileEntities = tileEntities;
    }

    public int getDimension() {
        return dimension;
    }

    public void setDimension(int dimension) {
        this.dimension = dimension;
    }

    public int getGenerator() {
        return generator;
    }

    public void setGenerator(int generator) {
        this.generator = generator;
    }

    public List<Object> getExtraTags() {
        return extras;
    }
}
