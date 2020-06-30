package com.tiviacz.travelersbackpack.api.events;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@Cancelable
@SideOnly(Side.CLIENT)
public class TankTooltipEvent extends Event
{
    private final FluidStack fluidstack;
    private final List<String> tooltip;

    /**
     * Event is fired before FluidTanks' tooltips are set.
     * @param fluidstack
     * @param tooltip
     */
    public TankTooltipEvent(FluidStack fluidstack, List<String> tooltip)
    {
        this.fluidstack = fluidstack;
        this.tooltip = tooltip;
    }

    public FluidStack getFluid()
    {
        return this.fluidstack;
    }

    public List<String> getTooltip()
    {
        return this.tooltip;
    }
}