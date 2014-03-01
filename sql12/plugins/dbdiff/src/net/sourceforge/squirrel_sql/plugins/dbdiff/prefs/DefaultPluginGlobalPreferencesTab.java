/*
 * Copyright (C) 2011 Rob Manning
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

package net.sourceforge.squirrel_sql.plugins.dbdiff.prefs;

import java.awt.Component;

import javax.swing.JScrollPane;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel;
import net.sourceforge.squirrel_sql.client.util.IOptionPanel;

/**
 * 
 */
public class DefaultPluginGlobalPreferencesTab implements IGlobalPreferencesPanel
{
	private IOptionPanel optionPanel = null;

	private final JScrollPane _myscrolledPanel;

	public DefaultPluginGlobalPreferencesTab(IOptionPanel optionPanel)
	{
		this.optionPanel = optionPanel;
		_myscrolledPanel = new JScrollPane(optionPanel.getPanelComponent());
		_myscrolledPanel.getVerticalScrollBar().setUnitIncrement(10);
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel#
	 *      initialize(net.sourceforge.squirrel_sql.client.IApplication)
	 */
	@Override
	public void initialize(IApplication app)
	{
		/* Do Nothing */
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.preferences.IGlobalPreferencesPanel#
	 *      uninitialize(net.sourceforge.squirrel_sql.client.IApplication)
	 */
	@Override
	public void uninitialize(IApplication app)
	{
		/* Do Nothing */
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#applyChanges()
	 */
	@Override
	public void applyChanges()
	{
		if (optionPanel != null)
		{
			optionPanel.applyChanges();
		}
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#getTitle()
	 */
	@Override
	public String getTitle()
	{
		return optionPanel.getTitle();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#getHint()
	 */
	@Override
	public String getHint()
	{
		return optionPanel.getHint();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.client.util.IOptionPanel#getPanelComponent()
	 */
	@Override
	public Component getPanelComponent()
	{
		return _myscrolledPanel;
	}

}
