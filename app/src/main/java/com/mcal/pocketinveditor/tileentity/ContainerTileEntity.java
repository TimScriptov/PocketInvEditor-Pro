package com.mcal.pocketinveditor.tileentity;

import java.util.List;
import com.mcal.pocketinveditor.InventorySlot;

public class ContainerTileEntity extends TileEntity {
    private List<InventorySlot> items;

    public List<InventorySlot> getItems() {
        return items;
    }

    public void setItems(List<InventorySlot> items) {
        this.items = items;
    }

    public int getContainerSize() {
        return 27;
    }
}
