package net.sourceforge.squirrel_sql.fw.gui.action;
/*
 * Copyright (C) 2001-2002 Colin Bell
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

public class SelectInternalFrameAction
	extends BaseAction
	implements PropertyChangeListener
{
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n
	{
		String SHORT_DESCRIPTION = "Active window";
	}

	private static final String FRAME_PTR = "FRAME_PTR";

	private static final int MAX_TITLE_LENGTH = 50;

	public SelectInternalFrameAction(JInternalFrame child)
	{
		this(child, null);
	}

	public SelectInternalFrameAction(JInternalFrame child, String title)
	{
		super(getTitle(child, title));
		putValue(FRAME_PTR, child);
		putValue(SHORT_DESCRIPTION, i18n.SHORT_DESCRIPTION);
		if (title != null && title.length() > 0)
		{
			child.addPropertyChangeListener(JInternalFrame.TITLE_PROPERTY, this);
		}
	}

	public void actionPerformed(ActionEvent evt)
	{
		JInternalFrame fr = getInternalFrame();
		if (fr != null)
		{
			new SelectInternalFrameCommand(fr).execute();
		}
	}

	public void propertyChange(PropertyChangeEvent evt)
	{
		putValue(BaseAction.NAME, getInternalFrame().getTitle());
	}

	private JInternalFrame getInternalFrame() throws IllegalStateException
	{
		JInternalFrame fr = (JInternalFrame) getValue(FRAME_PTR);
		if (fr == null)
		{
			throw new IllegalStateException("No JInternalFrame associated with SelectInternalFrameAction");
		}
		return fr;
	}

	private static String getTitle(JInternalFrame child, String title)
	{
		if (child == null)
		{
			throw new IllegalArgumentException("null JInternalFrame passed");
		}
		String myTitle = title;
		if (title != null && title.length() > 0)
		{
			myTitle = title;
		}
		else
		{
			myTitle = child.getTitle();
		}
		if (myTitle.length() > MAX_TITLE_LENGTH)
		{
			myTitle = myTitle.substring(0, MAX_TITLE_LENGTH) + "...";
		}
		
		return myTitle;
	}
}