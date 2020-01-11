package com.mcal.pocketinveditor.io.nbt.tileentity;

import java.util.List;
import com.mcal.pocketinveditor.tileentity.ChestTileEntity;
import com.mcal.pocketinveditor.tileentity.ContainerTileEntity;
import org.spout.nbt.IntTag;
import org.spout.nbt.Tag;

public class ChestTileEntityStore<T extends ChestTileEntity> extends ContainerTileEntityStore<T> {
    public void loadTag(T entity, Tag tag) {
        String name = tag.getName();
        if (name.equals("pairx")) {
            entity.setPairX(((IntTag) tag).getValue().intValue());
        } else if (name.equals("pairz")) {
            entity.setPairZ(((IntTag) tag).getValue().intValue());
        } else {
            super.loadTag(entity, tag);
        }
    }

    public List<Tag> save(T entity) {
        List<Tag> tags = super.save(entity);
        if (entity.getPairX() != -65535) {
            tags.add(new IntTag("pairx", entity.getPairX()));
            tags.add(new IntTag("pairz", entity.getPairZ()));
        }
        return tags;
    }
}
