package com.tiviacz.travelersbackpack.compat.create;

import com.simibubi.create.content.logistics.box.PackageEntity;
import net.minecraft.world.entity.Entity;

public class CreateCompat {
    public static boolean isPackageEntity(Entity entity) {
        return entity instanceof PackageEntity;
    }
}