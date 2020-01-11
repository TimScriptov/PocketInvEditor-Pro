package com.mcal.pocketinveditor.pro;

import android.content.Intent;
import com.mcal.pocketinveditor.TileEntityViewActivity;
import com.mcal.pocketinveditor.tileentity.MobSpawnerTileEntity;
import com.mcal.pocketinveditor.tileentity.NetherReactorTileEntity;
import com.mcal.pocketinveditor.tileentity.SignTileEntity;
import com.mcal.pocketinveditor.tileentity.TileEntity;

public class TileEntityEditActivity extends TileEntityViewActivity {
    protected Intent getTileEntityIntent(Class<? extends TileEntity> clazz) {
        if (SignTileEntity.class.isAssignableFrom(clazz)) {
            return new Intent(this, EditSignActivity.class);
        }
        if (NetherReactorTileEntity.class.isAssignableFrom(clazz)) {
            return new Intent(this, EditNetherReactorActivity.class);
        }
        if (MobSpawnerTileEntity.class.isAssignableFrom(clazz)) {
            return new Intent(this, EditMobSpawnerActivity.class);
        }
        return super.getTileEntityIntent(clazz);
    }

    protected boolean showUpgradeForEditMessage(TileEntity entity) {
        return false;
    }
}
