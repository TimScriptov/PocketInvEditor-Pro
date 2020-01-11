package com.mcal.pocketinveditor.io.nbt.tileentity;

import java.util.List;
import com.mcal.pocketinveditor.io.nbt.NBTConverter;
import com.mcal.pocketinveditor.tileentity.ContainerTileEntity;
import org.spout.nbt.ListTag;
import org.spout.nbt.Tag;

public class ContainerTileEntityStore<T extends ContainerTileEntity> extends TileEntityStore<T> {
    public void loadTag(T entity, Tag tag) {
        if (tag.getName().equals("Items")) {
            entity.setItems(NBTConverter.readInventory((ListTag) tag));
        } else {
            super.loadTag(entity, tag);
        }
    }

    public List<Tag> save(T entity) {
        List<Tag> tags = super.save(entity);
        tags.add(NBTConverter.writeInventory(entity.getItems(), "Items"));
        return tags;
    }
}
