package net.sourceforge.squirrel_sql.fw.gui.action;
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
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.JInternalFrame;

import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class SelectInternalFrameAction extends BaseAction
{
	/** Internationalized strings for this class. */
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(SelectInternalFrameAction.class);

	private static final String FRAME_PTR = "FRAME_PTR";

	private static final int MAX_TITLE_LENGTH = 50;

	private MyPropertyChangeListener _myLis = null;

	public SelectInternalFrameAction(JInternalFrame child)
	{
		super(getTitle(child));
		putValue(FRAME_PTR, child);
		putValue(SHORT_DESCRIPTION,
				s_stringMgr.getString("SelectInternalFrameAction.description"));
		// TODO: This listener should be removed.
		_myLis = new MyPropertyChangeListener();
		child.addPropertyChangeListener(JInternalFrame.TITLE_PROPERTY, _myLis);
	}

	public void actionPerformed(ActionEvent evt)
	{
		final JInternalFrame fr = getInternalFrame();
		if (fr != null)
		{
			new SelectInternalFrameCommand(fr).execute();
		}
	}

	private JInternalFrame getInternalFrame() throws IllegalStateException
	{
		final JInternalFrame fr = (JInternalFrame) getValue(FRAME_PTR);
		if (fr == null)
		{
			throw new IllegalStateException("No JInternalFrame associated with SelectInternalFrameAction");
		}
		return fr;
	}

	private static String getTitle(JInternalFrame child)
	{
		if (child == null)
		{
			throw new IllegalArgumentException("null JInternalFrame passed");
		}

		String myTitle = child.getTitle();
		if (myTitle.length() > MAX_TITLE_LENGTH)
		{
			myTitle = myTitle.substring(0, MAX_TITLE_LENGTH) + "...";
		}

		return myTitle;
	}

	// This class keeps the Action title in synch with the internal frame
	// title.
	private class MyPropertyChangeListener implements PropertyChangeListener
	{
		public void propertyChange(PropertyChangeEvent evt)
		{
			putValue(BaseAction.NAME, getTitle(getInternalFrame()));
		}
	}
}

