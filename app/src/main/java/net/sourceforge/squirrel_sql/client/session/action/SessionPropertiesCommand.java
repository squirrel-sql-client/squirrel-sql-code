package net.sourceforge.squirrel_sql.client.session.action;
/*
 * Copyright (C) 2001-2004 Colin Bell
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
import net.sourceforge.squirrel_sql.client.gui.WindowManager;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.ICommand;
/**
 * This <CODE>ICommand</CODE> displays a session properties dialog box
 * and allows the user to modify the properties.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SessionPropertiesCommand implements ICommand
{
	/** The session whose properties are to be displayed/maintained. */
	private final ISession _session;
   private int _tabIndexToSelect = -1;

   /**
    * Ctor.
    *
    * @param	session The session whose properties are to be displayed/maintained.
    *
    * @throws	IllegalArgumentException
    *			Thrown if a <TT>null</TT> <TT>ISession</TT> passed.
    */
   public SessionPropertiesCommand(ISession session)
   {
      this(session, -1);
   }

   public SessionPropertiesCommand(ISession session, int tabIndexToSelect)
   {
      super();
      _tabIndexToSelect = tabIndexToSelect;
      if (session == null)
      {
         throw new IllegalArgumentException("Null ISession passed");
      }
      _session = session;
   }

   /**
    * Display the properties dialog.
    */
   public void execute()
   {
      if (_session != null)
      {
         WindowManager winMgr = _session.getApplication().getWindowManager();
         winMgr.showSessionPropertiesDialog(_session, _tabIndexToSelect);
      }
   }
}
