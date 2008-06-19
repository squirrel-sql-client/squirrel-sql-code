/*
 * Copyright (C) 2008 Michael Romankiewicz
 * mirommail(at)web.de
 *
 * This program is free software; you can redistribute it and/or
 * modify it under the terms of the GNU General Public License
 * as published by the Free Software Foundation; either version 2
 * of the License, or any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package net.sourceforge.squirrel_sql.plugins.firebirdmanager;

import java.io.File;
import java.net.URL;

import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileFilter;

import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class FirebirdManagerHelper {
	/**
	 * Mode of editing<br>
	 * No editing, only data displaying 
	 */
	public final static int DISPLAY_MODE = 0;
	/**
	 * Mode of editing<br>
	 * No editing, only data displaying 
	 */
	public final static int NEW_MODE = 1;
	public final static int EDIT_MODE = 2;
	
	// Logger for this class
    private final static ILogger log = LoggerController.createLogger(FirebirdManagerHelper.class);
    // line separator for easy using ;-)
    public final static String CR = System.getProperty("line.separator", "\n");

    
    private FirebirdManagerHelper() {}
	
    /**
     * Load an icon image with iconName from the sub package images to package of class FirebirdManagerPlugin
     * @param imageIconName filename of the icon image
     * @return ImageIcon or null
     */
	public static ImageIcon loadIcon(String imageIconName) {
        URL imgURL = FirebirdManagerPlugin.class.getResource("images/" + imageIconName);
        if (imgURL != null) {
            return new ImageIcon(imgURL);
        } else {
            log.error("Couldn't find file: images/" + imageIconName);
            return null;
        }
	}

	
	/**
	 * Open a JFileChooser to select a filename or directory starting with startName 
	 * @param startName JFileChooser starts at this point
	 * @return selected filename or dir or an emtpy string
	 */
	public static String getFileOrDir(String startName, boolean fileSelect) {
		String selection = "";

		JFileChooser fc = createFileChooser(fileSelect, startName, false);
	    fc.setMultiSelectionEnabled(false);

	    int returnVal = -1;
    	returnVal = fc.showOpenDialog(null);

    	if (returnVal == JFileChooser.APPROVE_OPTION) {
	      selection = fc.getSelectedFile().getAbsolutePath();
	    }
    	
    	return selection;
	}
	
	/**
	 * Create a file chooser
	 * @param fileSelect true for using as file selection box, false for using as directory selection box 
	 * @param startDirectory initial directory 
	 * @param saveDialog true for using as save dialog, false for using as open dialog 
	 * @return created file chooser
	 */
	private static JFileChooser createFileChooser(boolean fileSelect, String startDirectory,
			boolean saveDialog) {
		if (startDirectory == null) {
			startDirectory = "";
		}
	    JFileChooser fc = new JFileChooser(startDirectory);
	    fc.setSelectedFile(new File(startDirectory));
	    fc.setCurrentDirectory(fc.getSelectedFile());
	    fc.enableInputMethods(false);
	    fc.setFileHidingEnabled(true);
	    if (fileSelect)
	    	fc.setFileSelectionMode(JFileChooser.FILES_ONLY);
	    else
	    	fc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
	    
	    if (saveDialog)
	    	fc.setDialogType(JFileChooser.SAVE_DIALOG);
	    else
	    	fc.setDialogType(JFileChooser.OPEN_DIALOG);
	    	
	    return fc;
	}

	/**
	 * Returns if filename is an existing file
	 * @param filename filename to check
	 * @return true/false
	 */
	public static boolean fileExists(String filename) {
		File file = new File(filename);
		return file.exists();
	}
	
	/**
	 * Get the parsed string as int or defaultValue if an exception occured
	 * @param string string to convert
	 * @param defaultValue default value which will be returned on an exception
	 * @return converted string or defaultValue
	 */
	public static int convertStringToIntDef(String string, int defaultValue) {
		try {
			return Integer.parseInt(string);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	/**
	 * Get the host from the url
	 * @param url url
	 * @return found url or localhost
	 */
    public static String getHost(String url) {
		// jdbc:firebirdsql:[//host[:port]/
    	String s = getUrlWithoutClass(url);
    	if ("//".equals(s.substring(0, 2))) {
    		String host = s.substring(2);
    		int posPort = host.indexOf(':');
    		int posPath = host.indexOf('/');
    		if (posPath == 0) {
    			posPath = host.indexOf('\\');
    		}
    		
    		if (posPort > 0
    				&& posPort < posPath) {
    			return host.substring(0, posPort);
    		} else {
    			return host.substring(0, posPath);
    		}
    	} else {
    		return "localhost";
    	}
    }
    
	/**
	 * Get the port from the url
	 * @param url url
	 * @return found port or 3050
	 */
    public static int getPort(String url) {
    	String s = getUrlWithoutClass(url);
    	if ("//".equals(s.substring(0, 2))) {
    		String host = s.substring(2);
    		int posPort = host.indexOf(':');
    		int posPath = host.indexOf('/');
    		if (posPath == 0) {
    			posPath = host.indexOf('\\');
    		}
    		
    		if (posPort > 0
    				&& posPort < posPath) {
    			return Integer.parseInt(host.substring(posPort, posPath));
    		}
    	}
		return 3050;
    }
    
    private static String getUrlWithoutClass(String url) {
    	String type = "jdbc:firebirdsql:";
    	return url.substring(type.length());
    }
	
    /**
     * Opens a file chooser and returns the selected file for open or close operations 
     * @param owner owner for JFileChooser
     * @param loading true = load a file; false = save a file
     * @param extension extension of the properties file
     * @param description description for extension
     * @return selected file
     */
	public static File getPropertiesFile(boolean saving, String startName,
			String extension, String description) {
		JFileChooser fc = createFileChooser(true, startName, saving);
		final String finalExtension = extension;
		final String finalDescription = description;

		// FileFilter
		if (extension != null && extension.length() > 0) {
			FileFilter ff = new FileFilter() {
				public boolean accept(File f) {
					return f.isDirectory()
							|| f.getName().toLowerCase().endsWith(
									finalExtension);
				}

				public String getDescription() {
					return finalDescription;
				}
			};
			fc.setFileFilter(ff);
		}
		
		int returnVal = -1;
		if (saving) {
			returnVal = fc.showSaveDialog(null);
		} else {
			returnVal = fc.showOpenDialog(null);
		}
		
		if (returnVal == JFileChooser.APPROVE_OPTION) {
			if (fc.getSelectedFile().getAbsolutePath().endsWith("." + finalExtension)) {
				return fc.getSelectedFile();
			} else {
				return new File(fc.getSelectedFile().getAbsolutePath() + "." + finalExtension);
			}
		} else {
			return null;
		}
		
	}

}
