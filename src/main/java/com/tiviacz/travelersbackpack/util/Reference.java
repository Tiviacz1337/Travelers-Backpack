package com.tiviacz.travelersbackpack.util;

import net.minecraft.world.entity.EntityType;

import java.util.ArrayList;
import java.util.List;

public class Reference {
    //Screen IDs
    public static final byte NO_SCREEN_ID = 0;
    public static final byte ITEM_SCREEN_ID = 1;
    public static final byte WEARABLE_SCREEN_ID = 2;
    public static final byte BLOCK_ENTITY_SCREEN_ID = 3;

    //Packets
    public static final byte SWAP_TOOL = 0;
    public static final byte SWITCH_HOSE_MODE = 1;
    public static final byte TOGGLE_HOSE_TANK = 2;
    public static final byte OPEN_SCREEN = 4;
    public static final byte TOGGLE_VISIBILITY = 5;

    //Translation Keys
    public static final String NO_SPACE = "action.travelersbackpack.unequip_nospace";
    public static final String OTHER_BACKPACK = "action.travelersbackpack.equip_otherbackpack";
    public static final String DEPLOY = "action.travelersbackpack.deploy_sleeping_bag";
}