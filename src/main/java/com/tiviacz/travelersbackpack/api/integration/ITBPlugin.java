package com.tiviacz.travelersbackpack.api.integration;

public interface ITBPlugin
{
    String getModName();

    boolean canLoad();

    void preInit();

    void init();

    void postInit();
}
