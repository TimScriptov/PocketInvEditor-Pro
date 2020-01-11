package com.mcal.pocketinveditor.pro;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.Spinner;
import com.mcal.pocketinveditor.EditorActivity;
import com.mcal.pocketinveditor.EntitiesInfoActivity;
import com.mcal.pocketinveditor.entity.EntityType;
import com.mcal.pocketinveditor.tileentity.MobSpawnerTileEntity;

public class EditMobSpawnerActivity extends Activity implements OnClickListener, OnFocusChangeListener, OnItemSelectedListener {
    private Spinner mobTypeSpinner;
    private MobSpawnerTileEntity tileEntity;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_mob_spawner);
        mobTypeSpinner = findViewById(R.id.entities_type_spinner);
        EntitiesNameArrays.setSpinner(mobTypeSpinner, EntitiesNameArrays.getMobTypesWithNone(this));
        mobTypeSpinner.setOnItemSelectedListener(this);
        onLevelDataLoad();
    }

    public void onLevelDataLoad() {
        tileEntity = (MobSpawnerTileEntity) EditorActivity.level.getTileEntities().get(getIntent().getIntExtra("Index", -1));
        System.out.println("Entity id: " + tileEntity.getEntityId());
        mobTypeSpinner.setSelection(indexOf(EntitiesEditActivity.mobTypes, EntityType.getById(tileEntity.getEntityId())) + 1);
    }

    public void onPause() {
        super.onPause();
    }

    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
        }
    }

    public void onClick(View v) {
    }

    public void checkAndStore() {
    }

    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        if (parent == mobTypeSpinner) {
            int index = position - 1;
            int entityId = 0;
            if (index >= 0) {
                entityId = EntitiesEditActivity.mobTypes[index].getId();
            }
            if (tileEntity.getEntityId() != entityId) {
                tileEntity.setEntityId(entityId);
                EntitiesInfoActivity.saveTileEntities(this);
            }
        }
    }

    public void onNothingSelected(AdapterView<?> adapterView) {
    }

    public static int indexOf(Object[] arr, Object obj) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i].equals(obj)) {
                return i;
            }
        }
        return -1;
    }
}
