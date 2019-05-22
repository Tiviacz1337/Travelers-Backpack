package com.tiviacz.travellersbackpack.network.client;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncPlayerDataPacket implements IMessage
{
	private NBTTagCompound tag;
	private boolean update;
	
	public SyncPlayerDataPacket()
	{
		
	}
	
	public SyncPlayerDataPacket(NBTTagCompound tag, boolean update)
	{
		this.tag = tag;
		this.update = update;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.tag = ByteBufUtils.readTag(buf);
		this.update = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		ByteBufUtils.writeTag(buf, this.tag);
		buf.writeBoolean(this.update);
	}
	
	public static class Handler implements IMessageHandler<SyncPlayerDataPacket, IMessage>
    {
    	public Handler() {}
    	
    	@Override
        public IMessage onMessage(SyncPlayerDataPacket message, MessageContext ctx)
        {
    		if(ctx.side.isClient())
            { 
    			Minecraft mc = Minecraft.getMinecraft();
    			
    			if(mc.player != null)
    			{
    				if(message.update)
    				{
    					mc.addScheduledTask(new Runnable() 
                        {
                        	@Override
                        	public void run() 
                        	{
                        		mc.player.getEntityData().setTag("Wearable", message.tag);
                        	}
                        });
    				}
    				
    				if(!message.update)
    				{
    					mc.addScheduledTask(new Runnable() 
                        {
                        	@Override
                        	public void run() 
                        	{
                        		if(mc.player.getEntityData().hasKey("Wearable"))
                        		{
                        			mc.player.getEntityData().removeTag("Wearable");
                        		}
                        	}
                        });
    				}
    			}
            }
    		return null;
        }
    }
}