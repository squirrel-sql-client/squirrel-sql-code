package net.sourceforge.squirrel_sql.client.gui;
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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Version;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;

public class SplashScreen extends JWindow {

	private JProgressBar _progressBar;

	public SplashScreen(SquirrelResources rsrc, int progressBarSize) throws IllegalArgumentException {
		super();
		if (rsrc == null) {
			throw new IllegalArgumentException("Null Resources passed");
		}
		createUserInterface(rsrc, progressBarSize);
	}

	private void createUserInterface(SquirrelResources rsrc, int progressBarSize) {
		JPanel mainPnl = new JPanel(new BorderLayout());
		mainPnl.setBackground(new Color(SquirrelResources.S_SPLASH_IMAGE_BACKGROUND));

		Icon icon = rsrc.getIcon(SquirrelResources.IImageNames.SPLASH_SCREEN);
		mainPnl.add(BorderLayout.NORTH, new JLabel(icon));

		MultipleLineLabel versionLbl = new MultipleLineLabel();
		versionLbl.setOpaque(false);
		versionLbl.append(Version.getVersion());
		versionLbl.append("\n");
		versionLbl.append(Version.getCopyrightStatement());
		versionLbl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		mainPnl.add(BorderLayout.CENTER, versionLbl);

		_progressBar = new JProgressBar(0, progressBarSize);
		_progressBar.setStringPainted(true);
		_progressBar.setString("");
		_progressBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		_progressBar.setBackground(new Color(SquirrelResources.S_SPLASH_IMAGE_BACKGROUND));
		_progressBar.setForeground(Color.blue);
		mainPnl.add(BorderLayout.SOUTH, _progressBar);

		mainPnl.setBorder(BorderFactory.createRaisedBevelBorder());
		getContentPane().add(mainPnl);

		pack();

		GUIUtils.centerWithinScreen(this);
		setVisible(true);
	}

	public void indicateNewTask(final String text) {
		// Using a thread for this gets rid of most of the "white flash".
		try {
			SwingUtilities.invokeAndWait(new Runnable() {
				public void run() {
					_progressBar.setString(text);
					_progressBar.setValue(_progressBar.getValue() + 1);
				}
			});
			Thread.yield();
		} catch(Exception ignore) {
		}
	}

	private static class MyLabel extends JLabel {
		MyLabel(String text) {
			super(text, SwingConstants.CENTER);
		}
	}
}
