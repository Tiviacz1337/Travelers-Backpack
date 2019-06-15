package com.tiviacz.travellersbackpack.proxy;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.capability.BackpackStorage;
import com.tiviacz.travellersbackpack.capability.BackpackWearable;
import com.tiviacz.travellersbackpack.capability.IBackpack;
import com.tiviacz.travellersbackpack.fluids.FluidEffectRegistry;
import com.tiviacz.travellersbackpack.handlers.GuiHandler;
import com.tiviacz.travellersbackpack.init.ModFluids;
import com.tiviacz.travellersbackpack.network.CycleToolPacket;
import com.tiviacz.travellersbackpack.network.EquipBackpackPacket;
import com.tiviacz.travellersbackpack.network.GuiPacket;
import com.tiviacz.travellersbackpack.network.SleepingBagPacket;
import com.tiviacz.travellersbackpack.network.UnequipBackpackPacket;
import com.tiviacz.travellersbackpack.network.client.SyncBackpackCapability;
import com.tiviacz.travellersbackpack.network.client.SyncBackpackCapabilityMP;
import com.tiviacz.travellersbackpack.tileentity.TileEntityTravellersBackpack;
import com.tiviacz.travellersbackpack.util.Reference;

import net.minecraft.item.Item;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy 
{
    public void preInit(FMLPreInitializationEvent event)
    {
    	registerPackets();
    	ModFluids.registerFluids();
    	FluidEffectRegistry.initEffects();
    	NetworkRegistry.INSTANCE.registerGuiHandler(TravellersBackpack.INSTANCE, new GuiHandler());
    	CapabilityManager.INSTANCE.register(IBackpack.class, new BackpackStorage(), BackpackWearable::new);
    }

    public void init(FMLInitializationEvent event)
    {
    	registerTileEntities();
    }
    
    public void postInit(FMLPostInitializationEvent event)
    {
    	
    }
    
    public void registerItemRenderer(Item item, int meta, String id)
	{
    	
	}
    
    public void registerTileEntities()
    {
    	GameRegistry.registerTileEntity(TileEntityTravellersBackpack.class, new ResourceLocation(TravellersBackpack.MODID, "TileEntityTravellersBackpack"));
    }
    
    public void registerPackets()
    {
    	TravellersBackpack.NETWORK.registerMessage(SleepingBagPacket.Handler.class, SleepingBagPacket.class, Reference.SLEEPING_BAG_PACKET_ID, Side.SERVER);
    	TravellersBackpack.NETWORK.registerMessage(GuiPacket.Handler.class, GuiPacket.class, Reference.GUI_PACKET_ID, Side.SERVER);
    	TravellersBackpack.NETWORK.registerMessage(EquipBackpackPacket.Handler.class, EquipBackpackPacket.class, Reference.EQUIP_BACKPACK_PACKET_ID, Side.SERVER);
    	TravellersBackpack.NETWORK.registerMessage(UnequipBackpackPacket.Handler.class, UnequipBackpackPacket.class, Reference.UNEQUIP_BACKPACK_PACKET_ID, Side.SERVER);
    	TravellersBackpack.NETWORK.registerMessage(CycleToolPacket.Handler.class, CycleToolPacket.class, Reference.CYCLE_TOOL_PACKET_ID, Side.SERVER);
    	TravellersBackpack.NETWORK.registerMessage(SyncBackpackCapability.Handler.class, SyncBackpackCapability.class, Reference.SYNC_BACKPACK_CAPABILITY_ID, Side.CLIENT);
    	TravellersBackpack.NETWORK.registerMessage(SyncBackpackCapabilityMP.Handler.class, SyncBackpackCapabilityMP.class, Reference.SYNC_BACKPACK_CAPABILITY_MP_ID, Side.CLIENT);
    }
    
    public void handleBackpackCapability(NBTTagCompound tag, int entityID)
    {
    	
    }
}