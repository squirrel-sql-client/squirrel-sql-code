package org.rege.isqlj.squirrel;

/**
* <p>Title: sqsc-isqlj</p>
* <p>Description: SquirrelSQL plugin for iSqlJ</p>
* <p>Copyright: Copyright (c) 2003 Stathis Alexopoulos</p>
* @author Stathis Alexopoulos stathis@rege.org
* <br>
* <br>
* <p>
*    This file is part of sqsc-isqlj.
* </p>
* <br>
* <p>
*    sqsc-isqlj is free software; you can redistribute it and/or modify
*    it under the terms of the GNU Lesser General Public License as published by
*    the Free Software Foundation; either version 2 of the License, or
*    (at your option) any later version.
*
*    Foobar is distributed in the hope that it will be useful,
*    but WITHOUT ANY WARRANTY; without even the implied warranty of
*    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
*    GNU Lesser General Public License for more details.
*
*    You should have received a copy of the GNU Lesser General Public License
*    along with Foobar; if not, write to the Free Software
*    Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
* </p>
*/

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.fw.util.BaseException;
import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.action.ISessionAction;


public class ExecuteISqlJAction
		extends SquirrelAction 
		implements ISessionAction
{
    private ISession        session;
    private ISqlJPlugin     plugin;

    public ExecuteISqlJAction( IApplication app, Resources rsrc, ISqlJPlugin plugin)
            throws IllegalArgumentException 
	{
        super(app, rsrc);
        if (plugin ==  null) 
		{
            throw new IllegalArgumentException("null ISqlJPlugin passed");
        }
        this.plugin = plugin;
    }
    {
    }
	
    public void actionPerformed(ActionEvent evt) 
	{
        if (session != null) 
		{
			try
			{
            	new ExecuteISqlJCommand( getParentFrame(evt), session, plugin).execute();
			}
			catch (BaseException ex)
			{
				session.showErrorMessage(ex);
			}
        }
    }
    public void setSession(ISession session) 
	{
        this.session = session;
    }
	
}

