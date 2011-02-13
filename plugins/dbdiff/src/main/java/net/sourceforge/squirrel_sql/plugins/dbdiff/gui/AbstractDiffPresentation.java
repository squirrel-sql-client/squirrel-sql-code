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

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.plugins.dbdiff.SessionInfoProvider;

/**
 * Base class for all DiffPresentation implementations that supports the common requirement to have access to
 * the SessionInfoProvider.
 */
public abstract class AbstractDiffPresentation implements IDiffPresentation
{

	/** the class that provides out session information */
	protected SessionInfoProvider sessionInfoProvider = null;

	/** the source session. This comes from prov */
	protected ISession sourceSession = null;

	/** the destination session. This comes from prov */
	protected ISession destSession = null;

	public void setSessionInfoProvider(SessionInfoProvider provider)
	{
		Utilities.checkNull("setSessionInfoProvider", "provider", provider);
		sessionInfoProvider = provider;
		sourceSession = sessionInfoProvider.getSourceSession();
		destSession = sessionInfoProvider.getDestSession();
	}

}
