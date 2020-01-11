package com.mcal.pocketinveditor.geo;

import java.util.HashMap;
import java.util.Map;
import com.mcal.pocketinveditor.material.MaterialCount;
import com.mcal.pocketinveditor.material.MaterialKey;
import com.mcal.pocketinveditor.util.Vector3f;

public final class GeoUtils {
    public static Map<MaterialKey, MaterialCount> countBlocks(AreaBlockAccess mgr, CuboidRegion region) {
        Map<MaterialKey, MaterialCount> counts = new HashMap();
        MaterialKey key = new MaterialKey((short) 0, (short) 0);
        for (int x = region.x; x < region.x + region.width; x++) {
            for (int z = region.z; z < region.z + region.length; z++) {
                for (int y = region.y; y < region.y + region.height; y++) {
                    key.typeId = (short) mgr.getBlockTypeId(x, y, z);
                    key.damage = (short) mgr.getBlockData(x, y, z);
                    MaterialCount oldCount = (MaterialCount) counts.get(key);
                    if (oldCount == null) {
                        MaterialKey permKey = new MaterialKey(key);
                        counts.put(permKey, new MaterialCount(permKey, 1));
                    } else {
                        oldCount.count++;
                    }
                }
            }
        }
        return counts;
    }

    public static int replaceBlocks(AreaBlockAccess mgr, CuboidRegion region, MaterialKey from, MaterialKey to) {
        int replacedCount = 0;
        for (int x = region.x; x < region.x + region.width; x++) {
            for (int z = region.z; z < region.z + region.length; z++) {
                for (int y = region.y; y < region.y + region.height; y++) {
                    short type = (short) mgr.getBlockTypeId(x, y, z);
                    short data = (short) mgr.getBlockData(x, y, z);
                    if (type == from.typeId && (data == from.damage || from.damage == (short) -1)) {
                        mgr.setBlockTypeId(x, y, z, to.typeId);
                        mgr.setBlockData(x, y, z, to.damage);
                        replacedCount++;
                    }
                }
            }
        }
        return replacedCount;
    }

    public static void setBlocks(AreaBlockAccess mgr, CuboidRegion region, MaterialKey to) {
        for (int x = region.x; x < region.x + region.width; x++) {
            for (int z = region.z; z < region.z + region.length; z++) {
                for (int y = region.y; y < region.y + region.height; y++) {
                    mgr.setBlockTypeId(x, y, z, to.typeId);
                    mgr.setBlockData(x, y, z, to.damage);
                }
            }
        }
    }

    public static int makeSphere(AreaBlockAccess mgr, Vector3f center, int radius, MaterialKey to, boolean hollow) {
        return makeSphere(mgr, center.getBlockX(), center.getBlockY(), center.getBlockZ(), radius, to, hollow);
    }

    public static int makeSphere(AreaBlockAccess mgr, int centerX, int centerY, int centerZ, int radius, MaterialKey to, boolean hollow) {
        int replacedCount = 0;
        int radiusSquared = radius * radius;
        int hollowRadiusSquared = (radius - 1) * (radius - 1);
        for (int x = -radius; x < radius; x++) {
            for (int z = -radius; z < radius; z++) {
                for (int y = -radius; y < radius; y++) {
                    double distFromCenter = (Math.pow((double) x, 2.0d) + Math.pow((double) y, 2.0d)) + Math.pow((double) z, 2.0d);
                    if (distFromCenter <= ((double) radiusSquared) && (!hollow || distFromCenter >= ((double) hollowRadiusSquared))) {
                        mgr.setBlockTypeId(centerX + x, centerY + y, centerZ + z, to.typeId);
                        mgr.setBlockData(centerX + x, centerY + y, centerZ + z, to.damage);
                        replacedCount++;
                    }
                }
            }
        }
        return replacedCount;
    }

    public static int makeDome(AreaBlockAccess mgr, Vector3f center, int radius, MaterialKey to, boolean hollow) {
        return makeDome(mgr, center.getBlockX(), center.getBlockY(), center.getBlockZ(), radius, to, hollow);
    }

