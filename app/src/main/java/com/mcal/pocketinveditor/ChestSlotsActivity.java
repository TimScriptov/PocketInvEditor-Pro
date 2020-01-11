package com.mcal.pocketinveditor;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;
import com.mcal.pocketinveditor.io.xml.MaterialIconLoader;
import com.mcal.pocketinveditor.io.xml.MaterialLoader;
import com.mcal.pocketinveditor.material.Material;
import com.mcal.pocketinveditor.material.RepairableMaterials;
import com.mcal.pocketinveditor.pro.R;
import com.mcal.pocketinveditor.tileentity.ContainerTileEntity;
import java.util.List;
import org.spout.nbt.NBTConstants;

public final class ChestSlotsActivity extends ListActivity implements OnItemLongClickListener, LevelDataLoadListener {
    public static final int DIALOG_SLOT_OPTIONS = 805;
    public static final int EDIT_SLOT_REQUEST = 534626;
    private ContainerTileEntity container;
    private int currentlySelectedSlot = -1;
    private List<InventorySlot> inventory;
    private ArrayAdapter<InventorySlot> inventoryListAdapter;
    protected Intent slotActivityResultIntent = null;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        getListView().setOnItemLongClickListener(this);
        if (Material.materials == null) {
            new MaterialLoader(getResources().getXml(R.xml.item_data)).run();
            new MaterialIconLoader(this).run();
        }
        if (EditorActivity.level != null) {
            onLevelDataLoad();
        } else {
            EditorActivity.loadLevelData(this, this, getIntent().getStringExtra("world"));
        }
    }

    public void onLevelDataLoad() {
        container = (ContainerTileEntity) EditorActivity.level.getTileEntities().get(getIntent().getIntExtra("Index", -1));
        inventory = container.getItems();
        inventoryListAdapter = new MaterialIconArrayAdapter<>(this, R.layout.slot_list_item, R.id.slot_list_main_text, inventory);
        setListAdapter(inventoryListAdapter);
        inventoryListAdapter.notifyDataSetChanged();
        if (slotActivityResultIntent != null) {
            onSlotActivityResult(slotActivityResultIntent);
        }
    }

    public void onStart() {
        super.onStart();
        if (EditorActivity.level != null && inventoryListAdapter != null) {
            inventoryListAdapter.notifyDataSetChanged();
        }
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (getIntent().getBooleanExtra("CanEditSlots", false)) {
            openInventoryEditScreen(position, inventory.get(position));
        } else {
            showGetProMessage();
        }
    }

    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (!getIntent().getBooleanExtra("CanEditSlots", false)) {
            return false;
        }
        currentlySelectedSlot = position;
        showDialog(DIALOG_SLOT_OPTIONS);
        return true;
    }

    private void openInventoryEditScreen(int position, InventorySlot slot) {
        Intent intent = new Intent(this, EditInventorySlotActivity.class);
        ItemStack stack = slot.getContents();
        intent.putExtra("TypeId", stack.getTypeId());
        intent.putExtra("Damage", stack.getDurability());
        intent.putExtra("Count", stack.getAmount());
        intent.putExtra("Slot", slot.getSlot());
        intent.putExtra("Index", position);
        startActivityForResult(intent, EDIT_SLOT_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode != EDIT_SLOT_REQUEST || resultCode != -1) {
            return;
        }
        if (EditorActivity.level == null || inventory == null) {
            this.slotActivityResultIntent = intent;
        } else {
            onSlotActivityResult(intent);
        }
    }

    protected void onSlotActivityResult(Intent intent) {
        int slotIndex = intent.getIntExtra("Index", -1);
        if (slotIndex < 0) {
            System.err.println("wrong slot index");
            return;
        }
        ItemStack stack = inventory.get(slotIndex).getContents();
        stack.setAmount(intent.getIntExtra("Count", 0));
        stack.setDurability(intent.getShortExtra("Damage", (short) 0));
        stack.setTypeId(intent.getShortExtra("TypeId", (short) 0));
        EntitiesInfoActivity.saveTileEntities(this);
        slotActivityResultIntent = null;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (!getIntent().getBooleanExtra("CanEditSlots", false)) {
            return false;
        }
        super.onCreateOptionsMenu(menu);
        menu.add(getResources().getString(R.string.add_empty_slot));
        menu.add(getResources().getString(R.string.warp_to_tile_entity));
        menu.add(getResources().getString(R.string.inventory_repair_all));
        menu.add(getResources().getString(R.string.loadout_export));
        menu.add(getResources().getString(R.string.loadout_import));
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        CharSequence title = item.getTitle();
        Resources res = getResources();
        if (item.getTitle().equals(getResources().getString(R.string.add_empty_slot))) {
            InventorySlot newSlot = addEmptySlot();
            if (newSlot == null) {
                return true;
            }
            openInventoryEditScreen(inventoryListAdapter.getPosition(newSlot), newSlot);
            return true;
        } else if (item.getTitle().equals(getResources().getString(R.string.warp_to_tile_entity))) {
            TileEntityViewActivity.warpToTileEntity(this, this.container);
            return true;
        } else {
            if (item.getTitle().equals(getResources().getString(R.string.inventory_repair_all))) {
                repairAllItems();
            } else if (title.equals(res.getString(R.string.loadout_export))) {
                openExportLoadoutActivity();
                return true;
            } else if (title.equals(res.getString(R.string.loadout_import))) {
                openImportLoadoutActivity();
                return true;
            }
            return super.onOptionsItemSelected(item);
        }
    }

    private InventorySlot addEmptySlot() {
        if (inventory.size() > container.getContainerSize()) {
            return null;
        }
        InventorySlot slot = new InventorySlot((byte) inventory.size(), new ItemStack((short) 0, (short) 0, 1));
        alignSlots();
        inventory.add(slot);
        inventoryListAdapter.notifyDataSetChanged();
        EntitiesInfoActivity.saveTileEntities(this);
        return slot;
    }

    private void alignSlots() {
        for (int i = 0; i < inventory.size(); i++) {
            inventory.get(i).setSlot((byte) i);
        }
    }

    public Dialog onCreateDialog(int dialogId) {
        switch (dialogId) {
            case DIALOG_SLOT_OPTIONS:
                return createSlotOptionsDialog();
            default:
                return super.onCreateDialog(dialogId);
        }
    }

    public void onPrepareDialog(int dialogId, Dialog dialog) {
        switch (dialogId) {
            case DIALOG_SLOT_OPTIONS:
                ((AlertDialog) dialog).setTitle(inventory.get(currentlySelectedSlot).toString());
                return;
            default:
                super.onPrepareDialog(dialogId, dialog);
                return;
        }
    }

    protected AlertDialog createSlotOptionsDialog() {
        return new Builder(this).setTitle("Slot name goes here").setItems(new CharSequence[]{getResources().getText(R.string.slot_duplicate), getResources().getText(R.string.slot_delete)}, new OnClickListener() {
            public void onClick(DialogInterface dialogI, int button) {
                switch (button) {
                    case NBTConstants.TYPE_END:
                        duplicateSelectedSlot();
                        return;
                    case NBTConstants.TYPE_BYTE:
                        deleteSelectedSlot();
                        return;
                    default:
                        return;
                }
            }
        }).create();
    }

    protected void duplicateSelectedSlot() {
        InventorySlot oldSlot = inventory.get(currentlySelectedSlot);
        InventorySlot newSlot = addEmptySlot();
        if (newSlot != null) {
            newSlot.setContents(new ItemStack(oldSlot.getContents()));
        }
        inventoryListAdapter.notifyDataSetChanged();
        EntitiesInfoActivity.saveTileEntities(this);
    }

    protected void deleteSelectedSlot() {
        inventory.remove(currentlySelectedSlot);
        inventoryListAdapter.notifyDataSetChanged();
        EntitiesInfoActivity.saveTileEntities(this);
    }

    protected void showGetProMessage() {
        Toast.makeText(this, R.string.get_pro_to_edit_containers, 0).show();
    }

    protected void repairAllItems() {
        int repairedCount = 0;
        for (InventorySlot slot : inventory) {
            ItemStack stack = slot.getContents();
            if (stack.getDurability() > (short) 0 && RepairableMaterials.isRepairable(stack)) {
                stack.setDurability((short) 0);
                repairedCount++;
            }
        }
        inventoryListAdapter.notifyDataSetChanged();
        EntitiesInfoActivity.saveTileEntities(this);
    }

    protected void openImportLoadoutActivity() {
        Intent intent = new Intent(this, LoadoutImportActivity.class);
        intent.putExtras(getIntent());
        intent.putExtra("IsTileEntity", true);
        startActivity(intent);
    }

    protected void openExportLoadoutActivity() {
        Intent intent = new Intent(this, LoadoutExportActivity.class);
        intent.putExtras(getIntent());
        intent.putExtra("IsTileEntity", true);
        startActivity(intent);
    }
}
