package com.mcal.pocketinveditor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import com.mcal.pocketinveditor.entity.Player;
import com.mcal.pocketinveditor.entity.PlayerAbilities;
import com.mcal.pocketinveditor.pro.R;
import com.mcal.pocketinveditor.util.Vector3f;
import java.io.File;
import java.io.FileOutputStream;

public final class WorldInfoActivity extends Activity implements OnClickListener, OnFocusChangeListener, OnItemSelectedListener, LevelDataLoadListener {
    public static final int DAY_LENGTH = 19200;
    private static final int DIALOG_CHANGE_GAME_MODE = 1167366;
    private static final int DIALOG_MOVE_PLAYER = 4142;
    private static final String[] GAMEMODES = new String[]{"Survival", "Creative"};
    private TextView dayCycleStopTimeText;
    private CheckBox flyingBox;
    private Button fullHealthButton;
    private Button gameModeChangeButton;
    private TextView gameModeText;
    private Spinner generatorSpinner;
    private EditText healthText;
    private Button infiniteHealthButton;
    private CheckBox instaBuildBox;
    private CheckBox invulnerableBox;
    private CheckBox mayFlyBox;
    private Button movePlayerButton;
    private TextView playerXText;
    private TextView playerYText;
    private TextView playerZText;
    private Button sidewaysOffButton;
    private Button sidewaysOnButton;
    private CheckBox spawnMobsBox;
    private Button spawnToPlayerButton;
    private Button timeToMorningButton;
    private Button timeToNightButton;
    private Button warpToSpawnButton;
    private TextView worldFolderNameText;
    private TextView worldNameText;
    private EditText worldTimeText;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.world_info);
        gameModeText = findViewById(R.id.world_info_gamemode);
        gameModeChangeButton = findViewById(R.id.world_info_gamemode_change);
        gameModeChangeButton.setOnClickListener(this);
        worldTimeText = findViewById(R.id.world_info_time_text);
        worldTimeText.setOnFocusChangeListener(this);
        spawnToPlayerButton = findViewById(R.id.world_info_spawn_to_player_button);
        spawnToPlayerButton.setOnClickListener(this);
        warpToSpawnButton = findViewById(R.id.world_info_warp_to_spawn_button);
        warpToSpawnButton.setOnClickListener(this);
        timeToMorningButton = findViewById(R.id.world_info_time_to_morning);
        timeToMorningButton.setOnClickListener(this);
        timeToNightButton = findViewById(R.id.world_info_time_to_night);
        timeToNightButton.setOnClickListener(this);
        playerXText = findViewById(R.id.world_info_player_x);
        playerYText = findViewById(R.id.world_info_player_y);
        playerZText = findViewById(R.id.world_info_player_z);
        healthText = findViewById(R.id.world_info_health);
        fullHealthButton = findViewById(R.id.world_info_full_health);
        infiniteHealthButton = findViewById(R.id.world_info_infinite_health);
        healthText.setOnFocusChangeListener(this);
        fullHealthButton.setOnClickListener(this);
        infiniteHealthButton.setOnClickListener(this);
        sidewaysOnButton = findViewById(R.id.world_info_sideways_on);
        sidewaysOffButton = findViewById(R.id.world_info_sideways_off);
        sidewaysOnButton.setOnClickListener(this);
        sidewaysOffButton.setOnClickListener(this);
        worldNameText = findViewById(R.id.world_info_name);
        worldNameText.setOnFocusChangeListener(this);
        worldFolderNameText = findViewById(R.id.world_info_folder_name);
        worldFolderNameText.setOnFocusChangeListener(this);
        movePlayerButton = findViewById(R.id.world_info_move_player);
        movePlayerButton.setOnClickListener(this);
        flyingBox = findViewById(R.id.world_info_flying);
        flyingBox.setOnClickListener(this);
        invulnerableBox = findViewById(R.id.world_info_invulnerable);
        invulnerableBox.setOnClickListener(this);
        instaBuildBox = findViewById(R.id.world_info_insta_build);
        instaBuildBox.setOnClickListener(this);
        mayFlyBox = findViewById(R.id.world_info_may_fly);
        mayFlyBox.setOnClickListener(this);
        dayCycleStopTimeText = findViewById(R.id.world_info_day_cycle_stop_time_text);
        dayCycleStopTimeText.setOnFocusChangeListener(this);
        spawnMobsBox = findViewById(R.id.world_info_spawn_mobs);
        spawnMobsBox.setOnClickListener(this);
        generatorSpinner = findViewById(R.id.world_info_generator);
        generatorSpinner.setOnItemSelectedListener(this);
    }

    public void onStart() {
        super.onStart();
        EditorActivity.loadLevelData(this, this, getIntent().getStringExtra("world"));
    }

    public void onLevelDataLoad() {
        gameModeText.setText(EditorActivity.level.getGameType() == 1 ? R.string.gamemode_creative : R.string.gamemode_survival);
        updateTimeText();
        updatePlayerPositionText();
        updatePlayerHealthText();
        worldNameText.setText(EditorActivity.level.getLevelName());
        worldFolderNameText.setText(EditorActivity.worldFolder.getName());
        updatePlayerAbilitiesCheckBoxes();
        dayCycleStopTimeText.setText(Long.toString((long) EditorActivity.level.getDayCycleStopTime()));
        spawnMobsBox.setChecked(EditorActivity.level.getSpawnMobs());
        generatorSpinner.setSelection(EditorActivity.level.getGenerator());
        updateForGameMode();
    }

    public void updateTimeText() {
        worldTimeText.setText(Long.toString(EditorActivity.level.getTime()));
    }

    public void updatePlayerPositionText() {
        Vector3f loc = EditorActivity.level.getPlayer().getLocation();
        playerXText.setText("X: " + Float.toString(loc.getX()));
        playerYText.setText("Y: " + Float.toString(loc.getY()));
        playerZText.setText("Z: " + Float.toString(loc.getZ()));
    }

    private void setSpawnToPlayerPosition() {
        Level level = EditorActivity.level;
        Vector3f loc = level.getPlayer().getLocation();
        level.setSpawnX((int) loc.getX());
        level.setSpawnY((int) loc.getY());
        level.setSpawnZ((int) loc.getZ());
        Player player = level.getPlayer();
        player.setSpawnX((int) loc.getX());
        player.setSpawnY((int) loc.getY());
        player.setSpawnZ((int) loc.getZ());
        player.setBedPositionX((int) loc.getX());
        player.setBedPositionY((int) loc.getY());
        player.setBedPositionZ((int) loc.getZ());
    }

    private void setTimeToMorning() {
        EditorActivity.level.setTime((EditorActivity.level.getTime() / 19200) * 19200);
        EditorActivity.save(this);
    }

    private void setTimeToNight() {
        EditorActivity.level.setTime(((EditorActivity.level.getTime() / 19200) * 19200) + 9600);
        EditorActivity.save(this);
    }

    public void updatePlayerHealthText() {
        this.healthText.setText(Short.toString(EditorActivity.level.getPlayer().getHealth()));
    }

    private void setPlayerHealthToFull() {
        EditorActivity.level.getPlayer().setHealth((short) 20);
        EditorActivity.save(this);
        updatePlayerHealthText();
    }

    private void setPlayerHealthToInfinite() {
        EditorActivity.level.getPlayer().setHealth(Short.MAX_VALUE);
        EditorActivity.save(this);
        updatePlayerHealthText();
    }

    private void warpPlayerToSpawn() {
        Vector3f loc = EditorActivity.level.getPlayer().getLocation();
        Level level = EditorActivity.level;
        loc.setX((float) level.getSpawnX());
        loc.setY((float) level.getSpawnY());
        loc.setZ((float) level.getSpawnZ());
        EditorActivity.save(this);
        updatePlayerPositionText();
    }

    private void playerSideways(boolean doThis) {
        EditorActivity.level.getPlayer().setDeathTime(doThis ? Short.MAX_VALUE : (short) 0);
        EditorActivity.save(this);
    }

    private void updatePlayerAbilitiesCheckBoxes() {
        PlayerAbilities abilities = EditorActivity.level.getPlayer().getAbilities();
        flyingBox.setChecked(abilities.flying);
        invulnerableBox.setChecked(abilities.invulnerable);
        instaBuildBox.setChecked(abilities.instabuild);
        mayFlyBox.setChecked(abilities.mayFly);
    }

    public void onClick(View v) {
        if (v == gameModeChangeButton) {
            showDialog(DIALOG_CHANGE_GAME_MODE);
        } else if (v == spawnToPlayerButton) {
            setSpawnToPlayerPosition();
            EditorActivity.save(this);
        } else if (v == timeToMorningButton) {
            setTimeToMorning();
            updateTimeText();
        } else if (v == timeToNightButton) {
            setTimeToNight();
            updateTimeText();
        } else if (v == fullHealthButton) {
            setPlayerHealthToFull();
        } else if (v == infiniteHealthButton) {
            setPlayerHealthToInfinite();
        } else if (v == warpToSpawnButton) {
            warpPlayerToSpawn();
        } else if (v == sidewaysOnButton) {
            playerSideways(true);
        } else if (v == sidewaysOffButton) {
            playerSideways(false);
        } else if (v == movePlayerButton) {
            showDialog(DIALOG_MOVE_PLAYER);
        } else if (v == flyingBox) {
            EditorActivity.level.getPlayer().getAbilities().flying = flyingBox.isChecked();
            EditorActivity.save(this);
        } else if (v == invulnerableBox) {
            EditorActivity.level.getPlayer().getAbilities().invulnerable = invulnerableBox.isChecked();
            EditorActivity.save(this);
        } else if (v == instaBuildBox) {
            EditorActivity.level.getPlayer().getAbilities().instabuild = instaBuildBox.isChecked();
            EditorActivity.save(this);
        } else if (v == mayFlyBox) {
            EditorActivity.level.getPlayer().getAbilities().mayFly = mayFlyBox.isChecked();
            EditorActivity.save(this);
        } else if (v == spawnMobsBox) {
            EditorActivity.level.setSpawnMobs(instaBuildBox.isChecked());
            EditorActivity.save(this);
        }
    }

    public Dialog onCreateDialog(int dialogId) {
        switch (dialogId) {
            case DIALOG_MOVE_PLAYER:
                return createMovePlayerDialog();
            case DIALOG_CHANGE_GAME_MODE:
                return createChangeGameModeDialog();
            default:
                return super.onCreateDialog(dialogId);
        }
    }

    public void onPrepareDialog(int dialogId, Dialog dialog) {
        int levelType = 1;
        switch (dialogId) {
            case DIALOG_MOVE_PLAYER:
                Vector3f playerLoc = EditorActivity.level.getPlayer().getLocation();
                ((EditText) dialog.findViewById(R.id.entities_spawn_x)).setText(Float.toString(playerLoc.getX()));
                ((EditText) dialog.findViewById(R.id.entities_spawn_y)).setText(Float.toString(playerLoc.getY()));
                ((EditText) dialog.findViewById(R.id.entities_spawn_z)).setText(Float.toString(playerLoc.getZ()));
                return;
            case DIALOG_CHANGE_GAME_MODE:
                if (EditorActivity.level.getGameType() != 1) {
                    levelType = 0;
                }
                ((AlertDialog) dialog).getListView().setSelection(levelType);
                return;
            default:
                super.onPrepareDialog(dialogId, dialog);
                return;
        }
    }

    protected AlertDialog createChangeGameModeDialog() {
        return new Builder(this).setTitle(R.string.gamemode).setSingleChoiceItems(GAMEMODES, -1, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int item) {
                EditorActivity.level.setGameType(item);
                gameModeText.setText(EditorActivity.level.getGameType() == 1 ? R.string.gamemode_creative : R.string.gamemode_survival);
                EditorActivity.level.getPlayer().getAbilities().initForGameType(item);
                EditorActivity.save(WorldInfoActivity.this);
                updatePlayerAbilitiesCheckBoxes();
                updateForGameMode();
                dialog.dismiss();
            }
        }).create();
    }

    protected Dialog createMovePlayerDialog() {
        return new Builder(this).setTitle(R.string.world_info_move_player).setView(getLayoutInflater().inflate(R.layout.move_player_dialog, null)).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                try {
                    AlertDialog dialog = (AlertDialog) dialogI;
                    EditorActivity.level.getPlayer().setLocation(new Vector3f(Float.parseFloat(((EditText) dialog.findViewById(R.id.entities_spawn_x)).getText().toString()), Float.parseFloat(((EditText) dialog.findViewById(R.id.entities_spawn_y)).getText().toString()), Float.parseFloat(((EditText) dialog.findViewById(R.id.entities_spawn_z)).getText().toString())));
                    EditorActivity.save(WorldInfoActivity.this);
                    updatePlayerPositionText();
                } catch (NumberFormatException e) {
                    Toast.makeText(WorldInfoActivity.this, R.string.invalid_number, 0).show();
                } catch (Exception e2) {
                    e2.printStackTrace();
                }
            }
			}).setNegativeButton(android.R.string.cancel, null).create();
    }

    public void onFocusChange(View v, boolean hasFocus) {
        if (v == worldTimeText) {
            if (!hasFocus) {
                checkTimeInputAfterChange();
            }
        } else if (v == healthText) {
            if (!hasFocus) {
                checkHealthInputAfterChange();
            }
        } else if (v == worldNameText) {
            if (!hasFocus) {
                checkWorldNameAfterChange();
            }
        } else if (v == worldFolderNameText) {
            if (!hasFocus) {
                checkWorldFolderNameAfterChange();
            }
        } else if (v == dayCycleStopTimeText && !hasFocus) {
            checkStopTimeInputAfterChange();
        }
    }

    public void onPause() {
        super.onPause();
        checkTimeInputAfterChange();
        checkHealthInputAfterChange();
        checkWorldNameAfterChange();
        checkWorldFolderNameAfterChange();
        checkStopTimeInputAfterChange();
    }

    public void checkTimeInputAfterChange() {
        try {
            long newTime = Long.parseLong(worldTimeText.getText().toString());
            this.worldTimeText.setError(null);
            if (newTime != EditorActivity.level.getTime()) {
                EditorActivity.level.setTime(newTime);
                EditorActivity.save(this);
            }
        } catch (NumberFormatException e) {
            this.worldTimeText.setError(getResources().getText(R.string.invalid_number));
        }
    }

    public void checkHealthInputAfterChange() {
        try {
            short newHealth = Short.parseShort(healthText.getText().toString());
            healthText.setError(null);
            if (newHealth != EditorActivity.level.getPlayer().getHealth()) {
                EditorActivity.level.getPlayer().setHealth(newHealth);
                EditorActivity.save(this);
            }
        } catch (NumberFormatException e) {
            this.healthText.setError(getResources().getText(R.string.invalid_number));
        }
    }

    protected void checkWorldNameAfterChange() {
        String newText = worldNameText.getText().toString();
        if (!newText.equals(EditorActivity.level.getLevelName())) {
            EditorActivity.level.setLevelName(newText);
            EditorActivity.save(this);
            try {
                FileOutputStream fos = new FileOutputStream(new File(EditorActivity.worldFolder, "levelname.txt"));
                fos.write(newText.getBytes("UTF-8"));
                fos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    protected void checkWorldFolderNameAfterChange() {
        String newText = worldFolderNameText.getText().toString();
        if (!newText.equals(EditorActivity.worldFolder.getName())) {
            File newLoc = new File(EditorActivity.worldFolder.getParentFile(), newText);
            if (newLoc.exists()) {
                worldFolderNameText.setError(getResources().getText(R.string.folder_exists));
                return;
            }
            this.worldFolderNameText.setError(null);
            if (EditorActivity.worldFolder.renameTo(newLoc)) {
                Toast.makeText(this, R.string.saved, 0).show();
                EditorActivity.worldFolder = newLoc;
                return;
            }
            worldFolderNameText.setError(getResources().getText(R.string.folder_rename_failed));
        }
    }

    public void checkStopTimeInputAfterChange() {
        try {
            int newTime = Integer.parseInt(dayCycleStopTimeText.getText().toString());
            dayCycleStopTimeText.setError(null);
            if (newTime != EditorActivity.level.getDayCycleStopTime()) {
                EditorActivity.level.setDayCycleStopTime(newTime);
                EditorActivity.save(this);
            }
        } catch (NumberFormatException e) {
            dayCycleStopTimeText.setError(getResources().getText(R.string.invalid_number));
        }
    }

    private void updateForGameMode() {
        boolean creative = true;
        if (EditorActivity.level.getGameType() != 1) {
            creative = false;
        }
        dayCycleStopTimeText.setEnabled(creative);
        spawnMobsBox.setEnabled(creative);
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == generatorSpinner && position != EditorActivity.level.getGenerator()) {
            EditorActivity.level.setGenerator(position);
            EditorActivity.save(this);
        }
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }
}
