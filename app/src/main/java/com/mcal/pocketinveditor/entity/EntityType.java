package com.mcal.pocketinveditor.entity;

import java.util.HashMap;
import java.util.Map;

public enum EntityType {
    CHICKEN(10, Chicken.class),
    COW(11, Cow.class),
    PIG(12, Pig.class),
    SHEEP(13, Sheep.class),
    WOLF(14, Wolf.class),
    VILLAGER(15, Villager.class),
    MUSHROOM_COW(16, MushroomCow.class),
    SQUID(17, Squid.class),
    RABBIT(18, Rabbit.class),
    BAT(19, Bat.class),
    IRON_GOLEM(20, IronGolem.class),
    SNOW_GOLEM(21, SnowGolem.class),
    OCELOT(21, Ocelot.class),
    ZOMBIE(32, Zombie.class),
    CREEPER(33, Creeper.class),
    SKELETON(34, Skeleton.class),
    SPIDER(35, Spider.class),
    PIG_ZOMBIE(36, PigZombie.class),
    SLIME(37, Slime.class),
    ENDERMAN(38, Enderman.class),
    SILVERFISH(39, Silverfish.class),
    CAVE_SPIDER(40, CaveSpider.class),
    GHAST(41, Ghast.class),
    LAVA_SLIME(42, LavaSlime.class),
    BLAZE(43, Blaze.class),
    ZOMBIE_VILLAGER(44, ZombieVillager.class),
    WITCH(45, Witch.class),
	ITEM(64, Item.class),
	PRIMED_TNT(65, TNTPrimed.class),
    FALLING_BLOCK(66, FallingBlock.class),
    EXPERIENCE_POTION(68, ExperiencePotion.class),
    EXPERIENCE_ORB(69, ExperienceOrb.class),
    FISHING_HOOK(77, FishingHook.class),
    ARROW(80, Arrow.class),
    SNOWBALL(81, Snowball.class),
    EGG(82, Egg.class),
    PAINTING(83, Painting.class),
    MINECART(84, Minecart.class),
    FIREBALL(85, Fireball.class),
    THROWN_POTION(86, ThrownPotion.class),
    THROWN_ENDER_PEARL(87, ThrownEnderPearl.class),
    BOAT(90, Boat.class),
    LIGHTNING_BOLT(93, LightningBolt.class),
    SMALL_FIREBALL(94, SmallFireball.class),
    TRIPOD_CAMERA(95, TripodCamera.class),
    MINECART_HOPPER(96, MinecartHopper.class),
    MINECART_TNT(97, MinecartTNT.class),
    MINECART_CHEST(98, MinecartChest.class),
	PLAYER(63, Player.class),
	
    /*STRAY(46, Stray.class),
    HUSK(47, Husk.class),
    HORSE(23, Horse.class),
    SKELETON_HORSE(26, SkeletonHorse.class),
    ZOMBIE_HORSE(27, ZombieHorse.class),
    MULE(25, Mule.class),
    DONKEY(24, Donkey.class),
    GUARD(49, Guard.class),
    ELDERGUARDIAN(50, ElderGuardian.class),
    WITHER(52, ElderGuardian.class),
    ENDERMITE(55, Endermite.class),
    SHULKER(54, Shulker.class),
    POLARBEAR(28, PolarBear.class),
    ENDERDRAGON(53, Enderdragon.class),
    EVOKER(104, Evoker.class),
    VINDICATOR(57, Vindicator.class),
    LLAMA_CREAMY(29, LlamaCreamy.class),
    VEX(105, Vex.class),
    PARROT(21278, Parrot.class),
    COD(9072, Cod.class),
    SALMON(9069, Salmon.class),
    PUFFERFISH(9068, PufferFish.class),
    TROPICALFISH(9071, TropicalFish.class),
    DOLPHIN(8991, Dolphin.class),
    TURTLE_EGG(4938, TurtleEgg.class),
    DROWNED(199534, Drowned.class),
    PHANTOM(68410, Phantom.class),
    PANDA(4977, Panda.class),
    CAT(21323, Cat.class),*/
	UNKNOWN(-1, null);
	
    private static Map<Class<? extends Entity>, EntityType> classMap;
    private static Map<Integer, EntityType> idMap;
    private Class<? extends Entity> entityClass;
    private int id;

    static {
        idMap = new HashMap<>();
        classMap = new HashMap<>();
        for (EntityType type : values()) {
            idMap.put(Integer.valueOf(type.getId()), type);
            if (type.getEntityClass() != null) {
                classMap.put(type.getEntityClass(), type);
            }
        }
    }

    private EntityType(int id, Class<? extends Entity> entityClass) {
        this.id = id;
        this.entityClass = entityClass;
    }

    public int getId() {
        return id;
    }

    public Class<? extends Entity> getEntityClass() {
        return entityClass;
    }

    public static EntityType getById(int id) {
        EntityType type = idMap.get(Integer.valueOf(id));
        if (type == null) {
            return UNKNOWN;
        }
        return type;
    }

    public static EntityType getByClass(Class<? extends Entity> clazz) {
        EntityType type = classMap.get(clazz);
        if (type == null) {
            return UNKNOWN;
        }
        return type;
    }
}
