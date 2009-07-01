/*
 Copyright (C) 2009  Jos� David Moreno Ju�rez

 This program is free software: you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sourceforge.squirrel_sql.plugins.oracle.sqlloader.control;

import java.awt.event.ActionEvent;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.action.SquirrelAction;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.Resources;
import net.sourceforge.squirrel_sql.plugins.oracle.sqlloader.ui.ControlFileGenerationFrame;

/**
 * Action to generate a SQL*Loader control file for the tables selected in the
 * objects tree.
 * 
 * @author Jos� David Moreno Ju�rez
 * 
 */
public class GenerateControlFileAction extends SquirrelAction {

	private static final long serialVersionUID = 1L;

	private ISession session;

	/**
	 * Creates a new {@link GenerateControlFileAction} for the specified
	 * application.
	 * 
	 * @param application
	 *            the application
	 */
	public GenerateControlFileAction(IApplication application) {
		super(application);
	}

	/**
	 * Creates a new {@link GenerateControlFileAction} with the specified
	 * application and resources file.
	 * 
	 * @param application
	 *            the application
	 * @param resources
	 *            resources file to get plugin's configuration values
	 */
	public GenerateControlFileAction(IApplication application,
			Resources resources) {
		super(application, resources);
	}

	/**
	 * Creates a new {@link GenerateControlFileAction} with the specified
	 * application, resources file and session.
	 * 
	 * @param application
	 *            the application
	 * @param properties
	 *            resources file to get plugin's configuration values
	 * @param session
	 *            session
	 */
	public GenerateControlFileAction(IApplication application,
			Resources properties, ISession session) {
		super(application, properties);
		this.session = session;
	}

	/*
	 * (sin Javadoc)
	 * 
	 * @see java.awt.event.ActionListener#actionPerformed(java.awt.event.ActionEvent)
	 */
	public void actionPerformed(ActionEvent e) {
		ControlFileGenerationFrame dialog = new ControlFileGenerationFrame("Control file generation settings", session);
		ControlFileGenerationFrame.centerWithinDesktop(dialog);
		dialog.setVisible(true);
	}
}
