package com.tiviacz.travelersbackpack.client;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class BackpackParticles 
{
	public static void spawnNyanParticles(EntityPlayer player, World world)
    {
        int i = 2;
        
        for(int j = 0; j < i * 3; ++j)
        {
            float f = world.rand.nextFloat() * (float) Math.PI * 2.0F;
            float f1 = world.rand.nextFloat() * 0.5F + 0.5F;
            float f2 = MathHelper.sin(f) * i * 0.1F * f1;
            float f3 = MathHelper.cos(f) * i * 0.1F * f1;
            world.spawnParticle(EnumParticleTypes.NOTE, player.posX + f2, player.getRenderBoundingBox().minY + 0.8F, player.posZ + f3, (double)(float)Math.pow(2.0D, (world.rand.nextInt(169) - 12) / 12.0D) / 24.0D, -1.0D, 0.0D);
}
    }
	
	public static void spawnSlimeParticles(EntityPlayer player, World world)
    {
        int i = 3;
        
        for(int j = 0; j < i * 2; ++j)
        {
            float f = world.rand.nextFloat() * (float) Math.PI * 2.0F;
            float f1 = world.rand.nextFloat() * 0.5F + 0.5F;
            float f2 = MathHelper.sin(f) * i * 0.5F * f1;
            float f3 = MathHelper.cos(f) * i * 0.5F * f1;
            world.spawnParticle(EnumParticleTypes.SLIME, player.posX + f2, player.getRenderBoundingBox().minY, player.posZ + f3, 0.0D, 0.0625D, 0.0D);
        }
    }
	
	public static void spawnEmeraldParticles(EntityPlayer player, World world)
    {
		if(world.rand.nextInt(3) == 1)
		{
			float f = world.rand.nextFloat() * (float) Math.PI * 2.0F;
	        float f1 = world.rand.nextFloat() * 0.5F + 0.5F;
	        float f2 = MathHelper.sin(f) * 0.5F * f1;
	        float f3 = MathHelper.cos(f) * 0.5F * f1;
	        world.spawnParticle(EnumParticleTypes.VILLAGER_HAPPY, player.posX + f2, player.getRenderBoundingBox().minY + world.rand.nextFloat() + 0.5F, player.posZ + f3, (double)(float)Math.pow(2.0D, (world.rand.nextInt(169) - 12) / 12.0D) / 24.0D, -1.0D, 0.0D);
		}
    }
}