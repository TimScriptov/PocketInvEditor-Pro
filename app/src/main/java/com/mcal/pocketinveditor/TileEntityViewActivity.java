package com.mcal.pocketinveditor;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import com.mcal.pocketinveditor.EntitiesInfoActivity.LoadEntitiesTask;
import com.mcal.pocketinveditor.io.EntityDataConverter.EntityData;
import com.mcal.pocketinveditor.material.MaterialKey;
import com.mcal.pocketinveditor.material.icon.MaterialIcon;
import com.mcal.pocketinveditor.pro.R;
import com.mcal.pocketinveditor.tileentity.ContainerTileEntity;
import com.mcal.pocketinveditor.tileentity.FurnaceTileEntity;
import com.mcal.pocketinveditor.tileentity.MobSpawnerTileEntity;
import com.mcal.pocketinveditor.tileentity.NetherReactorTileEntity;
import com.mcal.pocketinveditor.tileentity.SignTileEntity;
import com.mcal.pocketinveditor.tileentity.TileEntity;
import com.mcal.pocketinveditor.util.Vector3f;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TileEntityViewActivity extends ListActivity implements EntityDataLoadListener, LevelDataLoadListener {
    protected ArrayAdapter<TileEntity> listAdapter;
    protected List<TileEntity> tileEntities;

    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        if (getIntent().getBooleanExtra("CanEditSlots", false)) {
            registerForContextMenu(getListView());
        }
        if (EditorActivity.level != null) {
            onLevelDataLoad();
        } else {
            EditorActivity.loadLevelData(this, this, getIntent().getStringExtra("world"));
        }
    }

    protected void onResume() {
        super.onResume();
        if (listAdapter != null) {
            listAdapter.notifyDataSetChanged();
        }
    }

    public void onLevelDataLoad() {
        loadEntities();
    }

    protected void loadEntities() {
        new Thread(new LoadEntitiesTask(this, this)).start();
    }

    public void onEntitiesLoaded(EntityData entitiesDat) {
        EditorActivity.level.setEntities(entitiesDat.entities);
        EditorActivity.level.setTileEntities(entitiesDat.tileEntities);
        if (!isFinishing()) {
            tileEntities = new ArrayList<>(entitiesDat.tileEntities);
            final Vector3f playerLoc = EditorActivity.level.getPlayer().getLocation();
            Collections.sort(tileEntities, new Comparator<TileEntity>() {
                public int compare(TileEntity a, TileEntity b) {
                    double aDist = a.distanceSquaredTo(playerLoc);
                    double bDist = b.distanceSquaredTo(playerLoc);
                    if (aDist < bDist) {
                        return -1;
                    }
                    if (aDist > bDist) {
                        return 1;
                    }
                    return 0;
                }
            });
            this.listAdapter = new ArrayAdapter<TileEntity>(this, R.layout.slot_list_item, R.id.slot_list_main_text, tileEntities) {
                public View getView(int position, View convertView, ViewGroup parent) {
                    View retval = super.getView(position, convertView, parent);
                    ImageView iconView = retval.findViewById(R.id.slot_list_icon);
                    MaterialKey iconKey = TileEntityViewActivity.getIconMaterial(getItem(position).getClass());
                    MaterialIcon icon = null;
                    if (iconKey != null) {
                        icon = MaterialIcon.icons.get(iconKey);
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
            setListAdapter(listAdapter);
            listAdapter.notifyDataSetChanged();
        }
    }

    protected void onListItemClick(ListView l, View v, int position, long id) {
        TileEntity entity = listAdapter.getItem(position);
        if (!showUpgradeForEditMessage(entity)) {
            Intent intent = getTileEntityIntent(entity.getClass());
            if (intent != null) {
                intent.putExtras(getIntent());
                intent.putExtra("Index", EditorActivity.level.getTileEntities().indexOf(entity));
                startActivity(intent);
            }
        }
    }

    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        menu.setHeaderTitle(listAdapter.getItem(((AdapterContextMenuInfo) menuInfo).position).toString());
        menu.add(R.string.warp_to_tile_entity);
    }

    public boolean onContextItemSelected(MenuItem item) {
        AdapterContextMenuInfo menuInfo = (AdapterContextMenuInfo) item.getMenuInfo();
        if (!item.getTitle().equals(getResources().getString(R.string.warp_to_tile_entity))) {
            return super.onContextItemSelected(item);
        }
        warpToTileEntity(this, listAdapter.getItem(menuInfo.position));
        return true;
    }

    protected Intent getTileEntityIntent(Class<? extends TileEntity> clazz) {
        if (ContainerTileEntity.class.isAssignableFrom(clazz)) {
            return new Intent(this, ChestSlotsActivity.class);
        }
        return null;
    }

    protected boolean showUpgradeForEditMessage(TileEntity entity) {
        if (entity.getClass() == SignTileEntity.class) {
            Toast.makeText(this, R.string.get_pro_to_edit_signs, 0).show();
            return true;
        } else if (entity.getClass() != NetherReactorTileEntity.class) {
            return false;
        } else {
            Toast.makeText(this, R.string.get_pro_to_adjust_reactor, 0).show();
            return true;
        }
    }

    public static MaterialKey getIconMaterial(Class<? extends TileEntity> clazz) {
        if (clazz == FurnaceTileEntity.class) {
            return new MaterialKey((short) 61, (short) 0);
        }
        if (clazz == SignTileEntity.class) {
            return new MaterialKey((short) 323, (short) 0);
        }
        if (clazz == NetherReactorTileEntity.class) {
            return new MaterialKey((short) 247, (short) 0);
        }
        if (clazz == MobSpawnerTileEntity.class) {
            return new MaterialKey((short) 52, (short) 0);
        }
        return new MaterialKey((short) 54, (short) 0);
    }

    public static void warpToTileEntity(Activity activity, TileEntity entity) {
        EditorActivity.level.getPlayer().setLocation(new Vector3f(((float) entity.getX()) + 0.5f, (float) (entity.getY() + 1), ((float) entity.getZ()) + 0.5f));
        EditorActivity.save(activity);
    }
}
