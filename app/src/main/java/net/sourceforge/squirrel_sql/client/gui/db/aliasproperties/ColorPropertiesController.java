package net.sourceforge.squirrel_sql.client.gui.db.aliasproperties;

/*
 * Copyright (C) 2009 Rob Manning
 * manningr@users.sourceforge.net
 * 
 * Based on initial work from Colin Bell
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

import java.awt.Component;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.db.ISQLAliasExt;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * This dialog allows the user to review and maintain background color overrides for an alias.
 */
public class ColorPropertiesController implements IAliasPropertiesPanelController
{
	/**
	 * Internationalized strings for this class.
	 */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(ColorPropertiesController.class);

	private ColorPropertiesPanel _propsPnl;

	private ISQLAliasExt _alias;

	public static interface i18n {
		// i18n[ColorPropertiesController.title=Color]
		String TITLE = s_stringMgr.getString("ColorPropertiesController.title");
		
		// i18n[ColorPropertiesController.hint=Set session colors for this Alias]
		String HINT = s_stringMgr.getString("ColorPropertiesController.hint");
	}
	
	public ColorPropertiesController(ISQLAliasExt alias, IApplication app)
	{
		_alias = alias;
		_propsPnl = new ColorPropertiesPanel(alias.getColorProperties());
	}

	public Component getPanelComponent()
	{
		return _propsPnl;
	}

	public void applyChanges()
	{
		_alias.setColorProperties(_propsPnl.getSQLAliasColorProperties());
	}

	public String getTitle()
	{
		return i18n.TITLE;
	}

	public String getHint()
	{
		return i18n.HINT;
	}

}
