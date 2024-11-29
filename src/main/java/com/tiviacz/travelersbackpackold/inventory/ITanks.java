package com.tiviacz.travelersbackpackold.inventory;

public interface ITanks
{
    FluidTank getLeftTank();

    FluidTank getRightTank();

    boolean updateTankSlots();
}