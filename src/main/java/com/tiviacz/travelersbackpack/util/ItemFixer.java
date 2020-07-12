package com.tiviacz.travelersbackpack.util;

import com.google.common.collect.ImmutableMap;
import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.IFixableData;

import java.util.Map;

public class ItemFixer implements IFixableData
{
        private static final Map<String, String> OLD_TO_NEW_ID_MAP = ImmutableMap.<String, String>builder()
                .put("travellersbackpack:travellers_backpack", "travelersbackpack:travelers_backpack")
                .put("travellersbackpack:backpack_tank", "travelersbackpack:backpack_tank")
                .put("travellersbackpack:hose", "travelersbackpack:hose")
                .put("travellersbackpack:hose_nozzle", "travelersbackpack:hose_nozle")
                .put("travellersbackpack:sleeping_bag_bottom", "travelersbackpack:sleeping_bag_bottom")
                .build();

    @Override
    public int getFixVersion()
    {
        return 32;
    }

    @Override
    public NBTTagCompound fixTagCompound(NBTTagCompound compound)
    {
        String oldRegistryName = compound.getString("id");
        int oldMeta = compound.getShort("Damage");
        String newRegistryName = OLD_TO_NEW_ID_MAP.get(oldRegistryName);

        if(newRegistryName != null)
        {
            compound.setString("id", newRegistryName);
            compound.setShort("Damage", (short)oldMeta);
            LogHelper.info("Data fixed the item {}, with a meta of {}. It is now {}.", oldRegistryName, oldMeta, newRegistryName);
        }
        else if(oldRegistryName.contains("travellers_backpack"))
        {
            newRegistryName = oldRegistryName.replace("travellersbackpack", TravelersBackpack.MODID);
            compound.setString("id", newRegistryName);
            compound.setShort("Damage", (short)oldMeta);
            LogHelper.info("Data fixed the item {}, with a meta of {}. It is now {}.", oldRegistryName, oldMeta, newRegistryName);
        }

        return compound;
    }
}
