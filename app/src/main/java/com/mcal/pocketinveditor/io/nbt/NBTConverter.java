package com.mcal.pocketinveditor.io.nbt;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.mcal.pocketinveditor.EditInventorySlotActivity;
import com.mcal.pocketinveditor.InventorySlot;
import com.mcal.pocketinveditor.ItemStack;
import com.mcal.pocketinveditor.Level;
import com.mcal.pocketinveditor.entity.Chicken;
import com.mcal.pocketinveditor.entity.Cow;
import com.mcal.pocketinveditor.entity.Entity;
import com.mcal.pocketinveditor.entity.EntityType;
import com.mcal.pocketinveditor.entity.Item;
import com.mcal.pocketinveditor.entity.Pig;
import com.mcal.pocketinveditor.entity.Player;
import com.mcal.pocketinveditor.entity.PlayerAbilities;
import com.mcal.pocketinveditor.entity.Sheep;
import com.mcal.pocketinveditor.entity.Zombie;
import com.mcal.pocketinveditor.geo.ChunkManager;
import com.mcal.pocketinveditor.io.EntityDataConverter.EntityData;
import com.mcal.pocketinveditor.io.nbt.entity.EntityStore;
import com.mcal.pocketinveditor.io.nbt.entity.EntityStoreLookupService;
import com.mcal.pocketinveditor.io.nbt.tileentity.TileEntityStore;
import com.mcal.pocketinveditor.io.nbt.tileentity.TileEntityStoreLookupService;
import com.mcal.pocketinveditor.pro.EditTerrainActivity;
import com.mcal.pocketinveditor.tileentity.TileEntity;
import com.mcal.pocketinveditor.util.Vector3f;
import org.spout.nbt.ByteTag;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.FloatTag;
import org.spout.nbt.IntTag;
import org.spout.nbt.ListTag;
import org.spout.nbt.LongTag;
import org.spout.nbt.NBTConstants;
import org.spout.nbt.ShortTag;
import org.spout.nbt.StringTag;
import org.spout.nbt.Tag;

public final class NBTConverter {
    public static InventorySlot readInventorySlot(CompoundTag compoundTag) {
        List<Tag> tags = compoundTag.getValue();
        byte slot = (byte) 0;
        short id = (short) 0;
        short damage = (short) 0;
        int count = 0;
        List<Object> extraTags = new ArrayList<>();
        for (Tag tag : tags) {
            if (tag.getName().equals("Slot")) {
                slot = ((ByteTag) tag).getValue().byteValue();
            } else if (tag.getName().equals("id")) {
                id = ((ShortTag) tag).getValue().shortValue();
            } else if (tag.getName().equals("Damage")) {
                damage = ((ShortTag) tag).getValue().shortValue();
            } else if (tag.getName().equals("Count")) {
                count = ((ByteTag) tag).getValue().byteValue();
                if (count < 0) {
                    count += ChunkManager.WORLD_WIDTH;
                }
            } else {
                extraTags.add(tag);
            }
        }
        ItemStack stack = new ItemStack(id, damage, count);
        stack.getExtraTags().addAll(extraTags);
        return new InventorySlot(slot, stack);
    }

    public static CompoundTag writeInventorySlot(InventorySlot slot) {
        List<Tag> values = new ArrayList<>(4);
        ItemStack stack = slot.getContents();
        values.add(new ByteTag("Count", (byte) stack.getAmount()));
        values.add(new ShortTag("Damage", stack.getDurability()));
        values.add(new ByteTag("Slot", slot.getSlot()));
        values.add(new ShortTag("id", stack.getTypeId()));
        
        return new CompoundTag("", values);
    }

    public static ListTag<CompoundTag> writeInventory(List<InventorySlot> slots, String name) {
        List<CompoundTag> values = new ArrayList<>(slots.size());
        for (InventorySlot slot : slots) {
            values.add(writeInventorySlot(slot));
        }
        return new ListTag(name, CompoundTag.class, values);
    }

    public static List<InventorySlot> readInventory(ListTag<CompoundTag> listTag) {
        List<InventorySlot> slots = new ArrayList<>();
        for (CompoundTag tag : listTag.getValue()) {
            slots.add(readInventorySlot(tag));
        }
        return slots;
    }

