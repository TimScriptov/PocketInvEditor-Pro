package com.mcal.pocketinveditor;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.mcal.pocketinveditor.io.xml.MaterialIconLoader;
import com.mcal.pocketinveditor.io.xml.MaterialLoader;
import com.mcal.pocketinveditor.material.Material;
import com.mcal.pocketinveditor.material.MaterialKey;
import com.mcal.pocketinveditor.material.RepairableMaterials;
import com.mcal.pocketinveditor.material.icon.MaterialIcon;
import com.mcal.pocketinveditor.pro.R;
import java.util.List;

public final class ArmorSlotsActivity extends ListActivity implements OnItemLongClickListener, LevelDataLoadListener {
    public static final int DIALOG_SLOT_OPTIONS = 805;
    public static final int EDIT_SLOT_REQUEST = 534626;
    private int currentlySelectedSlot = -1;
    private List<ItemStack> inventory;
    private ArrayAdapter<ItemStack> inventoryListAdapter;
    protected Intent slotActivityResultIntent = null;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
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
        this.inventory = EditorActivity.level.getPlayer().getArmor();
        if (this.inventory != null) {
            this.inventoryListAdapter = new ArrayAdapter<ItemStack>(this, R.layout.slot_list_item, R.id.slot_list_main_text, inventory) {
                public View getView(int position, View convertView, ViewGroup parent) {
                    View retval = super.getView(position, convertView, parent);
                    ImageView iconView = retval.findViewById(R.id.slot_list_icon);
                    ItemStack stack = getItem(position);
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
                    return retval;
                }
            };
            setListAdapter(inventoryListAdapter);
            inventoryListAdapter.notifyDataSetChanged();
            if (slotActivityResultIntent != null) {
                onSlotActivityResult(slotActivityResultIntent);
            }
        }
    }

    public void onStart() {
        super.onStart();
        if (EditorActivity.level != null && inventoryListAdapter != null) {
            inventoryListAdapter.notifyDataSetChanged();
        }
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        if (getIntent().getBooleanExtra("CanEditArmor", false)) {
            openInventoryEditScreen(position, inventory.get(position));
        } else {
            showGetProMessage();
        }
    }

    public boolean onItemLongClick(AdapterView<?> adapterView, View view, int position, long id) {
        if (!getIntent().getBooleanExtra("CanEditArmor", false)) {
            return false;
        }
        currentlySelectedSlot = position;
        showDialog(DIALOG_SLOT_OPTIONS);
        return true;
    }

    private void openInventoryEditScreen(int position, ItemStack stack) {
        Intent intent = new Intent(this, EditInventorySlotActivity.class);
        intent.putExtra("TypeId", stack.getTypeId());
        intent.putExtra("Damage", stack.getDurability());
        intent.putExtra("Count", stack.getAmount());
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
        ItemStack stack = inventory.get(slotIndex);
        stack.setAmount(intent.getIntExtra("Count", 0));
        stack.setDurability(intent.getShortExtra("Damage", (short) 0));
        stack.setTypeId(intent.getShortExtra("TypeId", (short) 0));
        EditorActivity.save(this);
        this.slotActivityResultIntent = null;
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (!getIntent().getBooleanExtra("CanEditArmor", false)) {
            return false;
        }
        super.onCreateOptionsMenu(menu);
        menu.add(getResources().getString(R.string.inventory_repair_all));
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(getResources().getString(R.string.inventory_repair_all))) {
            repairAllItems();
        }
        return super.onOptionsItemSelected(item);
    }

    protected void showGetProMessage() {
        Toast.makeText(this, R.string.armor_get_pro_to_edit, 0).show();
    }

    protected void repairAllItems() {
        int repairedCount = 0;
        for (ItemStack stack : inventory) {
            if (stack.getDurability() > (short) 0 && RepairableMaterials.isRepairable(stack)) {
                stack.setDurability((short) 0);
                repairedCount++;
            }
        }
        inventoryListAdapter.notifyDataSetChanged();
        EditorActivity.save(this);
    }
}
