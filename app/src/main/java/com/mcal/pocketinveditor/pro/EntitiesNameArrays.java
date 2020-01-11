package com.mcal.pocketinveditor.pro;

import android.content.Context;
import android.content.res.Resources;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;
import com.mcal.pocketinveditor.EntityTypeLocalization;
import com.mcal.pocketinveditor.entity.EntityType;
import java.util.ArrayList;
import java.util.List;

public final class EntitiesNameArrays {
    public static List<CharSequence> getAllNames(Context context) {
        Resources res = context.getResources();
        EntityType[] types = EntityType.values();
        List<CharSequence> outlist = new ArrayList<>(types.length);
        for (EntityType type : types) {
            if (!(type == EntityType.UNKNOWN || type == EntityType.PLAYER)) {
                outlist.add(res.getString(EntityTypeLocalization.namesMap.get(type).intValue()));
            }
        }
        return outlist;
    }

    public static List<CharSequence> getReplaceToTypes(Context context) {
        Resources res = context.getResources();
        EntityType[] types = EntityType.values();
        List<CharSequence> outlist = new ArrayList<>(types.length - 1);
        for (EntityType type : types) {
            if (!(type == EntityType.UNKNOWN || type == EntityType.PLAYER || type == EntityType.ITEM)) {
                outlist.add(res.getString(EntityTypeLocalization.namesMap.get(type).intValue()));
            }
        }
        return outlist;
    }

    public static List<CharSequence> getReplaceFromTypes(Context context) {
        Resources res = context.getResources();
        EntityType[] types = EntityType.values();
        List<CharSequence> outlist = new ArrayList<>(types.length + 1);
        for (EntityType type : types) {
            if (!(type == EntityType.UNKNOWN || type == EntityType.PLAYER)) {
                outlist.add(res.getString(EntityTypeLocalization.namesMap.get(type).intValue()));
            }
        }
        outlist.add(res.getString(R.string.entity_any));
        return outlist;
    }

    public static List<CharSequence> getMobTypes(Context context) {
        Resources res = context.getResources();
        EntityType[] types = EntitiesEditActivity.mobTypes;
        List<CharSequence> outlist = new ArrayList<>(types.length);
        for (EntityType type : types) {
            outlist.add(res.getString((Integer) EntityTypeLocalization.namesMap.get(type).intValue()));
        }
        return outlist;
    }

    public static List<CharSequence> getMobTypesWithNone(Context context) {
        Resources res = context.getResources();
        EntityType[] types = EntitiesEditActivity.mobTypes;
        List<CharSequence> outlist = new ArrayList<>(types.length + 1);
        outlist.add(res.getString(R.string.entity_none));
        for (EntityType type : types) {
            outlist.add(res.getString(EntityTypeLocalization.namesMap.get(type).intValue()));
        }
        return outlist;
    }

    public static void setSpinner(AdapterView<SpinnerAdapter> adapterView, List<CharSequence> entries) {
        ArrayAdapter<CharSequence> adapter = new ArrayAdapter<>(adapterView.getContext(), android.R.layout.simple_spinner_item, entries);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        adapterView.setAdapter(adapter);
    }
}
