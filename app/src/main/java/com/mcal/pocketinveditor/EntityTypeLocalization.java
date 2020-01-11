package com.mcal.pocketinveditor;

import android.content.Context;
import android.content.res.Resources;
import com.mcal.pocketinveditor.entity.EntityType;
import com.mcal.pocketinveditor.pro.R;
import java.util.EnumMap;
import java.util.Map;
import java.util.Map.Entry;

public class EntityTypeLocalization {
    public static Map<EntityType, Integer> namesMap = new EnumMap<>(EntityType.class);

    static {
		namesMap.put(EntityType.CHICKEN, Integer.valueOf(R.string.entity_chicken));
        namesMap.put(EntityType.COW, Integer.valueOf(R.string.entity_cow));
        namesMap.put(EntityType.PIG, Integer.valueOf(R.string.entity_pig));
        namesMap.put(EntityType.SHEEP, Integer.valueOf(R.string.entity_sheep));
        namesMap.put(EntityType.WOLF, Integer.valueOf(R.string.entity_wolf));
        namesMap.put(EntityType.VILLAGER, Integer.valueOf(R.string.entity_villager));
        namesMap.put(EntityType.MUSHROOM_COW, Integer.valueOf(R.string.entity_mushroom_cow));
        namesMap.put(EntityType.SQUID, Integer.valueOf(R.string.entity_squid));
        namesMap.put(EntityType.RABBIT, Integer.valueOf(R.string.entity_rabbit));
        namesMap.put(EntityType.BAT, Integer.valueOf(R.string.entity_bat));
        namesMap.put(EntityType.IRON_GOLEM, Integer.valueOf(R.string.entity_iron_golem));
        namesMap.put(EntityType.SNOW_GOLEM, Integer.valueOf(R.string.entity_snow_golem));
        namesMap.put(EntityType.OCELOT, Integer.valueOf(R.string.entity_ocelot));
        namesMap.put(EntityType.ZOMBIE, Integer.valueOf(R.string.entity_zombie));
        namesMap.put(EntityType.CREEPER, Integer.valueOf(R.string.entity_creeper));
        namesMap.put(EntityType.SKELETON, Integer.valueOf(R.string.entity_skeleton));
        namesMap.put(EntityType.SPIDER, Integer.valueOf(R.string.entity_spider));
        namesMap.put(EntityType.PIG_ZOMBIE, Integer.valueOf(R.string.entity_pigzombie));
        namesMap.put(EntityType.SLIME, Integer.valueOf(R.string.entity_slime));
        namesMap.put(EntityType.ENDERMAN, Integer.valueOf(R.string.entity_enderman));
        namesMap.put(EntityType.SILVERFISH, Integer.valueOf(R.string.entity_silverfish));
        namesMap.put(EntityType.CAVE_SPIDER, Integer.valueOf(R.string.entity_cave_spider));
        namesMap.put(EntityType.GHAST, Integer.valueOf(R.string.entity_ghast));
        namesMap.put(EntityType.LAVA_SLIME, Integer.valueOf(R.string.entity_lava_slime));
        namesMap.put(EntityType.BLAZE, Integer.valueOf(R.string.entity_blaze));
        namesMap.put(EntityType.ZOMBIE_VILLAGER, Integer.valueOf(R.string.entity_zombie_villager));
        namesMap.put(EntityType.WITCH, Integer.valueOf(R.string.entity_witch));
        namesMap.put(EntityType.ITEM, Integer.valueOf(R.string.entity_item));
        namesMap.put(EntityType.PRIMED_TNT, Integer.valueOf(R.string.entity_primedtnt));
        namesMap.put(EntityType.FALLING_BLOCK, Integer.valueOf(R.string.entity_fallingblock));
        namesMap.put(EntityType.EXPERIENCE_POTION, Integer.valueOf(R.string.entity_experience_potion));
        namesMap.put(EntityType.EXPERIENCE_ORB, Integer.valueOf(R.string.entity_experience_orb));
        namesMap.put(EntityType.FISHING_HOOK, Integer.valueOf(R.string.entity_fishing_hook));
        namesMap.put(EntityType.ARROW, Integer.valueOf(R.string.entity_arrow));
        namesMap.put(EntityType.SNOWBALL, Integer.valueOf(R.string.entity_snowball));
        namesMap.put(EntityType.EGG, Integer.valueOf(R.string.entity_thrownegg));
        namesMap.put(EntityType.PAINTING, Integer.valueOf(R.string.entity_painting));
        namesMap.put(EntityType.MINECART, Integer.valueOf(R.string.entity_minecart));
        namesMap.put(EntityType.FIREBALL, Integer.valueOf(R.string.entity_fireball));
        namesMap.put(EntityType.THROWN_POTION, Integer.valueOf(R.string.entity_thrown_potion));
        namesMap.put(EntityType.THROWN_ENDER_PEARL, Integer.valueOf(R.string.entity_thrown_ender_pearl));
        namesMap.put(EntityType.BOAT, Integer.valueOf(R.string.entity_boat));
        namesMap.put(EntityType.LIGHTNING_BOLT, Integer.valueOf(R.string.entity_lightning_bolt));
        namesMap.put(EntityType.SMALL_FIREBALL, Integer.valueOf(R.string.entity_small_fireball));
        namesMap.put(EntityType.TRIPOD_CAMERA, Integer.valueOf(R.string.entity_tripod_camera));
        namesMap.put(EntityType.MINECART_HOPPER, Integer.valueOf(R.string.entity_minecart_hopper));
        namesMap.put(EntityType.MINECART_TNT, Integer.valueOf(R.string.entity_minecart_tnt));
        namesMap.put(EntityType.MINECART_CHEST, Integer.valueOf(R.string.entity_minecart_chest));
		
		//namesMap.put(EntityType.PLAYER, Integer.valueOf("PLAYER"));
		
		/*namesMap.put(EntityType.STRAY, Integer.valueOf("STRAY"));
        namesMap.put(EntityType.HUSK, Integer.valueOf("HUSK"));
        namesMap.put(EntityType.HORSE, Integer.valueOf("HORSE"));
        namesMap.put(EntityType.SKELETON_HORSE, Integer.valueOf("SKELETON HORSE"));
        namesMap.put(EntityType.ZOMBIE_HORSE, Integer.valueOf("ZOMBIE HORSE"));
        namesMap.put(EntityType.MULE, Integer.valueOf("MULE"));
        namesMap.put(EntityType.DONKEY, Integer.valueOf("DONKEY"));
		namesMap.put(EntityType.GUARD, Integer.valueOf("GUARD"));
        namesMap.put(EntityType.ELDERGUARDIAN, Integer.valueOf("ELDERGUARDIAN"));
        namesMap.put(EntityType.WITHER, Integer.valueOf("WITHER"));
		namesMap.put(EntityType.ENDERMITE, Integer.valueOf("ENDERMITE"));
        namesMap.put(EntityType.SHULKER, Integer.valueOf("SHULKER"));
        namesMap.put(EntityType.POLARBEAR, Integer.valueOf("POLARBEAR"));
        namesMap.put(EntityType.ENDERDRAGON, Integer.valueOf("ENDERDRAGON"));
		namesMap.put(EntityType.EVOKER, Integer.valueOf("EVOKER"));
        namesMap.put(EntityType.VINDICATOR, Integer.valueOf("VINDICATOR"));
        namesMap.put(EntityType.LLAMA_CREAMY, Integer.valueOf("LLAMA CREAMY"));
        namesMap.put(EntityType.VEX, Integer.valueOf("VEX"));
		namesMap.put(EntityType.PARROT, Integer.valueOf("PARROT"));
		namesMap.put(EntityType.COD, Integer.valueOf("COD"));
        namesMap.put(EntityType.SALMON, Integer.valueOf("SALMON"));
        namesMap.put(EntityType.PUFFERFISH, Integer.valueOf("PUFFERFISH"));
        namesMap.put(EntityType.TROPICALFISH, Integer.valueOf("TROPICALFISH"));
        namesMap.put(EntityType.DOLPHIN, Integer.valueOf("DOLPHIN"));
		namesMap.put(EntityType.DROWNED, Integer.valueOf("DROWNED"));
		namesMap.put(EntityType.TURTLE_EGG, Integer.valueOf("TURTLE EGG"));
		namesMap.put(EntityType.PHANTOM, Integer.valueOf("PHANTOM"));
		namesMap.put(EntityType.PANDA, Integer.valueOf("PANDA"));
        namesMap.put(EntityType.CAT, Integer.valueOf("CAT"));*/
		namesMap.put(EntityType.UNKNOWN, Integer.valueOf(R.string.entity_unknown));
    }
	
    public static EntityType lookupFromString(CharSequence name, Context ctx) {
        Resources res = ctx.getResources();
        for (Entry<EntityType, Integer> entry : namesMap.entrySet()) {
            EntityType type = entry.getKey();
            if (res.getText(entry.getValue().intValue()).equals(name)) {
                return type;
            }
        }
        return EntityType.UNKNOWN;
    }
}
