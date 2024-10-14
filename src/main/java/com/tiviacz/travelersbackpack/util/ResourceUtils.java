package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.DyeColor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ResourceUtils
{
    public static final List<ResourceLocation> SLEEPING_BAG_TEXTURE_RESOURCE_LOCATIONS = new ArrayList<>();

    public static ResourceLocation getSleepingBagTexture(int colorId)
    {
        return SLEEPING_BAG_TEXTURE_RESOURCE_LOCATIONS.get(colorId);
    }

    public static ResourceLocation getDefaultSleepingBagTexture()
    {
        return SLEEPING_BAG_TEXTURE_RESOURCE_LOCATIONS.get(14);
    }

    public static void createSleepingBagTextureLocations()
    {
        SLEEPING_BAG_TEXTURE_RESOURCE_LOCATIONS.clear();

        for(DyeColor color : DyeColor.values())
        {
            ResourceLocation res = ResourceLocation.fromNamespaceAndPath(TravelersBackpack.MODID, "textures/model/bags/" + color.getName().toLowerCase(Locale.ENGLISH) + "_sleeping_bag" + ".png");
            SLEEPING_BAG_TEXTURE_RESOURCE_LOCATIONS.add(res);
        }
    }
}