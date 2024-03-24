package com.tiviacz.travelersbackpack.compat.craftingtweaks;

import com.tiviacz.travelersbackpack.client.screens.TravelersBackpackScreen;

public interface ICraftingTweaks
{
    void onCraftingSlotsHidden();

    void onCraftingSlotsDisplayed();

    void setScreen(TravelersBackpackScreen screen);

    ICraftingTweaks EMPTY = new ICraftingTweaks()
    {
        @Override
        public void onCraftingSlotsHidden() {}

        @Override
        public void onCraftingSlotsDisplayed() {}

        @Override
        public void setScreen(TravelersBackpackScreen screen) {}
    };
}
