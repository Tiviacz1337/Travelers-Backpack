package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.api.integration.ITBPlugin;
import com.tiviacz.travelersbackpack.api.integration.TBPlugin;
import com.tiviacz.travelersbackpack.util.LogHelper;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class IntegrationHandler
{
    //Thanks to Shinoow
    //https://github.com/Shinoow/AbyssalCraft/blob/master/src/main/java/com/shinoow/abyssalcraft/init/IntegrationHandler.java/

    List<String> mods = new ArrayList<>();
    List<ITBPlugin> integrations = new ArrayList<>();
    List<ITBPlugin> temp = new ArrayList<>();

    private void findIntegrations(ASMDataTable asmDataTable)
    {
        LogHelper.info("Starting the Integration Handler.");

        fetchModIntegrations(asmDataTable);

        if(!temp.isEmpty())
            LogHelper.info("Found {} possible mod integration(s)!", temp.size());

        if(temp.isEmpty())
            LogHelper.info("Didn't find any possible mod integration(s)!");
    }

    private void fetchModIntegrations(ASMDataTable asmDataTable)
    {
        List<ITBPlugin> plugins = fetchPlugins(asmDataTable, TBPlugin.class, ITBPlugin.class);

        if(!plugins.isEmpty())
        {
            temp.addAll(plugins);
        }
    }

    private <T> List<T> fetchPlugins(ASMDataTable asmDataTable, Class annotationClass, Class<T> instanceClass)
    {
        String annotationClassName = annotationClass.getCanonicalName();
        Set<ASMDataTable.ASMData> asmDatas = asmDataTable.getAll(annotationClassName);
        List<T> instances = new ArrayList<>();
        for(ASMDataTable.ASMData asmData : asmDatas)
            try {
                Class<?> asmClass = Class.forName(asmData.getClassName());
                Class<? extends T> asmInstanceClass = asmClass.asSubclass(instanceClass);
                T instance = asmInstanceClass.newInstance();
                instances.add(instance);
            } catch(ClassNotFoundException | IllegalAccessException | InstantiationException e) {
                LogHelper.error("Failed to load: {}", asmData.getClassName(), e);
            }
        return instances;
    }

    private void search()
    {
        if(!temp.isEmpty())
        {
            for(ITBPlugin plugin : temp)
                if(plugin.canLoad())
                {
                    LogHelper.info("Found a integration for mod {}", plugin.getModName());
                    integrations.add(plugin);
                    mods.add(plugin.getModName());
                }

            temp.clear();

            if(!mods.isEmpty())
                LogHelper.info("Mod integrations found: {}", mods);
        }
    }

    public void preInit(FMLPreInitializationEvent event)
    {
        findIntegrations(event.getAsmData());
    }

    public void init(FMLInitializationEvent event)
    {
        search();
        if(!integrations.isEmpty()){
            for(ITBPlugin plugin : integrations)
                plugin.init();
        }
    }

    public void postInit(FMLPostInitializationEvent event)
    {
        if(!integrations.isEmpty()){
            for(ITBPlugin plugin : integrations)
                plugin.postInit();
        }
    }
}
