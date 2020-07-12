package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.util.ItemFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraftforge.common.util.ModFixs;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class DataFixerHandler
{
    public static void registerFixes()
    {
        ModFixs fixer = FMLCommonHandler.instance().getDataFixer().init(TravelersBackpack.MODID, 32);
        fixer.registerFix(FixTypes.ITEM_INSTANCE, new ItemFixer());
    }
}
