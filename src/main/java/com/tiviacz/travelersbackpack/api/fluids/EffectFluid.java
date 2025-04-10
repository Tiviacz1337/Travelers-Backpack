package com.tiviacz.travelersbackpack.api.fluids;

import com.tiviacz.travelersbackpack.inventory.FluidVariantWrapper;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.material.Fluid;

public abstract class EffectFluid {
    public final String uniqueId;
    public final Fluid fluid;
    public int effectID;
    public final long amountRequired;

    public EffectFluid(String uniqueId, FluidVariantWrapper fluidVariantWrapper, long amountRequired) {
        this(uniqueId, fluidVariantWrapper.fluidVariant().getFluid(), amountRequired);
    }

    public EffectFluid(String uniqueId, Fluid fluid, long amountRequired) {
        this.uniqueId = uniqueId;
        this.fluid = fluid;
        this.effectID = 0;
        this.amountRequired = amountRequired;

        if(fluid != null) {
            com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry.registerFluidEffect(this);
        }
    }

    public EffectFluid(String uniqueId, String modid, String fluidName, long amountRequired) {
        Fluid fluid = BuiltInRegistries.FLUID.get(ResourceLocation.fromNamespaceAndPath(modid, fluidName));
        this.uniqueId = uniqueId;
        this.fluid = fluid;
        this.effectID = 0;
        this.amountRequired = amountRequired;

        if(fluid != null) {
            com.tiviacz.travelersbackpack.fluids.EffectFluidRegistry.registerFluidEffect(this);
        }
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setEffectID(int id) {
        effectID = id;
    }

    public int getEffectID() {
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
    public abstract void affectDrinker(FluidVariantWrapper stack, Level world, Entity entity);

    /**
     * This method runs before hose is used.
     *
     * @param world  The World.
     * @param entity The entity that will be affected.
     */
    public abstract boolean canExecuteEffect(FluidVariantWrapper stack, Level world, Entity entity);
}