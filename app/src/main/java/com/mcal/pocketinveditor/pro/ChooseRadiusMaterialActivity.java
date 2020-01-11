package com.mcal.pocketinveditor.pro;

import android.content.Intent;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.TextView;

public class ChooseRadiusMaterialActivity extends ChooseBlockActivity {
    protected CheckBox hollowBox;
    protected TextView radiusEdit;
    protected TextView radiusPrompt;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        radiusEdit = findViewById(R.id.terrain_radius_entry);
        radiusEdit.setOnFocusChangeListener(this);
        hollowBox = findViewById(R.id.terrain_hollow_entry);
        radiusPrompt = findViewById(R.id.terrain_radius_prompt);
        String radTitleText = getIntent().getStringExtra("CustomRadiusTitle");
        if (radTitleText != null) {
            radiusPrompt.setText(radTitleText);
        }
        if (getIntent().getBooleanExtra("ShowHollowCheckBox", false)) {
            hollowBox.setVisibility(0);
        }
    }

    protected void reallySetContentView() {
        setContentView(R.layout.terrain_choose_radius_material);
    }

    public boolean checkInputAfterChange() {
        if (!super.checkInputAfterChange()) {
            return false;
        }
        boolean success;
        try {
            int radius = Integer.parseInt(radiusEdit.getText().toString());
            if (!true || radius < 1) {
                success = false;
            } else {
                success = true;
            }
        } catch (Exception e) {
            success = false;
        }
        return success;
    }

    protected Intent buildReturnIntent() {
        Intent returnIntent = super.buildReturnIntent();
        returnIntent.putExtra("Radius", Integer.parseInt(radiusEdit.getText().toString()));
        returnIntent.putExtra("Hollow", hollowBox.isChecked());
        return returnIntent;
    }
}
