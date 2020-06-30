package com.tiviacz.travelersbackpack;

import org.apache.logging.log4j.Logger;

import com.tiviacz.travelersbackpack.common.TravelersBackpackCreativeTab;
import com.tiviacz.travelersbackpack.proxy.CommonProxy;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;

@Mod(modid = TravelersBackpack.MODID, name = TravelersBackpack.NAME, version = TravelersBackpack.VERSION, updateJSON = TravelersBackpack.UPDATE_JSON)
public class TravelersBackpack
{
    public static final String MODID = "travelersbackpack";
    public static final String NAME = "Traveler's Backpack";
    public static final String VERSION = "1.0.30";
    public static final String UPDATE_JSON = "https://gist.githubusercontent.com/Tiviacz1337/906937677aa472285dff9d6c2a189d5e/raw";
    public static final String CLIENT_PROXY_CLASS = "com.tiviacz.travelersbackpack.proxy.ClientProxy";
    public static final String COMMON_PROXY_CLASS = "com.tiviacz.travelersbackpack.proxy.CommonProxy";

    public static Logger logger;
    
    @Instance
    public static TravelersBackpack INSTANCE;
    
    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel("travelersbackpack");
    
    public static CreativeTabs TRAVELERSBACKPACKTAB = new TravelersBackpackCreativeTab(TravelersBackpack.MODID);

    @SidedProxy(clientSide = CLIENT_PROXY_CLASS, serverSide = COMMON_PROXY_CLASS)
	public static CommonProxy proxy;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
    	logger = event.getModLog();
    	proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
    	proxy.init(event);
    }
    
    @EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
    	proxy.postInit(event);
    }
}