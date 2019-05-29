package com.tiviacz.travellersbackpack.proxy;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.fluids.FluidEffectRegistry;
import com.tiviacz.travellersbackpack.handlers.GuiHandler;
import com.tiviacz.travellersbackpack.init.ModFluids;
import com.tiviacz.travellersbackpack.network.CycleToolPacket;
import com.tiviacz.travellersbackpack.network.EquipBackpackPacket;
import com.tiviacz.travellersbackpack.network.GuiPacket;
import com.tiviacz.travellersbackpack.network.SleepingBagPacket;
import com.tiviacz.travellersbackpack.network.UnequipBackpackPacket;
import com.tiviacz.travellersbackpack.network.client.SyncPlayerDataPacket;
import com.tiviacz.travellersbackpack.network.client.UpdateInventoryPacket;
import com.tiviacz.travellersbackpack.tileentity.TileEntityTravellersBackpack;
import com.tiviacz.travellersbackpack.util.Reference;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
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
    	TravellersBackpack.NETWORK.registerMessage(SyncPlayerDataPacket.Handler.class, SyncPlayerDataPacket.class, Reference.SYNC_PLAYER_DATA_PACKET_ID, Side.CLIENT);
    	TravellersBackpack.NETWORK.registerMessage(UpdateInventoryPacket.Handler.class, UpdateInventoryPacket.class, Reference.UPDATE_INVENTORY_PACKET_ID, Side.CLIENT);
    }
}