    public static ListTag<CompoundTag> writeArmor(List<ItemStack> slots, String name) {
        List<CompoundTag> values = new ArrayList<>(slots.size());
        for (ItemStack slot : slots) {
            values.add(writeItemStack(slot, ""));
        }
        return new ListTag(name, CompoundTag.class, values);
    }

    public static List<ItemStack> readArmor(ListTag<CompoundTag> listTag) {
        List<ItemStack> slots = new ArrayList<>();
        for (CompoundTag tag : listTag.getValue()) {
            slots.add(readItemStack(tag));
        }
        return slots;
    }

    public static Player readPlayer(CompoundTag compoundTag) {
        List<Tag> tags = compoundTag.getValue();
        Player player = new Player();
        for (Tag tag : tags) {
            String name = tag.getName();
            if (tag.getName().equals("Pos")) {
                player.setLocation(readVector((ListTag) tag));
            } else if (tag.getName().equals("Motion")) {
                player.setVelocity(readVector((ListTag) tag));
            } else if (tag.getName().equals("Air")) {
                player.setAirTicks(((ShortTag) tag).getValue().shortValue());
            } else if (tag.getName().equals("Fire")) {
                player.setFireTicks(((ShortTag) tag).getValue().shortValue());
            } else if (tag.getName().equals("FallDistance")) {
                player.setFallDistance(((FloatTag) tag).getValue().floatValue());
            } else if (tag.getName().equals("Rotation")) {
                List<FloatTag> rotationTags = ((ListTag) tag).getValue();
                player.setYaw(((FloatTag) rotationTags.get(0)).getValue().floatValue());
                player.setPitch(((FloatTag) rotationTags.get(1)).getValue().floatValue());
            } else if (tag.getName().equals("OnGround")) {
                player.setOnGround(((ByteTag) tag).getValue().byteValue() > (byte) 0);
            } else if (tag.getName().equals("AttackTime")) {
                player.setAttackTime(((ShortTag) tag).getValue().shortValue());
            } else if (tag.getName().equals("DeathTime")) {
                player.setDeathTime(((ShortTag) tag).getValue().shortValue());
            } else if (tag.getName().equals("Health")) {
                player.setHealth(((ShortTag) tag).getValue().shortValue());
            } else if (tag.getName().equals("HurtTime")) {
                player.setHurtTime(((ShortTag) tag).getValue().shortValue());
            } else if (name.equals("Armor")) {
                player.setArmor(readArmor((ListTag) tag));
            } else if (name.equals("BedPositionX")) {
                player.setBedPositionX(((IntTag) tag).getValue().intValue());
            } else if (name.equals("BedPositionY")) {
                player.setBedPositionY(((IntTag) tag).getValue().intValue());
            } else if (name.equals("BedPositionZ")) {
                player.setBedPositionZ(((IntTag) tag).getValue().intValue());
            } else if (tag.getName().equals("Dimension")) {
                player.setDimension(((IntTag) tag).getValue().intValue());
            } else if (tag.getName().equals("Inventory")) {
                player.setInventory(readInventory((ListTag) tag));
            } else if (tag.getName().equals("Score")) {
                player.setScore(((IntTag) tag).getValue().intValue());
            } else if (tag.getName().equals("Sleeping")) {
                player.setSleeping(((ByteTag) tag).getValue().byteValue() != (byte) 0);
            } else if (name.equals("SleepTimer")) {
                player.setSleepTimer(((ShortTag) tag).getValue().shortValue());
            } else if (name.equals("SpawnX")) {
                player.setSpawnX(((IntTag) tag).getValue().intValue());
            } else if (name.equals("SpawnY")) {
                player.setSpawnY(((IntTag) tag).getValue().intValue());
            } else if (name.equals("SpawnZ")) {
                player.setSpawnZ(((IntTag) tag).getValue().intValue());
            } else if (name.equals("abilities")) {
                readAbilities((CompoundTag) tag, player.getAbilities());
            } else if (name.equals("Riding")) {
                player.setRiding(readSingleEntity((CompoundTag) tag));
            } else if (!name.equals("id")) {
                System.out.println("Unhandled player tag: " + name + ":" + tag);
                player.getExtraTags().add(tag);
            }
        }
        return player;
    }

    public static CompoundTag writePlayer(Player player, String name) {
        return writePlayer(player, name, false);
    }

