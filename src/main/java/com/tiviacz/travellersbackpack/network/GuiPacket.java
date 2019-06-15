package com.tiviacz.travellersbackpack.network;

import com.tiviacz.travellersbackpack.TravellersBackpack;
import com.tiviacz.travellersbackpack.capability.CapabilityUtils;
import com.tiviacz.travellersbackpack.util.Reference;

import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class GuiPacket implements IMessage
{
	private byte type;
	private byte from;
	
	public GuiPacket()
	{
		
	}
	
	public GuiPacket(byte type, byte from)
	{
		this.type = type;
		this.from = from;
	}

	@Override
	public void fromBytes(ByteBuf buf) 
	{
		this.type = buf.readByte();
		this.from = buf.readByte();
	}

	@Override
	public void toBytes(ByteBuf buf) 
	{
		buf.writeByte(this.type);
		buf.writeByte(this.from);
	}
	
	public static class Handler implements IMessageHandler<GuiPacket, IMessage>
    {
		public static final byte FROM_KEYBIND = 0;
		public static final byte BACKPACK_GUI = 1;
		
    	public Handler() {}
    	
    	@Override
        public IMessage onMessage(GuiPacket message, MessageContext ctx)
        {
    		final EntityPlayerMP sendingPlayer = ctx.getServerHandler().player;
    		
    		if(sendingPlayer instanceof EntityPlayer)
    		{
    			EntityPlayer player = (EntityPlayer)sendingPlayer;
    			
    			if(ctx.side.isServer() && player != null)
                { 
                	int posX = player.getPosition().getX();
                	int posY = player.getPosition().getY();
                	int posZ = player.getPosition().getZ();
                	World world = player.world;
                	
                	if(message.type == BACKPACK_GUI)
                	{
                		if(message.from == FROM_KEYBIND)
                		{
                			if(CapabilityUtils.isWearingBackpack(player))
                			{
                				final WorldServer playerWorldServer = sendingPlayer.getServerWorld();
                            	
                                playerWorldServer.addScheduledTask(new Runnable() 
                                {
                                	@Override
                                	public void run() 
                                	{
                                		player.openGui(TravellersBackpack.INSTANCE, Reference.TRAVELLERS_BACKPACK_WEARABLE_GUI_ID, world, posX, posY, posZ);
                                	}
                                });
                			}
                		}
                	}
                }
    		}
            return null;
        }
    }
}