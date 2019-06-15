package com.tiviacz.travellersbackpack.network.client;

import com.tiviacz.travellersbackpack.capability.CapabilityUtils;
import com.tiviacz.travellersbackpack.capability.IBackpack;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncBackpackCapability implements IMessage
{
	private NBTTagCompound tag;
	
	public SyncBackpackCapability()
	{
		
	}
	
	public SyncBackpackCapability(NBTTagCompound tag)
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
	
	public static class Handler implements IMessageHandler<SyncBackpackCapability, IMessage>
    {
    	public Handler() {}
    	
    	@Override
        public IMessage onMessage(SyncBackpackCapability message, MessageContext ctx)
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
                        	IBackpack cap = CapabilityUtils.getCapability(mc.player);
                        	cap.setWearable(new ItemStack(message.tag));
                        }
                    });
    			}
            }
    		return null;
        }
    }
}