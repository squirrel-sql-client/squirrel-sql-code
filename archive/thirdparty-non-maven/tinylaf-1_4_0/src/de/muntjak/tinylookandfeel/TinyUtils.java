/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;
import java.util.Map;

import javax.swing.JComponent;

/**
 * TinyUtils provides some static utility methods.
 * 
 * @author Hans Bickel
 * @since 1.4.0
 *
 */
public class TinyUtils {
	
	private static final String OS_NAME = getSystemPropertyPrivileged("os.name");
	
	/* Reusable RenderingHints. */
	private static final RenderingHints SAVED_HINTS = new RenderingHints(null);

	private static Map osSettings;
	
	private static boolean is1dot4 = false;
	private static boolean is1dot5 = false;
	private static boolean is1dot6 = false;
	private static String javaVersion;

	/* The value for this key is evaluated at drawString(JComponent, Graphics2D, String, int, int). */
	/** This key is a hidden "feature".
	 * Setting this client property (for any tabbed pane, menu or menu item) to
	 * <code>Boolean.TRUE</code> overrides all other text antialiasing settings.
	 */
	public static final Object AA_TEXT_PROPERTY_KEY = new StringBuffer("TinyAATextPropertyKey");
	
	static {
		javaVersion = getSystemPropertyPrivileged("java.version");

		if(javaVersion != null) {
			is1dot4 =
				javaVersion.startsWith("1.0") ||
				javaVersion.startsWith("1.1") ||
				javaVersion.startsWith("1.2") ||
				javaVersion.startsWith("1.3") ||
				javaVersion.startsWith("1.4");
			is1dot5 = javaVersion.startsWith("1.5");
			is1dot6 = javaVersion.startsWith("1.6");
		}
		
		// New in 1.4.0: Antialiased text for JTabbedPane tabs and menu items
		// (see drawString(...)).
		// Note: osSettings will be non-null with 1.6 or higher JREs and provided
		// that the platform supports desktop properties.
		// Also the PropertyChangeListener will receive events only
		// with 1.6 or higher JREs.
		osSettings = (Map)(Toolkit.getDefaultToolkit().getDesktopProperty("awt.font.desktophints"));
		
		Toolkit.getDefaultToolkit().addPropertyChangeListener("awt.font.desktophints",
			new PropertyChangeListener() {
				public void propertyChange(PropertyChangeEvent evt) {
//					System.out.println("old: " + evt.getOldValue() +
//						"\nnew: " + evt.getNewValue());
					osSettings = (Map)evt.getNewValue();
				}
			}
		);
	}

	/**
	 * Retrieves a system property by executing a privileged block.
	 * @param key a non-null system property key
	 * @return the value of a system property, null if the key is unknown
	 */
	public static String getSystemPropertyPrivileged(final String key) {
		try {
			return System.getProperty(key);
		}
		catch(SecurityException ex) {
//			System.out.println(TinyUtils.class.getName() + ": " +
//				"Exception while trying to get " + key +
//				" system property. " + ex);
			
			return (String)AccessController.doPrivileged(
				new PrivilegedAction() {
		            public Object run() {
		                return System.getProperty(key);
		            }
				}
			);
		}
	}
	
	/**
	 * Returns true if we are running on Linux,
	 * false otherwise.
	 * @return true if we are running on Linux,
	 * false otherwise.
	 */
	public static boolean isOSLinux() {
		return OS_NAME.toLowerCase().startsWith("linux");
	}
	
	/**
	 * Returns true if we are running on Mac OS,
	 * false otherwise.
	 * @return true if we are running on Mac OS,
	 * false otherwise.
	 */
	public static boolean isOSMac() {
		return OS_NAME.toLowerCase().startsWith("mac");
	}
	
	/**
	 * Returns <code>true</code> if we are running JRE 1.4 or lower,
	 * <code>false</code> otherwise.
	 * @return <code>true</code> if we are running JRE 1.4 or lower,
	 * <code>false</code> otherwise.
	 */
	public static boolean is1dot4() {
		return is1dot4;
	}
	
	/**
	 * Returns <code>true</code> if we are running JRE 1.5.x,
	 * <code>false</code> otherwise.
	 * @return <code>true</code> if we are running JRE 1.5.x,
	 * <code>false</code> otherwise.
	 */
	public static boolean is1dot5() {
		return is1dot5;
	}
	
	/**
	 * Returns <code>true</code> if we are running JRE 1.6.x,
	 * <code>false</code> otherwise.
	 * @return <code>true</code> if we are running JRE 1.6.x,
	 * <code>false</code> otherwise.
	 */
	public static boolean is1dot6() {
		return is1dot6;
	}
	
