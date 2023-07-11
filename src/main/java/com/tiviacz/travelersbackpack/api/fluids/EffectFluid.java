package com.tiviacz.travelersbackpack.api.fluids;

import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class EffectFluid
{
    public String uniqueId;
    public Fluid fluid;
    public int effectID;
    public int amountRequired;

    public EffectFluid(String uniqueId, FluidStack fluidStack, int amountRequired)
    {
        this(uniqueId, fluidStack.getFluid(), amountRequired);
    }

    public EffectFluid(String uniqueId, Fluid fluid, int amountRequired)
    {
        this.uniqueId = uniqueId;
        this.fluid = fluid;
        this.effectID = 0;
        this.amountRequired = amountRequired;

        if(fluid != null)
        {
            com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry.registerFluidEffect(this);
        }
    }

    public EffectFluid(String uniqueId, String modid, String fluidName, int amountRequired)
    {
        Fluid fluid = ForgeRegistries.FLUIDS.getValue(new ResourceLocation(modid, fluidName));
        this.uniqueId = uniqueId;
        this.fluid = fluid;
        this.effectID = 0;
        this.amountRequired = amountRequired;

        if(fluid != null)
        {
            com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry.registerFluidEffect(this);
        }
    }

    public String getUniqueId()
    {
        return uniqueId;
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