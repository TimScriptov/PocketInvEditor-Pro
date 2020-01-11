package com.mcal.pocketinveditor.pro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Process;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.TextView;
import com.mcal.pocketinveditor.EditInventorySlotActivity;
import com.mcal.pocketinveditor.material.Material;
import com.mcal.pocketinveditor.material.MaterialKey;

public class ChooseBlockActivity extends Activity implements OnClickListener, OnFocusChangeListener {
    protected Button blockBrowseButton;
    protected TextView dataEdit;
    protected TextView idEdit;
    protected Button okButton;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        reallySetContentView();
        setResult(0);
        idEdit = findViewById(R.id.terrain_id_entry);
        dataEdit = findViewById(R.id.terrain_data_entry);
        blockBrowseButton = findViewById(R.id.terrain_block_browse);
        blockBrowseButton.setOnClickListener(this);
        okButton = findViewById(R.id.terrain_ok);
        okButton.setOnClickListener(this);
        idEdit.setOnFocusChangeListener(this);
        dataEdit.setOnFocusChangeListener(this);
        String titleText = getIntent().getStringExtra("CustomChooseBlockTitle");
        if (titleText != null) {
            setTitle(titleText);
        }
    }

    protected void reallySetContentView() {
        setContentView(R.layout.terrain_choose_block);
    }

    public void onClick(View v) {
        if (v == blockBrowseButton) {
            showBrowseItemIdActivity();
        } else if (v == okButton && checkInputAfterChange()) {
            returnInput();
        }
    }

    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            checkInputAfterChange();
        }
    }

    protected void showBrowseItemIdActivity() {
        startActivityForResult(new Intent(this, BrowseBlocksActivity.class), EditInventorySlotActivity.BROWSE_ITEM_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == EditInventorySlotActivity.BROWSE_ITEM_REQUEST && resultCode == -1) {
            this.idEdit.setText(Integer.toString(intent.getIntExtra("TypeId", 0)));
            if (intent.getBooleanExtra("HasSubtypes", false)) {
                dataEdit.setText(Short.toString(intent.getShortExtra("Damage", (short) 0)));
            }
            checkInputAfterChange();
        }
    }

    public boolean checkInputAfterChange() {
        boolean valid = true;
        try {
            short typeId = Short.parseShort(idEdit.getText().toString());
            short damage = 0;
            if (typeId < (short) 0 || typeId >= (short) 256) {
                idEdit.setError(getResources().getText(R.string.invalid_number));
                valid = false;
                try {
                    damage = dataEdit.getText().length() >= 1 ? (short) 0 : Short.parseShort(dataEdit.getText().toString());
                    if (damage >= (short) 0 || damage >= (short) 16) {
                        dataEdit.setError(getResources().getText(R.string.invalid_number));
                        return false;
                    }
                    dataEdit.setError(null);
                    return valid;
                } catch (Exception e) {
                    dataEdit.setError(getResources().getText(R.string.invalid_number));
                    return false;
                }
            }
            this.idEdit.setError(null);
            if (Material.materialMap.get(new MaterialKey(typeId, (short) 0)) != null) {
                idEdit.setError(null);
            } else {
                idEdit.setError(getResources().getText(R.string.invalid_number));
                valid = false;
            }
            if (dataEdit.getText().length() >= 1) {
            }
            if (damage >= (short) 0) {
            }
            dataEdit.setError(getResources().getText(R.string.invalid_number));
            return false;
        } catch (Exception e2) {
            idEdit.setError(getResources().getText(R.string.invalid_number));
            valid = false;
        }
		return false;
	}

    protected Intent buildReturnIntent() {
        Intent returnIntent = new Intent();
        returnIntent.putExtra("BlockTypeId", Short.parseShort(idEdit.getText().toString()));
        returnIntent.putExtra("BlockData", dataEdit.getText().length() < 1 ? (short) -1 : Short.parseShort(dataEdit.getText().toString()));
        return returnIntent;
    }

    public void returnInput() {
        setResult(-1, buildReturnIntent());
        finish();
    }
}
