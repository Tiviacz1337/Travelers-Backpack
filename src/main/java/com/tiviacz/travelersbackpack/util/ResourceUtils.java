package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModItems;
import net.minecraft.item.DyeColor;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ResourceUtils
{
    public static final List<ResourceLocation> TEXTURE_RESOURCE_LOCATIONS = new ArrayList<>();
    public static final List<ResourceLocation> SLEEPING_BAG_TEXTURE_RESOURCE_LOCATIONS = new ArrayList<>();

    //Store resource locations for backpacks, to avoid tick by tick new resource locations creation.
    //Then get each texture by index from ModItems#BACKPACKS.
    //Any new approach is appreciated D:

    public static ResourceLocation getBackpackTexture(Item item)
    {
        return TEXTURE_RESOURCE_LOCATIONS.get(ModItems.BACKPACKS.indexOf(item));
    }

    public static ResourceLocation getSleepingBagTexture(int colorId)
    {
        return SLEEPING_BAG_TEXTURE_RESOURCE_LOCATIONS.get(colorId);
    }

    public static ResourceLocation getDefaultSleepingBagTexture()
    {
        return SLEEPING_BAG_TEXTURE_RESOURCE_LOCATIONS.get(14);
    }

    public static void createTextureLocations()
    {
        TEXTURE_RESOURCE_LOCATIONS.clear();

        for(String name : Reference.BACKPACK_NAMES)
        {
            ResourceLocation res = new ResourceLocation(TravelersBackpack.MODID, "textures/model/" + name.toLowerCase(Locale.ENGLISH) + ".png");
            TEXTURE_RESOURCE_LOCATIONS.add(res);
        }
    }

    public static void createSleepingBagTextureLocations()
    {
        SLEEPING_BAG_TEXTURE_RESOURCE_LOCATIONS.clear();

        for(DyeColor color : DyeColor.values())
        {
            ResourceLocation res = new ResourceLocation(TravelersBackpack.MODID, "textures/model/bags/" + color.getName().toLowerCase(Locale.ENGLISH) + "_sleeping_bag" + ".png");
            SLEEPING_BAG_TEXTURE_RESOURCE_LOCATIONS.add(res);
        }
    }

    public static ResourceLocation create(String path)
    {
        return new ResourceLocation(TravelersBackpack.MODID, path);
    }
}