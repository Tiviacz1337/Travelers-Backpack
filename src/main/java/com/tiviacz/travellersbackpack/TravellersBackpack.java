package com.tiviacz.travellersbackpack;

import org.apache.logging.log4j.Logger;

import com.tiviacz.travellersbackpack.common.TravellersBackpackCreativeTab;
import com.tiviacz.travellersbackpack.proxy.CommonProxy;

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

@Mod(modid = TravellersBackpack.MODID, name = TravellersBackpack.NAME, version = TravellersBackpack.VERSION, updateJSON = TravellersBackpack.UPDATE_JSON)
public class TravellersBackpack
{
    public static final String MODID = "travellersbackpack";
    public static final String NAME = "Traveller's Backpack";
    public static final String VERSION = "0.1.8a";
    public static final String UPDATE_JSON = "https://gist.githubusercontent.com/Tiviacz1337/906937677aa472285dff9d6c2a189d5e/raw/2d0fbb4c09b8e5da711c3b03914882e6a551a658/TravellersBackpackUpdateJSON.json";
    public static final String CLIENT_PROXY_CLASS = "com.tiviacz.travellersbackpack.proxy.ClientProxy";
    public static final String COMMON_PROXY_CLASS = "com.tiviacz.travellersbackpack.proxy.CommonProxy";

    public static Logger logger;
    
    @Instance
    public static TravellersBackpack INSTANCE;
    
    public static final SimpleNetworkWrapper NETWORK = NetworkRegistry.INSTANCE.newSimpleChannel("travellersbackpack");
    
    public static CreativeTabs TRAVELLERSBACKPACKTAB = new TravellersBackpackCreativeTab(TravellersBackpack.MODID);

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