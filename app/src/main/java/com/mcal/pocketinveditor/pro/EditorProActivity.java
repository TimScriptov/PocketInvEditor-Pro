package com.mcal.pocketinveditor.pro;

import android.content.Intent;
import android.os.Bundle;
import com.mcal.pocketinveditor.EditorActivity;
import com.mcal.pocketinveditor.InventorySlotsActivity;

public class EditorProActivity extends EditorActivity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        entitiesInfoButton.setText(R.string.entities_edit);
        viewTileEntitiesButton.setText(R.string.main_edit_tileentities);
        editTerrainButton.setVisibility(0);
    }

    public void startEntitiesInfo() {
        startActivityWithExtras(new Intent(this, EntitiesEditActivity.class));
    }

    public void startViewTileEntities() {
        Intent intent = new Intent(this, TileEntityEditActivity.class);
        intent.putExtra("CanEditSlots", true);
        startActivityWithExtras(intent);
    }

    public void startEditTerrain() {
        startActivityWithExtras(new Intent(this, EditTerrainActivity.class));
    }

    protected void startInventoryEditor() {
        Intent intent = new Intent(this, InventorySlotsActivity.class);
        intent.putExtra("CanEditArmor", true);
        startActivityWithExtras(intent);
    }
}
