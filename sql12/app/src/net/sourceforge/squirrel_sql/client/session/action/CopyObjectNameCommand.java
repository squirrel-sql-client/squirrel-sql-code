package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2003 Colin Bell
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
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;
import net.sourceforge.squirrel_sql.fw.util.ICommand;

import net.sourceforge.squirrel_sql.client.plugin.IPlugin;
import net.sourceforge.squirrel_sql.client.session.IObjectTreeAPI;
import net.sourceforge.squirrel_sql.client.session.ISession;
/**
 * This <CODE>Action</CODE> will copy the object names of all objects
 * currently in the object tree and place on the system clipboard.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class CopyObjectNameCommand implements ICommand
{
	/** Type of copy to do. */
	public interface ICopyTypes
	{
		int SIMPLE_NAME = 0;
		int QUALIFIED_NAME = 1;
	}

	/** Session to copy from. */
	private ISession _session;

	/** Type of copy to do. @see ICopyTypes */
	private int _copyType;

	/**
	 * Ctor specifying copy type.
	 *
	 * @param	copyType	Type of copy to do. @see ICopyTypes.
	 *
	 * @throws	IllegalArgumentException
	 * 			Thrown if a <TT>null</TT> <TT>ISession</TT> or an invalid
				<TT>copyType</TT> passed.
	 */
	public CopyObjectNameCommand(ISession session, int copyType)
	{
		super();
		if (session == null)
		{
			throw new IllegalArgumentException("ISession == null");
		}
		if (copyType < ICopyTypes.SIMPLE_NAME || copyType > ICopyTypes.QUALIFIED_NAME)
		{
			throw new IllegalArgumentException("Invalid copyType of : " + copyType + " passed");
		}

		_session = session;
		_copyType = copyType;
	}

	/**
	 * Copy to clipboard.
	 */
	public void execute()
	{
		final StringBuffer buf = new StringBuffer(100);
		final IPlugin dummyPlugin = _session.getApplication().getDummyAppPlugin();
		final IObjectTreeAPI api = _session.getObjectTreeAPI(dummyPlugin);
		final IDatabaseObjectInfo[] dbObjs = api.getSelectedDatabaseObjects();

		// Get all the selected object names and place in a comma separated list.
		for (int i = 0; i < dbObjs.length; i++)
		{
			final IDatabaseObjectInfo doi = dbObjs[i];
			final String name = _copyType == ICopyTypes.SIMPLE_NAME
									? doi.getSimpleName()
									: doi.getQualifiedName();
			buf.append(name).append(", ");
		}
		if (buf.length() > 0)
		{
			buf.setLength(buf.length() - 2);	// Remove trailing ", ".
			Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
			StringSelection data = new StringSelection(buf.toString());
			clip.setContents(data, data);
		}
	}
}
