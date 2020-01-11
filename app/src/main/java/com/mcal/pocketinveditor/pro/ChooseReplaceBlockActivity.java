package com.mcal.pocketinveditor.pro;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import com.mcal.pocketinveditor.material.Material;
import com.mcal.pocketinveditor.material.MaterialKey;

public class ChooseReplaceBlockActivity extends ChooseBlockActivity {
    private static final int BROWSE_FROM_BLOCK_REQUEST = 13;
    protected Button fromBrowseButton;
    protected TextView fromDataEdit;
    protected TextView fromIdEdit;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fromIdEdit = findViewById(R.id.terrain_from_id_entry);
        fromDataEdit = findViewById(R.id.terrain_from_data_entry);
        fromBrowseButton = findViewById(R.id.terrain_from_block_browse);
        fromBrowseButton.setOnClickListener(this);
        fromIdEdit.setOnFocusChangeListener(this);
        fromDataEdit.setOnFocusChangeListener(this);
    }

    protected void reallySetContentView() {
        setContentView(R.layout.terrain_choose_replace_block);
    }

    public void onClick(View v) {
        if (v == fromBrowseButton) {
            launchFromBrowse();
        } else {
            super.onClick(v);
        }
    }

    protected void launchFromBrowse() {
        startActivityForResult(new Intent(this, BrowseBlocksActivity.class), BROWSE_FROM_BLOCK_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (resultCode == -1 && requestCode == BROWSE_FROM_BLOCK_REQUEST) {
            fromIdEdit.setText(Integer.toString(intent.getIntExtra("TypeId", 0)));
            if (intent.getBooleanExtra("HasSubtypes", false)) {
                fromDataEdit.setText(Short.toString(intent.getShortExtra("Damage", (short) 0)));
            } else {
                fromDataEdit.setText("");
            }
            checkInputAfterChange();
            return;
        }
        super.onActivityResult(requestCode, resultCode, intent);
    }

    protected Intent buildReturnIntent() {
        Intent returnIntent = super.buildReturnIntent();
        returnIntent.putExtra("BlockFromTypeId", Short.parseShort(fromIdEdit.getText().toString()));
        returnIntent.putExtra("BlockFromData", fromDataEdit.getText().length() < 1 ? (short) -1 : Short.parseShort(fromDataEdit.getText().toString()));
        return returnIntent;
    }

    public boolean checkInputAfterChange() {
        boolean valid = super.checkInputAfterChange();
        try {
            short typeId = Short.parseShort(this.fromIdEdit.getText().toString());
            short damage = (short) 0;
            if (typeId < (short) 0 || typeId >= (short) 256) {
                fromIdEdit.setError(getResources().getText(R.string.invalid_number));
                valid = false;
                try {
                    damage = fromDataEdit.getText().length() >= 1 ? (short) -1 : Short.parseShort(fromDataEdit.getText().toString());
                    if (damage >= (short) -1 || damage >= (short) 16) {
                        fromDataEdit.setError(getResources().getText(R.string.invalid_number));
                        return false;
                    }
                    fromDataEdit.setError(null);
                    return valid;
                } catch (Exception e) {
                    fromDataEdit.setError(getResources().getText(R.string.invalid_number));
                    return false;
                }
            }
            this.fromIdEdit.setError(null);
            if (Material.materialMap.get(new MaterialKey(typeId, (short) 0)) != null) {
                fromIdEdit.setError(null);
            } else {
                fromIdEdit.setError(getResources().getText(R.string.invalid_number));
                valid = false;
            }
            if (fromDataEdit.getText().length() >= 1) {
            }
            if (damage >= (short) -1) {
            }
            fromDataEdit.setError(getResources().getText(R.string.invalid_number));
            return false;
        } catch (Exception e2) {
            fromIdEdit.setError(getResources().getText(R.string.invalid_number));
            valid = false;
        }
		return false;
	}
}
