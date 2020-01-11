package com.mcal.pocketinveditor;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.mcal.pocketinveditor.io.nbt.NBTConverter;
import com.mcal.pocketinveditor.pro.R;
import com.mcal.pocketinveditor.tileentity.ContainerTileEntity;
import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import org.spout.nbt.CompoundTag;
import org.spout.nbt.NBTConstants;
import org.spout.nbt.stream.NBTInputStream;

public class LoadoutImportActivity extends ListActivity {
    private static final int COMBINE_MODE = 1;
    private static final int DIALOG_IMPORT_OPTIONS = 1;
    public static final int INVENTORY_SIZE = 36;
    private static final int REPLACE_MODE = 0;
    private FindLoadoutsThread findLoadoutsThread;
    private List<LoadoutListItem> loadouts;
    private ListView loadoutsList;
    private int selectedImportMode = -1;
    private LoadoutListItem selectedLoadoutItem;

    private final class FindLoadoutsThread implements Runnable {
        private final LoadoutImportActivity activity;

        public FindLoadoutsThread(LoadoutImportActivity activity) {
            this.activity = activity;
        }

        public void run() {
            File loadoutsFolder = LoadoutExportActivity.getLoadoutFolder(activity);
            final List<LoadoutListItem> loadouts = new ArrayList<>();
            if (loadoutsFolder.exists()) {
                File[] arr = loadoutsFolder.listFiles();
                int len = arr.length;
                for (int i = 0; i < len; i += LoadoutImportActivity.DIALOG_IMPORT_OPTIONS) {
                    File loadoutFile = arr[i];
                    if (loadoutFile.getName().indexOf(LoadoutExportActivity.LOADOUT_EXTENSION) >= 0) {
                        loadouts.add(new LoadoutListItem(loadoutFile));
                    }
                }
            } else {
                System.err.println("no storage folder");
            }
            this.activity.runOnUiThread(new Runnable() {
                public void run() {
                    activity.receiveLoadouts(loadouts);
                }
            });
        }
    }

    private final class LoadoutListComparator implements Comparator<LoadoutListItem> {
        private LoadoutListComparator() {
        }

        public int compare(LoadoutListItem a, LoadoutListItem b) {
            return a.displayName.toLowerCase().compareTo(b.displayName.toLowerCase());
        }

        public boolean equals(LoadoutListItem a, LoadoutListItem b) {
            return a.displayName.toLowerCase().equals(b.displayName.toLowerCase());
        }
    }

    private final class LoadoutListItem {
        public final String displayName;
        public final File file;

        public LoadoutListItem(File file) {
            this.file = file;
            displayName = file.getName().substring(0, file.getName().indexOf(LoadoutExportActivity.LOADOUT_EXTENSION));
        }

        public String toString() {
            return displayName;
        }
    }

    private final class LoadoutLoader implements Runnable {
        public LoadoutListItem item;
        public List<InventorySlot> slots = null;

        public LoadoutLoader(LoadoutListItem item) {
            this.item = item;
        }

