package net.sourceforge.squirrel_sql.plugins.laf;
/*
 * Copyright (C) 2002 Colin Bell
 * colbell@users.sourceforge.net
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
import java.lang.reflect.Method;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
/**
 * Behaviour for the Skin Look and Feel.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SkinLookAndFeelController extends DefaultLookAndFeelController {
	/** Logger for this class. */
	private static ILogger s_log = LoggerController.createLogger(SkinLookAndFeelController.class);

	/**
	 * This Look and Feel is about to be installed. Load the selected
	 * themepack.
	 */
	public void aboutToBeInstalled(LAFRegister lafRegister) {
		try {
			ClassLoader cl = lafRegister.getLookAndFeelClassLoader();
			Class skinLafClass = cl.loadClass(LAFConstants.SKINNABLE_LAF_CLASS_NAME);
			Class skinClass = cl.loadClass(LAFConstants.SKIN_CLASS_NAME);
			Method loadThemePack =
				skinLafClass.getMethod("loadThemePack", new Class[] { String.class });
			Method setSkin = skinLafClass.getMethod("setSkin", new Class[] { skinClass });
			Object[] parms = new Object[] {
				lafRegister.getPlugin().getSkinThemePackFolder() + "/" + lafRegister.getPlugin().getLAFPreferences().getSkinThemePackName()
			};
			Object skin = loadThemePack.invoke(skinLafClass, parms);
			setSkin.invoke(skinLafClass, new Object[] { skin });
		} catch (Exception ex) {
			s_log.error("Error loading a Skinnable Look and Feel", ex);
		}
	}

}

