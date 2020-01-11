package com.mcal.pocketinveditor;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import com.mcal.pocketinveditor.material.MaterialKey;
import com.mcal.pocketinveditor.material.icon.MaterialIcon;
import com.mcal.pocketinveditor.pro.R;
import java.util.List;

public class MaterialIconArrayAdapter<T extends InventorySlot> extends ArrayAdapter<T> {
    public MaterialIconArrayAdapter(Context context, int resource, int textViewResourceId, List<T> objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View retval = super.getView(position, convertView, parent);
        ImageView iconView = retval.findViewById(R.id.slot_list_icon);
        ItemStack stack = ((InventorySlot) getItem(position)).getContents();
        MaterialIcon icon = MaterialIcon.icons.get(new MaterialKey(stack.getTypeId(), stack.getDurability()));
        if (icon == null) {
            icon = MaterialIcon.icons.get(new MaterialKey(stack.getTypeId(), (short) 0));
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
}
