package com.tiviacz.travellersbackpack.util;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Logger;

import com.tiviacz.travellersbackpack.TravellersBackpack;

public class LogHelper 
{
	private static Logger logger = TravellersBackpack.logger;

	public static void info(Object object)
    {
        logger.log(Level.INFO, object);
    }
}