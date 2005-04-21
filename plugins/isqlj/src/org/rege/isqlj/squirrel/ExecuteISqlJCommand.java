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

import java.awt.Frame;
import java.io.*;

import javax.swing.JFileChooser;

import net.sourceforge.squirrel_sql.fw.gui.*;
import net.sourceforge.squirrel_sql.fw.util.*;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ISQLPanelAPI;

import net.sourceforge.squirrel_sql.fw.sql.SQLConnection;

import org.rege.isqlj.JavaSql;
import org.rege.isqlj.ISqlJConnection;

public class ExecuteISqlJCommand
		implements ICommand
{
    private final ISession session;
    private ISqlJPlugin plugin;
    private final Frame frame;

    public ExecuteISqlJCommand( Frame frame, ISession session, ISqlJPlugin plugin)
        throws IllegalArgumentException 
	{
        super();
        if (session == null) 
		{
            throw new IllegalArgumentException("Null ISession passed");
        }
        if (plugin == null) 
		{
            throw new IllegalArgumentException("Null IPlugin passed");
        }
        this.frame = frame;
        this.session = session;
        this.plugin = plugin;
    }

    public void execute() throws BaseException 
	{
        if( session != null) 
		{
			String str = session.getSessionInternalFrame().getSQLPanelAPI().getSQLScriptToBeExecuted();
			try
			{
				JavaSql sqlj = new JavaSql();
				sqlj.getInterpreter().set( "session", new SqscConnection( session, plugin));
				sqlj.getInterpreter().set( "jdbc", 
						new ISqlJConnection( session.getSQLConnection().getConnection()));
				sqlj.exec( new StringReader( str));
			} catch( Exception ex)
			{
				throw new BaseException(ex);
			}
		}
    }

}


