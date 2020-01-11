package com.mcal.pocketinveditor.pro;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;
import com.mcal.pocketinveditor.DyeColor;
import com.mcal.pocketinveditor.EditorActivity;
import com.mcal.pocketinveditor.EntitiesInfoActivity;
import com.mcal.pocketinveditor.entity.EntityType;
import com.mcal.pocketinveditor.util.Vector3f;

public class EntitiesEditActivity extends EntitiesInfoActivity {
    public static final int DIALOG_AGE_MOBS = 30;
    public static final int DIALOG_BABYFY_MOBS = 26;
    public static final int DIALOG_BREED_ALL_ANIMALS = 31;
    public static final int DIALOG_DYE_ALL_SHEEP = 29;
    public static final int DIALOG_MOB_TIPPING = 27;
    public static final int DIALOG_REMOVE_ENTITIES = 25;
    public static final int DIALOG_REPLACE_ENTITIES = 28;
    public static final int DIALOG_SPAWN_MOBS = 23;
    public static final EntityType[] mobTypes;
    public Button ageAllAnimalsButton;
    public Button apoCowlypseButton;
    public Button babyfyButton;
    public Button cowTippingButton;
    public Button dyeAllSheepButton;
    public Button removeAllEntitiesButton;
    public Button removeEntitiesButton;
    public Button replaceEntitiesButton;
    public Button spawnDropsButton;
    public Button spawnMobsButton;

