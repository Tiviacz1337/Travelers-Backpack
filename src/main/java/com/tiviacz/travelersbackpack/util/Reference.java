package com.tiviacz.travelersbackpack.util;

import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class Reference {
    //Fluid Constants
    public static final int BUCKET = 1000;
    public static final int POTION = 250;

    //Screen IDs
    public static final int ITEM_SCREEN_ID = 1;
    public static final int WEARABLE_SCREEN_ID = 2;
    public static final int BLOCK_ENTITY_SCREEN_ID = 3;

    //Translation Keys
    public static final String NO_SPACE = "action.travelersbackpack.unequip_nospace";
    public static final String OTHER_BACKPACK = "action.travelersbackpack.equip_otherbackpack";
    public static final String DEPLOY = "action.travelersbackpack.deploy_sleeping_bag";

    public static final List<EntityType> ALLOWED_TYPE_ENTRIES = new ArrayList<>();
}