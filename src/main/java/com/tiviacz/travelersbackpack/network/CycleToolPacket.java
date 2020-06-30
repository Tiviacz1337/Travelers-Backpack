package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.capability.CapabilityUtils;
import com.tiviacz.travelersbackpack.common.ServerActions;
import com.tiviacz.travelersbackpack.util.Reference;
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
    	public Handler() {}
    	
    	@Override
        public IMessage onMessage(CycleToolPacket message, MessageContext ctx)
        {
    		final EntityPlayerMP sendingPlayer = ctx.getServerHandler().player;
    		
            if(ctx.side.isServer() && sendingPlayer != null)
            { 
            	if(CapabilityUtils.isWearingBackpack(sendingPlayer))
            	{
            		final WorldServer playerWorldServer = sendingPlayer.getServerWorld();
                    
            		if(message.typeOfAction == Reference.CYCLE_TOOL_ACTION)
            		{
            			playerWorldServer.addScheduledTask(() -> ServerActions.cycleTool(sendingPlayer, message.directionOfCycle));
            		}
            		
            		if(message.typeOfAction == Reference.SWITCH_HOSE_ACTION)
            		{
            			playerWorldServer.addScheduledTask(() -> ServerActions.switchHoseMode(sendingPlayer, message.directionOfCycle));
            		}
            		
            		if(message.typeOfAction == Reference.TOGGLE_HOSE_TANK)
            		{
            			playerWorldServer.addScheduledTask(() -> ServerActions.toggleHoseTank(sendingPlayer));
            		}
            		
            		if(message.typeOfAction == Reference.EMPTY_TANK)
            		{
            			playerWorldServer.addScheduledTask(() -> ServerActions.emptyTank(message.directionOfCycle, sendingPlayer, sendingPlayer.world));
            		}
            	}
            }
            return null;
        }
    }
}