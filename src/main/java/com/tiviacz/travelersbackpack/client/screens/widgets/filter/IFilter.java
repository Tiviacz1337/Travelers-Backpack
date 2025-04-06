package com.tiviacz.travelersbackpack.client.screens.widgets.filter;

import java.util.List;

public interface IFilter {
    List<Integer> getFilter();

    void updateSettings();

    int getFilterSlotCount();
}