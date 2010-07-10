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
import javax.swing.LookAndFeel;
/**
 * Behaviour of a look and feel.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface ILookAndFeelController {
	/**
	 * This Look and Feel is about to be installed.
	 */
	void aboutToBeInstalled(LAFRegister lafRegister, LookAndFeel laf);

	/**
	 * This Look and Feel has just been installed.
	 */
	void hasBeenInstalled(LAFRegister lafRegister, LookAndFeel laf);

	/**
	 * Return the component to display in the Look and Feel Preferences
	 * panel to configure this Look and Feel. Return <TT>null</TT> if no
	 * extra configuration required.
	 * 
	 * @return		The configuration component or <TT>null</TT>.
	 */
	BaseLAFPreferencesPanelComponent getPreferencesComponent();
}

