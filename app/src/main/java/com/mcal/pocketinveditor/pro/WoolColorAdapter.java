package com.mcal.pocketinveditor.pro;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import com.mcal.pocketinveditor.DyeColor;

public class WoolColorAdapter extends ArrayAdapter<DyeColor> {
    public WoolColorAdapter(Context context, int resource, int textViewResourceId) {
        super(context, resource, textViewResourceId, DyeColor.values());
    }

    public WoolColorAdapter(Context context, int resource, int textViewResourceId, DyeColor[] objects) {
        super(context, resource, textViewResourceId, objects);
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View retval = super.getView(position, convertView, parent);
        retval.setBackgroundColor(getItem(position).color);
        return retval;
    }
}
