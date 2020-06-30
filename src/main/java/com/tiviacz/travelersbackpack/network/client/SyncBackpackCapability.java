package com.tiviacz.travelersbackpack.network.client;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.capability.ITravelersBackpack;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

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
		@SideOnly(Side.CLIENT)
        public IMessage onMessage(SyncBackpackCapability message, MessageContext ctx)
        {
    		if(ctx.side.isClient())
            {
    			Minecraft mc = Minecraft.getMinecraft();

    			mc.addScheduledTask(() ->
				{
					if(mc.player != null)
					{
						ITravelersBackpack cap = CapabilityUtils.getCapability(mc.player);
						cap.setWearable(new ItemStack(message.tag));
					}
				});
            }
    		return null;
        }
    }
}