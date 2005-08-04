package net.sourceforge.squirrel_sql.client.gui;
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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.GridLayout;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.Version;
import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.gui.MultipleLineLabel;
import net.sourceforge.squirrel_sql.fw.util.ClassLoaderListener;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SplashScreen extends JWindow
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(SplashScreen.class);

	private JProgressBar _progressBar;

    private JLabel pluginLabel;
    
    SquirrelPreferences _prefs;
    
	public SplashScreen(SquirrelResources rsrc, 
                        int progressBarSize,
                        SquirrelPreferences prefs)
		throws IllegalArgumentException
	{
		super();
        _prefs = prefs;
		if (rsrc == null)
		{
			throw new IllegalArgumentException("Null Resources passed");
		}
		createUserInterface(rsrc, progressBarSize);
	}

	private void createUserInterface(SquirrelResources rsrc, int progressBarSize)
	{
		final JPanel mainPnl = new JPanel(new BorderLayout());
		final Color bgColor = new Color(SquirrelResources.S_SPLASH_IMAGE_BACKGROUND);
		mainPnl.setBackground(bgColor);

		Icon icon = rsrc.getIcon(SquirrelResources.IImageNames.SPLASH_SCREEN);
		mainPnl.add(BorderLayout.NORTH, new JLabel(icon));
		
		MultipleLineLabel versionLbl = new MultipleLineLabel();
		versionLbl.setOpaque(false);
		versionLbl.append(Version.getVersion());
		versionLbl.append("\n");
		versionLbl.append(Version.getCopyrightStatement());

        _progressBar = new JProgressBar(0, progressBarSize);
        _progressBar.setStringPainted(true);
        _progressBar.setString("");
        _progressBar.setBackground(bgColor);
        _progressBar.setForeground(Color.blue);
		

        if (_prefs.getShowPluginFilesInSplashScreen()) {
            _progressBar.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            versionLbl.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 10));
            JPanel centerPanel = new JPanel(new GridLayout(2,1));
            centerPanel.setBackground(bgColor);
            centerPanel.add(versionLbl);
            pluginLabel = new JLabel();
            pluginLabel.setForeground(new Color(71,73,139));
            pluginLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
            centerPanel.add(pluginLabel);
            mainPnl.add(BorderLayout.CENTER, centerPanel);
        } else {
            _progressBar.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            versionLbl.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
            mainPnl.add(BorderLayout.CENTER, versionLbl);
        }

		mainPnl.add(BorderLayout.SOUTH, _progressBar);

		mainPnl.setBorder(BorderFactory.createRaisedBevelBorder());
		getContentPane().add(mainPnl);

		pack();

		GUIUtils.centerWithinScreen(this);
		setVisible(true);
	}

    public void indicateLoadingFile(final String filename) {
        try
        {
            SwingUtilities.invokeAndWait(new Runnable()
            {
                public void run()
                {
                    if (filename != null) {
                        pluginLabel.setText("Loading file - "+filename);
                    } else {
                        pluginLabel.setText("");
                    }
                    pluginLabel.validate();
                }
            });
        }
        catch (Exception ex)
        {
            s_log.error("Error occured updating progress bar", ex);
        }        
    }

	public void indicateNewTask(final String text)
	{
		// Using a thread for this gets rid of most of the "white flash".
		try
		{
			SwingUtilities.invokeAndWait(new Runnable()
			{
				public void run()
				{
					_progressBar.setString(text);
					_progressBar.setValue(_progressBar.getValue() + 1);
				}
			});
			Thread.yield();
		}
		catch (Exception ex)
		{
			s_log.error("Error occured updating progress bar", ex);
		}
	}
    
    public ClassLoaderListener getClassLoaderListener() {
        return new ClassLoaderListener() {
            public void loadedZipFile(String filename) {
                indicateLoadingFile(filename);
            }
            public void finishedLoadingZipFiles() {
                indicateLoadingFile(null);
            }
        };
    }
}
