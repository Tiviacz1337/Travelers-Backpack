package com.tiviacz.travelersbackpack.compat.pneumonogravestones;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import net.pneumono.gravestones.Gravestones;
import net.pneumono.gravestones.api.GravestonesApi;

public class PneumonoGravestonesCompat {
    public static void register() {
        GravestonesApi.registerDataType(Gravestones.id(TravelersBackpack.MODID), new BackpackDataType());
    }
}
