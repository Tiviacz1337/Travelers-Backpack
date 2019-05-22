package com.tiviacz.travellersbackpack.network;

import com.tiviacz.travellersbackpack.common.ServerActions;
import com.tiviacz.travellersbackpack.wearable.WearableUtils;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class CycleToolPacket implements IMessage
{
	public int directionOfCycle;
	public int typeOfAction;
	
	public CycleToolPacket()
	{
		
	}
	
	public CycleToolPacket(int directionOfCycle, int typeOfAction)
	{
		this.directionOfCycle = directionOfCycle;
		this.typeOfAction = typeOfAction;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.directionOfCycle = buf.readInt();
		this.typeOfAction = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeInt(this.directionOfCycle);
		buf.writeInt(this.typeOfAction);
	}
	
	public static class Handler implements IMessageHandler<CycleToolPacket, IMessage>
    {
		public static final int CYCLE_TOOL_ACTION = 0;
		public static final int SWITCH_HOSE_ACTION = 1;
		public static final int TOGGLE_HOSE_TANK = 2;
		
    	public Handler() {}
    	
    	@Override
        public IMessage onMessage(CycleToolPacket message, MessageContext ctx)
        {
    		final EntityPlayerMP sendingPlayer = ctx.getServerHandler().player;
    		
            if(ctx.side.isServer() && sendingPlayer != null)
            { 
            	if(WearableUtils.isWearingBackpack(sendingPlayer))
            	{
            		final WorldServer playerWorldServer = sendingPlayer.getServerWorld();
                    
            		if(message.typeOfAction == CYCLE_TOOL_ACTION)
            		{
            			playerWorldServer.addScheduledTask(new Runnable() 
                		{
                			@Override
                			public void run() 
                			{
                				ServerActions.cycleTool(sendingPlayer, message.directionOfCycle);
                			}
                		});
            		}
            		
            		if(message.typeOfAction == SWITCH_HOSE_ACTION)
            		{
            			playerWorldServer.addScheduledTask(new Runnable() 
                		{
                			@Override
                			public void run() 
                			{
                				ServerActions.switchHoseMode(sendingPlayer, message.directionOfCycle);
                			}
                		});
            		}
            		
            		if(message.typeOfAction == TOGGLE_HOSE_TANK)
            		{
            			playerWorldServer.addScheduledTask(new Runnable() 
                		{
                			@Override
                			public void run() 
                			{
                				ServerActions.toggleHoseTank(sendingPlayer);
                			}
                		});
            		}
            	}
            }
            return null;
        }
    }
}