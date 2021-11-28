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

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;

public class RadianceLafData {

	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(RadianceLafData.class);

	private HashMap<String, Class<?>> lafMap = new HashMap<String, Class<?>>();

	private ClassLoader cl = null;

	public RadianceLafData(ClassLoader cl) {
		this.cl = cl;
		initLafMap();
	}

	private void initLafMap() {
		putClass("Business", "org.pushingpixels.radiance.theming.api.skin.RadianceBusinessLookAndFeel");
		putClass("BusinessBlackSteel", "org.pushingpixels.radiance.theming.api.skin.RadianceBusinessBlackSteelLookAndFeel");
		putClass("BusinessBlueSteelSkin", "org.pushingpixels.radiance.theming.api.skin.RadianceBusinessBlueSteelLookAndFeel");
		putClass("Creme", "org.pushingpixels.radiance.theming.api.skin.RadianceCremeLookAndFeel");
		putClass("CremeCoffee", "org.pushingpixels.radiance.theming.api.skin.RadianceCremeCoffeeLookAndFeel");
		putClass("Sahara", "org.pushingpixels.radiance.theming.api.skin.RadianceSaharaLookAndFeel");
		putClass("Moderate", "org.pushingpixels.radiance.theming.api.skin.RadianceModerateLookAndFeel");
		putClass("Nebula", "org.pushingpixels.radiance.theming.api.skin.RadianceNebulaLookAndFeel");
		putClass("NebulaAmethyst", "org.pushingpixels.radiance.theming.api.skin.RadianceNebulaAmethystLookAndFeel");
		putClass("NebulaBrickWall", "org.pushingpixels.radiance.theming.api.skin.RadianceNebulaBrickWallLookAndFeel");
		putClass("Autumn", "org.pushingpixels.radiance.theming.api.skin.RadianceAutumnLookAndFeel");
		putClass("MistSilver", "org.pushingpixels.radiance.theming.api.skin.RadianceMistSilverLookAndFeel");
		putClass("MistAqua", "org.pushingpixels.radiance.theming.api.skin.RadianceMistAquaLookAndFeel");
		putClass("Dust", "org.pushingpixels.radiance.theming.api.skin.RadianceDustLookAndFeel");
		putClass("DustCoffee", "org.pushingpixels.radiance.theming.api.skin.RadianceDustCoffeeLookAndFeel");
		putClass("Gemini", "org.pushingpixels.radiance.theming.api.skin.RadianceGeminiLookAndFeel");
		putClass("Mariner", "org.pushingpixels.radiance.theming.api.skin.RadianceMarinerLookAndFeel");
		putClass("Sentinel", "org.pushingpixels.radiance.theming.api.skin.RadianceSentinelLookAndFeel");
		putClass("Cerulean", "org.pushingpixels.radiance.theming.api.skin.RadianceCeruleanLookAndFeel");
		putClass("GreenMagic", "org.pushingpixels.radiance.theming.api.skin.RadianceGreenMagicLookAndFeel");
		putClass("OfficeSilver2007", "org.pushingpixels.radiance.theming.extras.api.skinpack.RadianceOfficeSilver2007LookAndFeel");
		putClass("OfficeBlue2007", "org.pushingpixels.radiance.theming.extras.api.skinpack.RadianceOfficeBlue2007LookAndFeel");
		putClass("OfficeBlack2007", "org.pushingpixels.radiance.theming.extras.api.skinpack.RadianceOfficeBlack2007LookAndFeel");
	}

	public Class<?> getSkinClassForName(String skinName) {
		return lafMap.get(skinName);
	}

	public Set<String> getRadianceSkins() {
		Set<String> result = new TreeSet<String>(lafMap.keySet());
		return result;
	}

	private void putClass(String skinName, String className) {

		try {
			Class<?> skinClass = Class.forName(className, true, cl);
			lafMap.put(skinName, skinClass);
		} catch (ClassNotFoundException e) {
			s_log.error("Unable to load LAF class (" + className + "):" + e.getMessage(), e);
		}

	}
}
