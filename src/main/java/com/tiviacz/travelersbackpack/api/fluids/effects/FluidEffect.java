package com.tiviacz.travelersbackpack.api.fluids.effects;

import net.minecraft.entity.Entity;
import net.minecraft.world.World;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;

public abstract class FluidEffect 
{
    public Fluid fluid;
    public int effectID;
    public int amountRequired;
    
    public FluidEffect(FluidStack fluidStack, int amountRequired)
    {
    	this(fluidStack.getFluid(), amountRequired);
    }

    public FluidEffect(Fluid fluid, int amountRequired)
    {
        this.fluid = fluid;
        this.effectID = 0;
        this.amountRequired = amountRequired;
        
        if(fluid != null)
        {
            com.tiviacz.travelersbackpack.fluids.FluidEffectRegistry.registerFluidEffect(this);
        }
    }

    public FluidEffect(String fluidName, int amountRequired)
    {
        Fluid fluid = FluidRegistry.getFluid(fluidName);
        this.fluid = fluid;
        this.effectID = 0;
        this.amountRequired = amountRequired;
        
        if(fluid != null)
        {
        	com.tiviacz.travelersbackpack.fluids.FluidEffectRegistry.registerFluidEffect(this);
        }
    }

    public void setEffectID(int id)
    {
        effectID = id;
    }

    public int getEffectID()
    {
        return effectID;
    }

    /**
     * This method determines what will happen to the player (or world!) when drinking the
     * corresponding fluid. For example set potion effects, set player on fire,
     * heal, fill hunger, etc. You can use the world parameter to make
     * conditions based on where the player is.
     *
     * @param world  The World.
     * @param entity The entity that will be affected.
     */
    public abstract void affectDrinker(FluidStack stack, World world, Entity entity);

    /**
     * This method runs before hose is used.
     *
     * @param world  The World.
     * @param entity The entity that will be affected.
     */
    public abstract boolean canExecuteEffect(FluidStack stack, World world, Entity entity);
}