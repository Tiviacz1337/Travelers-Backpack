package com.tiviacz.travellersbackpack.network.client;

import com.tiviacz.travellersbackpack.client.ClientActions;

import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class ParticlesPacket implements IMessage
{
	private byte particleID;
	private int entityID;
	
	public ParticlesPacket()
	{
		
	}
	
	public ParticlesPacket(byte particleID, int entityID)
	{
		this.particleID = particleID;
		this.entityID = entityID;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.particleID = buf.readByte();
		this.entityID = buf.readInt();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeByte(this.particleID);
		buf.writeInt(this.entityID);
	}
	
	public static class Handler implements IMessageHandler<ParticlesPacket, IMessage>
    {
    	public static final byte NYAN_PARTICLE = 0;
        public static final byte SLIME_PARTICLE = 1;
        public static final byte EMERALD_PARTICLE = 2;
        
    	public Handler() {}
    	
    	@Override
        public IMessage onMessage(ParticlesPacket message, MessageContext ctx)
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
                        	Entity entity = mc.world.getEntityByID(message.entityID);
                        	ClientActions.spawnParticlesAtEntity(entity, message.particleID);
                        }
                    });
    			}
            }
    		return null;
        }
    }
}