package com.tiviacz.travellersbackpack.network;

import com.tiviacz.travellersbackpack.capability.CapabilityUtils;
import com.tiviacz.travellersbackpack.common.ServerActions;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class UnequipBackpackPacket implements IMessage
{
	private boolean valid;
	
	public UnequipBackpackPacket()
	{
		
	}
	
	public UnequipBackpackPacket(boolean valid)
	{
		this.valid = valid;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.valid = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeBoolean(this.valid);
	}
	
	public static class Handler implements IMessageHandler<UnequipBackpackPacket, IMessage>
    {
    	public Handler() {}
    	
    	@Override
        public IMessage onMessage(UnequipBackpackPacket message, MessageContext ctx)
        {
    		final EntityPlayerMP sendingPlayer = ctx.getServerHandler().player;
    		
            if(ctx.side.isServer() && sendingPlayer != null)
            { 
            	if(message.valid)
            	{
            		if(CapabilityUtils.isWearingBackpack(sendingPlayer))
            		{
            			final WorldServer playerWorldServer = sendingPlayer.getServerWorld();
                        	
            			playerWorldServer.addScheduledTask(new Runnable() 
            			{
            				@Override
            				public void run() 
            				{
            					ServerActions.unequipBackpack(sendingPlayer);
            				}
            			});
            		}
            		else
            		{
            			sendingPlayer.closeScreen();
            			sendingPlayer.sendMessage(new TextComponentTranslation("actions.unequip_backpack.nobackpack"));
            		}
            	}
            }
            return null;
        }
    }
}