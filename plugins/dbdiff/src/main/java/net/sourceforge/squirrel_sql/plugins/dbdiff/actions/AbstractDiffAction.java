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

package net.sourceforge.squirrel_sql.plugins.dbdiff.actions;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.plugins.dbdiff.prefs.IPluginPreferencesManager;

public abstract class AbstractDiffAction extends SquirrelAction
{
	/**
	 * serialVersionUID
	 */
	private static final long serialVersionUID = 1L;

	protected IPluginPreferencesManager pluginPreferencesManager;

	public AbstractDiffAction(IApplication app, Resources rsrc)
	{
		super(app, rsrc);
	}

	/**
	 * @param pluginPreferencesManager
	 *           the pluginPreferencesManager to set
	 */
	public void setPluginPreferencesManager(IPluginPreferencesManager pluginPreferencesManager)
	{
		Utilities.checkNull("setPluginPreferencesManager", "pluginPreferencesManager", pluginPreferencesManager);
		this.pluginPreferencesManager = pluginPreferencesManager;
	}

}
