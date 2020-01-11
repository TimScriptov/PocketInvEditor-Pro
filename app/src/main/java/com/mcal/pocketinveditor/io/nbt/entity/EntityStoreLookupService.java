package com.mcal.pocketinveditor.io.nbt.entity;

import java.util.HashMap;
import java.util.Map;
import com.mcal.pocketinveditor.entity.Entity;
import com.mcal.pocketinveditor.entity.LivingEntity;

public class EntityStoreLookupService {
    public static EntityStore<LivingEntity> defaultLivingEntityStore = new LivingEntityStore<>();
    public static EntityStore<Entity> defaultStore = new EntityStore<>();
    public static Map<Integer, EntityStore> idMap = new HashMap<>();

    static {
        addStore(10, new AnimalEntityStore());
        addStore(11, new AnimalEntityStore());
        addStore(12, new AnimalEntityStore());
        addStore(13, new SheepEntityStore());
        addStore(14, new AnimalEntityStore());
        addStore(15, new AnimalEntityStore());
        addStore(16, new AnimalEntityStore());
        addStore(17, new AnimalEntityStore());
        addStore(18, new AnimalEntityStore());
        addStore(19, new LivingEntityStore());
        addStore(32, new LivingEntityStore());
        addStore(33, new LivingEntityStore());
        addStore(34, new LivingEntityStore());
        addStore(35, new LivingEntityStore());
        addStore(36, new PigZombieEntityStore());
        addStore(37, new LivingEntityStore());
        addStore(38, new EndermanEntityStore());
        addStore(39, new LivingEntityStore());
        addStore(40, new LivingEntityStore());
        addStore(41, new LivingEntityStore());
        addStore(42, new LivingEntityStore());
        addStore(64, new ItemEntityStore());
        addStore(65, new TNTPrimedEntityStore());
        addStore(66, new FallingBlockEntityStore());
        addStore(80, new ArrowEntityStore());
        addStore(81, new ProjectileEntityStore());
        addStore(82, new ProjectileEntityStore());
        addStore(83, new PaintingEntityStore());
        addStore(84, new MinecartEntityStore());
        addStore(85, new EntityStore());
        addStore(87, new ProjectileEntityStore());
        addStore(90, new EntityStore());
        addStore(96, new MinecartEntityStore());
        addStore(97, new MinecartEntityStore());
        addStore(98, new MinecartEntityStore());
    }

    public static void addStore(int id, EntityStore store) {
        idMap.put(Integer.valueOf(id), store);
    }

    public static EntityStore getStore(int id, Entity entity) {
        EntityStore ret = idMap.get(Integer.valueOf(id));
        if (ret != null) {
            return ret;
        }
        if (entity instanceof LivingEntity) {
            return defaultLivingEntityStore;
        }
        return defaultStore;
    }
}