        public void run() {
			FileInputStream fis = null;
			NBTInputStream nis = null;
			try {
				fis = new FileInputStream(item.file);
				nis = new NBTInputStream(fis, false, true);
				CompoundTag tag = (CompoundTag) nis.readTag();
				slots = NBTConverter.readLoadout(tag);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					if (nis != null) nis.close();
					if (fis != null) fis.close();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
			runOnUiThread(new Runnable() {
					public void run() {
						loadoutLoadedCallback(slots);
					}
				});
		}
    }

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ListView lv = getListView();
        lv.setTextFilterEnabled(true);
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                openLoadoutImportWindow(loadouts.get(position));
            }
        });
    }

    protected void onStart() {
        super.onStart();
        findLoadouts();
    }

    private void findLoadouts() {
        findLoadoutsThread = new FindLoadoutsThread(this);
        new Thread(findLoadoutsThread).start();
    }

    private void receiveLoadouts(List<LoadoutListItem> loadouts) {
        this.loadouts = loadouts;
        ArrayAdapter<LoadoutListItem> adapter = new ArrayAdapter<>(this, R.layout.world_list_item, loadouts);
        adapter.sort(new LoadoutListComparator());
        setListAdapter(adapter);
    }

    private void openLoadoutImportWindow(LoadoutListItem item) {
        selectedLoadoutItem = item;
        showDialog(DIALOG_IMPORT_OPTIONS);
    }

    private void importLoadout(LoadoutListItem loadoutItem, int mode) {
        selectedImportMode = mode;
        new Thread(new LoadoutLoader(loadoutItem)).start();
    }

    protected void loadoutLoadedCallback(List<InventorySlot> slots) {
        if (getIntent().getBooleanExtra("IsTileEntity", false)) {
            tileEntityLoadoutLoadedCallback(slots);
        } else {
            playerLoadoutLoadedCallback(slots);
        }
        finish();
    }

    protected void playerLoadoutLoadedCallback(List<InventorySlot> slots) {
        int mode = selectedImportMode;
        if (mode == 0) {
            EditorActivity.level.getPlayer().setInventory(slots);
        } else if (mode == DIALOG_IMPORT_OPTIONS) {
            List<InventorySlot> currentSlots = EditorActivity.level.getPlayer().getInventory();
            compactSlots(currentSlots);
            for (int i = 0; i < slots.size(); i += DIALOG_IMPORT_OPTIONS) {
                InventorySlot s = slots.get(i);
                ItemStack stack = s.getContents();
                if (s.getSlot() >= (byte) 9 && !addStack(currentSlots, stack)) {
                    Toast.makeText(this, R.string.loadout_import_too_many_items, DIALOG_IMPORT_OPTIONS).show();
                    break;
                }
            }
        }
        EditorActivity.save(this);
    }

    protected void tileEntityLoadoutLoadedCallback(List<InventorySlot> slots) {
        int mode = selectedImportMode;
        ContainerTileEntity container = (ContainerTileEntity) EditorActivity.level.getTileEntities().get(getIntent().getIntExtra("Index", -1));
        if (mode == 0) {
            container.setItems(slots);
        } else if (mode == DIALOG_IMPORT_OPTIONS) {
        }
        EntitiesInfoActivity.saveTileEntities(this);
    }

    public static void compactSlots(List<InventorySlot> slots) {
        int a = 9;
        for (int i = 0; i < slots.size(); i += DIALOG_IMPORT_OPTIONS) {
            InventorySlot s = slots.get(i);
            if (s.getSlot() >= (byte) 9) {
                s.setSlot((byte) a);
                a += DIALOG_IMPORT_OPTIONS;
            }
        }
    }

    public static boolean addStack(List<InventorySlot> slots, ItemStack stack) {
        if (slots.size() >= 45) {
            return false;
        }
        byte newId = (byte) 0;
        for (int i = 0; i < slots.size(); i += DIALOG_IMPORT_OPTIONS) {
            byte a = slots.get(i).getSlot();
            if (a > newId) {
                newId = a;
            }
        }
        InventorySlot newSlot = new InventorySlot((byte) (newId + DIALOG_IMPORT_OPTIONS), stack);
        System.out.println(newSlot);
        slots.add(newSlot);
        return true;
    }

    public Dialog onCreateDialog(int dialogId) {
        switch (dialogId) {
            case DIALOG_IMPORT_OPTIONS:
                return createImportOptionsDialog();
            default:
                return super.onCreateDialog(dialogId);
        }
    }

    public void onPrepareDialog(int dialogId, Dialog dialog) {
        switch (dialogId) {
            case DIALOG_IMPORT_OPTIONS:
                ((AlertDialog) dialog).setTitle(selectedLoadoutItem.toString());
                return;
            default:
                super.onPrepareDialog(dialogId, dialog);
                return;
        }
    }

    protected AlertDialog createImportOptionsDialog() {
        return new Builder(this).setTitle("Slot name goes here").setItems(new CharSequence[]{getResources().getText(R.string.loadout_import_replace), getResources().getText(R.string.loadout_import_combine)}, new OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                switch (button) {
                    case NBTConstants.TYPE_END:
                        importLoadout(selectedLoadoutItem, 0);
                        return;
                    case LoadoutImportActivity.DIALOG_IMPORT_OPTIONS:
                        importLoadout(selectedLoadoutItem, LoadoutImportActivity.DIALOG_IMPORT_OPTIONS);
                        return;
                    default:
                        return;
                }
            }
        }).create();
    }
}
