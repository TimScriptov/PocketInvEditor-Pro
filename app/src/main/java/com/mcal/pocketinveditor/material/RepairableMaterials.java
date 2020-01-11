package com.mcal.pocketinveditor.material;

import java.util.HashSet;
import java.util.Set;
import com.mcal.pocketinveditor.ItemStack;
import com.mcal.pocketinveditor.geo.ChunkManager;

public final class RepairableMaterials {
    public static final Set<Integer> ids = new HashSet<>();

    static {
        add(ChunkManager.WORLD_WIDTH, 259);
        add(261);
        add(267, 279);
        add(283, 286);
        add(298, 317);
        add(359);
    }

    private static void add(int id) {
        ids.add(Integer.valueOf(id));
    }

    private static void add(int begin, int end) {
        for (int i = begin; i <= end; i++) {
            ids.add(Integer.valueOf(i));
        }
    }

    public static boolean isRepairable(ItemStack stack) {
        return ids.contains(new Integer(stack.getTypeId()));
    }
}
