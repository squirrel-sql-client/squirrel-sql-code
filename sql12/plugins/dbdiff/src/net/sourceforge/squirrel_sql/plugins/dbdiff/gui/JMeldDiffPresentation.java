/*
 * Copyright (C) 2011 Rob Manning
 * manningr@users.sourceforge.net
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

package net.sourceforge.squirrel_sql.plugins.dbdiff.gui;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

import javax.swing.JFrame;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

import org.jmeld.settings.EditorSettings;
import org.jmeld.settings.JMeldSettings;
import org.jmeld.ui.JMeldPanel;
import org.jmeld.util.prefs.WindowPreference;

/**
 * A DiffPresentation that uses components from the JMeld project to render a comparison of the content of two
 * files in a JFrame.
 */
public class JMeldDiffPresentation extends AbstractSideBySideDiffPresentation
{
	/** The frame to place the JMeldPanel in */
	private JFrame frame;

	/**
	 * @see net.sourceforge.squirrel_sql.plugins.dbdiff.gui.AbstractSideBySideDiffPresentation#
	 *      executeDiff(java.lang.String, java.lang.String)
	 */
	@Override
	protected void executeDiff(String script1Filename, String script2Filename) throws Exception
	{
		final EditorSettings editorSettings = JMeldSettings.getInstance().getEditor();
		editorSettings.setRightsideReadonly(true);
		editorSettings.setLeftsideReadonly(true);

		frame = new JFrame();

		final JMeldPanel panel = new NonExitingJMeldPanel();
		panel.SHOW_TABBEDPANE_OPTION.disable();
		panel.SHOW_TOOLBAR_OPTION.disable();

		frame.add(panel);
		frame.addWindowListener(new WindowCloseListener());

		new WindowPreference(frame.getTitle(), frame);
		frame.addWindowListener(panel.getWindowListener());
		frame.setVisible(true);
		frame.toFront();

		GUIUtils.centerWithinParent(frame);
		panel.openComparison(script1Filename, script2Filename);
	}

	/**
	 * A WindowListener that responds to windowClosed events by hiding the JMeld frame and setting it to null
	 * so it can be garbage-collected.
	 */
	private class WindowCloseListener extends WindowAdapter
	{

		/**
		 * @see java.awt.event.WindowAdapter#windowClosed(java.awt.event.WindowEvent)
		 */
		@Override
		public void windowClosed(WindowEvent e)
		{
			if (frame != null)
			{
				frame.setVisible(false);
				frame = null;
			}
		}

	}

	/**
	 * The JMeldPanel registers a window listener that exits the JVM when it detects that the frame it resides
	 * in was closed. This subclass overrides this behavior with a no-op window listener.
	 */
	private class NonExitingJMeldPanel extends JMeldPanel
	{

		/**
		 * serialVersionUID
		 */
		private static final long serialVersionUID = 1L;

		/**
		 * @see org.jmeld.ui.JMeldPanel#getWindowListener()
		 */
		@Override
		public WindowListener getWindowListener()
		{
			return new WindowAdapter()
			{
			};
		}

	}
}