    public static int makeDome(AreaBlockAccess mgr, int centerX, int centerY, int centerZ, int radius, MaterialKey to, boolean hollow) {
        int replacedCount = 0;
        int radiusSquared = radius * radius;
        int hollowRadiusSquared = (radius - 1) * (radius - 1);
        for (int x = -radius; x < radius; x++) {
            for (int z = -radius; z < radius; z++) {
                for (int y = 0; y < radius; y++) {
                    double distFromCenter = (Math.pow((double) x, 2.0d) + Math.pow((double) y, 2.0d)) + Math.pow((double) z, 2.0d);
                    if (distFromCenter <= ((double) radiusSquared) && (!hollow || distFromCenter >= ((double) hollowRadiusSquared))) {
                        mgr.setBlockTypeId(centerX + x, centerY + y, centerZ + z, to.typeId);
                        mgr.setBlockData(centerX + x, centerY + y, centerZ + z, to.damage);
                        replacedCount++;
                    }
                }
            }
        }
        return replacedCount;
    }

    public static int makeHollowCuboid(AreaBlockAccess mgr, CuboidRegion region, MaterialKey to) {
        CuboidRegion temp = new CuboidRegion(region);
        temp.height = 1;
        setBlocks(mgr, temp, to);
        temp.y = (region.y + region.height) - 1;
        setBlocks(mgr, temp, to);
        int replacedCount = 0 + (temp.getBlockCount() * 2);
        temp = new CuboidRegion(region);
        temp.width = 1;
        setBlocks(mgr, temp, to);
        temp.x = (region.x + region.width) - 1;
        setBlocks(mgr, temp, to);
        replacedCount += temp.getBlockCount() * 2;
        temp = new CuboidRegion(region);
        temp.length = 1;
        setBlocks(mgr, temp, to);
        temp.z = (region.z + region.length) - 1;
        setBlocks(mgr, temp, to);
        return replacedCount + (temp.getBlockCount() * 2);
    }

    public static int makePyramid(AreaBlockAccess mgr, Vector3f center, int height, MaterialKey to, boolean hollow) {
        return makePyramid(mgr, center.getBlockX(), center.getBlockY(), center.getBlockZ(), height, to, hollow);
    }

    public static int makePyramid(AreaBlockAccess mgr, int centerX, int centerY, int centerZ, int height, MaterialKey to, boolean hollow) {
        int replacedCount = 0;
        for (int y = 0; y <= height; y++) {
            int s = height - y;
            int sh = s - 1;
            int x = (-s) + 1;
            while (x < s) {
                int z = (-s) + 1;
                while (z < s) {
                    if (!hollow || x <= (-sh) + 1 || x >= sh || z <= (-sh) + 1 || z >= sh) {
                        mgr.setBlockTypeId(centerX + x, centerY + y, centerZ + z, to.typeId);
                        mgr.setBlockData(centerX + x, centerY + y, centerZ + z, to.damage);
                        replacedCount++;
                    }
                    z++;
                }
                x++;
            }
        }
        return replacedCount;
    }

    public static int makeCylinder(AreaBlockAccess mgr, Vector3f center, int radius, int height, MaterialKey to, boolean hollow) {
        int centerX = center.getBlockX();
        int centerY = center.getBlockY();
        int centerZ = center.getBlockZ();
        int replacedCount = 0;
        int radiusSquared = radius * radius;
        int hollowRadiusSquared = (radius - 1) * (radius - 1);
        for (int x = -radius; x < radius; x++) {
            for (int z = -radius; z < radius; z++) {
                double distFromCenter = Math.pow((double) x, 2.0d) + Math.pow((double) z, 2.0d);
                if (distFromCenter <= ((double) radiusSquared) && (!hollow || distFromCenter >= ((double) hollowRadiusSquared))) {
                    for (int y = centerY; y < centerY + height; y++) {
                        mgr.setBlockTypeId(centerX + x, y, centerZ + z, to.typeId);
                        mgr.setBlockData(centerX + x, y, centerZ + z, to.damage);
                        replacedCount++;
                    }
                }
            }
        }
        return replacedCount;
    }
}