    public static CompoundTag writePlayer(Player player, String name, boolean writeId) {
        byte b;
        byte b2 = (byte) 1;
        List<Tag> tags = new ArrayList<>();
        tags.add(new ShortTag("Air", player.getAirTicks()));
        tags.add(new FloatTag("FallDistance", player.getFallDistance()));
        tags.add(new ShortTag("Fire", player.getFireTicks()));
        tags.add(writeVector(player.getVelocity(), "Motion"));
        tags.add(writeVector(player.getLocation(), "Pos"));
        List<FloatTag> rotationTags = new ArrayList<>(2);
        rotationTags.add(new FloatTag("", player.getYaw()));
        rotationTags.add(new FloatTag("", player.getPitch()));
        tags.add(new ListTag("Rotation", FloatTag.class, rotationTags));
        String str = "OnGround";
        if (player.isOnGround()) {
            b = (byte) 1;
        } else {
            b = (byte) 0;
        }
        tags.add(new ByteTag(str, b));
        tags.add(new ShortTag("AttackTime", player.getAttackTime()));
        tags.add(new ShortTag("DeathTime", player.getDeathTime()));
        tags.add(new ShortTag("Health", player.getHealth()));
        tags.add(new ShortTag("HurtTime", player.getHurtTime()));
        if (player.getArmor() != null) {
            tags.add(writeArmor(player.getArmor(), "Armor"));
        }
        tags.add(new IntTag("BedPositionX", player.getBedPositionX()));
        tags.add(new IntTag("BedPositionY", player.getBedPositionY()));
        tags.add(new IntTag("BedPositionZ", player.getBedPositionZ()));
        tags.add(new IntTag("Dimension", player.getDimension()));
        tags.add(writeInventory(player.getInventory(), "Inventory"));
        tags.add(new IntTag("Score", player.getScore()));
        String str2 = "Sleeping";
        if (!player.isSleeping()) {
            b2 = (byte) 0;
        }
        tags.add(new ByteTag(str2, b2));
        tags.add(new ShortTag("SleepTimer", player.getSleepTimer()));
        tags.add(new IntTag("SpawnX", player.getSpawnX()));
        tags.add(new IntTag("SpawnY", player.getSpawnY()));
        tags.add(new IntTag("SpawnZ", player.getSpawnZ()));
        tags.add(writeAbilities(player.getAbilities(), "abilities"));
        if (player.getRiding() != null) {
            tags.add(writeEntity(player.getRiding(), "Riding"));
        }
        if (writeId) {
            tags.add(new IntTag("id", EntityType.PLAYER.getId()));
        }
        
        Collections.sort(tags, new Comparator<Tag>() {
            public int compare(Tag a, Tag b) {
                return a.getName().compareTo(b.getName());
            }

            public boolean equals(Tag a, Tag b) {
                return a.getName().equals(b.getName());
            }
        });
        return new CompoundTag(name, tags);
    }

    public static Vector3f readVector(ListTag<FloatTag> tag) {
        List<FloatTag> tags = tag.getValue();
        return new Vector3f(tags.get(0).getValue().floatValue(), tags.get(1).getValue().floatValue(), tags.get(2).getValue().floatValue());
    }

    public static ListTag<FloatTag> writeVector(Vector3f vector, String tagName) {
        List<FloatTag> tags = new ArrayList<>(3);
        tags.add(new FloatTag("", vector.getX()));
        tags.add(new FloatTag("", vector.getY()));
        tags.add(new FloatTag("", vector.getZ()));
        return new ListTag(tagName, FloatTag.class, tags);
    }

