package net.sourceforge.squirrel_sql.client.gui;
/*
 * Copyright (C) 2001-2006 Colin Bell
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
import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JWindow;
import javax.swing.SwingUtilities;

import net.sourceforge.squirrel_sql.client.preferences.SquirrelPreferences;
import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.util.ClassLoaderListener;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class SplashScreen extends JWindow
{
	/** Logger for this class. */
	private final static ILogger s_log =
		LoggerController.createLogger(SplashScreen.class);

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
        StringManagerFactory.getStringManager(SplashScreen.class);

	private JProgressBar _progressBar;

    private JLabel _pluginLabel;
    
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
      final JPanel mainPnl = new JPanel(new GridBagLayout());
      final Color bgColor = new Color(SquirrelResources.S_SPLASH_IMAGE_BACKGROUND);
      mainPnl.setBackground(bgColor);
      mainPnl.setBorder(BorderFactory.createRaisedBevelBorder());

      GridBagConstraints gbc;

      Icon icon = rsrc.getIcon(SquirrelResources.IImageNames.SPLASH_SCREEN);

      gbc = new GridBagConstraints(0,0,1,1,1,1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(0,5,5,5),0,0);
      mainPnl.add(new JLabel(icon), gbc);

      _progressBar = new JProgressBar(0, progressBarSize);
      _progressBar.setStringPainted(true);
      _progressBar.setString("");
      _progressBar.setBackground(bgColor);
      _progressBar.setForeground(Color.blue);

      if (_prefs.getShowPluginFilesInSplashScreen())
      {
         _pluginLabel = new JLabel("Dummy");
         _pluginLabel.setForeground(new Color(SquirrelResources.S_SPLASH_IMAGE_BACKGROUND));

         gbc = new GridBagConstraints(0,1,1,1,0,0, 
                                      GridBagConstraints.NORTHWEST, 
                                      GridBagConstraints.HORIZONTAL, 
                                      new Insets(0,5,0,5),0,0);
         mainPnl.add(new VersionPane(false), gbc);

         gbc = new GridBagConstraints(0,2,1,1,0,0, 
                                      GridBagConstraints.NORTHWEST, 
                                      GridBagConstraints.HORIZONTAL, 
                                      new Insets(0,5,3,5),0,0);
         mainPnl.add(_pluginLabel, gbc);

         gbc = new GridBagConstraints(0,3,1,1,0,0, 
                                      GridBagConstraints.NORTHWEST, 
                                      GridBagConstraints.HORIZONTAL, 
                                      new Insets(0,5,3,5),0,0);
         mainPnl.add(_progressBar, gbc);

      }
      else
      {
         gbc = new GridBagConstraints(0,1,1,1,0,0, 
                                      GridBagConstraints.NORTHWEST, 
                                      GridBagConstraints.HORIZONTAL, 
                                      new Insets(0,5,7,5),0,0);
         mainPnl.add(new VersionPane(false), gbc);

         gbc = new GridBagConstraints(0,2,1,1,0,0, 
                                      GridBagConstraints.NORTHWEST, 
                                      GridBagConstraints.HORIZONTAL, 
                                      new Insets(0,5,5,5),0,0);
         mainPnl.add(_progressBar, gbc);
      }

      getContentPane().add(mainPnl);

      pack();
      setSize(400,500);

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
                   if (filename != null)
                   {
                      _pluginLabel.setForeground(new Color(71, 73, 139));
                      // i18n[SplashScreen.info.loadingfile=Loading file - ]
                      _pluginLabel.setText(s_stringMgr.getString("SplashScreen.info.loadingfile") + filename);
                   }
                   else
                   {
                      _pluginLabel.setForeground(new Color(SquirrelResources.S_SPLASH_IMAGE_BACKGROUND));
                      _pluginLabel.setText("Dummy");
                   }
                   _pluginLabel.validate();
                }
            });
        }
        catch (Exception ex)
        {
            //i18n[SplashScreen.error.updatingprogressbar=Error occured updating progress bar]
            s_log.error(s_stringMgr.getString("SplashScreen.error.updatingprogressbar"), ex);
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
            // i18n[SplashScreen.error.updatingprogressbar=Error occured updating progress bar]
			s_log.error(s_stringMgr.getString("SplashScreen.error.updatingprogressbar"), ex);
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
