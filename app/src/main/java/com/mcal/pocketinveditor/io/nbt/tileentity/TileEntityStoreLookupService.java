package com.mcal.pocketinveditor.io.nbt.tileentity;

import java.util.HashMap;
import java.util.Map;
import com.mcal.pocketinveditor.tileentity.ChestTileEntity;
import com.mcal.pocketinveditor.tileentity.FurnaceTileEntity;
import com.mcal.pocketinveditor.tileentity.MobSpawnerTileEntity;
import com.mcal.pocketinveditor.tileentity.NetherReactorTileEntity;
import com.mcal.pocketinveditor.tileentity.SignTileEntity;
import com.mcal.pocketinveditor.tileentity.TileEntity;

public class TileEntityStoreLookupService {
    public static Map<String, Class<? extends TileEntity>> classMap = new HashMap<>();
    public static TileEntityStore<TileEntity> defaultStore = new TileEntityStore<>();
    public static Map<String, TileEntityStore<? extends TileEntity>> idMap = new HashMap<>();

    static {
        addStore("Furnace", new FurnaceTileEntityStore(), FurnaceTileEntity.class);
        addStore("Chest", new ChestTileEntityStore(), ChestTileEntity.class);
        addStore("NetherReactor", new NetherReactorTileEntityStore(), NetherReactorTileEntity.class);
        addStore("Sign", new SignTileEntityStore(), SignTileEntity.class);
        addStore("MobSpawner", new MobSpawnerTileEntityStore(), MobSpawnerTileEntity.class);
    }

    public static void addStore(String id, TileEntityStore<? extends TileEntity> store, Class<? extends TileEntity> clazz) {
        String realId = id.toUpperCase();
        idMap.put(realId, store);
        classMap.put(realId, clazz);
    }

    public static TileEntity createTileEntityById(String id) {
        Class<TileEntity> clazz = (Class) classMap.get(id.toUpperCase());
        if (clazz == null) {
            return new TileEntity();
        }
        try {
            return clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
            return new TileEntity();
        }
    }

    public static TileEntityStore<? extends TileEntity> getStoreById(String id) {
        return (TileEntityStore) idMap.get(id.toUpperCase());
    }
}
