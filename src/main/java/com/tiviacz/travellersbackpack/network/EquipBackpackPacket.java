package com.tiviacz.travellersbackpack.network;

import com.tiviacz.travellersbackpack.common.ServerActions;
import com.tiviacz.travellersbackpack.wearable.WearableUtils;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class EquipBackpackPacket implements IMessage
{
	private boolean valid;
	
	public EquipBackpackPacket()
	{
		
	}
	
	public EquipBackpackPacket(boolean valid)
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
	
	public static class Handler implements IMessageHandler<EquipBackpackPacket, IMessage>
    {
    	public Handler() {}
    	
    	@Override
        public IMessage onMessage(EquipBackpackPacket message, MessageContext ctx)
        {
    		final EntityPlayerMP sendingPlayer = ctx.getServerHandler().player;
    		
            if(ctx.side.isServer() && sendingPlayer != null)
            { 
            	if(message.valid)
            	{
            		if(!WearableUtils.isWearingBackpack(sendingPlayer))
            		{
            			final WorldServer playerWorldServer = sendingPlayer.getServerWorld();
                        	
            			playerWorldServer.addScheduledTask(new Runnable() 
            			{
            				@Override
            				public void run() 
            				{
            					ServerActions.equipBackpack(sendingPlayer);
            				}
            			});
            		}
            		else
            		{
            			sendingPlayer.closeScreen();
            			sendingPlayer.sendMessage(new TextComponentTranslation("actions.equip_backpack.otherbackpack"));
            		}
            	}
            }
            return null;
        }
    }
}