	/**
	 * Returns the JRE version.
	 * @return the JRE version
	 */
	public static String getJavaVersion() {
		return javaVersion;
	}
	
	/* Called for JMenu, all kinds of menu items and JTabbedPane.
	 * (Because we need to set the text color for those
	 * components we cannot use the default text drawing
	 * mechanism.)
	 */
	/**
     * Draw a string with the graphics <code>g</code> at location
     * (<code>x</code>, <code>y</code>)
     * just like <code>g.drawString</code> would.
     * The character at index <code>underlinedIndex</code>
     * in text will be underlined. If <code>index</code> is beyond the
     * bounds of <code>text</code> (including < 0), nothing will be
     * underlined.
     *
     * @param client the component for which we are drawing
     * @param g Graphics to draw with
     * @param text String to draw
     * @param underlinedIndex Index of character in text to underline
     * @param x x coordinate to draw at
     * @param y y coordinate to draw at
     */
    public static void drawStringUnderlineCharAt(JComponent client,
    	Graphics g, String text, int underlinedIndex, int x, int y)
    {
    	if(text == null || text.length() <= 0) return;

    	if(g instanceof Graphics2D) {
	    	drawString(client, (Graphics2D)g, text, x, y);
    	}
    	else {
    		g.drawString(text, x, y);
    	}

        if(underlinedIndex >= 0 && underlinedIndex < text.length() ) {
            FontMetrics fm = g.getFontMetrics();
            int underlineRectX = x + fm.stringWidth(text.substring(0, underlinedIndex));
            int underlineRectY = y;
            int underlineRectWidth = fm.charWidth(text.charAt(underlinedIndex));
            int underlineRectHeight = 1;
            
            g.fillRect(underlineRectX, underlineRectY + fm.getDescent() - 1,
                       underlineRectWidth, underlineRectHeight);
        }
    }
 
    /*
     * What i do here is partially mimic the behaviour of 1.4, 1.5 and 1.6 JREs,
     * see the respective implementation of SwingUtilities2 class.
     */
    private static void drawString(JComponent client, Graphics2D g2d, String text, int x, int y) {
    	if(client != null && Boolean.TRUE.equals(client.getClientProperty(AA_TEXT_PROPERTY_KEY))) {
			// paint antialiased
			Object oldTA = g2d
				.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

			g2d.drawString(text, x, y);
			g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
				oldTA);

			return;
    	}
    	
    	if(osSettings != null) {
    		Map oldHints = getRenderingHints(g2d, osSettings, SAVED_HINTS);
		    g2d.addRenderingHints(osSettings);

		    g2d.drawString(text, x, y);
		    g2d.addRenderingHints(oldHints);
		    
		    return;
		}
 
    	if(is1dot5() && "true".equals(System.getProperty("swing.aatext"))) {
    		// paint antialiased
    		Object oldTA = g2d.getRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING);
    		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
    			RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    		
    		g2d.drawString(text, x, y);
    		g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
    			oldTA);
    		
    		return;
    	}

		// no AA
		g2d.drawString(text, x, y);
    }

    /**
     * Get rendering hints from a Graphics instance.
     * "hintsToSave" is a Map of RenderingHint key-values.
     * For each hint key present in that map, the value of that
     * hint is obtained from the Graphics and stored as the value
     * for the key in savedHints.
     */
    private static RenderingHints getRenderingHints(Graphics2D g2d,
       Map hintsToSave, RenderingHints savedHints)
    {
        if(savedHints == null) {
            savedHints = new RenderingHints(null);
        }
        else {
            savedHints.clear();
        }
        
        if(hintsToSave == null || hintsToSave.size() == 0) {
            return savedHints;
        }
        
        Iterator ii = hintsToSave.keySet().iterator();
        while(ii.hasNext()) {
            RenderingHints.Key key = (RenderingHints.Key)ii.next();
            Object value = g2d.getRenderingHint(key);
            
            savedHints.put(key, value);
        }
        
        return savedHints;
   }

	/**
	 * Returns the value of the system property corresponding
	 * to <code>key</code> argument.
	 * @param key a non-null system property key
	 * @return the value of a system property, null if either
	 * the key is unknown or a SecurityExcpetion was raised
	 */
	public static String getSystemProperty(String key) {
		try {
			return System.getProperty(key);
		}
		catch(SecurityException ex) {
			System.out.println("Exception while trying to get " + key +
				" system property. " + ex);
			return null;
		}
	}
}
