package com.mcal.pocketinveditor.pro;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.TextView;
import com.mcal.pocketinveditor.EditorActivity;
import com.mcal.pocketinveditor.EntitiesInfoActivity;
import com.mcal.pocketinveditor.tileentity.SignTileEntity;

public class EditSignActivity extends Activity implements OnFocusChangeListener {
    private TextView[] lineViews = new TextView[4];
    private SignTileEntity signTileEntity;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_sign);
        lineViews[0] = findViewById(R.id.sign_text_line_1);
        lineViews[0].setOnFocusChangeListener(this);
        lineViews[1] = findViewById(R.id.sign_text_line_2);
        lineViews[1].setOnFocusChangeListener(this);
        lineViews[2] = findViewById(R.id.sign_text_line_3);
        lineViews[2].setOnFocusChangeListener(this);
        lineViews[3] = findViewById(R.id.sign_text_line_4);
        lineViews[3].setOnFocusChangeListener(this);
        onLevelDataLoad();
    }

    public void onLevelDataLoad() {
        this.signTileEntity = (SignTileEntity) EditorActivity.level.getTileEntities().get(getIntent().getIntExtra("Index", -1));
        String[] lines = signTileEntity.getLines();
        for (int i = 0; i < lines.length; i++) {
            lineViews[i].setText(lines[i]);
        }
    }

    public void onFocusChange(View v, boolean hasFocus) {
        if (!hasFocus) {
            for (int i = 0; i < lineViews.length; i++) {
                if (lineViews[i] == v) {
                    TextView myView = (TextView) v;
                    if (!myView.getText().equals(signTileEntity.getLine(i))) {
                        signTileEntity.setLine(i, myView.getText().toString());
                        EntitiesInfoActivity.saveTileEntities(this);
                        return;
                    }
                    return;
                }
            }
        }
    }

    public void onPause() {
        super.onPause();
        boolean needsSaving = false;
        for (int i = 0; i < lineViews.length; i++) {
            TextView myView = lineViews[i];
            if (!myView.getText().equals(signTileEntity.getLine(i))) {
                System.out.println(myView.getText() + ":" + signTileEntity.getLine(i));
                signTileEntity.setLine(i, myView.getText().toString());
                needsSaving = true;
            }
        }
        if (needsSaving) {
            EntitiesInfoActivity.saveTileEntities(this);
        }
    }
}
