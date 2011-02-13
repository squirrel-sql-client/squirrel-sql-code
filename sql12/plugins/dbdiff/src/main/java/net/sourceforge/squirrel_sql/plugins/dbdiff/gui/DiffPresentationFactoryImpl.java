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

package net.sourceforge.squirrel_sql.plugins.dbdiff.gui;

import net.sourceforge.squirrel_sql.plugins.dbdiff.SessionInfoProvider;
import net.sourceforge.squirrel_sql.plugins.dbdiff.prefs.DBDiffPreferenceBean;

/**
 * 
 */
public class DiffPresentationFactoryImpl implements IDiffPresentationFactory
{

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.dbdiff.gui.IDiffPresentationFactory#createDiffPresentation(net.sourceforge.squirrel_sql.plugins.dbdiff.SessionInfoProvider,
	 *      DBDiffPreferenceBean)
	 */
	@Override
	public IDiffPresentation createDiffPresentation(SessionInfoProvider sessionInfoProvider,
		DBDiffPreferenceBean preferenceBean)
	{
		IDiffPresentation result = null;

		if (preferenceBean.isUseExternalGraphicalDiffTool())
		{
			result = new ExternalToolSideBySideDiffPresentation();
		}
		else
		{
			if (preferenceBean.isUseTabularDiffPresenation())
			{
				result = new TabularDiffPresentation();
			}
			else
			{
				result = new JMeldDiffPresentation();
			}
		}

		result.setSessionInfoProvider(sessionInfoProvider);
		return result;
	}

}
