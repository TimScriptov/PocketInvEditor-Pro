package com.mcal.pocketinveditor.pro;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Process;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import com.ipaulpro.afilechooser.FileChooserActivity;
import com.ipaulpro.afilechooser.utils.FileUtils;
import com.mcal.pocketinveditor.EditorActivity;
import com.mcal.pocketinveditor.EntitiesInfoActivity.LoadEntitiesTask;
import com.mcal.pocketinveditor.EntityDataLoadListener;
import com.mcal.pocketinveditor.entity.Entity;
import com.mcal.pocketinveditor.entity.Player;
import com.mcal.pocketinveditor.geo.Chunk;
import com.mcal.pocketinveditor.geo.ChunkManager;
import com.mcal.pocketinveditor.geo.CuboidClipboard;
import com.mcal.pocketinveditor.geo.CuboidRegion;
import com.mcal.pocketinveditor.geo.GeoUtils;
import com.mcal.pocketinveditor.io.EntityDataConverter.EntityData;
import com.mcal.pocketinveditor.io.nbt.schematic.SchematicIO;
import com.mcal.pocketinveditor.material.MaterialCount;
import com.mcal.pocketinveditor.material.MaterialKey;
import com.mcal.pocketinveditor.util.Vector3f;
import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class EditTerrainActivity extends Activity implements OnClickListener, EntityDataLoadListener {
    public static final int CHOOSE_AUTO_SELECT_BLOCK_REQUEST = 3;
    public static final int CHOOSE_CYLINDER_BLOCK_REQUEST = 12;
    public static final int CHOOSE_HOLLOW_CUBOID_BLOCK_REQUEST = 9;
    public static final int CHOOSE_HOLLOW_DOME_BLOCK_REQUEST = 7;
    public static final int CHOOSE_HOLLOW_PYRAMID_BLOCK_REQUEST = 10;
    public static final int CHOOSE_HOLLOW_SPHERE_BLOCK_REQUEST = 5;
    public static final int CHOOSE_REPLACE_BLOCK_REQUEST = 1;
    public static final int CHOOSE_SCHEMATIC_REQUEST = 4;
    public static final int CHOOSE_SET_BLOCK_REQUEST = 2;
    public static final int CHOOSE_SOLID_DOME_BLOCK_REQUEST = 8;
    public static final int CHOOSE_SOLID_PYRAMID_BLOCK_REQUEST = 11;
    public static final int CHOOSE_SOLID_SPHERE_BLOCK_REQUEST = 6;
    public static final String CLIPBOARD_BACKUP_SCHEMATIC = "clipboard.schematic";
    private static final int DIALOG_FIX_TERRAIN_HOLES = 5;
    private static final int DIALOG_NAME_SCHEMATIC = 4;
    private static final int DIALOG_PROGRESS = 1908;
    private static final int DIALOG_REGION_OUT_OF_WORLD = 1361179;
    private static final int DIALOG_SHIFT_REGION = 3;
    public static final float PLAYER_HEIGHT = 1.62f;
    public static final String SCHEMATIC_MIME = "application/x-schematic";
	public static final String FORUMS_PAGE_URL = "http://www.minecraftforum.net/topic/1167366-wip-pocketinveditor-a-minecraft-pe-inventory-editor-for-android/";
    public static CuboidClipboard clipboard;
    private static boolean isBusy = false;
    private Button analyzeButton;
    private Button autoSelectButton;
    private Button burnBabyBurnButton;
    private Button coordsToPlayerButton;
    private Button coordsToPlayerFeetButton;
    private Button copyButton;
    private Button cylinderButton;
    private Button expandButton;
    private Button fixTerrainHolesButton;
    private Button goToForumsButton;
    private TextView heightText;
    private Button hollowCuboidButton;
    private Button hollowDomeButton;
    private Button hollowPyramidButton;
    private Button hollowSphereButton;
    private TextView lengthText;
    private TextView locXText;
    private TextView locYText;
    private TextView locZText;
    private Button mapAreaButton;
    private ChunkManager mgr;
    private Button pasteButton;
    private Button pasteNoAirButton;
    private Button replaceBlocksButton;
    private Button schematicExportButton;
    private Button schematicImportButton;
    private Button selectWholeWorldButton;
    private Button setBlocksButton;
    private Button shiftButton;
    private Button solidDomeButton;
    private Button solidPyramidButton;
    private Button solidSphereButton;
    private TextView widthText;

    private class AnalyzeRegionTask implements Runnable {
        public CuboidRegion region;

        public void run() {
            finishTask(GeoUtils.countBlocks(getChunkManager(), region));
        }

        private void finishTask(final Map<MaterialKey, MaterialCount> blockCounts) {
            runOnUiThread(new Runnable() {
                public void run() {
                    analyzeRegionCallback(blockCounts);
                }
            });
        }
    }

    private class AutoSelectTask implements Runnable {
        public MaterialKey material;

        public void run() {
            System.out.println("Material: " + material);
            ChunkManager mgr = getChunkManager();
            Vector3f[] locations = new Vector3f[CHOOSE_SET_BLOCK_REQUEST];
            int index = 0;
            for (int x = 0; x < ChunkManager.WORLD_WIDTH; x += CHOOSE_REPLACE_BLOCK_REQUEST) {
                for (int z = 0; z < ChunkManager.WORLD_WIDTH; z += CHOOSE_REPLACE_BLOCK_REQUEST) {
                    for (int y = 0; y < ChunkManager.WORLD_HEIGHT; y += CHOOSE_REPLACE_BLOCK_REQUEST) {
                        short blockId = (short) mgr.getBlockTypeId(x, y, z);
                        short data = (short) mgr.getBlockData(x, y, z);
                        if (blockId == material.typeId && (material.damage == data || material.damage == (short) -1)) {
                            locations[index] = new Vector3f((float) x, (float) y, (float) z);
                            System.out.println("Got one: " + x + ":" + y + ":" + z);
                            index += EditTerrainActivity.CHOOSE_REPLACE_BLOCK_REQUEST;
                            if (index == EditTerrainActivity.CHOOSE_SET_BLOCK_REQUEST) {
                                finishTask(locations);
                                return;
                            }
                        }
                    }
                }
            }
            finishTask(null);
        }

        private void finishTask(final Vector3f[] result) {
            runOnUiThread(new Runnable() {
                public void run() {
                    autoSelectResultCallback(result);
                }
            });
        }
    }

    private class CopyTask implements Runnable {
        public CuboidRegion region;

        public void run() {
            ChunkManager mgr = getChunkManager();
            CuboidClipboard clipboard = new CuboidClipboard(region.getSize());
            clipboard.copy(mgr, region.getPosition());
            finishTask(clipboard);
        }

        private void finishTask(final CuboidClipboard clipboard) {
            runOnUiThread(new Runnable() {
                public void run() {
                    copyCallback(clipboard);
                }
            });
        }
    }

    private class CountComparator implements Comparator<MaterialCount> {
        public int compare(MaterialCount a, MaterialCount b) {
            if (a.count == b.count) {
                return 0;
            }
            return a.count > b.count ? -1 : CHOOSE_REPLACE_BLOCK_REQUEST;
        }

        public boolean equals(MaterialCount a, MaterialCount b) {
            return a.count == b.count;
        }
    }

    private class DiscoInfernoTask implements Runnable {
        public void run() {
            discoInferno();
            runOnUiThread(new Runnable() {
                public void run() {
                    doneDisco();
                }
            });
        }
    }

    private class FixTerrainHolesTask implements Runnable {
        public void run() {
            ChunkManager mgr = getChunkManager();
            for (int x = 0; x < 16; x += CHOOSE_REPLACE_BLOCK_REQUEST) {
                for (int z = 0; z < 16; z += CHOOSE_REPLACE_BLOCK_REQUEST) {
                    Chunk chunk = mgr.getChunk(x, z);
                    chunk.setDirtyTable(0, 0, 0);
                    chunk.setNeedsSaving(true);
                }
            }
            try {
                mgr.saveAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
            finishTask();
        }

        private void finishTask() {
            runOnUiThread(new Runnable() {
                public void run() {
                    fixTerrainHolesCallback();
                }
            });
        }
    }

    private class MakeSphereTask implements Runnable {
        public Vector3f center;
        public boolean dome;
        public int height;
        public boolean hollow;
        public int radius;
        public ShapeType shape;
        public MaterialKey to;

        private MakeSphereTask() {
            dome = false;
            shape = ShapeType.SPHERE;
            height = 0;
        }

        public void run() {
            ChunkManager mgr = getChunkManager();
            int replacedCount = 0;
            if (shape == ShapeType.SPHERE || shape == ShapeType.DOME) {
                if (dome) {
                    replacedCount = GeoUtils.makeDome(mgr, center, radius, to, hollow);
                } else {
                    replacedCount = GeoUtils.makeSphere(mgr, center, radius, to, hollow);
                }
            } else if (shape == ShapeType.PYRAMID) {
                replacedCount = GeoUtils.makePyramid(mgr, center, radius, to, hollow);
            } else if (shape == ShapeType.CYLINDER) {
                replacedCount = GeoUtils.makeCylinder(mgr, center, radius, height, to, hollow);
            }
            try {
                mgr.saveAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
            finishTask(replacedCount);
        }

        private void finishTask(final int replacedCount) {
            runOnUiThread(new Runnable() {
                public void run() {
                    makeSphereCallback(replacedCount);
                }
            });
        }
    }

    private class MapAreaTask implements Runnable {
        public Player player;
        public CuboidRegion region;
        public boolean solid;
        public MaterialKey to;
        public int widthPerBlock;

        private MapAreaTask() {
            solid = true;
            widthPerBlock = DIALOG_NAME_SCHEMATIC;
            player = null;
        }

        public void run() {
            ChunkManager mgr = getChunkManager();
            Bitmap map = Bitmap.createBitmap(region.width * widthPerBlock, region.length * widthPerBlock, Config.ARGB_8888);
            for (int x = 0; x < region.width; x += CHOOSE_REPLACE_BLOCK_REQUEST) {
                for (int z = 0; z < region.length; z += CHOOSE_REPLACE_BLOCK_REQUEST) {
                    int worldX = region.x + x;
                    int worldZ = region.z + z;
                    int worldY = mgr.getHighestBlockYAt(worldX, worldZ);
                    int colour = BlockColor.get(mgr.getBlockTypeId(worldX, worldY, worldZ), mgr.getBlockData(worldX, worldY, worldZ));
                    for (int rectX = 0; rectX < widthPerBlock; rectX += CHOOSE_REPLACE_BLOCK_REQUEST) {
                        for (int rectY = 0; rectY < widthPerBlock; rectY += CHOOSE_REPLACE_BLOCK_REQUEST) {
                            map.setPixel((widthPerBlock * x) + rectX, (widthPerBlock * z) + rectY, -16777216 | colour);
                        }
                    }
                }
            }
            drawEntities(map);
            File file = new File(getIntent().getStringExtra("world"), "pocketinveditor_map.png");
            try {
                writeBitmap(map, file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finishTask(file);
        }

        private void drawEntities(Bitmap map) {
            Paint playerDotPaint = new Paint();
            Canvas canvas = new Canvas(map);
            List<Entity> entities = EditorActivity.level.getEntities();
            if (entities != null) {
                for (Entity e : entities) {
                    drawEntity(canvas, e, playerDotPaint);
                }
            }
            drawEntity(canvas, player, playerDotPaint);
        }

        private void drawEntity(Canvas canvas, Entity e, Paint playerDotPaint) {
            Vector3f loc = e.getLocation();
            int iconLocX = (loc.getBlockX() - region.x) * widthPerBlock;
            int iconLocY = (loc.getBlockZ() - region.z) * widthPerBlock;
            EntityIcon.drawEntity(canvas, new Rect(iconLocX, iconLocY, iconLocX + 16, iconLocY + 16), playerDotPaint, e.getEntityType());
        }

        private void finishTask(final File file) {
            runOnUiThread(new Runnable() {
                public void run() {
                    mapAreaCallback(file);
                }
            });
        }

        private void writeBitmap(Bitmap bitmap, File file) throws Exception {
            FileOutputStream os = new FileOutputStream(file);
            bitmap.compress(CompressFormat.PNG, 90, os);
            os.close();
        }
    }

    private class PasteTask implements Runnable {
        public CuboidClipboard clipboard;
        public boolean noAir;
        public Vector3f point;

        public void run() {
            ChunkManager mgr = getChunkManager();
            this.clipboard.place(mgr, point, noAir);
            try {
                mgr.saveAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
            finishTask();
        }

        private void finishTask() {
            runOnUiThread(new Runnable() {
                public void run() {
                    pasteCallback();
                }
            });
        }
    }

    private class ReplaceBlocksTask implements Runnable {
        public MaterialKey from;
        public CuboidRegion region;
        public MaterialKey to;

        public void run() {
            ChunkManager mgr = getChunkManager();
            int replacedCount = GeoUtils.replaceBlocks(mgr, region, from, to);
            try {
                mgr.saveAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
            finishTask(replacedCount);
        }

        private void finishTask(final int replacedCount) {
            runOnUiThread(new Runnable() {
                public void run() {
                    replaceBlocksCallback(replacedCount);
                }
            });
        }
    }

    private class SchematicExportTask implements Runnable {
        public boolean callback = true;
        public CuboidClipboard clipboard;
        public File file;

        public SchematicExportTask(CuboidClipboard clipboard, File file) {
            this.clipboard = clipboard;
            this.file = file;
        }

        public void run() {
            boolean success = true;
            try {
                SchematicIO.save(clipboard, file);
            } catch (Exception e) {
                e.printStackTrace();
                success = false;
            }
            finishTask(success);
        }

        private void finishTask(final boolean success) {
            if (callback) {
                runOnUiThread(new Runnable() {
                    public void run() {
                        schematicExportCallback(file, success);
                    }
                });
            }
        }
    }

    private class SchematicImportTask implements Runnable {
        public File file;

        public SchematicImportTask(File file) {
            this.file = file;
        }

        public void run() {
            CuboidClipboard retval = null;
            try {
                retval = SchematicIO.read(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
            finishTask(retval);
        }

        private void finishTask(final CuboidClipboard result) {
            runOnUiThread(new Runnable() {
                public void run() {
                    schematicImportCallback(result, SchematicImportTask.this.file);
                }
            });
        }
    }

    private class SetBlocksTask implements Runnable {
        public CuboidRegion region;
        public boolean solid;
        public MaterialKey to;

        private SetBlocksTask() {
            solid = true;
        }

        public void run() {
            int amount;
            ChunkManager mgr = getChunkManager();
            if (solid) {
                GeoUtils.setBlocks(mgr, region, to);
                amount = (region.width * region.height) * region.length;
            } else {
                amount = GeoUtils.makeHollowCuboid(mgr, region, to);
            }
            try {
                mgr.saveAll();
            } catch (Exception e) {
                e.printStackTrace();
            }
            finishTask(amount);
        }

        private void finishTask(final int amount) {
            runOnUiThread(new Runnable() {
                public void run() {
                    setBlocksCallback(amount);
                }
            });
        }
    }

    public enum ShapeType {
        SPHERE,
        DOME,
        PYRAMID,
        CYLINDER
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.terrain_edit);
        burnBabyBurnButton = findViewById(R.id.terrain_burnbabyburn);
        burnBabyBurnButton.setOnClickListener(this);
        locXText = findViewById(R.id.location_select_x);
        locYText = findViewById(R.id.location_select_y);
        locZText = findViewById(R.id.location_select_z);
        widthText = findViewById(R.id.region_width_x);
        heightText = findViewById(R.id.region_width_y);
        lengthText = findViewById(R.id.region_width_z);
        analyzeButton = findViewById(R.id.terrain_analyze);
        analyzeButton.setOnClickListener(this);
        autoSelectButton = findViewById(R.id.terrain_autoselect);
        autoSelectButton.setOnClickListener(this);
        setBlocksButton = findViewById(R.id.terrain_setblocks);
        setBlocksButton.setOnClickListener(this);
        replaceBlocksButton = findViewById(R.id.terrain_replaceblocks);
        replaceBlocksButton.setOnClickListener(this);
        coordsToPlayerButton = findViewById(R.id.terrain_coords_to_player);
        coordsToPlayerButton.setOnClickListener(this);
        selectWholeWorldButton = findViewById(R.id.terrain_select_whole_world);
        selectWholeWorldButton.setOnClickListener(this);
        copyButton = findViewById(R.id.terrain_copy);
        copyButton.setOnClickListener(this);
        pasteButton = findViewById(R.id.terrain_paste);
        pasteButton.setOnClickListener(this);
        pasteNoAirButton = findViewById(R.id.terrain_paste_without_air);
        pasteNoAirButton.setOnClickListener(this);
        schematicExportButton = findViewById(R.id.terrain_schematic_export);
        schematicExportButton.setOnClickListener(this);
        schematicImportButton = findViewById(R.id.terrain_schematic_import);
        schematicImportButton.setOnClickListener(this);
        hollowSphereButton = findViewById(R.id.terrain_hollow_sphere);
        hollowSphereButton.setOnClickListener(this);
        solidSphereButton = findViewById(R.id.terrain_solid_sphere);
        solidSphereButton.setOnClickListener(this);
        hollowDomeButton = findViewById(R.id.terrain_hollow_dome);
        hollowDomeButton.setOnClickListener(this);
        solidDomeButton = findViewById(R.id.terrain_solid_dome);
        solidDomeButton.setOnClickListener(this);
        coordsToPlayerFeetButton = findViewById(R.id.terrain_coords_to_player_feet);
        coordsToPlayerFeetButton.setOnClickListener(this);
        hollowCuboidButton = findViewById(R.id.terrain_hollow_cuboid);
        hollowCuboidButton.setOnClickListener(this);
        hollowPyramidButton = findViewById(R.id.terrain_hollow_pyramid);
        hollowPyramidButton.setOnClickListener(this);
        solidPyramidButton = findViewById(R.id.terrain_solid_pyramid);
        solidPyramidButton.setOnClickListener(this);
        fixTerrainHolesButton = findViewById(R.id.terrain_fix_holes);
        fixTerrainHolesButton.setOnClickListener(this);
        mapAreaButton = findViewById(R.id.terrain_generate_map);
        mapAreaButton.setOnClickListener(this);
        cylinderButton = findViewById(R.id.terrain_cylinder);
        cylinderButton.setOnClickListener(this);
        checkEnablePasteButtons();
        loadEntities();
    }

    protected void loadEntities() {
        new Thread(new LoadEntitiesTask(this, this)).start();
    }

    public void onEntitiesLoaded(EntityData entitiesDat) {
        EditorActivity.level.setEntities(entitiesDat.entities);
        EditorActivity.level.setTileEntities(entitiesDat.tileEntities);
    }

    public void onClick(View v) {
        boolean z = true;
        if (v == burnBabyBurnButton) {
            Toast.makeText(this, R.string.terrain_editing, 0).show();
            new Thread(new DiscoInfernoTask()).start();
        } else if (v == analyzeButton) {
            AnalyzeRegionTask task = new AnalyzeRegionTask();
            task.region = getSelection();
            if (task.region != null) {
                showBusyIndicator();
                new Thread(task).start();
            }
        } else if (v == replaceBlocksButton) {
            if (getSelection() != null) {
                startActivityForResult(new Intent(this, ChooseReplaceBlockActivity.class), CHOOSE_REPLACE_BLOCK_REQUEST);
            }
        } else if (v == setBlocksButton) {
            if (getSelection() != null) {
                startActivityForResult(new Intent(this, ChooseBlockActivity.class), CHOOSE_SET_BLOCK_REQUEST);
            }
        } else if (v == coordsToPlayerButton) {
            moveCoordsToPlayer();
        } else if (v == selectWholeWorldButton) {
            setSelection(ChunkManager.worldRegion);
        } else if (v == autoSelectButton) {
            Intent intent = new Intent(this, ChooseBlockActivity.class);
            intent.putExtra("CustomChooseBlockTitle", getResources().getText(R.string.terrain_autoselect_choose_marker_block));
            startActivityForResult(intent, DIALOG_SHIFT_REGION);
        } else if (v == goToForumsButton) {
            Intent intent = new Intent("android.intent.action.VIEW");
            intent.setData(Uri.parse(FORUMS_PAGE_URL));
            startActivity(intent);
        } else if (v == copyButton) {
            CopyTask task2 = new CopyTask();
            task2.region = getSelection();
            if (task2.region != null) {
                startTerrainTask(task2);
            }
        } else if (v == pasteButton || v == pasteNoAirButton) {
            if (clipboard != null) {
                PasteTask task3 = new PasteTask();
                if (v != pasteNoAirButton) {
                    z = false;
                }
                task3.noAir = z;
                task3.clipboard = clipboard;
                task3.point = getSelectionPoint();
                if (task3.point != null) {
                    startTerrainTask(task3);
                }
            }
        } else if (v == shiftButton) {
            showDialog(DIALOG_SHIFT_REGION);
        } else if (v == schematicExportButton) {
            if (clipboard != null) {
                showDialog(DIALOG_NAME_SCHEMATIC);
            }
        } else if (v == schematicImportButton) {
            showImportSchematicActivity();
        } else if (v == hollowSphereButton) {
            if (getSelectionPoint() != null) {
                Intent intent = new Intent(this, ChooseRadiusMaterialActivity.class);
                intent.putExtra("CustomChooseBlockTitle", getResources().getText(R.string.terrain_hollow_sphere));
                startActivityForResult(intent, DIALOG_FIX_TERRAIN_HOLES);
            }
        } else if (v == solidSphereButton) {
            if (getSelectionPoint() != null) {
                Intent intent = new Intent(this, ChooseRadiusMaterialActivity.class);
                intent.putExtra("CustomChooseBlockTitle", getResources().getText(R.string.terrain_solid_sphere));
                startActivityForResult(intent, CHOOSE_SOLID_SPHERE_BLOCK_REQUEST);
            }
        } else if (v == hollowDomeButton) {
            if (getSelectionPoint() != null) {
                Intent intent = new Intent(this, ChooseRadiusMaterialActivity.class);
                intent.putExtra("CustomChooseBlockTitle", getResources().getText(R.string.terrain_hollow_dome));
                startActivityForResult(intent, CHOOSE_HOLLOW_DOME_BLOCK_REQUEST);
            }
        } else if (v == solidDomeButton) {
            if (getSelectionPoint() != null) {
                Intent intent = new Intent(this, ChooseRadiusMaterialActivity.class);
                intent.putExtra("CustomChooseBlockTitle", getResources().getText(R.string.terrain_solid_dome));
                startActivityForResult(intent, CHOOSE_SOLID_DOME_BLOCK_REQUEST);
            }
        } else if (v == coordsToPlayerFeetButton) {
            moveCoordsToPlayerFeet();
        } else if (v == hollowCuboidButton) {
            if (getSelection() != null) {
                startActivityForResult(new Intent(this, ChooseBlockActivity.class), CHOOSE_HOLLOW_CUBOID_BLOCK_REQUEST);
            }
        } else if (v == hollowPyramidButton) {
            if (getSelectionPoint() != null) {
                Intent intent = new Intent(this, ChooseRadiusMaterialActivity.class);
                intent.putExtra("CustomChooseBlockTitle", getResources().getText(R.string.terrain_hollow_pyramid));
                intent.putExtra("CustomRadiusTitle", getResources().getText(R.string.terrain_height));
                startActivityForResult(intent, CHOOSE_HOLLOW_PYRAMID_BLOCK_REQUEST);
            }
        } else if (v == solidPyramidButton) {
            if (getSelectionPoint() != null) {
                Intent intent = new Intent(this, ChooseRadiusMaterialActivity.class);
                intent.putExtra("CustomChooseBlockTitle", getResources().getText(R.string.terrain_solid_pyramid));
                intent.putExtra("CustomRadiusTitle", getResources().getText(R.string.terrain_height));
                startActivityForResult(intent, CHOOSE_SOLID_PYRAMID_BLOCK_REQUEST);
            }
        } else if (v == fixTerrainHolesButton) {
            showDialog(DIALOG_FIX_TERRAIN_HOLES);
        } else if (v == mapAreaButton) {
            MapAreaTask task4 = new MapAreaTask();
            task4.region = getSelection();
            task4.player = EditorActivity.level.getPlayer();
            if (task4.region != null) {
                EntityIcon.loadEntitySprites(this);
                startTerrainTask(task4);
            }
        } else if (v == cylinderButton && getSelectionPoint() != null) {
            try {
                Integer.parseInt(heightText.getText().toString());
                Intent intent = new Intent(this, ChooseRadiusMaterialActivity.class);
                intent.putExtra("CustomChooseBlockTitle", getResources().getText(R.string.terrain_cylinder));
                intent.putExtra("ShowHollowCheckBox", true);
                startActivityForResult(intent, CHOOSE_CYLINDER_BLOCK_REQUEST);
            } catch (Exception e) {
                new Builder(this).setMessage(R.string.invalid_number).setPositiveButton(android.R.string.ok, null).show();
            }
        }
    }

    protected void onPause() {
        super.onPause();
        if (mgr != null) {
            mgr.unloadChunks(false);
            try {
                mgr.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
            mgr = null;
            System.out.println("Cleared ChunkManager cache");
        }
        System.gc();
    }

    public Dialog onCreateDialog(int dialogId) {
        switch (dialogId) {
            case DIALOG_SHIFT_REGION:
                return null;
            case DIALOG_NAME_SCHEMATIC:
                return new Builder(this).setMessage(R.string.terrain_schematic_input_name).setView(getLayoutInflater().inflate(R.layout.schematic_name_dialog, null)).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogI, int button) {
                        File schematicsFolder = new File(Environment.getExternalStorageDirectory(), "Android/data/com.mcal.pocketinveditor.pro/schematics");
                        schematicsFolder.mkdirs();
                        File schematicFile = new File(schematicsFolder, ((TextView) ((AlertDialog) dialogI).findViewById(R.id.terrain_schematic_input_name)).getText().toString() + ".schematic");
                        if (schematicFile.exists()) {
                            Toast.makeText(EditTerrainActivity.this, R.string.file_exists, EditTerrainActivity.CHOOSE_REPLACE_BLOCK_REQUEST).show();
                        } else {
                            new Thread(new SchematicExportTask(EditTerrainActivity.clipboard, schematicFile)).start();
                        }
                    }
                }).setNegativeButton(android.R.string.cancel, null).create();
            case DIALOG_FIX_TERRAIN_HOLES:
                return new Builder(this).setMessage(R.string.terrain_fix_holes_description).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogI, int button) {
                        startTerrainTask(new FixTerrainHolesTask());
                    }
					}).setNegativeButton(android.R.string.cancel, null).create();
            case DIALOG_PROGRESS:
                Dialog d = new ProgressDialog(this);
                //d.setIndeterminate(true);
                d.setCancelable(false);
                //d.setMessage(getResources().getText(R.string.terrain_busy));
                return d;
            case DIALOG_REGION_OUT_OF_WORLD:
                return new Builder(this).setMessage(R.string.terrain_region_out_of_world).setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialogI, int button) {
                        CuboidRegion clippedRegion = ChunkManager.worldRegion.createIntersection(getSelection(true));
                        if (clippedRegion != null && clippedRegion.isValid()) {
                            setSelection(clippedRegion);
                        }
                    }
					}).setNegativeButton(android.R.string.cancel, null).create();
            default:
                return super.onCreateDialog(dialogId);
        }
    }

    public CuboidRegion getSelection() {
        return getSelection(false);
    }

    public CuboidRegion getSelection(boolean ignoreOutOfWorld) {
        try {
            int x = Integer.parseInt(locXText.getText().toString());
            int y = Integer.parseInt(locYText.getText().toString());
            int z = Integer.parseInt(locZText.getText().toString());
            int width = Integer.parseInt(widthText.getText().toString());
            int height = Integer.parseInt(heightText.getText().toString());
            int length = Integer.parseInt(lengthText.getText().toString());
            if (width >= CHOOSE_REPLACE_BLOCK_REQUEST && height >= CHOOSE_REPLACE_BLOCK_REQUEST && length >= CHOOSE_REPLACE_BLOCK_REQUEST) {
                return new CuboidRegion(x, y, z, width, height, length);
            }
            new Builder(this).setMessage(R.string.invalid_number).setPositiveButton(android.R.string.ok, null).show();
            return null;
        } catch (Exception e) {
            new Builder(this).setMessage(R.string.invalid_number).setPositiveButton(android.R.string.ok, null).show();
            return null;
        }
    }

    public void setSelection(CuboidRegion region) {
        locXText.setText(Integer.toString(region.x));
        locYText.setText(Integer.toString(region.y));
        locZText.setText(Integer.toString(region.z));
        widthText.setText(Integer.toString(region.width));
        heightText.setText(Integer.toString(region.height));
        lengthText.setText(Integer.toString(region.length));
    }

    public Vector3f getSelectionPoint() {
        try {
            return new Vector3f(
			(float) Integer.parseInt(locXText.getText().toString()),
			(float) Integer.parseInt(locYText.getText().toString()),
			(float) Integer.parseInt(locZText.getText().toString()));
        } catch (Exception e) {
            Toast.makeText(this, R.string.invalid_number, 0).show();
            return null;
        }
    }

    public void handleRegionOutOfWorld() {
        showDialog(DIALOG_REGION_OUT_OF_WORLD);
    }

    public ChunkManager getChunkManager() {
        if (mgr == null) {
            mgr = new ChunkManager(new File(getIntent().getStringExtra("world") + "/db"));
        }
        return mgr;
    }

    protected void showBusyIndicator() {
        isBusy = true;
        showDialog(DIALOG_PROGRESS);
    }

    protected void hideBusyIndicator() {
        try {
            dismissDialog(DIALOG_PROGRESS);
        } catch (Exception e) {
            e.printStackTrace();
        }
        isBusy = false;
        System.gc();
    }

    protected void startTerrainTask(Runnable run) {
        showBusyIndicator();
        new Thread(run).start();
    }

    protected void moveCoordsToPlayer() {
        Vector3f leVector = EditorActivity.level.getPlayer().getLocation();
        locXText.setText(Integer.toString(leVector.getBlockX()));
        locYText.setText(Integer.toString(leVector.getBlockY()));
        locZText.setText(Integer.toString(leVector.getBlockZ()));
    }

    protected void moveCoordsToPlayerFeet() {
        Vector3f leVector = EditorActivity.level.getPlayer().getLocation();
        locXText.setText(Integer.toString(leVector.getBlockX()));
        locYText.setText(Integer.toString((int) (leVector.getY() - PLAYER_HEIGHT)));
        locZText.setText(Integer.toString(leVector.getBlockZ()));
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode != -1 || isBusy) {
            return;
        }
        short typeId;
        short damage;
        if (requestCode == CHOOSE_SET_BLOCK_REQUEST || requestCode == CHOOSE_HOLLOW_CUBOID_BLOCK_REQUEST) {
            try {
                typeId = intent.getShortExtra("BlockTypeId", (short) 0);
                damage = intent.getShortExtra("BlockData", (short) 0);
                if (damage == (short) -1) {
                    damage = (short) 0;
                }
                SetBlocksTask task = new SetBlocksTask();
                task.region = getSelection();
                task.to = new MaterialKey(typeId, damage);
                task.solid = requestCode == CHOOSE_SET_BLOCK_REQUEST;
                showBusyIndicator();
                new Thread(task).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == CHOOSE_REPLACE_BLOCK_REQUEST) {
            try {
                typeId = intent.getShortExtra("BlockTypeId", (short) 0);
                damage = intent.getShortExtra("BlockData", (short) 0);
                short fromTypeId = intent.getShortExtra("BlockFromTypeId", (short) 0);
                short fromDamage = intent.getShortExtra("BlockFromData", (short) 0);
                if (damage == (short) -1) {
                    damage = (short) 0;
                }
                ReplaceBlocksTask task2 = new ReplaceBlocksTask();
                task2.region = getSelection();
                task2.from = new MaterialKey(fromTypeId, fromDamage);
                task2.to = new MaterialKey(typeId, damage);
                showBusyIndicator();
                new Thread(task2).start();
            } catch (Exception e2) {
                e2.printStackTrace();
            }
        } else if (requestCode == DIALOG_SHIFT_REGION) {
            try {
                typeId = intent.getShortExtra("BlockTypeId", (short) 0);
                damage = intent.getShortExtra("BlockData", (short) -1);
                AutoSelectTask task3 = new AutoSelectTask();
                task3.material = new MaterialKey(typeId, damage);
                showBusyIndicator();
                new Thread(task3).start();
            } catch (Exception e22) {
                e22.printStackTrace();
            }
        } else if (requestCode == DIALOG_NAME_SCHEMATIC) {
            new Thread(new SchematicImportTask(FileUtils.getFile(intent.getData()))).start();
        } else if (requestCode == DIALOG_FIX_TERRAIN_HOLES || requestCode == CHOOSE_SOLID_SPHERE_BLOCK_REQUEST || requestCode == CHOOSE_HOLLOW_DOME_BLOCK_REQUEST || requestCode == CHOOSE_SOLID_DOME_BLOCK_REQUEST) {
            try {
                typeId = intent.getShortExtra("BlockTypeId", (short) 0);
                damage = intent.getShortExtra("BlockData", (short) 0);
                if (damage == (short) -1) {
                    damage = (short) 0;
                }
                int radius = intent.getIntExtra("Radius", 0);
                EditTerrainActivity.MakeSphereTask task = new MakeSphereTask();
                task.center = getSelectionPoint();
                task.to = new MaterialKey(typeId, damage);
                boolean r8 = requestCode == DIALOG_FIX_TERRAIN_HOLES || requestCode == CHOOSE_HOLLOW_DOME_BLOCK_REQUEST;
                task.hollow = r8;
                r8 = requestCode == CHOOSE_HOLLOW_DOME_BLOCK_REQUEST || requestCode == CHOOSE_SOLID_DOME_BLOCK_REQUEST;
                task.dome = r8;
                task.radius = radius;
                showBusyIndicator();
                new Thread(task).start();
            } catch (Exception e222) {
                e222.printStackTrace();
            }
        } else if (requestCode == CHOOSE_HOLLOW_PYRAMID_BLOCK_REQUEST || requestCode == CHOOSE_SOLID_PYRAMID_BLOCK_REQUEST) {
            try {
                typeId = intent.getShortExtra("BlockTypeId", (short) 0);
                damage = intent.getShortExtra("BlockData", (short) 0);
                if (damage == (short) -1) {
                    damage = (short) 0;
                }
                int radius = intent.getIntExtra("Radius", 0);
                EditTerrainActivity.MakeSphereTask task = new MakeSphereTask();
                task.center = getSelectionPoint();
                task.to = new MaterialKey(typeId, damage);
                boolean r8 = requestCode == CHOOSE_HOLLOW_PYRAMID_BLOCK_REQUEST || requestCode == CHOOSE_HOLLOW_PYRAMID_BLOCK_REQUEST;
                task.hollow = r8;
                task.shape = ShapeType.PYRAMID;
                task.radius = radius;
                showBusyIndicator();
                new Thread(task).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (requestCode == CHOOSE_CYLINDER_BLOCK_REQUEST) {
            try {
                typeId = intent.getShortExtra("BlockTypeId", (short) 0);
                damage = intent.getShortExtra("BlockData", (short) 0);
                if (damage == (short) -1) {
                    damage = (short) 0;
                }
                int radius = intent.getIntExtra("Radius", 0);
                EditTerrainActivity.MakeSphereTask task = new MakeSphereTask();
                task.center = getSelectionPoint();
                task.to = new MaterialKey(typeId, damage);
                task.hollow = intent.getBooleanExtra("Hollow", false);
                task.shape = ShapeType.CYLINDER;
                task.radius = radius;
                task.height = Integer.parseInt(heightText.getText().toString());
                showBusyIndicator();
                new Thread(task).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void discoInferno() {
        ChunkManager mgr = new ChunkManager(new File(getIntent().getStringExtra("world") + "/chunks.dat"));
        Player player = EditorActivity.level.getPlayer();
        int beginX = (int) player.getLocation().getX();
        int beginZ = (int) player.getLocation().getZ();
        int beginY = ((int) player.getLocation().getY()) + CHOOSE_HOLLOW_PYRAMID_BLOCK_REQUEST;
        if (beginY > 127 || beginY < 0) {
            beginY = 127;
        }
        for (int x = beginX - 21; x < beginX + 21; x += CHOOSE_REPLACE_BLOCK_REQUEST) {
            for (int z = beginZ - 21; z < beginZ + 21; z += CHOOSE_REPLACE_BLOCK_REQUEST) {
                mgr.setBlockTypeId(x, beginY, z, CHOOSE_HOLLOW_PYRAMID_BLOCK_REQUEST);
            }
        }
        try {
            mgr.saveAll();
            mgr.unloadChunks(false);
            mgr.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void doneDisco() {
        Toast.makeText(this, R.string.saved, 0).show();
    }

    private void showImportSchematicActivity() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType(SCHEMATIC_MIME);
        intent.setClass(this, FileChooserActivity.class);
        startActivityForResult(intent, DIALOG_NAME_SCHEMATIC);
    }

    private void checkEnablePasteButtons() {
        boolean z;
        boolean z2 = true;
        Button button = pasteButton;
        if (clipboard != null) {
            z = true;
        } else {
            z = false;
        }
        button.setEnabled(z);
        button = pasteNoAirButton;
        if (clipboard != null) {
            z = true;
        } else {
            z = false;
        }
        button.setEnabled(z);
        Button button2 = schematicExportButton;
        if (clipboard == null) {
            z2 = false;
        }
        button2.setEnabled(z2);
    }

    protected void autoSelectResultCallback(Vector3f[] result) {
        hideBusyIndicator();
        System.out.println(result);
        if (result != null) {
            setSelection(CuboidRegion.fromPoints(result[0], result[CHOOSE_REPLACE_BLOCK_REQUEST]));
        } else {
            Toast.makeText(this, R.string.terrain_autoselect_not_found, 0).show();
        }
    }

    protected void analyzeRegionCallback(Map<MaterialKey, MaterialCount> blockCounts) {
        hideBusyIndicator();
        List<MaterialCount> countList = new ArrayList<>(blockCounts.values());
        Collections.sort(countList, new CountComparator());
        BlockCountsActivity.currentCounts = countList;
        startActivity(new Intent(this, BlockCountsActivity.class));
    }

    protected void replaceBlocksCallback(int amount) {
        hideBusyIndicator();
        System.out.println(amount);
        Toast.makeText(this, amount + " " + getResources().getText(R.string.terrain_number_blocks_changed) + " " + getResources().getText(R.string.saved), CHOOSE_REPLACE_BLOCK_REQUEST).show();
    }

    protected void setBlocksCallback(int amount) {
        hideBusyIndicator();
        Toast.makeText(this, amount + " " + getResources().getText(R.string.terrain_number_blocks_changed) + " " + getResources().getText(R.string.saved), CHOOSE_REPLACE_BLOCK_REQUEST).show();
    }

    protected void copyCallback(CuboidClipboard clipboard) {
        hideBusyIndicator();
        this.clipboard = clipboard;
        checkEnablePasteButtons();
    }

    protected void pasteCallback() {
        hideBusyIndicator();
        Toast.makeText(this, R.string.saved, 0).show();
    }

    protected void schematicImportCallback(CuboidClipboard clipboard, File file) {
        boolean z = true;
        if (clipboard == null) {
            Toast.makeText(this, R.string.terrain_schematic_import_fail, CHOOSE_REPLACE_BLOCK_REQUEST).show();
            return;
        }
        boolean z2;
        this.clipboard = clipboard;
        Button button = pasteButton;
        if (clipboard != null) {
            z2 = true;
        } else {
            z2 = false;
        }
        button.setEnabled(z2);
        button = pasteNoAirButton;
        if (clipboard != null) {
            z2 = true;
        } else {
            z2 = false;
        }
        button.setEnabled(z2);
        Button button2 = schematicExportButton;
        if (clipboard == null) {
            z = false;
        }
        button2.setEnabled(z);
        Toast.makeText(this, R.string.terrain_schematic_imported, 0).show();
    }

    protected void schematicExportCallback(File file, boolean success) {
        if (success) {
            Toast.makeText(this, getResources().getText(R.string.terrain_schematic_exported) + " " + file.getAbsolutePath(), CHOOSE_REPLACE_BLOCK_REQUEST).show();
        } else {
            Toast.makeText(this, R.string.terrain_schematic_export_fail, CHOOSE_REPLACE_BLOCK_REQUEST).show();
        }
    }

    protected void makeSphereCallback(int amount) {
        hideBusyIndicator();
        Toast.makeText(this, amount + " " + getResources().getText(R.string.terrain_number_blocks_changed) + " " + getResources().getText(R.string.saved), CHOOSE_REPLACE_BLOCK_REQUEST).show();
    }

    protected void fixTerrainHolesCallback() {
        hideBusyIndicator();
        Toast.makeText(this, R.string.saved, 0).show();
    }

    protected void mapAreaCallback(File file) {
        hideBusyIndicator();
        Intent intent = new Intent("android.intent.action.VIEW");
        intent.setDataAndType(Uri.fromFile(file), "image/png");
        startActivity(intent);
    }
}
