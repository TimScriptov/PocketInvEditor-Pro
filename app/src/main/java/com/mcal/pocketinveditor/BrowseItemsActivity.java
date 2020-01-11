package com.mcal.pocketinveditor;

import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import com.mcal.pocketinveditor.io.xml.MaterialIconLoader;
import com.mcal.pocketinveditor.io.xml.MaterialLoader;
import com.mcal.pocketinveditor.material.Material;
import com.mcal.pocketinveditor.material.MaterialKey;
import com.mcal.pocketinveditor.material.icon.MaterialIcon;
import com.mcal.pocketinveditor.pro.R;
import java.util.List;

public class BrowseItemsActivity extends ListActivity implements TextWatcher, OnItemClickListener {
    private EditText filterInputText;
    private ArrayAdapter<Material> itemsListAdapter;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setResult(0);
        if (Material.materials == null) {
            new MaterialLoader(getResources().getXml(R.xml.item_data)).run();
            new MaterialIconLoader(this).run();
        }
        setContentView(R.layout.item_id_browse);
        this.itemsListAdapter = new ArrayAdapter<Material>(this, R.layout.item_id_list_item, R.id.item_id_main_text, getMaterialsForList()) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View retval = super.getView(position, convertView, parent);
                ImageView iconView = retval.findViewById(R.id.item_id_icon);
                Material material = getItem(position);
                MaterialIcon icon = MaterialIcon.icons.get(new MaterialKey((short) material.getId(), material.getDamage()));
                if (icon == null) {
                    icon = MaterialIcon.icons.get(new MaterialKey((short) material.getId(), (short) 0));
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
        filterInputText = findViewById(R.id.item_id_browse_filter_text);
        filterInputText.addTextChangedListener(this);
        setListAdapter(itemsListAdapter);
        getListView().setOnItemClickListener(this);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent returnIntent = new Intent();
        Material material = (Material) parent.getItemAtPosition(position);
        returnIntent.putExtra("TypeId", material.getId());
        returnIntent.putExtra("HasSubtypes", material.hasSubtypes());
        returnIntent.putExtra("Damage", material.getDamage());
        setResult(-1, returnIntent);
        finish();
    }

    public void afterTextChanged(Editable e) {
    }

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        itemsListAdapter.getFilter().filter(s);
    }

    protected List<Material> getMaterialsForList() {
        return Material.materials;
    }
}
