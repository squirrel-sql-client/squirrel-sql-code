package net.sourceforge.squirrel_sql.plugins.laf;
/*
 * Copyright (C) 2002-2006 Colin Bell
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
import java.awt.LayoutManager;
import javax.swing.JPanel;
/**
 * Base class for any LAF Controller component to be placed in the
 * Look And Feel Preferences panel.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
abstract class BaseLAFPreferencesPanelComponent extends JPanel
{
	BaseLAFPreferencesPanelComponent()
	{
		super();
	}
	BaseLAFPreferencesPanelComponent(LayoutManager lmgr)
	{
		super(lmgr);
	}
	/**
	 * Called when this Look and Feel is specified in the preferences panel.
	 */
	public void loadPreferencesPanel()
	{
	}
	/**
	 * Called when this Look and Feel is specified in the preferences panel
	 * and save is requested. Returing <TT>true</TT> will cause the LAF
	 * to be forced to be changed. E.G. if you've changed the theme for a LAF
	 * and you need to force a change of the LAF (even tough its the same one)
	 * in order to see the new theme. Returns <TT>false</TT> by default.
	 * 
	 * @return	<TT>true</TT> to force LAF to be changed.
	 */
	public boolean applyChanges()
	{
		return false;
	}
}
