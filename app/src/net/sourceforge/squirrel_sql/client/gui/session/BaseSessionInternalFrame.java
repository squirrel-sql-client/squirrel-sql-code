package net.sourceforge.squirrel_sql.client.gui.session;
/*
 * Copyright (C) 2003-2004 Jason Height
 * jmheight@users.sourceforge.net
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
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.client.gui.BaseInternalFrame;
import net.sourceforge.squirrel_sql.client.session.ISession;

import java.io.FileOutputStream;
import java.io.File;
/**
 * Base functionality for Squirrels internal frames that are attached directly
 * to a session.
 *
 * @author <A HREF="mailto:jmheight@users.sourceforge.net">Jason Height</A>
 */
public class BaseSessionInternalFrame extends BaseInternalFrame
{
	private ISession _session;
   private String _titleWithoutFile = "";
   private String _sqlFilePath = null;

   /**
	 * Creates a non-resizable, non-closable, non-maximizable,
	 * non-iconifiable JInternalFrame with no title.
	 */
	public BaseSessionInternalFrame(ISession session)
	{
		super();
		setupSheet(session);
	}

   /**
	 * Creates a non-closable, non-maximizable, non-iconifiable
	 * JInternalFrame with the specified title and with
	 * resizability specified.
	 *
	 * @param	title		Title for internal frame.
	 * @param	resizable	<TT>true</TT> if frame can be resized.
	 */
	public BaseSessionInternalFrame(ISession session, String title, boolean resizable)
	{
		super(title, resizable);
      _titleWithoutFile = title;
		setupSheet(session);
	}

   /**
	 * Creates a JInternalFrame with the specified title and with
	 * resizability, closability, maximizability and
	 * iconifability specified.
	 *
	 * @param	title		Title for internal frame.
	 * @param	resizable	<TT>true</TT> if frame can be resized.
	 * @param	closeable	<TT>true</TT> if frame can be closed.
	 * @param	maximizable	<TT>true</TT> if frame can be maximized.
	 * @param	iconifiable	<TT>true</TT> if frame can be iconified.
	 */
	public BaseSessionInternalFrame(ISession session, String title, boolean resizable,
			boolean closable, boolean maximizable, boolean iconifiable)
	{
		super(title, resizable, closable, maximizable, iconifiable);
      _titleWithoutFile = title;
		setupSheet(session);
	}

   public void setTitle(String title)
   {
      _titleWithoutFile = title;


      if(null == _sqlFilePath)
      {
         super.setTitle(_titleWithoutFile);
      }
      else
      {
         super.setTitle(_titleWithoutFile + "   SQL file: " + _sqlFilePath);
      }
   }

	private final void setupSheet(ISession session)
	{
		if (session == null)
		{
			throw new IllegalArgumentException("Null ISession passed");
		}
		_session = session;
		_session.getApplication().getWindowManager().registerSessionSheet(this);
		addInternalFrameListener(new SheetActivationListener());
	}

	public ISession getSession()
	{
		return _session;
	}

   public void setSqlFile(File sqlFile)
   {
      _sqlFilePath = sqlFile.getAbsolutePath();
      setTitle(_titleWithoutFile);
   }

   public boolean hasSQLPanelAPI()
   {
      return false;
   }

   /**
	 * Sets the session behind this sheet to the active session when the
	 * frame is activated
	 */
	private class SheetActivationListener extends InternalFrameAdapter
	{
		public void internalFrameActivated(InternalFrameEvent e)
		{
         _session.setActiveSessionWindow((BaseSessionInternalFrame)e.getInternalFrame());
			_session.getApplication().getSessionManager().setActiveSession(_session);
		}
	}
}
