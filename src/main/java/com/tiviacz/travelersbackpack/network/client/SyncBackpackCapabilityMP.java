package com.tiviacz.travelersbackpack.network.client;

import com.tiviacz.travelersbackpack.TravelersBackpack;

import io.netty.buffer.ByteBuf;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SyncBackpackCapabilityMP implements IMessage
{
	private NBTTagCompound tag;
	private int entityID;
	
	public SyncBackpackCapabilityMP()
	{
		
	}
	
	public SyncBackpackCapabilityMP(NBTTagCompound tag, int entityID)
	{
		this.tag = tag;
		this.entityID = entityID;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.tag = ByteBufUtils.readTag(buf);
		this.entityID = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		ByteBufUtils.writeTag(buf, this.tag);
		buf.writeInt(this.entityID);
	}
	
	public static class Handler implements IMessageHandler<SyncBackpackCapabilityMP, IMessage>
    {
    	public Handler() {}
    	
    	@Override
        public IMessage onMessage(SyncBackpackCapabilityMP message, MessageContext ctx)
        {
    		if(ctx.side.isClient())
    		{ 
        		FMLCommonHandler.instance().getWorldThread(ctx.netHandler).addScheduledTask(() -> TravelersBackpack.proxy.handleBackpackCapability(message.tag, message.entityID));
    		}
			return null;
        }
    }
}