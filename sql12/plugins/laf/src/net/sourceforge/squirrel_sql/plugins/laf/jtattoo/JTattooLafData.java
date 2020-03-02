package net.sourceforge.squirrel_sql.plugins.laf.jtattoo;

/*
 * Copyright (C) 2013 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

/**
 * Metadata and helper methods for the JTattoo look-and-feel.
 * 
 * @author manningr
 */
public class JTattooLafData
{

	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(JTattooLafData.class);

	private HashMap<String, Class<?>> lafMap = new HashMap<String, Class<?>>();

	private ClassLoader cl = null;
	
	public static final String MCWIN_LAF_CLASS_NAME = "com.jtattoo.plaf.mcwin.McWinLookAndFeel";
	
	public JTattooLafData(ClassLoader cl)
	{
		this.cl = cl;
		initLafMap();
	}

	private void initLafMap()
	{
		putClass("Acryl", "com.jtattoo.plaf.acryl.AcrylLookAndFeel");
		putClass("Aluminium", "com.jtattoo.plaf.aluminium.AluminiumLookAndFeel");
		putClass("Bernstein", "com.jtattoo.plaf.bernstein.BernsteinLookAndFeel");
		putClass("Fast", "com.jtattoo.plaf.fast.FastLookAndFeel");
		putClass("Graphite", "com.jtattoo.plaf.graphite.GraphiteLookAndFeel");
		putClass("HiFi", "com.jtattoo.plaf.hifi.HiFiLookAndFeel");
		putClass("Luna", "com.jtattoo.plaf.luna.LunaLookAndFeel");
		putClass("McWin", "com.jtattoo.plaf.mcwin.McWinLookAndFeel");
		putClass("Mint", "com.jtattoo.plaf.mint.MintLookAndFeel");
		putClass("Noire", "com.jtattoo.plaf.noire.NoireLookAndFeel");
		putClass("Smart", "com.jtattoo.plaf.smart.SmartLookAndFeel");
		putClass("Texture", "com.jtattoo.plaf.texture.TextureLookAndFeel");
	}

	public Class<?> getSkinClassForName(String skinName) {
		return lafMap.get(skinName);
	}
	
	public Set<String> getSkins()
	{
		Set<String> result = new TreeSet<String>(lafMap.keySet());
		return result;
	}

	private void putClass(String skinName, String className) {
		
		try
		{
			Class<?> skinClass = Class.forName(className, true, cl);
			lafMap.put(skinName, skinClass);
		}
		catch (ClassNotFoundException e)
		{
			// Created too many logs with Java 8 or higher
			// s_log.error("Unable to load LAF class ("+className+"): " + e.getMessage(), e);
			s_log.error("Unable to load LAF class ("+className+"): " + e);
		}
		
	}
}
