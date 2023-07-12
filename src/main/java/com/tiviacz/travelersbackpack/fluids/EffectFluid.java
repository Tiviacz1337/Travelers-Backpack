package com.tiviacz.travelersbackpack.fluids;

import net.fabricmc.fabric.api.transfer.v1.fluid.FluidVariant;
import net.fabricmc.fabric.api.transfer.v1.storage.StorageView;
import net.minecraft.entity.Entity;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;

public abstract class EffectFluid
{
    public String uniqueId;
    public Fluid fluid;
    public int effectID;
    public long amountRequired;

    public EffectFluid(String uniqueId, FluidVariant fluidVariant, long amountRequired)
    {
        this(uniqueId, fluidVariant.getFluid(), amountRequired);
    }

    public EffectFluid(String uniqueId, Fluid fluid, long amountRequired)
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

    public EffectFluid(String uniqueId, String modid, String fluidName, long amountRequired)
    {
        Fluid fluid = Registry.FLUID.get(new Identifier(modid, fluidName));
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
    public abstract void affectDrinker(StorageView<FluidVariant> variant, World world, Entity entity);

    /**
     * This method runs before hose is used.
     *
     * @param world  The World.
     * @param entity The entity that will be affected.
     */
    public abstract boolean canExecuteEffect(StorageView<FluidVariant> variant, World world, Entity entity);
}