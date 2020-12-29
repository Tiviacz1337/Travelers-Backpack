package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.minecraft.util.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class ResourceUtils
{
    public static final List<ResourceLocation> MODEL_RESOURCE_LOCATIONS = new ArrayList<>();
    public static final List<ResourceLocation> WEARABLE_RESOURCE_LOCATIONS = new ArrayList<>();

    //Store resource locations for backpacks, to avoid tick by tick new resource locations creation.
    //Then get each texture by index from ModItems#BACKPACKS.

    public static void createModelTextureLocations()
    {
        WEARABLE_RESOURCE_LOCATIONS.clear();

        for(String name : Reference.BACKPACK_NAMES)
        {
            ResourceLocation res = new ResourceLocation(TravelersBackpack.MODID, "model/" + name.toLowerCase());
            MODEL_RESOURCE_LOCATIONS.add(res);
        }
    }

    public static void createWearableTextureLocations()
    {
        WEARABLE_RESOURCE_LOCATIONS.clear();

        for(String name : Reference.BACKPACK_NAMES)
        {
            ResourceLocation res = new ResourceLocation(TravelersBackpack.MODID, "textures/model/" + name.toLowerCase() + ".png");
            WEARABLE_RESOURCE_LOCATIONS.add(res);
        }
    }
}