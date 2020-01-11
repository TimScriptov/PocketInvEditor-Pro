package com.mcal.pocketinveditor.pro;

import android.app.ListActivity;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.mcal.pocketinveditor.io.xml.MaterialIconLoader;
import com.mcal.pocketinveditor.io.xml.MaterialLoader;
import com.mcal.pocketinveditor.material.Material;
import com.mcal.pocketinveditor.material.MaterialCount;
import com.mcal.pocketinveditor.material.MaterialKey;
import com.mcal.pocketinveditor.material.icon.MaterialIcon;
import java.util.List;

public class BlockCountsActivity extends ListActivity {
    public static List<MaterialCount> currentCounts;
    private List<MaterialCount> counts;
    private ArrayAdapter<MaterialCount> countsAdapter;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (currentCounts == null) {
            finish();
        }
        if (Material.materials == null) {
            new MaterialLoader(getResources().getXml(R.xml.item_data)).run();
            new MaterialIconLoader(this).run();
        }
        this.counts = currentCounts;
        this.countsAdapter = new ArrayAdapter<MaterialCount>(this, R.layout.item_id_list_item, R.id.item_id_main_text, this.counts) {
            public View getView(int position, View convertView, ViewGroup parent) {
                View retval = super.getView(position, convertView, parent);
                ImageView iconView = retval.findViewById(R.id.item_id_icon);
                MaterialCount myCount = getItem(position);
                MaterialIcon icon = MaterialIcon.icons.get(myCount.key);
                if (icon == null) {
                    icon = MaterialIcon.icons.get(new MaterialKey(myCount.key.typeId, (short) 0));
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
        setListAdapter(countsAdapter);
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(getResources().getString(17039361));
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getTitle().equals(getResources().getString(17039361))) {
            ((ClipboardManager) getSystemService("clipboard")).setText(buildCSV());
        }
        return super.onOptionsItemSelected(item);
    }

    public String buildCSV() {
        StringBuilder b = new StringBuilder();
        b.append("ID,Data,Amount\n");
        for (int i = 0; i < counts.size(); i++) {
            MaterialCount count = counts.get(i);
            b.append(count.key.typeId);
            b.append(",");
            b.append(count.key.damage);
            b.append(",");
            b.append(count.count);
            b.append("\n");
        }
        return b.toString();
    }
}
