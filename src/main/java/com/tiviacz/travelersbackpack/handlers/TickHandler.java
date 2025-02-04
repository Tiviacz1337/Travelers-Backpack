package com.tiviacz.travelersbackpack.handlers;

import com.tiviacz.travelersbackpack.util.Supporters;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class TickHandler {
    private static int nextSupportersFetch = 0;

    public static void register() {
        ServerTickEvents.END_SERVER_TICK.register(server -> {
            if(nextSupportersFetch > server.getTickCount()) {
                return;
            }
            nextSupportersFetch = server.getTickCount() + (20 * 60 * 60); //Fetch every hour
            Supporters.updateSupporters();
        });
    }
}
