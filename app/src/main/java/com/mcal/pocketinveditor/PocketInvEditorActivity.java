package com.mcal.pocketinveditor;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Build.VERSION;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import com.mcal.pocketinveditor.io.xml.MaterialIconLoader;
import com.mcal.pocketinveditor.io.xml.MaterialLoader;
import com.mcal.pocketinveditor.material.Material;
import com.mcal.pocketinveditor.pro.R;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class PocketInvEditorActivity extends ListActivity {
    protected static final int DIALOG_BREAK_LOCK = 201;
    private static final int DIALOG_NO_WORLDS_FOUND = 200;
    protected static final int PERMISSION_REQUEST_STORAGE = 1234;
    private FindWorldsThread findWorldsThread;
    protected boolean hasPermission = true;
    protected File worldToBreakLock;
    private List<WorldListItem> worlds;
    private ListView worldsList;

    private final class FindWorldsThread implements Runnable {
        private final PocketInvEditorActivity activity;

        public FindWorldsThread(PocketInvEditorActivity activity) {
            this.activity = activity;
        }

        public void run() {
            File worldsFolder = new File(Environment.getExternalStorageDirectory(), "games/com.mojang/minecraftWorlds");
            System.err.println(worldsFolder);
            final List<WorldListItem> worldFolders = new ArrayList<>();
            if (worldsFolder.exists()) {
                for (File worldFolder : worldsFolder.listFiles()) {
                    if (worldFolder.isDirectory() && new File(worldFolder, "level.dat").exists()) {
                        worldFolders.add(new WorldListItem(worldFolder));
                    }
                }
            } else {
                System.err.println("no storage folder");
            }
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    activity.receiveWorldFolders(worldFolders);
                }
            });
        }
    }

    private final class WorldListDateComparator implements Comparator<WorldListItem> {
        public int compare(WorldListItem a, WorldListItem b) {
            long result = a.levelDat.lastModified() - b.levelDat.lastModified();
            if (result < 0) {
                return 1;
            }
            if (result > 0) {
                return -1;
            }
            return 0;
        }

        public boolean equals(WorldListItem a, WorldListItem b) {
            return a.levelDat.lastModified() == b.levelDat.lastModified();
        }
    }

    private final class WorldListItem {
        public final File folder;
        public final File levelDat;
        public String name = null;

        public WorldListItem(File file) {
            folder = file;
            levelDat = new File(folder, "level.dat");
            try {
                File nameFile = new File(folder, "levelname.txt");
                if (nameFile.exists()) {
                    FileInputStream fis = new FileInputStream(nameFile);
                    byte[] buf = new byte[((int) nameFile.length())];
                    fis.read(buf);
                    fis.close();
                    name = new String(buf, "UTF-8");
                }
            } catch (IOException ie) {
                ie.printStackTrace();
            }
        }

        public String toString() {
            if (name != null) {
                return name;
            }
            return folder.getName();
        }
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        loadContentView();
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                openWorld(worlds.get(position).folder);
            }
        });
        if (Material.materials == null) {
            loadMaterials();
        }
        if (VERSION.SDK_INT >= 23) {
            hasPermission = grabPermissions();
        }
    }

    protected void loadContentView() {
    }

    protected void onStart() {
        super.onStart();
        if (hasPermission) {
            loadWorlds();
        }
    }

    protected void loadWorlds() {
        this.findWorldsThread = new FindWorldsThread(this);
        new Thread(findWorldsThread).start();
    }

    private void receiveWorldFolders(List<WorldListItem> worlds) {
        this.worlds = worlds;
        ArrayAdapter<WorldListItem> adapter = new ArrayAdapter<>(this, R.layout.world_list_item, worlds);
        adapter.sort(new WorldListDateComparator());
        setListAdapter(adapter);
        if (worlds.size() == 0) {
            displayNoWorldsWarning();
        }
    }

    protected void openWorld(File worldFile) {
        Intent intent = new Intent(this, EditorActivity.class);
        intent.putExtra("world", worldFile.getAbsolutePath());
        startActivity(intent);
    }

    private void loadMaterials() {
        new Thread(new MaterialLoader(getResources().getXml(R.xml.item_data))).start();
        new Thread(new MaterialIconLoader(this)).start();
    }

    private void displayNoWorldsWarning() {
        showDialog(DIALOG_NO_WORLDS_FOUND);
    }

    public Dialog onCreateDialog(int dialogId) {
        switch (dialogId) {
            case DIALOG_NO_WORLDS_FOUND:
                return createNoWorldsFoundDialog();
            case DIALOG_BREAK_LOCK:
                return createBreakLockDialog();
            default:
                return super.onCreateDialog(dialogId);
        }
    }

    protected AlertDialog createNoWorldsFoundDialog() {
        return new Builder(this).setTitle(R.string.noworldsfound_title).setMessage(R.string.noworldsfound_text).create();
    }

    protected AlertDialog createBreakLockDialog() {
        return new Builder(this).setTitle(R.string.world_list_break_lock_title).setMessage(R.string.world_list_break_lock_text).setPositiveButton(android.R.string.ok, new OnClickListener() {
            public void onClick(DialogInterface dialog, int button) {
                if (worldToBreakLock != null) {
                    new File(worldToBreakLock, "db/LOCK").delete();
                    openWorld(worldToBreakLock);
                }
            }
			}).setNegativeButton(android.R.string.cancel, null).create();
    }

    protected boolean grabPermissions() {
        return true;
    }
}
