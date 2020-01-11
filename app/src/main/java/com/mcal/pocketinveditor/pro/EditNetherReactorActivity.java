package com.mcal.pocketinveditor.pro;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.TextView;
import com.mcal.pocketinveditor.EditorActivity;
import com.mcal.pocketinveditor.EntitiesInfoActivity;
import com.mcal.pocketinveditor.tileentity.NetherReactorTileEntity;

public class EditNetherReactorActivity extends Activity implements OnClickListener, OnFocusChangeListener {
    private CheckedTextView hasFinishedCheck;
    private CheckedTextView isInitializedCheck;
    private Button overtimeButton;
    private TextView progressText;
    private NetherReactorTileEntity tileEntity;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_reactor);
        isInitializedCheck = findViewById(R.id.reactor_is_initialized);
        isInitializedCheck.setOnClickListener(this);
        hasFinishedCheck = findViewById(R.id.reactor_has_finished);
        hasFinishedCheck.setOnClickListener(this);
        progressText = findViewById(R.id.reactor_progress);
        progressText.setOnFocusChangeListener(this);
        overtimeButton = findViewById(R.id.reactor_overtime);
        overtimeButton.setOnClickListener(this);
        onLevelDataLoad();
    }

    public void onLevelDataLoad() {
        tileEntity = (NetherReactorTileEntity) EditorActivity.level.getTileEntities().get(getIntent().getIntExtra("Index", -1));
        isInitializedCheck.setChecked(tileEntity.isInitialized());
        hasFinishedCheck.setChecked(tileEntity.hasFinished());
        progressText.setText(Short.toString(tileEntity.getProgress()));
    }

    public void onPause() {
        super.onPause();
        checkAndStore();
    }

    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus && v == progressText) {
            checkAndStore();
        }
    }

    public void onClick(View v) {
        if (v == overtimeButton) {
            tileEntity.setInitialized(true);
            tileEntity.setFinished(false);
            tileEntity.setProgress(Short.MIN_VALUE);
            isInitializedCheck.setChecked(tileEntity.isInitialized());
            hasFinishedCheck.setChecked(tileEntity.hasFinished());
            progressText.setText(Short.toString(tileEntity.getProgress()));
            EntitiesInfoActivity.saveTileEntities(this);
        } else if (v == isInitializedCheck) {
            isInitializedCheck.toggle();
            checkAndStore();
        } else if (v == hasFinishedCheck) {
            hasFinishedCheck.toggle();
            checkAndStore();
        }
    }

    public void checkAndStore() {
        boolean needsSaving = false;
        if (tileEntity.isInitialized() != isInitializedCheck.isChecked()) {
            tileEntity.setInitialized(isInitializedCheck.isChecked());
            needsSaving = true;
        }
        if (tileEntity.hasFinished() != hasFinishedCheck.isChecked()) {
            tileEntity.setFinished(hasFinishedCheck.isChecked());
            needsSaving = true;
        }
        try {
            if (tileEntity.getProgress() != Short.parseShort(progressText.getText().toString())) {
                tileEntity.setProgress(Short.parseShort(progressText.getText().toString()));
                needsSaving = true;
            }
        } catch (Exception e) {
            progressText.setError(getResources().getText(R.string.invalid_number));
        }
        if (needsSaving) {
            EntitiesInfoActivity.saveTileEntities(this);
        }
    }
}
