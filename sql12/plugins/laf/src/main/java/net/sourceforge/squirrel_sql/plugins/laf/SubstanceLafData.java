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
		putClass("Autumn", "org.jvnet.substance.skin.SubstanceAutumnLookAndFeel");
		putClass("BusinessBlackSteel", "org.jvnet.substance.skin.SubstanceBusinessBlackSteelLookAndFeel");
		putClass("BusinessBlueSteelSkin", "org.jvnet.substance.skin.SubstanceBusinessBlueSteelLookAndFeel");
		putClass("BusinessSkin", "org.jvnet.substance.skin.SubstanceBusinessLookAndFeel");
		putClass("ChallengerDeepSkin", "org.jvnet.substance.skin.SubstanceChallengerDeepLookAndFeel");
		putClass("CremeCoffeeSkin", "org.jvnet.substance.skin.SubstanceCremeCoffeeLookAndFeel");
		putClass("CremeSkin", "org.jvnet.substance.skin.SubstanceCremeLookAndFeel");
		putClass("DustCoffeeSkin", "org.jvnet.substance.skin.SubstanceDustCoffeeLookAndFeel");
		putClass("DustSkin", "org.jvnet.substance.skin.SubstanceDustLookAndFeel");
		putClass("EmeraldDuskSkin", "org.jvnet.substance.skin.SubstanceEmeraldDuskLookAndFeel");
		putClass("MagmaSkin", "org.jvnet.substance.skin.SubstanceMagmaLookAndFeel");
		putClass("MistAquaSkin", "org.jvnet.substance.skin.SubstanceMistAquaLookAndFeel");
		putClass("MistSilverSkin", "org.jvnet.substance.skin.SubstanceMistSilverLookAndFeel");
		putClass("ModerateSkin", "org.jvnet.substance.skin.SubstanceModerateLookAndFeel");
		putClass("NebulaBrickWallSkin", "org.jvnet.substance.skin.SubstanceNebulaBrickWallLookAndFeel");
		putClass("NebulaSkin", "org.jvnet.substance.skin.SubstanceNebulaLookAndFeel");
		putClass("OfficeBlue2007Skin", "org.jvnet.substance.skin.SubstanceOfficeBlue2007LookAndFeel");
		putClass("OfficeSilver2007Skin", "org.jvnet.substance.skin.SubstanceOfficeSilver2007LookAndFeel");
		putClass("RavenGraphiteGlassSkin", "org.jvnet.substance.skin.SubstanceRavenGraphiteGlassLookAndFeel");
		putClass("RavenGraphiteSkin", "org.jvnet.substance.skin.SubstanceRavenGraphiteLookAndFeel");
		putClass("RavenSkin", "org.jvnet.substance.skin.SubstanceRavenLookAndFeel");
		putClass("SaharaSkin", "org.jvnet.substance.skin.SubstanceSaharaLookAndFeel");
		putClass("TwilightSkin", "org.jvnet.substance.skin.SubstanceTwilightLookAndFeel");
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
