package com.tiviacz.travelersbackpack.compat.craftingtweaks;

import com.tiviacz.travelersbackpack.client.screen.TravelersBackpackHandledScreen;

public interface ICraftingTweaks
{
    void onCraftingSlotsHidden();

    void onCraftingSlotsDisplayed();

    void setScreen(TravelersBackpackHandledScreen screen);

    ICraftingTweaks EMPTY = new ICraftingTweaks()
    {
        @Override
        public void onCraftingSlotsHidden() {}

        @Override
        public void onCraftingSlotsDisplayed() {}

        @Override
        public void setScreen(TravelersBackpackHandledScreen screen) {}
    };
}
