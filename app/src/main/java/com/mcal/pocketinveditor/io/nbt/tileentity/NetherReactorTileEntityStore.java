package com.mcal.pocketinveditor.io.nbt.tileentity;

import java.util.List;
import com.mcal.pocketinveditor.tileentity.NetherReactorTileEntity;
import org.spout.nbt.ByteTag;
import org.spout.nbt.ShortTag;
import org.spout.nbt.Tag;

public class NetherReactorTileEntityStore<T extends NetherReactorTileEntity> extends TileEntityStore<T> {
    public void loadTag(T entity, Tag tag) {
        boolean z = true;
        if (tag.getName().equals("HasFinished")) {
            if (((ByteTag) tag).getValue().byteValue() == (byte) 0) {
                z = false;
            }
            entity.setFinished(z);
        } else if (tag.getName().equals("IsInitialized")) {
            if (((ByteTag) tag).getValue().byteValue() == (byte) 0) {
                z = false;
            }
            entity.setInitialized(z);
        } else if (tag.getName().equals("Progress")) {
            entity.setProgress(((ShortTag) tag).getValue().shortValue());
        } else {
            super.loadTag(entity, tag);
        }
    }

    public List<Tag> save(T entity) {
        byte b;
        byte b2 = (byte) 1;
        List<Tag> tags = super.save(entity);
        String str = "HasFinished";
        if (entity.hasFinished()) {
            b = (byte) 1;
        } else {
            b = (byte) 0;
        }
        tags.add(new ByteTag(str, b));
        String str2 = "IsInitialized";
        if (!entity.isInitialized()) {
            b2 = (byte) 0;
        }
        tags.add(new ByteTag(str2, b2));
        tags.add(new ShortTag("Progress", entity.getProgress()));
        return tags;
    }
}
