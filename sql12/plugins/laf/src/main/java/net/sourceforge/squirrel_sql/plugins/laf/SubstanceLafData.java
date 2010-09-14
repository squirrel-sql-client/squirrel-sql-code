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

import org.jvnet.substance.api.SubstanceSkin;
import org.jvnet.substance.skin.AutumnSkin;
import org.jvnet.substance.skin.BusinessBlackSteelSkin;
import org.jvnet.substance.skin.BusinessBlueSteelSkin;
import org.jvnet.substance.skin.BusinessSkin;
import org.jvnet.substance.skin.ChallengerDeepSkin;
import org.jvnet.substance.skin.CremeCoffeeSkin;
import org.jvnet.substance.skin.CremeSkin;
import org.jvnet.substance.skin.DustCoffeeSkin;
import org.jvnet.substance.skin.DustSkin;
import org.jvnet.substance.skin.EmeraldDuskSkin;
import org.jvnet.substance.skin.MagmaSkin;
import org.jvnet.substance.skin.MistAquaSkin;
import org.jvnet.substance.skin.MistSilverSkin;
import org.jvnet.substance.skin.ModerateSkin;
import org.jvnet.substance.skin.NebulaBrickWallSkin;
import org.jvnet.substance.skin.NebulaSkin;
import org.jvnet.substance.skin.OfficeBlue2007Skin;
import org.jvnet.substance.skin.OfficeSilver2007Skin;
import org.jvnet.substance.skin.RavenGraphiteGlassSkin;
import org.jvnet.substance.skin.RavenGraphiteSkin;
import org.jvnet.substance.skin.RavenSkin;
import org.jvnet.substance.skin.SaharaSkin;
import org.jvnet.substance.skin.TwilightSkin;

public class SubstanceLafData
{

	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(SubstanceLafData.class);

	private HashMap<String, SubstanceSkin> lafMap = new HashMap<String, SubstanceSkin>();

	public SubstanceLafData()
	{
		initLafMap();
	}

	private void initLafMap()
	{
		lafMap.put(AutumnSkin.NAME, new AutumnSkin());
		lafMap.put(BusinessBlackSteelSkin.NAME, new BusinessBlackSteelSkin());
		lafMap.put(BusinessBlueSteelSkin.NAME, new BusinessBlueSteelSkin());
		lafMap.put(BusinessSkin.NAME, new BusinessSkin());
		lafMap.put(ChallengerDeepSkin.NAME, new ChallengerDeepSkin());
		lafMap.put(CremeCoffeeSkin.NAME, new CremeCoffeeSkin());
		lafMap.put(CremeSkin.NAME, new CremeSkin());
		lafMap.put(DustCoffeeSkin.NAME, new DustCoffeeSkin());
		lafMap.put(DustSkin.NAME, new DustSkin());
		lafMap.put(EmeraldDuskSkin.NAME, new EmeraldDuskSkin());
		lafMap.put(MagmaSkin.NAME, new MagmaSkin());
		lafMap.put(MistAquaSkin.NAME, new MistAquaSkin());
		lafMap.put(MistSilverSkin.NAME, new MistSilverSkin());
		lafMap.put(ModerateSkin.NAME, new ModerateSkin());
		lafMap.put(NebulaBrickWallSkin.NAME, new NebulaBrickWallSkin());
		lafMap.put(NebulaSkin.NAME, new NebulaSkin());
		lafMap.put(OfficeBlue2007Skin.NAME, new OfficeBlue2007Skin());
		lafMap.put(OfficeSilver2007Skin.NAME, new OfficeSilver2007Skin());
		lafMap.put(RavenGraphiteGlassSkin.NAME, new RavenGraphiteGlassSkin());
		lafMap.put(RavenGraphiteSkin.NAME, new RavenGraphiteSkin());
		lafMap.put(RavenSkin.NAME, new RavenSkin());
		lafMap.put(SaharaSkin.NAME, new SaharaSkin());
		lafMap.put(TwilightSkin.NAME, new TwilightSkin());
	}

	public SubstanceSkin getSkinForName(String skinName)
	{
		SubstanceSkin skin = (SubstanceSkin) lafMap.get(skinName);
		if (skin == null) {
			if (s_log.isInfoEnabled()) {
				s_log.info("Unable to locate Skin class for skinName ("+skinName+").  Using Autumn instead.");
			}
			skin = new AutumnSkin();
		}
		return skin;
	}

	public Set<String> getSubstanceSkins()
	{
		Set<String> result = new TreeSet<String>(lafMap.keySet());
		return result;
	}

}
