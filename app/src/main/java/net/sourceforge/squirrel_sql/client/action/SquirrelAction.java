package net.sourceforge.squirrel_sql.client.action;
/*
 * Copyright (C) 2001-2006 Colin Bell
 * colbell@users.sourceforge.net
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
import javax.swing.KeyStroke;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.gui.action.BaseAction;
import net.sourceforge.squirrel_sql.fw.util.IResources;

public abstract class SquirrelAction extends BaseAction
{
	protected IApplication _app;
   private IResources _rsrc;

   protected SquirrelAction(IApplication app)
	{
		this(app, app.getResources());
	}

	protected SquirrelAction(IApplication app, IResources rsrc)
	{
		super();
      _rsrc = rsrc;
      if (app == null)
		{
			throw new IllegalArgumentException("Null IApplication passed");
		}
		if (rsrc == null)
		{
			throw new IllegalArgumentException("No Resources object in IApplication");
		}

		_app = app;
		rsrc.setupAction(this, _app.getSquirrelPreferences().getShowColoriconsInToolbar());
	}

	protected IApplication getApplication()
	{
		return _app;
	}

   public KeyStroke getKeyStroke()
   {
      return _rsrc.getKeyStroke(this);
   }
}
