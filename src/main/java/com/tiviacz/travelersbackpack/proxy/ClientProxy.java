package com.tiviacz.travelersbackpack.proxy;

import com.tiviacz.travelersbackpack.client.render.RendererItemTravelersBackpack;
import org.lwjgl.input.Keyboard;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;
import com.tiviacz.travelersbackpack.client.render.LayerTravelersBackpack;
import com.tiviacz.travelersbackpack.client.render.RendererTileTravelersBackpack;
import com.tiviacz.travelersbackpack.init.ModItems;
import com.tiviacz.travelersbackpack.tileentity.TileEntityTravelersBackpack;
import com.tiviacz.travelersbackpack.util.Reference;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy
{
	public static KeyBinding openBackpack = new KeyBinding(Reference.INVENTORY, Keyboard.KEY_B, Reference.CATEGORY);
	public static KeyBinding toggleTank = new KeyBinding(Reference.TOGGLE_TANK, Keyboard.KEY_N, Reference.CATEGORY);
	public static KeyBinding cycleTool = new KeyBinding(Reference.CYCLE_TOOL, Keyboard.KEY_Z, Reference.CATEGORY);
	
	@Override
    public void preInit(FMLPreInitializationEvent event)
    {
    	super.preInit(event);
    	addBackpackItemRenderer();
    }

	@Override
    public void init(FMLInitializationEvent event)
    {
    	super.init(event);
    	registerTESR();
    	addWearableModel();
    	registerKeyBindings();
    }
    
    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
    	super.postInit(event);
    }
    
    @Override
	public void registerItemRenderer(Item item, int meta, String id)
	{
		ModelLoader.setCustomModelResourceLocation(item, meta, new ModelResourceLocation(item.getRegistryName(), id));
	}
    
    public void registerKeyBindings()
    {
    	ClientRegistry.registerKeyBinding(openBackpack);
    	ClientRegistry.registerKeyBinding(toggleTank);
    	ClientRegistry.registerKeyBinding(cycleTool);
    }
    
    public void registerTESR()
    {
    	ClientRegistry.bindTileEntitySpecialRenderer(TileEntityTravelersBackpack.class, new RendererTileTravelersBackpack());
    }
    
    public void addBackpackItemRenderer()
    {
    	ModItems.TRAVELERS_BACKPACK.setTileEntityItemStackRenderer(new RendererItemTravelersBackpack());
    }
    
    public void addWearableModel()
    {
    	for(RenderPlayer renderer : Minecraft.getMinecraft().getRenderManager().getSkinMap().values()) 
        {
        	renderer.addLayer(new LayerTravelersBackpack(renderer));
    	}
    }
    
    public void handleBackpackCapability(NBTTagCompound tag, int entityID)
    {
		EntityPlayer player = (EntityPlayer)Minecraft.getMinecraft().player.world.getEntityByID(entityID);
		ITravelersBackpack cap = CapabilityUtils.getCapability(player);

		if(cap != null)
		{
			cap.setWearable(new ItemStack(tag));
		}
	}
}