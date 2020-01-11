package com.mcal.pocketinveditor;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;
import com.mcal.pocketinveditor.io.xml.MaterialIconLoader;
import com.mcal.pocketinveditor.io.xml.MaterialLoader;
import com.mcal.pocketinveditor.material.Material;
import com.mcal.pocketinveditor.material.MaterialKey;
import com.mcal.pocketinveditor.material.RepairableMaterials;
import com.mcal.pocketinveditor.material.icon.MaterialIcon;
import com.mcal.pocketinveditor.pro.R;
import java.util.ArrayList;
import java.util.List;
import org.spout.nbt.NBTConstants;

public class InventorySlotsActivity extends Activity implements OnItemClickListener, OnItemLongClickListener, LevelDataLoadListener {
    public static final int DIALOG_CLEAR_INVENTORY = 2;
    public static final int DIALOG_SLOT_OPTIONS = 805;
    public static final int EDIT_SLOT_REQUEST = 534626;
    private int currentlySelectedSlot = -1;
    private View emptyView;
    private GridView gridView;
    private List<InventorySlot> inventory;
    private ArrayAdapter<InventorySlot> inventoryListAdapter;
    protected Intent slotActivityResultIntent = null;
    private List<InventorySlot> tempInventory;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setContentView(R.layout.inventory_slots_grid);
        gridView = findViewById(R.id.grid);
        emptyView = findViewById(R.id.grid_empty);
        gridView.setOnItemClickListener(this);
        gridView.setOnItemLongClickListener(this);
        gridView.setEmptyView(emptyView);
        if (Material.materials == null) {
            new MaterialLoader(getResources().getXml(R.xml.item_data)).run();
            new MaterialIconLoader(this).run();
        }
        int packageNameHash = getPackageName().hashCode();
        if (packageNameHash != 1898279492 && packageNameHash != -1594799101) {
            return;
        }
        if (EditorActivity.level != null) {
            onLevelDataLoad();
        } else {
            EditorActivity.loadLevelData(this, this, getIntent().getStringExtra("world"));
        }
    }

    public void onLevelDataLoad() {
        tempInventory = EditorActivity.level.getPlayer().getInventory();
        int slotsSize = tempInventory.size() - 8;
        if (slotsSize < 0) {
            slotsSize = 0;
        }
        inventory = new ArrayList<>(slotsSize);
        for (InventorySlot slot : tempInventory) {
            if (slot.getSlot() > (byte) 8) {
                inventory.add(slot);
            }
        }
        this.inventoryListAdapter = new ArrayAdapter<InventorySlot>(this, R.layout.slot_grid_item, R.id.slot_list_main_text, inventory) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View retval = super.getView(position, convertView, parent);
                ImageView iconView = retval.findViewById(R.id.slot_list_icon);
                InventorySlot slot = getItem(position);
                ItemStack stack = slot.getContents();
                MaterialIcon icon = MaterialIcon.icons.get(new MaterialKey(stack.getTypeId(), stack.getDurability()));
                if (icon == null) {
                    icon = MaterialIcon.icons.get(new MaterialKey(stack.getTypeId(), (short) 0));
                }
                if (icon != null) {
                    BitmapDrawable myDrawable = new BitmapDrawable(icon.bitmap);
                    myDrawable.setDither(false);
                    myDrawable.setAntiAlias(false);
                    myDrawable.setFilterBitmap(false);
                    iconView.setImageDrawable(myDrawable);
                    iconView.setVisibility(0);
                } else {
                    iconView.setVisibility(4);
                }
                ((TextView) retval.findViewById(R.id.slot_list_slot_name)).setText("Slot " + slot.getSlot());
                ((TextView) retval.findViewById(R.id.slot_list_slot_amount)).setText(Integer.toString(stack.getAmount()));
                return retval;
            }
        };
        gridView.setAdapter(inventoryListAdapter);
        inventoryListAdapter.notifyDataSetChanged();
        if (slotActivityResultIntent != null) {
            onSlotActivityResult(slotActivityResultIntent);
        }
    }

    public void onStart() {
        super.onStart();
        EditorActivity.loadLevelData(this, this, getIntent().getStringExtra("world"));
    }

    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
        openInventoryEditScreen(position, inventory.get(position));
    }

    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
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
            slotActivityResultIntent = intent;
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
        EditorActivity.save(this);
        slotActivityResultIntent = null;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(getResources().getString(R.string.add_empty_slot));
        menu.add(getResources().getString(R.string.loadout_export));
        menu.add(getResources().getString(R.string.loadout_import));
        menu.add(getResources().getString(R.string.clear_inventory));
        if (getIntent().getBooleanExtra("CanEditArmor", false)) {
            menu.add(getResources().getString(R.string.armor_edit));
        } else {
            menu.add(getResources().getString(R.string.armor_view));
        }
        menu.add(getResources().getString(R.string.inventory_repair_all));
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle() == null) {
            return super.onOptionsItemSelected(item);
        }
        if (item.getTitle().equals(getResources().getString(R.string.add_empty_slot))) {
            InventorySlot newSlot = addEmptySlot();
            if (newSlot == null) {
                return true;
            }
            openInventoryEditScreen(inventoryListAdapter.getPosition(newSlot), newSlot);
            return true;
        } else if (item.getTitle().equals(getResources().getString(R.string.loadout_export))) {
            openExportLoadoutActivity();
            return true;
        } else if (item.getTitle().equals(getResources().getString(R.string.loadout_import))) {
            openImportLoadoutActivity();
            return true;
        } else if (item.getTitle().equals(getResources().getString(R.string.clear_inventory))) {
            showDialog(DIALOG_CLEAR_INVENTORY);
            return true;
        } else {
            if (item.getTitle().equals(getResources().getString(R.string.armor_view)) || item.getTitle().equals(getResources().getString(R.string.armor_edit))) {
                openEditArmorActivity();
            } else if (item.getTitle().equals(getResources().getString(R.string.inventory_repair_all))) {
                repairAllItems();
            }
            return super.onOptionsItemSelected(item);
        }
    }

    private InventorySlot addEmptySlot() {
        if (this.inventory.size() > 35) {
            return null;
        }
        List<InventorySlot> outInventory = new ArrayList<>();
        for (int i = 0; i < tempInventory.size(); i++) {
            if (tempInventory.get(i).getSlot() < (byte) 9) {
                outInventory.add(tempInventory.get(i));
            }
        }
        InventorySlot slot = new InventorySlot((byte) (inventory.size() + 9), new ItemStack((short) 0, (short) 0, 1));
        alignSlots();
        inventory.add(slot);
        inventoryListAdapter.notifyDataSetChanged();
        outInventory.addAll(inventory);
        EditorActivity.level.getPlayer().setInventory(outInventory);
        EditorActivity.save(this);
        return slot;
    }

    private void alignSlots() {
        for (int i = 0; i < inventory.size(); i++) {
            inventory.get(i).setSlot((byte) (i + 9));
        }
    }

    public Dialog onCreateDialog(int dialogId) {
        switch (dialogId) {
            case DIALOG_CLEAR_INVENTORY:
                return createClearInventoryDialog();
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
        CharSequence[] options = new CharSequence[DIALOG_CLEAR_INVENTORY];
        options[0] = getResources().getText(R.string.slot_duplicate);
        options[1] = getResources().getText(R.string.slot_delete);
        return new Builder(this).setTitle("Slot name goes here").setItems(options, new OnClickListener() {
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

    protected AlertDialog createClearInventoryDialog() {
        return new Builder(this).setMessage(R.string.clear_inventory_are_you_sure).setPositiveButton(R.string.clear_inventory_clear, new OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                EditorActivity.level.getPlayer().getInventory().clear();
                EditorActivity.save(InventorySlotsActivity.this);
                inventoryListAdapter.notifyDataSetChanged();
            }
        }).setNegativeButton(android.R.string.cancel, null).create();
    }

    protected void duplicateSelectedSlot() {
        InventorySlot oldSlot = inventory.get(currentlySelectedSlot);
        InventorySlot newSlot = addEmptySlot();
        if (newSlot != null) {
            newSlot.setContents(new ItemStack(oldSlot.getContents()));
        }
        this.inventoryListAdapter.notifyDataSetChanged();
        EditorActivity.save(this);
    }

    protected void deleteSelectedSlot() {
        EditorActivity.level.getPlayer().getInventory().remove(inventory.get(currentlySelectedSlot));
        inventory.remove(currentlySelectedSlot);
        inventoryListAdapter.notifyDataSetChanged();
        EditorActivity.save(this);
    }

    protected void openImportLoadoutActivity() {
        startActivity(new Intent(this, LoadoutImportActivity.class));
    }

    protected void openExportLoadoutActivity() {
        startActivity(new Intent(this, LoadoutExportActivity.class));
    }

    protected void openEditArmorActivity() {
        Intent intent = new Intent(this, ArmorSlotsActivity.class);
        intent.putExtras(getIntent());
        startActivity(intent);
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
        EditorActivity.save(this);
    }
}
