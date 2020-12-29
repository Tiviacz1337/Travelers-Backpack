package com.tiviacz.travelersbackpack.util;

import com.tiviacz.travelersbackpack.TravelersBackpack;
import org.apache.logging.log4j.Level;

public class LogHelper
{
    public static void log(Level level, String format, Object... data)
    {
        TravelersBackpack.LOGGER.log(level, format, data);
    }

    public static void info(String format, Object... data)
    {
        log(Level.INFO, format, data);
    }

    public static void error(String format, Object... data)
    {
        log(Level.ERROR, format, data);
    }
}
