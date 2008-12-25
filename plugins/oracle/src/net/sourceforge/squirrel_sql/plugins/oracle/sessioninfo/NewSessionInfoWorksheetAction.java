package net.sourceforge.squirrel_sql.plugins.oracle.sessioninfo;
/*
 * Copyright (C) 2003 Jason Height
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
import java.awt.event.ActionEvent;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.util.Resources;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;

import net.sourceforge.squirrel_sql.client.session.ISession;


/**
 * This <CODE>Action</CODE> displays a new Oracle Session Information Worksheet.
 *
 * @author  <A HREF="mailto:jmheight@users.sourceforge.net">Jason Height</A>
 */
public class NewSessionInfoWorksheetAction extends SquirrelAction {
    private Resources _resources;

	/**
	 * Ctor.
	 *
	 * @param   app	 Application API.
	 *
	 * @throws  IllegalArgumentException
	 *			Thrown if a <TT>null</TT> <TT>IApplication</TT> passed.
	 */
	public NewSessionInfoWorksheetAction(IApplication app, Resources rsrc) {
		super(app, rsrc);
                _resources = rsrc;

		if (app == null) {
			throw new IllegalArgumentException("Null IApplication passed");
		}
	}

	/**
	 * Display the about box.
	 *
	 * @param   evt	 The event being processed.
	 */
	public void actionPerformed(ActionEvent evt) {
          ISession activeSession = getApplication().getSessionManager().getActiveSession();
          if (activeSession == null)
            throw new IllegalArgumentException("This method should not be called with a null activeSession");


          final SessionInfoInternalFrame sif = new SessionInfoInternalFrame(activeSession, _resources);
          getApplication().getMainFrame().addWidget(sif);

          // If we don't invokeLater here no Short-Cut-Key is sent
          // to the internal frame
          // seen under java version "1.4.1_01" and Linux
          SwingUtilities.invokeLater(new Runnable()
          {
                  public void run()
                  {
                          sif.setVisible(true);
                  }
          });
	}
}