    public static Level readLevel(CompoundTag compoundTag) {
        Level level = new Level();
        for (Tag tag : compoundTag.getValue()) {
            String name = tag.getName();
            if (name.equals("DayCycleStopTime")) {
                level.setDayCycleStopTime(((IntTag) tag).getValue().intValue());
            } else if (name.equals("GameType")) {
                level.setGameType(((IntTag) tag).getValue().intValue());
            } else if (name.equals("LastPlayed")) {
                level.setLastPlayed(((LongTag) tag).getValue().longValue());
            } else if (name.equals("LevelName")) {
                level.setLevelName(((StringTag) tag).getValue());
            } else if (name.equals("Platform")) {
                level.setPlatform(((IntTag) tag).getValue().intValue());
            } else if (name.equals("Player")) {
                level.setPlayer(readPlayer((CompoundTag) tag));
            } else if (name.equals("RandomSeed")) {
                level.setRandomSeed(((LongTag) tag).getValue().longValue());
            } else if (name.equals("SizeOnDisk")) {
                level.setSizeOnDisk(((LongTag) tag).getValue().longValue());
            } else if (name.equals("SpawnX")) {
                level.setSpawnX(((IntTag) tag).getValue().intValue());
            } else if (name.equals("SpawnY")) {
                level.setSpawnY(((IntTag) tag).getValue().intValue());
            } else if (name.equals("SpawnZ")) {
                level.setSpawnZ(((IntTag) tag).getValue().intValue());
            } else if (name.equals("StorageVersion")) {
                level.setStorageVersion(((IntTag) tag).getValue().intValue());
            } else if (name.equals("Time")) {
                level.setTime(((LongTag) tag).getValue().longValue());
            } else if (name.equals("spawnMobs")) {
                level.setSpawnMobs(((ByteTag) tag).getValue().byteValue() != (byte) 0);
            } else if (name.equals("Dimension")) {
                level.setDimension(((IntTag) tag).getValue().intValue());
            } else if (name.equals("Generator")) {
                level.setGenerator(((IntTag) tag).getValue().intValue());
            } else {
                System.out.println("Unhandled level tag: " + name + ":" + tag);
                level.getExtraTags().add(tag);
            }
        }
        return level;
    }

    public static CompoundTag writeLevel(Level level) {
        List<Tag> tags = new ArrayList<>(11);
        tags.add(new IntTag("DayCycleStopTime", level.getDayCycleStopTime()));
        tags.add(new IntTag("GameType", level.getGameType()));
        tags.add(new LongTag("LastPlayed", level.getLastPlayed()));
        tags.add(new StringTag("LevelName", level.getLevelName()));
        tags.add(new IntTag("Platform", level.getPlatform()));
        tags.add(writePlayer(level.getPlayer(), "Player"));
        tags.add(new LongTag("RandomSeed", level.getRandomSeed()));
        tags.add(new LongTag("SizeOnDisk", level.getSizeOnDisk()));
        tags.add(new IntTag("SpawnX", level.getSpawnX()));
        tags.add(new IntTag("SpawnY", level.getSpawnY()));
        tags.add(new IntTag("SpawnZ", level.getSpawnZ()));
        tags.add(new IntTag("StorageVersion", level.getStorageVersion()));
        tags.add(new LongTag("Time", level.getTime()));
        tags.add(new ByteTag("spawnMobs", level.getSpawnMobs() ? (byte) 1 : (byte) 0));
        tags.add(new IntTag("Dimension", level.getDimension()));
        tags.add(new IntTag("Generator", level.getGenerator()));
        
        return new CompoundTag("", tags);
    }

    public static EntityData readEntities(CompoundTag tag) {
        List<Entity> entities = null;
        List<TileEntity> tileEntities = null;
        for (Tag t : tag.getValue()) {
            if (t.getName().equals("Entities")) {
                entities = readEntitiesPart(((ListTag) t).getValue());
            } else if (t.getName().equals("TileEntities")) {
                tileEntities = readTileEntitiesPart(((ListTag) t).getValue());
            }
        }
        if (tileEntities == null) {
            tileEntities = new ArrayList<>();
        }
        return new EntityData(entities, tileEntities);
    }

    public static List<Entity> readEntitiesPart(List<CompoundTag> tags) {
        List<Entity> entities = new ArrayList<>(tags.size());
        for (CompoundTag entityTag : tags) {
            for (Tag t : entityTag.getValue()) {
                if (t.getName().equals("id")) {
                    entities.add(readEntity(((IntTag) t).getValue().intValue(), entityTag));
                    break;
                }
            }
        }
        return entities;
    }

    public static Entity readEntity(int id, CompoundTag tag) {
        Entity entity = createEntityById(id);
        entity.setEntityTypeId(id);
        EntityStore store = EntityStoreLookupService.getStore(id, entity);
        if (store == null) {
            System.err.println("Warning: unknown entity id " + id + "; using default entity store.");
            store = EntityStoreLookupService.defaultStore;
        }
        store.load(entity, tag);
        return entity;
    }