    static {
        EntityType[] entityTypeArr = new EntityType[DIALOG_MOB_TIPPING];
        entityTypeArr[0] = EntityType.CHICKEN;
        entityTypeArr[1] = EntityType.COW;
        entityTypeArr[2] = EntityType.PIG;
        entityTypeArr[3] = EntityType.SHEEP;
        entityTypeArr[4] = EntityType.WOLF;
        entityTypeArr[5] = EntityType.VILLAGER;
        entityTypeArr[6] = EntityType.MUSHROOM_COW;
        entityTypeArr[7] = EntityType.SQUID;
        entityTypeArr[8] = EntityType.RABBIT;
        entityTypeArr[9] = EntityType.BAT;
        entityTypeArr[10] = EntityType.IRON_GOLEM;
        entityTypeArr[11] = EntityType.SNOW_GOLEM;
        entityTypeArr[12] = EntityType.OCELOT;
        entityTypeArr[13] = EntityType.ZOMBIE;
        entityTypeArr[14] = EntityType.CREEPER;
        entityTypeArr[15] = EntityType.SKELETON;
        entityTypeArr[16] = EntityType.SPIDER;
        entityTypeArr[17] = EntityType.PIG_ZOMBIE;
        entityTypeArr[18] = EntityType.SLIME;
        entityTypeArr[19] = EntityType.ENDERMAN;
        entityTypeArr[20] = EntityType.SILVERFISH;
        entityTypeArr[21] = EntityType.CAVE_SPIDER;
        entityTypeArr[22] = EntityType.GHAST;
        entityTypeArr[23] = EntityType.LAVA_SLIME;
        entityTypeArr[24] = EntityType.BLAZE;
        entityTypeArr[25] = EntityType.ZOMBIE_VILLAGER;
        entityTypeArr[26] = EntityType.WITCH;
		
		/*entityTypeArr[27] = EntityType.STRAY;
		entityTypeArr[28] = EntityType.HUSK;
		entityTypeArr[29] = EntityType.HORSE;
		entityTypeArr[30] = EntityType.SKELETON_HORSE;
		entityTypeArr[31] = EntityType.ZOMBIE_HORSE;
		entityTypeArr[32] = EntityType.MULE;
		entityTypeArr[33] = EntityType.DONKEY;
		entityTypeArr[34] = EntityType.GUARD;
		entityTypeArr[35] = EntityType.ELDERGUARDIAN;
		entityTypeArr[36] = EntityType.WITHER;
		entityTypeArr[37] = EntityType.ENDERMITE;
		entityTypeArr[38] = EntityType.SHULKER;
		entityTypeArr[39] = EntityType.POLARBEAR;
		entityTypeArr[40] = EntityType.ENDERDRAGON;
		entityTypeArr[41] = EntityType.EVOKER;
		entityTypeArr[42] = EntityType.VINDICATOR;
		entityTypeArr[43] = EntityType.LLAMA_CREAMY;
		entityTypeArr[44] = EntityType.VEX;
		entityTypeArr[45] = EntityType.PARROT;
		entityTypeArr[46] = EntityType.COD;
		entityTypeArr[47] = EntityType.SALMON;
		entityTypeArr[48] = EntityType.PUFFERFISH;
		entityTypeArr[49] = EntityType.TROPICALFISH;
		entityTypeArr[50] = EntityType.DOLPHIN;
		entityTypeArr[51] = EntityType.TURTLE_EGG;
		entityTypeArr[52] = EntityType.DROWNED;
		entityTypeArr[53] = EntityType.PHANTOM;
		entityTypeArr[54] = EntityType.PANDA;
		entityTypeArr[55] = EntityType.CAT;*/
        mobTypes = entityTypeArr;
    }

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        spawnMobsButton = findViewById(R.id.entities_spawn_mobs);
        spawnMobsButton.setOnClickListener(this);
        spawnDropsButton = findViewById(R.id.entities_spawn_drops);
        spawnDropsButton.setOnClickListener(this);
        removeEntitiesButton = findViewById(R.id.entities_remove_entities);
        removeEntitiesButton.setOnClickListener(this);
        cowTippingButton = findViewById(R.id.entities_cow_tipping);
        cowTippingButton.setOnClickListener(this);
        apoCowlypseButton = findViewById(R.id.entities_apocowlypse);
        apoCowlypseButton.setOnClickListener(this);
        babyfyButton = findViewById(R.id.entities_babyfy_mobs);
        babyfyButton.setOnClickListener(this);
        replaceEntitiesButton = findViewById(R.id.entities_replace_entities);
        replaceEntitiesButton.setOnClickListener(this);
        dyeAllSheepButton = findViewById(R.id.entities_dye_all_sheep);
        dyeAllSheepButton.setOnClickListener(this);
        ageAllAnimalsButton = findViewById(R.id.entities_age_all_animals);
        ageAllAnimalsButton.setOnClickListener(this);
        removeAllEntitiesButton = findViewById(R.id.entities_remove_all_entities);
        removeAllEntitiesButton.setOnClickListener(this);
    }

    public void onClick(View v) {
        if (v == spawnMobsButton) {
            showDialog(DIALOG_SPAWN_MOBS);
        } else if (v == removeEntitiesButton) {
            showDialog(DIALOG_REMOVE_ENTITIES);
        } else if (v == apoCowlypseButton) {
            apoCowlypse();
        } else if (v == cowTippingButton) {
            showDialog(DIALOG_MOB_TIPPING);
        } else if (v == babyfyButton) {
            showDialog(DIALOG_BABYFY_MOBS);
        } else if (v == replaceEntitiesButton) {
            showDialog(DIALOG_REPLACE_ENTITIES);
        } else if (v == dyeAllSheepButton) {
            showDialog(DIALOG_DYE_ALL_SHEEP);
        } else if (v == ageAllAnimalsButton) {
            showDialog(DIALOG_AGE_MOBS);
        } else if (v == removeAllEntitiesButton) {
            removeAllEntities();
            EntitiesInfoActivity.save(this);
            countEntities();
        } else {
            super.onClick(v);
        }
    }

    public Dialog onCreateDialog(int dialogId) {
        switch (dialogId) {
            case DIALOG_SPAWN_MOBS:
                return createSpawnMobsDialog();
            case DIALOG_REMOVE_ENTITIES:
                return createRemoveEntitiesDialog();
            case DIALOG_BABYFY_MOBS:
                return createBabyfyMobsDialog(false);
            case DIALOG_MOB_TIPPING:
                return createMobTippingDialog();
            case DIALOG_REPLACE_ENTITIES:
                return createReplaceEntitiesDialog();
            case DIALOG_DYE_ALL_SHEEP:
                return createDyeAllSheepDialog();
            case DIALOG_AGE_MOBS:
                return createBabyfyMobsDialog(true);
            case DIALOG_BREED_ALL_ANIMALS:
                return createBreedAllAnimalsDialog();
            default:
                return super.onCreateDialog(dialogId);
        }
    }

    protected Dialog createSpawnMobsDialog() {
        View textEntryView = getLayoutInflater().inflate(R.layout.entities_spawn_dialog, null);
        EntitiesNameArrays.setSpinner((Spinner) textEntryView.findViewById(R.id.entities_type_spinner), EntitiesNameArrays.getMobTypes(this));
        return new Builder(this).setTitle(R.string.entities_spawn_mobs).setView(textEntryView).setPositiveButton(android.R.string.ok, new OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                AlertDialog dialog = (AlertDialog) dialogI;
                try {
                    float x = Float.parseFloat(((EditText) dialog.findViewById(R.id.entities_spawn_x)).getText().toString());
                    float y = Float.parseFloat(((EditText) dialog.findViewById(R.id.entities_spawn_y)).getText().toString());
                    float z = Float.parseFloat(((EditText) dialog.findViewById(R.id.entities_spawn_z)).getText().toString());
                    int count = Integer.parseInt(((EditText) dialog.findViewById(R.id.entities_spawn_count)).getText().toString());
                    spawnMobs(EntitiesEditActivity.mobTypes[((Spinner) dialog.findViewById(R.id.entities_type_spinner)).getSelectedItemPosition()], new Vector3f(x, y, z), count);
                    EntitiesInfoActivity.save(EntitiesEditActivity.this);
                    countEntities();
                } catch (NumberFormatException e) {
                    Toast.makeText(EntitiesEditActivity.this, R.string.invalid_number, 0).show();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
			}).setNegativeButton(android.R.string.cancel, null).create();
    }

    protected Dialog createRemoveEntitiesDialog() {
        View textEntryView = getLayoutInflater().inflate(R.layout.entities_remove_dialog, null);
        EntitiesNameArrays.setSpinner((Spinner) textEntryView.findViewById(R.id.entities_type_spinner), EntitiesNameArrays.getAllNames(this));
        return new Builder(this).setTitle(R.string.entities_remove_entities).setView(textEntryView).setPositiveButton(android.R.string.ok, new OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                try {
                    removeEntities(EntityType.values()[((Spinner) ((AlertDialog) dialogI).findViewById(R.id.entities_type_spinner)).getSelectedItemPosition()]);
                    EntitiesInfoActivity.save(EntitiesEditActivity.this);
                    countEntities();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
			}).setNegativeButton(android.R.string.cancel, null).create();
    }

    protected Dialog createBabyfyMobsDialog(boolean age) {
        View textEntryView = getLayoutInflater().inflate(R.layout.entities_babyfy_dialog, null);
        final int ageTicks = age ? 0 : EntitiesInfoActivity.BABY_GROWTH_TICKS;
        return new Builder(this).setTitle(age ? R.string.entities_age_all_animals : R.string.entities_babyfy_mobs).setView(textEntryView).setPositiveButton(android.R.string.ok, new OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                try {
                    setAllAnimalsAge(EntitiesEditActivity.mobTypes[((Spinner) ((AlertDialog) dialogI).findViewById(R.id.entities_type_spinner)).getSelectedItemPosition()], ageTicks);
                    EntitiesInfoActivity.save(EntitiesEditActivity.this);
                    countEntities();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
			}).setNegativeButton(android.R.string.cancel, null).create();
    }

    protected Dialog createMobTippingDialog() {
        View textEntryView = getLayoutInflater().inflate(R.layout.entities_tip_dialog, null);
        EntitiesNameArrays.setSpinner((Spinner) textEntryView.findViewById(R.id.entities_type_spinner), EntitiesNameArrays.getMobTypes(this));
        return new Builder(this).setTitle(R.string.entities_mob_tipping).setView(textEntryView).setPositiveButton(android.R.string.ok, new OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                AlertDialog dialog = (AlertDialog) dialogI;
                try {
                    cowTipping(EntitiesEditActivity.mobTypes[((Spinner) dialog.findViewById(R.id.entities_type_spinner)).getSelectedItemPosition()], (short) ((SeekBar) dialog.findViewById(R.id.entities_tip_amount)).getProgress());
                    EntitiesInfoActivity.save(EntitiesEditActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
			}).setNegativeButton(android.R.string.cancel, null).create();
    }

    protected Dialog createReplaceEntitiesDialog() {
        View textEntryView = getLayoutInflater().inflate(R.layout.entities_replace_dialog, null);
        EntitiesNameArrays.setSpinner((Spinner) textEntryView.findViewById(R.id.entities_type_spinner), EntitiesNameArrays.getReplaceFromTypes(this));
        EntitiesNameArrays.setSpinner((Spinner) textEntryView.findViewById(R.id.entities_replace_type_spinner), EntitiesNameArrays.getReplaceToTypes(this));
        return new Builder(this).setTitle(R.string.entities_replace_entities).setView(textEntryView).setPositiveButton(android.R.string.ok, new OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                AlertDialog dialog = (AlertDialog) dialogI;
                int toLoc = ((Spinner) dialog.findViewById(R.id.entities_type_spinner)).getSelectedItemPosition();
                EntityType type = null;
                if (toLoc < EntityType.UNKNOWN.ordinal()) {
                    type = EntityType.values()[toLoc];
                }
                int toTypeIndex = ((Spinner) dialog.findViewById(R.id.entities_replace_type_spinner)).getSelectedItemPosition();
                if (toTypeIndex >= EntityType.ITEM.ordinal()) {
                    toTypeIndex++;
                }
                try {
                    replaceEntities(type, EntityType.values()[toTypeIndex]);
                    EntitiesInfoActivity.save(EntitiesEditActivity.this);
                    countEntities();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
			}).setNegativeButton(android.R.string.cancel, null).create();
    }

    protected Dialog createDyeAllSheepDialog() {
        final WoolColorAdapter adapter = new WoolColorAdapter(this, R.layout.entities_dye_dialog_item, android.R.id.text1);
        return new Builder(this).setTitle(R.string.entities_dye_all_sheep).setSingleChoiceItems(adapter, 0, new OnClickListener() {
            public void onClick(DialogInterface dialogI, int item) {
                try {
                    dyeAllSheep(adapter.getItem(item).getWoolData());
                    EntitiesInfoActivity.save(EntitiesEditActivity.this);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                dialogI.dismiss();
            }
        }).create();
    }

    protected Dialog createBreedAllAnimalsDialog() {
        return new Builder(this).setTitle(R.string.entities_breed_all_animals).setView(getLayoutInflater().inflate(R.layout.entities_babyfy_dialog, null)).setPositiveButton(android.R.string.ok, new OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                try {
                    setAllBreeding(EntitiesEditActivity.mobTypes[((Spinner) ((AlertDialog) dialogI).findViewById(R.id.entities_type_spinner)).getSelectedItemPosition()], EntitiesInfoActivity.BREED_TICKS);
                    EntitiesInfoActivity.save(EntitiesEditActivity.this);
                    countEntities();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
			}).setNegativeButton(android.R.string.cancel, null).create();
    }

    public void onPrepareDialog(int dialogId, Dialog dialog) {
        switch (dialogId) {
            case DIALOG_SPAWN_MOBS:
                Vector3f playerLoc = EditorActivity.level.getPlayer().getLocation();
                ((EditText) dialog.findViewById(R.id.entities_spawn_x)).setText(Float.toString(playerLoc.getX()));
                ((EditText) dialog.findViewById(R.id.entities_spawn_y)).setText(Float.toString(playerLoc.getY()));
                ((EditText) dialog.findViewById(R.id.entities_spawn_z)).setText(Float.toString(playerLoc.getZ()));
                return;
            default:
                super.onPrepareDialog(dialogId, dialog);
                return;
        }
    }
}
