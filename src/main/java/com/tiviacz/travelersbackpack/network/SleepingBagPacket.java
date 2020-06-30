package com.tiviacz.travelersbackpack.network;

import com.tiviacz.travelersbackpack.common.ServerActions;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SleepingBagPacket implements IMessage
{
	public int x;
    public int y;
    public int z;
    
    public SleepingBagPacket()
    {
    	
    }
    
    public SleepingBagPacket(int X, int Y, int Z)
    {
        this.x = X;
        this.y = Y;
        this.z = Z;
    }

    @Override
    public void fromBytes(ByteBuf buf)
    {
        x = buf.readInt();
        y = buf.readInt();
        z = buf.readInt();
    }

    @Override
    public void toBytes(ByteBuf buf)
    {
        buf.writeInt(x);
        buf.writeInt(y);
        buf.writeInt(z);
    }
    
    public static class Handler implements IMessageHandler<SleepingBagPacket, IMessage>
    {
    	public Handler() {}
    	
    	@Override
        public IMessage onMessage(SleepingBagPacket message, MessageContext ctx)
        {
    		final EntityPlayerMP sendingPlayer = ctx.getServerHandler().player;
    		
            if(ctx.side.isServer() && sendingPlayer != null)
            { 
            	final WorldServer playerWorldServer = sendingPlayer.getServerWorld();
            	
                playerWorldServer.addScheduledTask(() -> ServerActions.toggleSleepingBag(sendingPlayer, message.x, message.y, message.z));
            }
            return null;
        }
    }
}