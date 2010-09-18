package net.sourceforge.squirrel_sql.plugins.laf;

/*
 * Copyright (C) 2010 Rob Manning
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

public class SubstanceLafData
{

	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(SubstanceLafData.class);

	private HashMap<String, Class<?>> lafMap = new HashMap<String, Class<?>>();

	private ClassLoader cl = null;
	
	public SubstanceLafData(ClassLoader cl)
	{
		this.cl = cl;
		initLafMap();
	}

	private void initLafMap()
	{
		putClass("Autumn", "org.jvnet.substance.skin.AutumnSkin");
		putClass("BusinessBlackSteel", "org.jvnet.substance.skin.BusinessBlackSteelSkin");
		putClass("BusinessBlueSteelSkin", "org.jvnet.substance.skin.BusinessBlueSteelSkin");
		putClass("BusinessSkin", "org.jvnet.substance.skin.BusinessSkin");
		putClass("ChallengerDeepSkin", "org.jvnet.substance.skin.ChallengerDeepSkin");
		putClass("CremeCoffeeSkin", "org.jvnet.substance.skin.CremeCoffeeSkin");
		putClass("CremeSkin", "org.jvnet.substance.skin.CremeSkin");
		putClass("DustCoffeeSkin", "org.jvnet.substance.skin.DustCoffeeSkin");
		putClass("DustSkin", "org.jvnet.substance.skin.DustSkin");
		putClass("EmeraldDuskSkin", "org.jvnet.substance.skin.EmeraldDuskSkin");
		putClass("MagmaSkin", "org.jvnet.substance.skin.MagmaSkin");
		putClass("MistAquaSkin", "org.jvnet.substance.skin.MistAquaSkin");
		putClass("MistSilverSkin", "org.jvnet.substance.skin.MistSilverSkin");
		putClass("ModerateSkin", "org.jvnet.substance.skin.ModerateSkin");
		putClass("NebulaBrickWallSkin", "org.jvnet.substance.skin.NebulaBrickWallSkin");
		putClass("NebulaSkin", "org.jvnet.substance.skin.NebulaSkin");
		putClass("OfficeBlue2007Skin", "org.jvnet.substance.skin.OfficeBlue2007Skin");
		putClass("OfficeSilver2007Skin", "org.jvnet.substance.skin.OfficeSilver2007Skin");
		putClass("RavenGraphiteGlassSkin", "org.jvnet.substance.skin.RavenGraphiteGlassSkin");
		putClass("RavenGraphiteSkin", "org.jvnet.substance.skin.RavenGraphiteSkin");
		putClass("RavenSkin", "org.jvnet.substance.skin.RavenSkin");
		putClass("SaharaSkin", "org.jvnet.substance.skin.SaharaSkin");
		putClass("TwilightSkin", "org.jvnet.substance.skin.TwilightSkin");
	}

	public Class<?> getSkinClassForName(String skinName) {
		return lafMap.get(skinName);
	}
	
	public Set<String> getSubstanceSkins()
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
			s_log.error("Unable to load LAF class ("+className+"):"+e.getMessage(), e);
		}
		
	}
}
