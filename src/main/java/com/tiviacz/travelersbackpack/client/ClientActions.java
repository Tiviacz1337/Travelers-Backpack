package com.tiviacz.travelersbackpack.client;

import com.tiviacz.travelersbackpack.network.client.ParticlesPacket;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientActions 
{
	@SideOnly(Side.CLIENT)
    public static void spawnParticlesAtEntity(Entity entity, byte particleCode)
    {
        if(entity instanceof EntityPlayer)
        {
            EntityPlayer player = (EntityPlayer)entity;
            
            switch(particleCode)
            {
                case ParticlesPacket.Handler.NYAN_PARTICLE:
                    BackpackParticles.spawnNyanParticles(player, player.world);
                    break;
                case ParticlesPacket.Handler.SLIME_PARTICLE:
                   	BackpackParticles.spawnSlimeParticles(player, player.world);
                    break; 
                case ParticlesPacket.Handler.EMERALD_PARTICLE:
                	BackpackParticles.spawnEmeraldParticles(player, player.world);
                	break;
            }
        }
    }
}