    public static Entity createEntityById(int id) {
        switch (id) {
            case NBTConstants.TYPE_COMPOUND:
                return new Chicken();
            case NBTConstants.TYPE_INT_ARRAY:
                return new Cow();
            case EditTerrainActivity.CHOOSE_CYLINDER_BLOCK_REQUEST:
                return new Pig();
            case 13:
                return new Sheep();
            case 32:
                return new Zombie();
            case EditInventorySlotActivity.MAX_SLOT_SIZE:
                return new Item();
            default:
                EntityType type = EntityType.getById(id);
                if (!(type == null || type.getEntityClass() == null)) {
                    try {
                        return type.getEntityClass().newInstance();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                System.err.println("Unable to find entity class for entity type " + id);
                return new Entity();
        }
    }

    public static CompoundTag writeEntities(List<Entity> entities, List<TileEntity> tileEntities) {
        List<CompoundTag> entitiesTags = new ArrayList<>(entities.size());
        for (Entity entity : entities) {
            entitiesTags.add(writeEntity(entity));
        }
        ListTag<CompoundTag> entitiesListTag = new ListTag<>("Entities", CompoundTag.class, entitiesTags);
        List<CompoundTag> tileEntitiesTags = new ArrayList<>(tileEntities.size());
        for (TileEntity entity2 : tileEntities) {
            tileEntitiesTags.add(writeTileEntity(entity2));
        }
        ListTag<CompoundTag> tileEntitiesListTag = new ListTag<>("TileEntities", CompoundTag.class, tileEntitiesTags);
        List<Tag> compoundTagList = new ArrayList<>(2);
        compoundTagList.add(entitiesListTag);
        compoundTagList.add(tileEntitiesListTag);
        return new CompoundTag("", compoundTagList);
    }

    public static CompoundTag writeEntity(Entity entity) {
        return writeEntity(entity, "");
    }

    public static CompoundTag writeEntity(Entity entity, String tagName) {
        int typeId = entity.getEntityTypeId();
        EntityStore store = EntityStoreLookupService.getStore(typeId, entity);
        if (store == null) {
            System.err.println("Warning: unknown entity id " + typeId + "; using default entity store.");
            store = EntityStoreLookupService.defaultStore;
        }
        List<Tag> tags = store.save(entity);
        Collections.sort(tags, new Comparator<Tag>() {
            public int compare(Tag a, Tag b) {
                return a.getName().compareTo(b.getName());
            }

            public boolean equals(Tag a, Tag b) {
                return a.getName().equals(b.getName());
            }
        });
        return new CompoundTag(tagName, tags);
    }

    public static ItemStack readItemStack(CompoundTag compoundTag) {
        List<Tag> tags = compoundTag.getValue();
        short id = (short) 0;
        short damage = (short) 0;
        int count = 0;
        List<Object> extraTags = new ArrayList<>();
        for (Tag tag : tags) {
            if (tag.getName().equals("id")) {
                id = ((ShortTag) tag).getValue().shortValue();
            } else if (tag.getName().equals("Damage")) {
                damage = ((ShortTag) tag).getValue().shortValue();
            } else if (tag.getName().equals("Count")) {
                count = ((ByteTag) tag).getValue().byteValue();
                if (count < 0) {
                    count += ChunkManager.WORLD_WIDTH;
                }
            } else {
                extraTags.add(tag);
            }
        }
        ItemStack stack = new ItemStack(id, damage, count);
        stack.getExtraTags().addAll(extraTags);
        return stack;
    }

    public static CompoundTag writeItemStack(ItemStack stack, String name) {
        List<Tag> values = new ArrayList<>(3);
        values.add(new ByteTag("Count", (byte) stack.getAmount()));
        values.add(new ShortTag("Damage", stack.getDurability()));
        values.add(new ShortTag("id", stack.getTypeId()));
        
        return new CompoundTag(name, values);
    }

    public static List<TileEntity> readTileEntitiesPart(List<CompoundTag> tags) {
        List<TileEntity> entities = new ArrayList<>(tags.size());
        for (CompoundTag entityTag : tags) {
            for (Tag t : entityTag.getValue()) {
                if (t.getName().equals("id")) {
                    entities.add(readTileEntity(((StringTag) t).getValue(), entityTag));
                    break;
                }
            }
        }
        return entities;
    }

    public static TileEntity readTileEntity(String id, CompoundTag tag) {
        TileEntity entity = createTileEntityById(id);
        entity.setId(id);
        TileEntityStore store = TileEntityStoreLookupService.getStoreById(id);
        if (store == null) {
            System.err.println("Warning: unknown tile entity id " + id + "; using default tileentity store.");
            store = TileEntityStoreLookupService.defaultStore;
        }
        store.load(entity, tag);
        return entity;
    }

    public static TileEntity createTileEntityById(String id) {
        return TileEntityStoreLookupService.createTileEntityById(id);
    }

    public static CompoundTag writeTileEntity(TileEntity entity) {
        String typeId = entity.getId();
        TileEntityStore store = TileEntityStoreLookupService.getStoreById(typeId);
        if (store == null) {
            System.err.println("Warning: unknown tileentity id " + typeId + "; using default tileentity store.");
            store = TileEntityStoreLookupService.defaultStore;
        }
        List<Tag> tags = store.save(entity);
        Collections.sort(tags, new Comparator<Tag>() {
            public int compare(Tag a, Tag b) {
                return a.getName().compareTo(b.getName());
            }

            public boolean equals(Tag a, Tag b) {
                return a.getName().equals(b.getName());
            }
        });
        return new CompoundTag("", tags);
    }

    public static CompoundTag writeLoadout(List<InventorySlot> slots) {
		ListTag<CompoundTag> slotsTag = writeInventory(slots, "Inventory");
		return new CompoundTag("", Collections.singletonList((Tag) slotsTag));
	}

    public static List<InventorySlot> readLoadout(CompoundTag tag) {
        for (Tag t : tag.getValue()) {
            if (t.getName().equals("Inventory")) {
                return readInventory((ListTag) t);
            }
        }
        System.err.println("Why is this blank?!");
        return null;
    }

    public static void readAbilities(CompoundTag tag, PlayerAbilities abilities) {
        for (Tag t : tag.getValue()) {
            String n = t.getName();
            if (t instanceof ByteTag) {
                boolean value = ((ByteTag) t).getValue().byteValue() != (byte) 0;
                if (n.equals("flying")) {
                    abilities.flying = value;
                } else if (n.equals("instabuild")) {
                    abilities.instabuild = value;
                } else if (n.equals("invulnerable")) {
                    abilities.invulnerable = value;
                } else if (n.equals("mayfly")) {
                    abilities.mayFly = value;
                } else if (n.equals("lightning")) {
                    abilities.lightning = value;
                } else {
                    System.out.println("Unsupported tag in readAbilities: " + n);
                }
            } else if (n.equals("flySpeed")) {
                abilities.flySpeed = ((FloatTag) t).getValue().floatValue();
            } else if (n.equals("walkSpeed")) {
                abilities.walkSpeed = ((FloatTag) t).getValue().floatValue();
            } else {
                System.out.println("Unsupported tag type in readAbilities: " + n + ":" + n.getClass());
            }
        }
    }

    public static CompoundTag writeAbilities(PlayerAbilities abilities, String name) {
        byte b;
        byte b2 = (byte) 1;
        List<Tag> values = new ArrayList<>(4);
        values.add(new ByteTag("flying", abilities.flying ? (byte) 1 : (byte) 0));
        values.add(new FloatTag("flySpeed", abilities.flySpeed));
        String str = "instabuild";
        if (abilities.instabuild) {
            b = (byte) 1;
        } else {
            b = (byte) 0;
        }
        values.add(new ByteTag(str, b));
        str = "invulnerable";
        if (abilities.invulnerable) {
            b = (byte) 1;
        } else {
            b = (byte) 0;
        }
        values.add(new ByteTag(str, b));
        str = "lightning";
        if (abilities.lightning) {
            b = (byte) 1;
        } else {
            b = (byte) 0;
        }
        values.add(new ByteTag(str, b));
        String str2 = "mayfly";
        if (!abilities.mayFly) {
            b2 = (byte) 0;
        }
        values.add(new ByteTag(str2, b2));
        values.add(new FloatTag("walkSpeed", abilities.walkSpeed));
        return new CompoundTag(name, values);
    }

    public static Entity readSingleEntity(CompoundTag entityTag) {
        for (Tag t : entityTag.getValue()) {
            if (t.getName().equals("id")) {
                return readEntity(((IntTag) t).getValue().intValue(), entityTag);
            }
        }
        return null;
    }

    public static TileEntity readSingleTileEntity(CompoundTag entityTag) {
        for (Tag t : entityTag.getValue()) {
            if (t.getName().equals("id")) {
                return readTileEntity(((StringTag) t).getValue(), entityTag);
            }
        }
        return null;
    }
}
