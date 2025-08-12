package com.tiviacz.travelersbackpack.inventory.upgrades.filter;

import java.util.List;

public interface IFilter {
    List<Integer> getFilter();

    void updateSettings();

    int getFilterSlotCount();
}