package com.tiviacz.travelersbackpack.proxy;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import com.tiviacz.travelersbackpack.capability.TravelersBackpackCapability;
import com.tiviacz.travelersbackpack.common.VillagerTrades;
import com.tiviacz.travelersbackpack.fluids.FluidEffectRegistry;
import com.tiviacz.travelersbackpack.common.InventoryRecipesRegistry;
import com.tiviacz.travelersbackpack.handlers.GuiHandler;
import com.tiviacz.travelersbackpack.handlers.IntegrationHandler;
import com.tiviacz.travelersbackpack.init.ModFluids;
import com.tiviacz.travelersbackpack.network.*;
import com.tiviacz.travelersbackpack.network.client.ParticlesPacket;
import com.tiviacz.travelersbackpack.network.client.SyncBackpackCapability;
import com.tiviacz.travelersbackpack.network.client.SyncBackpackCapabilityMP;
import com.tiviacz.travelersbackpack.network.client.SyncGuiPacket;
import com.tiviacz.travelersbackpack.tileentity.TileEntityTravelersBackpack;
import com.tiviacz.travelersbackpack.util.Reference;
import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy 
{
    IntegrationHandler integrationHandler = new IntegrationHandler();

    public void preInit(FMLPreInitializationEvent event)
    {
    	registerPackets();
    	ModFluids.registerFluids();
    	FluidEffectRegistry.initEffects();
        InventoryRecipesRegistry.initRecipes();
    	NetworkRegistry.INSTANCE.registerGuiHandler(TravelersBackpack.INSTANCE, new GuiHandler());
        TravelersBackpackCapability.register();
        integrationHandler.preInit(event);
    }

    public void init(FMLInitializationEvent event)
    {
    	registerTileEntities();
    	VillagerTrades.addTrades();
    	integrationHandler.init(event);
    }
    
    public void postInit(FMLPostInitializationEvent event)
    {
    	integrationHandler.postInit(event);
    }
    
    public void registerItemRenderer(Item item, int meta, String id)
	{
    	
	}
    
    public void registerTileEntities()
    {
    	GameRegistry.registerTileEntity(TileEntityTravelersBackpack.class, new ResourceLocation(TravelersBackpack.MODID, "TileEntityTravelersBackpack"));
    }
    
    public void registerPackets()
    {
    	TravelersBackpack.NETWORK.registerMessage(SleepingBagPacket.Handler.class, SleepingBagPacket.class, Reference.SLEEPING_BAG_PACKET_ID, Side.SERVER);
    	TravelersBackpack.NETWORK.registerMessage(GuiPacket.Handler.class, GuiPacket.class, Reference.GUI_PACKET_ID, Side.SERVER);
    	TravelersBackpack.NETWORK.registerMessage(EquipBackpackPacket.Handler.class, EquipBackpackPacket.class, Reference.EQUIP_BACKPACK_PACKET_ID, Side.SERVER);
    	TravelersBackpack.NETWORK.registerMessage(UnequipBackpackPacket.Handler.class, UnequipBackpackPacket.class, Reference.UNEQUIP_BACKPACK_PACKET_ID, Side.SERVER);
    	TravelersBackpack.NETWORK.registerMessage(CycleToolPacket.Handler.class, CycleToolPacket.class, Reference.CYCLE_TOOL_PACKET_ID, Side.SERVER);
    	TravelersBackpack.NETWORK.registerMessage(SyncBackpackCapability.Handler.class, SyncBackpackCapability.class, Reference.SYNC_BACKPACK_CAPABILITY_ID, Side.CLIENT);
    	TravelersBackpack.NETWORK.registerMessage(SyncBackpackCapabilityMP.Handler.class, SyncBackpackCapabilityMP.class, Reference.SYNC_BACKPACK_CAPABILITY_MP_ID, Side.CLIENT);
    	TravelersBackpack.NETWORK.registerMessage(SyncGuiPacket.Handler.class, SyncGuiPacket.class, Reference.SYNC_GUI_PACKET_ID, Side.CLIENT);
    	TravelersBackpack.NETWORK.registerMessage(ParticlesPacket.Handler.class, ParticlesPacket.class, Reference.PARTICLE_PACKET_ID, Side.CLIENT);
    }
    
    public void handleBackpackCapability(NBTTagCompound tag, int entityID)
    {
    	
    }
}