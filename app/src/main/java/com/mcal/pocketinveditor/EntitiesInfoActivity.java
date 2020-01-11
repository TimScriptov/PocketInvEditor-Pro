package com.mcal.pocketinveditor;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;
import com.mcal.pocketinveditor.entity.Animal;
import com.mcal.pocketinveditor.entity.Cow;
import com.mcal.pocketinveditor.entity.Entity;
import com.mcal.pocketinveditor.entity.EntityType;
import com.mcal.pocketinveditor.entity.LivingEntity;
import com.mcal.pocketinveditor.entity.Sheep;
import com.mcal.pocketinveditor.io.EntityDataConverter.EntityData;
import com.mcal.pocketinveditor.io.leveldb.LevelDBConverter;
import com.mcal.pocketinveditor.pro.R;
import com.mcal.pocketinveditor.tileentity.SignTileEntity;
import com.mcal.pocketinveditor.tileentity.TileEntity;
import com.mcal.pocketinveditor.util.Vector3f;
import java.io.File;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class EntitiesInfoActivity extends Activity implements OnClickListener, EntityDataLoadListener, LevelDataLoadListener {
    public static final int BABY_GROWTH_TICKS = -24000;
    public static final int BREED_TICKS = 9999;
    private List<Entity> entitiesList;
    private TextView entityCountText;

    public static class LoadEntitiesTask implements Runnable {
        private final Activity activity;
        private final EntityDataLoadListener listener;

        public LoadEntitiesTask(Activity activity, EntityDataLoadListener listener) {
            this.activity = activity;
            this.listener = listener;
        }

        public void run() {
            try {
                final EntityData entitiesList = LevelDBConverter.readAllEntities(new File(EditorActivity.worldFolder, "db"));
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        listener.onEntitiesLoaded(entitiesList);
                    }
                });
            } catch (Exception e) {
                e.printStackTrace();
                activity.runOnUiThread(new Runnable() {
                    public void run() {
                        listener.onEntitiesLoaded(new EntityData(new ArrayList<>(), new ArrayList<>()));
                    }
                });
            }
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.entities_info);
        entityCountText = findViewById(R.id.entities_main_count);
        if (EditorActivity.level != null) {
            onLevelDataLoad();
        } else {
            EditorActivity.loadLevelData(this, this, getIntent().getStringExtra("world"));
        }
    }

    public void onLevelDataLoad() {
        loadEntities();
    }

    protected void loadEntities() {
        new Thread(new LoadEntitiesTask(this, this)).start();
    }

    public void onEntitiesLoaded(EntityData entitiesDat) {
        EditorActivity.level.setEntities(entitiesDat.entities);
        EditorActivity.level.setTileEntities(entitiesDat.tileEntities);
        entitiesList = entitiesDat.entities;
        countEntities();
    }

    public void onClick(View v) {
    }

    protected void countEntities() {
        Map<EntityType, Integer> countMap = new EnumMap<>(EntityType.class);
        for (Entity e : entitiesList) {
            EntityType entityType = e.getEntityType();
            int newCount = 1;
            Integer oldCount = countMap.get(entityType);
            if (oldCount != null) {
                newCount = 1 + oldCount.intValue();
            }
            countMap.put(entityType, Integer.valueOf(newCount));
        }
        String entityCountString = buildEntityCountString(countMap);
        if (entityCountString.length() == 0) {
            entityCountString = getResources().getText(R.string.entities_none).toString();
        }
        entityCountText.setText(entityCountString);
    }

    private String buildEntityCountString(Map<EntityType, Integer> countMap) {
        StringBuilder builder = new StringBuilder();
        for (Entry<EntityType, Integer> entry : countMap.entrySet()) {
            Integer resId = EntityTypeLocalization.namesMap.get(entry.getKey());
            if (resId == null) {
                resId = new Integer(R.string.entity_unknown);
            }
            builder.append(getResources().getText(resId.intValue()));
            builder.append(':');
            builder.append(entry.getValue());
            builder.append('\n');
        }
        return builder.toString();
    }

    public void apoCowlypse() {
        List<Entity> list = EditorActivity.level.getEntities();
        Vector3f playerLoc = EditorActivity.level.getPlayer().getLocation();
        int endX = ((int) playerLoc.getX()) + 16;
        int beginZ = ((int) playerLoc.getZ()) - 16;
        int endZ = ((int) playerLoc.getZ()) + 16;
        for (int x = ((int) playerLoc.getX()) - 16; x < endX; x += 2) {
            for (int z = beginZ; z < endZ; z += 2) {
                Cow cow = new Cow();
                cow.setLocation(new Vector3f((float) x, 128.0f, (float) z));
                cow.setEntityTypeId(EntityType.COW.getId());
                cow.setHealth((short) 128);
                list.add(cow);
            }
        }
        save(this);
        countEntities();
    }

    public void cowTipping(EntityType type, short tipNess) {
        for (Entity entity : EditorActivity.level.getEntities()) {
            if (entity.getEntityType() == type) {
                ((LivingEntity) entity).setDeathTime(tipNess);
            }
        }
        save(this);
    }

    public void spawnMobs(EntityType type, Vector3f loc, int count) throws Exception {
        List<Entity> entities = EditorActivity.level.getEntities();
        for (int i = 0; i < count; i++) {
            Entity e = type.getEntityClass().newInstance();
            e.setEntityTypeId(type.getId());
            e.setLocation(loc);
            if (e instanceof LivingEntity) {
                ((LivingEntity) e).setHealth((short) ((LivingEntity) e).getMaxHealth());
            }
            entities.add(e);
        }
    }

    public int removeEntities(EntityType type) {
        int removedCount = 0;
        List<Entity> entities = EditorActivity.level.getEntities();
        for (int i = entities.size() - 1; i >= 0; i--) {
            if (entities.get(i).getEntityType() == type) {
                entities.remove(i);
                removedCount++;
            }
        }
        return removedCount;
    }

    public void setAllAnimalsAge(EntityType type, int ticks) {
        for (Entity e : EditorActivity.level.getEntities()) {
            if (e.getEntityType() == type) {
                ((Animal) e).setAge(ticks);
            }
        }
    }

    public void replaceEntities(EntityType type, EntityType toType) {
        List<Entity> entities = EditorActivity.level.getEntities();
        Class<? extends Entity> toEntityClass = toType.getEntityClass();
        int toEntityId = toType.getId();
        for (int i = 0; i < entities.size(); i++) {
            Entity e = entities.get(i);
            if (type == null || e.getEntityType() == type) {
                try {
                    Entity newE = toEntityClass.newInstance();
                    newE.setEntityTypeId(toEntityId);
                    newE.setLocation(e.getLocation());
                    newE.setOnGround(e.isOnGround());
                    newE.setVelocity(e.getVelocity());
                    newE.setPitch(e.getPitch());
                    newE.setYaw(e.getYaw());
                    newE.setFallDistance(e.getFallDistance());
                    entities.set(i, newE);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
    }

    public void dyeAllSheep(byte colour) {
        for (Entity e : EditorActivity.level.getEntities()) {
            if (e instanceof Sheep) {
                ((Sheep) e).setColor(colour);
            }
        }
    }

    public void setAllBreeding(EntityType type, int breedTicks) {
        for (Entity e : EditorActivity.level.getEntities()) {
            if (e.getEntityType() == type) {
            }
        }
    }

    public void spawnOnSigns() {
        List<Entity> entities = EditorActivity.level.getEntities();
        String spawnedText = "Spawned!";
        for (TileEntity t : EditorActivity.level.getTileEntities()) {
            if (t instanceof SignTileEntity) {
                SignTileEntity sign = (SignTileEntity) t;
                if (!sign.getLine(2).equals(spawnedText)) {
                    try {
                        int count = Integer.parseInt(sign.getLine(1));
                    } catch (Exception e) {
                    }
                }
            }
        }
    }

    public int removeAllEntities() {
        int removedCount = 0;
        List<Entity> entities = EditorActivity.level.getEntities();
        for (int i = entities.size() - 1; i >= 0; i--) {
            if (entities.get(i).getEntityType() != EntityType.PAINTING) {
                entities.remove(i);
                removedCount++;
            }
        }
        return removedCount;
    }

    public static void save(final Activity context) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    LevelDBConverter.writeAllEntities(EditorActivity.level.getEntities(), new File(EditorActivity.worldFolder, "db"));
                    if (context != null) {
                        context.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(context, R.string.saved, 0).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (context != null) {
                        context.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(context, R.string.savefailed, 0).show();
                            }
                        });
                    }
                }
            }
        }).start();
    }

    public static void saveTileEntities(final Activity context) {
        new Thread(new Runnable() {
            public void run() {
                try {
                    LevelDBConverter.writeAllTileEntities(EditorActivity.level.getTileEntities(), new File(EditorActivity.worldFolder, "db"));
                    if (context != null) {
                        context.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(context, R.string.saved, 0).show();
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (context != null) {
                        context.runOnUiThread(new Runnable() {
                            public void run() {
                                Toast.makeText(context, R.string.savefailed, 1).show();
                            }
                        });
                    }
                }
            }
        }).start();
    }
}
