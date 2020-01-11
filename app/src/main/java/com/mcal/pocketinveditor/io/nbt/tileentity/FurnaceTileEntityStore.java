package com.mcal.pocketinveditor.io.nbt.tileentity;

import java.util.List;
import com.mcal.pocketinveditor.tileentity.ContainerTileEntity;
import com.mcal.pocketinveditor.tileentity.FurnaceTileEntity;
import org.spout.nbt.ShortTag;
import org.spout.nbt.Tag;

public class FurnaceTileEntityStore<T extends FurnaceTileEntity> extends ContainerTileEntityStore<T> {
    public void loadTag(T entity, Tag tag) {
        String name = tag.getName();
        if (name.equals("BurnTime")) {
            entity.setBurnTime(((ShortTag) tag).getValue().shortValue());
        } else if (name.equals("CookTime")) {
            entity.setCookTime(((ShortTag) tag).getValue().shortValue());
        } else {
            super.loadTag(entity, tag);
        }
    }

    public List<Tag> save(T entity) {
        List<Tag> tags = super.save(entity);
        tags.add(new ShortTag("BurnTime", entity.getBurnTime()));
        tags.add(new ShortTag("CookTime", entity.getCookTime()));
        return tags;
    }
}
