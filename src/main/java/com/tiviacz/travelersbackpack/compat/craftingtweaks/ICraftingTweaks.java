package com.tiviacz.travelersbackpack.compat.craftingtweaks;

import com.tiviacz.travelersbackpack.client.screens.BackpackScreen;

public interface ICraftingTweaks {
    void onCraftingSlotsHidden();

    void onCraftingSlotsDisplayed();

    void setScreen(BackpackScreen screen);

    ICraftingTweaks EMPTY = new ICraftingTweaks() {
        @Override
        public void onCraftingSlotsHidden() {
        }

        @Override
        public void onCraftingSlotsDisplayed() {
        }

        @Override
        public void setScreen(BackpackScreen screen) {
        }
    };
}