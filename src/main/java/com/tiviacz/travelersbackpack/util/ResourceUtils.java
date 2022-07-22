package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModItems;
import net.minecraft.item.Item;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class ResourceUtils
{
    public static final List<Identifier> TEXTURE_IDENTIFIERS = new ArrayList<>();

    //Store resource locations for backpacks, to avoid tick by tick new resource locations creation.
    //Then get each texture by index from ModItems#BACKPACKS.
    //Any new approach is appreciated D:

    public static Identifier getBackpackTexture(Item item)
    {
        return TEXTURE_IDENTIFIERS.get(ModItems.BACKPACKS.indexOf(item));
    }

    public static void createTextureLocations()
    {
        TEXTURE_IDENTIFIERS.clear();

        for(String name : Reference.BACKPACK_NAMES)
        {
            Identifier res = new Identifier(TravelersBackpack.MODID, "textures/model/" + name.toLowerCase(Locale.ENGLISH) + ".png");
            TEXTURE_IDENTIFIERS.add(res);
        }
    }
}