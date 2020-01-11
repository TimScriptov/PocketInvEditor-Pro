package com.mcal.pocketinveditor.pro;

import java.util.ArrayList;
import java.util.List;
import com.mcal.pocketinveditor.BrowseItemsActivity;
import com.mcal.pocketinveditor.geo.ChunkManager;
import com.mcal.pocketinveditor.material.Material;

public class BrowseBlocksActivity extends BrowseItemsActivity {
    private static List<Material> blockList;

    protected List<Material> getMaterialsForList() {
        if (blockList == null) {
            blockList = new ArrayList<>();
            for (Material m : Material.materials) {
                if (m.getId() < ChunkManager.WORLD_WIDTH) {
                    blockList.add(m);
                }
            }
        }
        return blockList;
    }
}
