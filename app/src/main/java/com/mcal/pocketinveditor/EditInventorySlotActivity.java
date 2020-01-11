package com.mcal.pocketinveditor;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.EditText;
import com.mcal.pocketinveditor.pro.R;

public class EditInventorySlotActivity extends Activity implements OnClickListener, OnFocusChangeListener {
    public static final int BROWSE_ITEM_REQUEST = 500;
    public static final int MAX_SLOT_SIZE = 64;
    private Button browseItemIdButton;
    private EditText countEdit;
    private EditText damageEdit;
    private Button fillSlotButton;
    private EditText idEdit;
    private Button maxCountToSlotButton;
    private int originalCount;
    private short originalDamage;
    private short originalTypeId;
    private Intent returnIntent = new Intent();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.inventoryslot);
        setResult(0);
        idEdit = findViewById(R.id.slot_identry);
        damageEdit = findViewById(R.id.slot_damageentry);
        countEdit = findViewById(R.id.slot_countentry);
        browseItemIdButton = findViewById(R.id.slot_browseitemid);
        browseItemIdButton.setOnClickListener(this);
        fillSlotButton = findViewById(R.id.slot_fillslot);
        fillSlotButton.setOnClickListener(this);
        maxCountToSlotButton = findViewById(R.id.slot_maxcount);
        maxCountToSlotButton.setOnClickListener(this);
        Intent intent = getIntent();
        originalTypeId = intent.getShortExtra("TypeId", (short) 0);
        originalDamage = intent.getShortExtra("Damage", (short) 0);
        originalCount = intent.getIntExtra("Count", 0);
        idEdit.setText(Short.toString(originalTypeId));
        damageEdit.setText(Short.toString(originalDamage));
        countEdit.setText(Integer.toString(originalCount));
        idEdit.setOnFocusChangeListener(this);
        damageEdit.setOnFocusChangeListener(this);
        countEdit.setOnFocusChangeListener(this);
        returnIntent.putExtras(intent);
    }

    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            checkInputAfterChange();
        }
    }

    private boolean checkInputAfterChange() {
        boolean isCorrect = true;
        short newId = originalTypeId;
        short newDamage = originalDamage;
        int newCount = originalCount;
        try {
            newId = Short.parseShort(idEdit.getText().toString());
            idEdit.setText(Short.toString(newId));
            idEdit.setError(null);
        } catch (NumberFormatException e) {
            idEdit.setError(getResources().getText(R.string.invalid_number));
            isCorrect = false;
        }
        try {
            newDamage = Short.parseShort(damageEdit.getText().toString());
            damageEdit.setText(Short.toString(newDamage));
            damageEdit.setError(null);
        } catch (NumberFormatException e2) {
            damageEdit.setError(getResources().getText(R.string.invalid_number));
            isCorrect = false;
        }
        try {
            newCount = Integer.parseInt(this.countEdit.getText().toString());
            if (newCount < 0 || newCount > 255) {
                throw new NumberFormatException("derp");
            }
            countEdit.setText(Integer.toString(newCount));
            countEdit.setError(null);
            if (isCorrect) {
                returnIntent.putExtra("TypeId", newId);
                returnIntent.putExtra("Damage", newDamage);
                returnIntent.putExtra("Count", newCount);
                if (newId == originalTypeId && newDamage == originalDamage && newCount == originalCount) {
                    setResult(0);
                } else {
                    setResult(-1, returnIntent);
                }
            } else {
                setResult(0);
            }
            return isCorrect;
        } catch (NumberFormatException e3) {
            countEdit.setError(getResources().getText(R.string.invalid_number));
            isCorrect = false;
        }
		return isCorrect;
    }

    public void onBackPressed() {
        if (checkInputAfterChange()) {
            super.onBackPressed();
        }
    }

    private boolean checkAllInput() {
        try {
            Short.decode(idEdit.getText().toString());
            Short.decode(damageEdit.getText().toString());
            Short.decode(countEdit.getText().toString());
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    public void onClick(View v) {
        if (v == browseItemIdButton) {
            showBrowseItemIdActivity();
        } else if (v == fillSlotButton) {
            fillSlotToMax();
        } else if (v == maxCountToSlotButton) {
            fillSlotToMaxByteValue();
        }
    }

    public void showBrowseItemIdActivity() {
        startActivityForResult(new Intent(this, BrowseItemsActivity.class), BROWSE_ITEM_REQUEST);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == BROWSE_ITEM_REQUEST && resultCode == -1) {
            idEdit.setText(Integer.toString(intent.getIntExtra("TypeId", 0)));
            if (intent.getBooleanExtra("HasSubtypes", false)) {
                damageEdit.setText(Short.toString(intent.getShortExtra("Damage", (short) 0)));
            }
            checkInputAfterChange();
        }
    }

    private void fillSlotToMax() {
        returnIntent.putExtra("Count", MAX_SLOT_SIZE);
        if (MAX_SLOT_SIZE != originalCount) {
            setResult(-1, returnIntent);
        }
        countEdit.setText(Integer.toString(MAX_SLOT_SIZE));
    }

    private void fillSlotToMaxByteValue() {
        returnIntent.putExtra("Count", 255);
        if (255 != originalCount) {
            setResult(-1, returnIntent);
        }
        countEdit.setText(Integer.toString(255));
    }
}
