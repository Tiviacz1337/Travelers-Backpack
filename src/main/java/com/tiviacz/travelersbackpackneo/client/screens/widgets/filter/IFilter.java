package com.tiviacz.travelersbackpackneo.client.screens.widgets.filter;

import java.util.List;

public interface IFilter {
    public List<Integer> getFilter();

    public void updateSettings();

    public int getFilterSlotCount();
}
