package com.tiviacz.travelersbackpack.util;

import net.minecraft.entity.EntityType;

import java.util.Arrays;
import java.util.List;

public class Reference
{
    //Screen IDs
    public static final byte NO_SCREEN_ID = 0;
    public static final byte ITEM_SCREEN_ID = 1;
    public static final byte WEARABLE_SCREEN_ID = 2;
    public static final byte BLOCK_ENTITY_SCREEN_ID = 3;

    //Packets
    public static final byte SWAP_TOOL = 0;
    public static final byte SWITCH_HOSE_MODE = 1;
    public static final byte TOGGLE_HOSE_TANK = 2;
    public static final byte EMPTY_TANK = 3;
    public static final byte OPEN_SCREEN = 4;

    //Translation Keys
    public static final String NO_BACKPACK = "action.travelersbackpack.unequip_nobackpack";
    public static final String NO_SPACE = "action.travelersbackpack.unequip_nospace";
    public static final String OTHER_BACKPACK = "action.travelersbackpack.equip_otherbackpack";
    public static final String FAIL = "action.travelersbackpack.equip_fail";
    public static final String DEPLOY = "action.travelersbackpack.deploy_sleeping_bag";

    public static String[] BACKPACK_NAMES = {
            "Standard",
            "Netherite",
            "Diamond",
            "Gold",
            "Emerald",
            "Iron",
            "Lapis",
            "Redstone",
            "Coal",

            "Quartz",
            "Bookshelf",
            "End",
            "Nether",
            "Sandstone",
            "Snow",
            "Sponge",

            "Cake",

            "Cactus",
            "Hay",
            "Melon",
            "Pumpkin",

            "Creeper",
            "Dragon",
            "Enderman",
            "Blaze",
            "Ghast",
            "Magma_Cube",
            "Skeleton",
            "Spider",
            "Wither",

            "Bat",
            "Bee",
            "Wolf",
            "Fox",
            "Ocelot",
            "Horse",
            "Cow",
            "Pig",
            "Sheep",
            "Chicken",
            "Squid",
            "Villager",
            "Iron_Golem"
    };

    public static final List<EntityType> COMPATIBLE_TYPE_ENTRIES = Arrays.asList(
            EntityType.ENDERMAN,
            EntityType.PIGLIN,
            EntityType.SKELETON,
            EntityType.WITHER_SKELETON,
            EntityType.ZOMBIE
    );
}