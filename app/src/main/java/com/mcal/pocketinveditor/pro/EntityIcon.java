package com.mcal.pocketinveditor.pro;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import com.mcal.pocketinveditor.entity.EntityType;
import java.io.InputStream;
import java.util.EnumMap;
import java.util.Map;

public class EntityIcon {
    private static Bitmap entitySpriteBmp;
    private static Map<EntityType, Integer> iconIndex = new EnumMap<>(EntityType.class);
    private static final Rect tempRect = new Rect();

    static {
        add(EntityType.PIG, Integer.valueOf(0));
        add(EntityType.SHEEP, Integer.valueOf(1));
        add(EntityType.COW, Integer.valueOf(2));
        add(EntityType.CHICKEN, Integer.valueOf(3));
        add(EntityType.PLAYER, Integer.valueOf(7));
        add(EntityType.ZOMBIE, Integer.valueOf(8));
        add(EntityType.SKELETON, Integer.valueOf(9));
        add(EntityType.SPIDER, Integer.valueOf(10));
        add(EntityType.CREEPER, Integer.valueOf(13));
        add(EntityType.PIG_ZOMBIE, Integer.valueOf(16));
    }

    private static void add(EntityType type, Integer location) {
        iconIndex.put(type, location);
    }

    public static void loadEntitySprites(Context context) {
        if (entitySpriteBmp == null) {
            try {
                InputStream is = context.getAssets().open("EntityCSS.png");
                entitySpriteBmp = BitmapFactory.decodeStream(is);
                is.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void drawEntity(Canvas canvas, Rect area, Paint paint, EntityType type) {
        if (entitySpriteBmp != null && type != EntityType.ITEM) {
            Integer locationi = iconIndex.get(type);
            if (locationi == null) {
                System.err.println(type + ": no entity icon bound");
                return;
            }
            int location = locationi.intValue();
            int x = location % 12;
            int y = location / 12;
            tempRect.set(x * 16, y * 16, (x + 1) * 16, (y + 1) * 16);
            canvas.drawBitmap(entitySpriteBmp, tempRect, area, paint);
        }
    }
}
