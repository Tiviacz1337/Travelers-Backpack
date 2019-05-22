package com.tiviacz.travellersbackpack.network.client;

import com.tiviacz.travellersbackpack.gui.inventory.InventoryTravellersBackpack;
import com.tiviacz.travellersbackpack.wearable.WearableUtils;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UpdateInventoryPacket implements IMessage
{
	private NBTTagCompound tag;
	
	public UpdateInventoryPacket()
	{
		
	}
	
	public UpdateInventoryPacket(NBTTagCompound tag)
	{
		this.tag = tag;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.tag = ByteBufUtils.readTag(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		ByteBufUtils.writeTag(buf, this.tag);
	}
	
	public static class Handler implements IMessageHandler<UpdateInventoryPacket, IMessage>
    {
    	public Handler() {}
    	
    	@Override
        public IMessage onMessage(UpdateInventoryPacket message, MessageContext ctx)
        {
    		if(ctx.side.isClient())
            { 
    			Minecraft mc = Minecraft.getMinecraft();
    			
    			if(mc.player != null)
    			{
    				mc.addScheduledTask(new Runnable() 
                    {
    					@Override
    					public void run() 
    					{
    						InventoryTravellersBackpack inv = WearableUtils.getBackpackInv(mc.player);
    						inv.loadItems(message.tag);
    					}
                    });
    			}
            }
    		return null;
        }
    }
}