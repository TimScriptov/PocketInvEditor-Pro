package com.mcal.pocketinveditor;

import com.mcal.pocketinveditor.io.EntityDataConverter.EntityData;

public interface EntityDataLoadListener {
    void onEntitiesLoaded(EntityData entityData);
}
