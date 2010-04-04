/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Iterator;
import java.util.Properties;
import java.util.Vector;

import javax.swing.plaf.InsetsUIResource;

import de.muntjak.tinylookandfeel.util.BooleanReference;
import de.muntjak.tinylookandfeel.util.ColorRoutines;
import de.muntjak.tinylookandfeel.util.SBReference;
import de.muntjak.tinylookandfeel.util.ColoredFont;
import de.muntjak.tinylookandfeel.util.HSBReference;
import de.muntjak.tinylookandfeel.util.IntReference;


/**
 * Theme is a data container for all the properties of a TinyLaF theme.
 * <p>
 * <a name="themeswitching">
 * Notes on <b>Theme Switching</b>:<br>
 * This class has several static methods to load themes.
 * After loading a theme, {@code TinyLookAndFeel} needs to be
 * re-installed and the uis need to be recreated like this:
 * <pre>
 *   // re-install the Tiny Look and Feel
 *   UIManager.setLookAndFeel(new TinyLookAndFeel());
 *
 *   // Update the ComponentUIs for all Components. This
 *   // needs to be invoked for all windows.
 *   SwingUtilities.updateComponentTreeUI(rootComponent);
 * </pre>
 * To enable your users to switch themes, follow these steps:
 * <ol>
 * <li>Call {@link #getAvailableThemes()}
 * to get an array of {@link de.muntjak.tinylookandfeel.ThemeDescription} objects. By
 * iterating through the array and calling
 * {@link de.muntjak.tinylookandfeel.ThemeDescription#getName()}, you can
 * build a menu presenting the available themes.
 * <li>If a user selects an item from the themes menu, your code can
 * call {@link #loadTheme(ThemeDescription)} passing in the selected
 * {@code ThemeDescription} objectt.
 * <li>Follow the recommendations above (re-install {@code TinyLookAndFeel} and
 * recreate the uis).
 * </ol>
 * 
 * @version 1.4.0
 * @author Hans Bickel
 */
public class Theme {
	
	/* Must be false for production. */
	private static final boolean DEBUG = false;

	/** A file name filter which accepts only file names ending with &quot;.theme&quot;. */
	public static final FilenameFilter THEMES_FILTER = new FilenameFilter() {
		public boolean accept(File dir, String name) {
			return name.toLowerCase().endsWith(".theme");
		}
	};

	/* The different error codes (set when loading themes) */
	public static final int ERROR_NONE 							= 1;
	public static final int ERROR_NULL_ARGUMENT 				= 2;
	public static final int ERROR_FILE_NOT_FOUND 				= 3;
	public static final int ERROR_IO_EXCEPTION 					= 4;
	public static final int ERROR_NO_TINYLAF_THEME 				= 5;
	public static final int ERROR_WIN99_STYLE 					= 6;
	public static final int ERROR_INVALID_THEME_DESCRIPTION 	= 7;
	
	/* The default scroll bar size. */
	private static final int DEFAULT_SCROLL_SIZE = 17;
	
	/**
	 * If problems occur while loading themes, this variable
	 * will hold one of the following error codes:
	 * <ul>
	 * <li><code>ERROR_FILE_NOT_FOUND</code> - the specified file
	 * doesn't exist.
	 * <li><code>ERROR_IO_EXCEPTION</code> - a <code>java.io.IOException</code>
	 * occured while trying to load a theme.
	 * <li><code>ERROR_INVALID_THEME_DESCRIPTION</code> - <code>loadTheme()</code>
	 * was called passing in an invalid <code>ThemeDescription</code> argument.
	 * <li><code>ERROR_NO_TINYLAF_THEME</code> - the specified resource
	 * is no TinyLaF theme.
	 * <li><code>ERROR_NULL_ARGUMENT</code> - the argument to <code>loadTheme</code>
	 * was <code>null</code>.
	 * <li><code>ERROR_WIN99_STYLE</code> - since version 1.4.0, the 99 style
	 * is supported no more.
	 * </ul>
	 */
	public static int errorCode = ERROR_NONE;
	
	protected static final Properties MAC_FONT_MAPPINGS = new Properties();
	protected static final Properties LINUX_FONT_MAPPINGS = new Properties();
	
	protected static final int YQ_STYLE = 2;
	
	public static final String DEFAULT_THEME = "Default.theme";
	public static final String FILE_EXTENSION = ".theme";

	static final URL YQ_URL = getYQ_URL();
	static final URI YQ_URI = getYQ_URI();
	
	protected static final int FILE_ID_1 	= 0x1234;
	protected static final int FILE_ID_2 	= 0x2234;
	public static final int FILE_ID_3A 		= 0x3234;	// must be public - used in SBReference
	protected static final int FILE_ID_3B 	= 0x3235;
	protected static final int FILE_ID_3C 	= 0x3236;
	protected static final int FILE_ID_3D 	= 0x3237;
	protected static final int FILE_ID_3E 	= 0x3238;
	protected static final int FILE_ID_3F 	= 0x3239;
	protected static final int FILE_ID_4 	= 0x4000;
	protected static final int FILE_ID_4B 	= 0x4001;
	protected static final int FILE_ID_4C 	= 0x4002;
	protected static final int FILE_ID_4D 	= 0x4003;
	protected static final int FILE_ID_4E 	= 0x4004;

	public static int fileID;
	
//	Colors
	public static SBReference mainColor;
	public static SBReference disColor;
	public static SBReference backColor;
	public static SBReference frameColor;
	public static SBReference sub1Color;
	public static SBReference sub2Color;
	public static SBReference sub3Color;
	public static SBReference sub4Color;
	public static SBReference sub5Color;
	public static SBReference sub6Color;
	public static SBReference sub7Color;
	public static SBReference sub8Color;
	
//	Fonts
	public static ColoredFont plainFont;
	public static ColoredFont boldFont;
	
	public static ColoredFont buttonFont;
	public static SBReference buttonFontColor;
	public static ColoredFont labelFont;
	public static SBReference labelFontColor;
	public static ColoredFont comboFont;
	public static ColoredFont listFont;
	public static ColoredFont menuFont;
	public static SBReference menuFontColor;
	public static ColoredFont menuItemFont;
	public static SBReference menuItemFontColor;
	public static ColoredFont passwordFont;
	public static ColoredFont radioFont;
	public static SBReference radioFontColor;
	public static ColoredFont checkFont;
	public static SBReference checkFontColor;
	public static ColoredFont tableFont;
	public static SBReference tableFontColor;
	public static ColoredFont tableHeaderFont;
	public static SBReference tableHeaderFontColor;
	public static ColoredFont textAreaFont;
	public static ColoredFont textFieldFont;
	public static ColoredFont textPaneFont;
	public static ColoredFont titledBorderFont;
	public static SBReference titledBorderFontColor;
	public static ColoredFont toolTipFont;
	public static ColoredFont treeFont;
	public static ColoredFont tabFont;
	public static SBReference tabFontColor;
	public static ColoredFont editorFont;
	public static ColoredFont frameTitleFont;
	public static ColoredFont internalFrameTitleFont;
	public static ColoredFont internalPaletteTitleFont;
	public static ColoredFont progressBarFont;
	
//	Progressbar
	public static SBReference progressColor;
	public static SBReference progressTrackColor;
	public static SBReference progressBorderColor;
	public static SBReference progressDarkColor;
	public static SBReference progressLightColor;
	public static SBReference progressSelectForeColor;
	public static SBReference progressSelectBackColor;
	
//	Text
	public static SBReference textBgColor;
	public static SBReference textSelectedBgColor;
	public static SBReference textDisabledBgColor;
	// New in 1.4.0
	public static SBReference textNonEditableBgColor;
	public static SBReference textTextColor;
	public static SBReference textSelectedTextColor;
	public static SBReference textBorderColor;
	public static SBReference textBorderDisabledColor;
	public static SBReference textCaretColor;
	public static SBReference textPaneBgColor;
	public static SBReference editorPaneBgColor;
	public static SBReference desktopPaneBgColor;
	
	public static InsetsUIResource textInsets;
	
//	Combo
	public static SBReference comboBorderColor;
	public static SBReference comboBorderDisabledColor;
	public static SBReference comboSelectedBgColor;
	public static SBReference comboSelectedTextColor;
	public static SBReference comboFocusBgColor;
	public static SBReference comboArrowColor;
	public static SBReference comboArrowDisabledColor;
	public static SBReference comboButtColor;
	public static SBReference comboButtRolloverColor;
	public static SBReference comboButtPressedColor;
	public static SBReference comboButtDisabledColor;
	public static SBReference comboButtBorderColor;
	public static SBReference comboButtBorderDisabledColor;
	public static SBReference comboBgColor;
	public static SBReference comboTextColor;
	
	public static IntReference comboSpreadLight;
	public static IntReference comboSpreadLightDisabled;
	public static IntReference comboSpreadDark;
	public static IntReference comboSpreadDarkDisabled;    	
	public static InsetsUIResource comboInsets;    
	public static BooleanReference comboRollover;
	public static BooleanReference comboFocus;
	
//	List
	public static SBReference listBgColor;
	public static SBReference listTextColor;
	public static SBReference listSelectedBgColor;
	public static SBReference listSelectedTextColor;
	// new in 1.4.0
	public static SBReference listFocusBorderColor;
	
//	Menu
	public static SBReference menuBarColor;
	public static SBReference menuRolloverBgColor;
	public static SBReference menuRolloverFgColor;
	public static SBReference menuDisabledFgColor;
	// New in 1.4.0
	public static SBReference menuItemDisabledFgColor;
	public static SBReference menuItemRolloverColor;
	public static SBReference menuItemSelectedTextColor;
	public static SBReference menuBorderColor;
	public static SBReference menuPopupColor;
	public static SBReference menuInnerHilightColor;
	public static SBReference menuInnerShadowColor;
	public static SBReference menuOuterHilightColor;
	public static SBReference menuOuterShadowColor;
	public static SBReference menuIconColor;
	public static SBReference menuIconRolloverColor;
	public static SBReference menuIconDisabledColor;
	public static SBReference menuSeparatorColor;
	
	public static BooleanReference menuRollover;
	// New in 1.4.0
	public static BooleanReference menuPopupShadow;
	public static BooleanReference menuAllowTwoIcons;
	// End New in 1.4.0
	
//	Toolbar
	public static SBReference toolBarColor;
	public static SBReference toolBarDarkColor;
	public static SBReference toolBarLightColor;
	public static SBReference toolButtColor;
	public static SBReference toolButtSelectedColor;
	public static SBReference toolButtRolloverColor;
	public static SBReference toolButtPressedColor;
	public static SBReference toolBorderColor;
	public static SBReference toolBorderSelectedColor;
	public static SBReference toolBorderRolloverColor;
	public static SBReference toolBorderPressedColor;
	public static SBReference toolGripDarkColor;
	public static SBReference toolGripLightColor;
	public static SBReference toolSeparatorColor;
	
	// new in 1.3
	public static InsetsUIResource toolMargin;

	public static BooleanReference toolFocus;
	public static BooleanReference toolRollover;
	
//	Button
	public static SBReference buttonNormalColor;
	public static SBReference buttonRolloverBgColor;
	public static SBReference buttonPressedColor;
	public static SBReference buttonDisabledColor;
	public static SBReference buttonRolloverColor;
	public static SBReference buttonDefaultColor;
	public static SBReference buttonCheckColor;
	public static SBReference buttonCheckDisabledColor;
	public static SBReference buttonBorderColor;
	public static SBReference buttonBorderDisabledColor;
	public static SBReference buttonDisabledFgColor;
	public static SBReference checkDisabledFgColor;
	public static SBReference radioDisabledFgColor;
	
	// new in 1.4.0
	public static SBReference toggleSelectedBg;
	
	public static BooleanReference buttonRolloverBorder;
	public static BooleanReference buttonFocus;
	public static BooleanReference buttonFocusBorder;
	public static BooleanReference buttonEnter;
	
	// new in 1.3.04
	public static BooleanReference shiftButtonText;
	
	public static InsetsUIResource buttonMargin;

	public static IntReference buttonSpreadLight;
	public static IntReference buttonSpreadLightDisabled;
	public static IntReference buttonSpreadDark;
	public static IntReference buttonSpreadDarkDisabled;
	
//	CheckBox
	// new in 1.3
	public static InsetsUIResource checkMargin;
	
//	Tabbed
	public static SBReference tabPaneBorderColor;
	public static SBReference tabNormalColor;
	public static SBReference tabSelectedColor;
	public static SBReference tabDisabledColor;
	public static SBReference tabDisabledSelectedColor;
	public static SBReference tabDisabledTextColor;
	public static SBReference tabBorderColor;
	public static SBReference tabRolloverColor;
	
	// new in 1.4.0
	public static SBReference tabPaneDisabledBorderColor;
	public static SBReference tabDisabledBorderColor;
	// end new in 1.4.0

	public static BooleanReference tabRollover;
	
	// new in 1.3.05
	public static BooleanReference tabFocus;
	public static BooleanReference ignoreSelectedBg;
	public static BooleanReference fixedTabs;
	
	public static InsetsUIResource tabInsets;
	public static InsetsUIResource tabAreaInsets;
	
//	Slider
	public static BooleanReference sliderRolloverEnabled;
	
	// new in 1.3.05
	public static BooleanReference sliderFocusEnabled;
	
	public static SBReference sliderThumbColor;
	public static SBReference sliderThumbRolloverColor;
	public static SBReference sliderThumbPressedColor;
	public static SBReference sliderThumbDisabledColor;
	public static SBReference sliderBorderColor;
	public static SBReference sliderDarkColor;
	public static SBReference sliderLightColor;
	public static SBReference sliderBorderDisabledColor;
	public static SBReference sliderTrackColor;
	public static SBReference sliderTrackBorderColor;
	public static SBReference sliderTrackDarkColor;
	public static SBReference sliderTrackLightColor;
	public static SBReference sliderTickColor;
	public static SBReference sliderTickDisabledColor;
	
	// new in 1.3.05
	public static SBReference sliderFocusColor;
	
//	Spinner
	public static BooleanReference spinnerRollover;
	
	public static SBReference spinnerButtColor;
	public static SBReference spinnerButtRolloverColor;
	public static SBReference spinnerButtPressedColor;
	public static SBReference spinnerButtDisabledColor;
	public static SBReference spinnerBorderColor;
	public static SBReference spinnerBorderDisabledColor;
	public static SBReference spinnerArrowColor;
	public static SBReference spinnerArrowDisabledColor;

	public static IntReference spinnerSpreadLight;
	public static IntReference spinnerSpreadLightDisabled;
	public static IntReference spinnerSpreadDark;
	public static IntReference spinnerSpreadDarkDisabled;
	
//	Scrollbar
	public static SBReference scrollTrackColor;
	public static SBReference scrollTrackDisabledColor;
	public static SBReference scrollTrackBorderColor;
	public static SBReference scrollTrackBorderDisabledColor;
	public static SBReference scrollThumbColor;
	public static SBReference scrollThumbRolloverColor;
	public static SBReference scrollThumbPressedColor;
	public static SBReference scrollThumbDisabledColor;
	public static SBReference scrollButtColor;
	public static SBReference scrollButtRolloverColor;
	public static SBReference scrollButtPressedColor;
	public static SBReference scrollButtDisabledColor;
	public static SBReference scrollArrowColor;
	public static SBReference scrollArrowDisabledColor;
	public static SBReference scrollGripLightColor;
	public static SBReference scrollGripDarkColor;
	public static SBReference scrollBorderColor;
	public static SBReference scrollBorderLightColor;
	public static SBReference scrollBorderDisabledColor;
	public static SBReference scrollLightDisabledColor;
	public static SBReference scrollPaneBorderColor;
	
	public static IntReference scrollSpreadLight;
	public static IntReference scrollSpreadLightDisabled;
	public static IntReference scrollSpreadDark;
	public static IntReference scrollSpreadDarkDisabled;
	
	public static BooleanReference scrollRollover;
	
	// New in 1.4.0
	public static IntReference scrollSize;
	
//	Tree
	public static SBReference treeBgColor;
	public static SBReference treeTextColor;
	public static SBReference treeTextBgColor;
	public static SBReference treeSelectedTextColor;
	public static SBReference treeSelectedBgColor;
	public static SBReference treeLineColor;
	
//	Frame
	public static SBReference frameCaptionColor;
	public static SBReference frameCaptionDisabledColor;
	public static SBReference frameBorderColor;
	public static SBReference frameLightColor;
	public static SBReference frameBorderDisabledColor;
	public static SBReference frameLightDisabledColor;
	public static SBReference frameTitleColor;
	// New in 1.4.0
	public static SBReference frameTitleShadowColor;
	public static SBReference frameTitleDisabledColor;
	public static SBReference frameButtColor;
	public static SBReference frameButtRolloverColor;
	public static SBReference frameButtPressedColor;
	public static SBReference frameButtDisabledColor;
	public static SBReference frameButtCloseColor;
	public static SBReference frameButtCloseRolloverColor;
	public static SBReference frameButtClosePressedColor;
	public static SBReference frameButtCloseDisabledColor;
	public static SBReference frameButtBorderColor;
	public static SBReference frameButtBorderDisabledColor;
	
	public static IntReference frameButtSpreadLight;
	public static IntReference frameButtSpreadDark;
	public static IntReference frameButtSpreadLightDisabled;
	public static IntReference frameButtSpreadDarkDisabled;
	
	public static SBReference frameButtCloseBorderColor;
	public static SBReference frameButtCloseDarkColor;
	public static SBReference frameButtCloseLightColor;
	public static SBReference frameButtCloseBorderDisabledColor;
	
	public static IntReference frameButtCloseSpreadLight;
	public static IntReference frameButtCloseSpreadLightDisabled;
	public static IntReference frameButtCloseSpreadDark;
	public static IntReference frameButtCloseSpreadDarkDisabled;
	
	public static SBReference frameSymbolColor;
	public static SBReference frameSymbolPressedColor;
	public static SBReference frameSymbolDisabledColor;
	public static SBReference frameSymbolDarkColor;
	public static SBReference frameSymbolLightColor;
	public static SBReference frameSymbolDarkDisabledColor;
	public static SBReference frameSymbolLightDisabledColor;
	public static SBReference frameSymbolCloseColor;
	public static SBReference frameSymbolClosePressedColor;
	public static SBReference frameSymbolCloseDisabledColor;
	public static SBReference frameSymbolCloseDarkColor;
	public static SBReference frameSymbolCloseDarkDisabledColor;
	
	public static IntReference frameSpreadDark;
	public static IntReference frameSpreadLight;
	public static IntReference frameSpreadDarkDisabled;
	public static IntReference frameSpreadLightDisabled;
	
//	Table
	public static SBReference tableBackColor;
	public static SBReference tableHeaderBackColor;
	public static SBReference tableHeaderRolloverBackColor;
	public static SBReference tableHeaderRolloverColor;
	public static SBReference tableHeaderArrowColor;
	public static SBReference tableGridColor;
	public static SBReference tableSelectedBackColor;
	public static SBReference tableSelectedForeColor;
	public static SBReference tableBorderDarkColor;
	public static SBReference tableBorderLightColor;
	public static SBReference tableHeaderDarkColor;
	public static SBReference tableHeaderLightColor;
	public static SBReference tableFocusBorderColor;
	// New in 1.4.0
	public static SBReference tableAlternateRowColor;
	
//	Icons
	private static final int hue = 51;
	
	public static HSBReference[] colorizer = new HSBReference[20];
	
	public static BooleanReference[] colorize = new BooleanReference[20];
	
//	Separator
	public static SBReference separatorColor;
	
//	ToolTip
	public static SBReference tipBorderColor;
	public static SBReference tipBorderDis;
	public static SBReference tipBgColor;
	public static SBReference tipBgDis;
	public static SBReference tipTextColor;
	public static SBReference tipTextDis;
	
//	Misc
	public static SBReference titledBorderColor;
	
	// new in 1.4.0
	public static SBReference splitPaneButtonColor;
	
	static {
		initData();
	}
	
	/**
	 * No public constructor.
	 *
	 */
	private Theme() {}
	
	private static URI getYQ_URI() {
		try {
			return new URI("file:/YQ%20Theme");
		}
		catch(URISyntaxException ex) {
			System.err.println("Exception creating YQ URI:\n" + ex);
			return null;
		}
	}
	
	private static URL getYQ_URL() {
		try {
			return new URL("file:/YQ%20Theme");
		}
		catch(MalformedURLException ex) {
			System.err.println("Exception creating YQ URL:\n" + ex);
			return null;
		}
	}
	
	/**
	 * Searches for available TinyLaF theme resources and returns
	 * them as an array of <code>ThemeDescription</code>s.
	 * The search order is as follows:
	 * <ul>
	 * <li>Searches user's home directory.
	 * <li>Searches user's current working directory.
	 * <li>Searches for known themes which might be present in the class path.
	 * </ul>
	 * @return an array of <code>ThemeDescription</code> objects which can be of
	 * zero length but will not be <code>null</code>.
	 */
	public static ThemeDescription[] getAvailableThemes() {
		Vector themes = new Vector();
		
		// add YQ default theme
		if(Theme.YQ_URI != null) {
			themes.add(new ThemeDescription(Theme.YQ_URI));
		}

		// search user dir
		try {
			String userDir = TinyUtils.getSystemProperty("user.home");
			
			if(userDir != null) {
				File dir = new File(userDir);
				File[] files = dir.listFiles(THEMES_FILTER);
				
				if(files != null && files.length > 0) {
					for(int i = 0; i < files.length; i++) {
						ThemeDescription td = 
							new ThemeDescription(files[i].toURI());
						
						if(td.isValid()) {
							themes.add(td);
							
							if(DEBUG) {
								System.out.println(td.getName() + " found at user.home: " + userDir);
							}
						}
					}
				}
			}
		}
		catch(SecurityException ignore) {}
		
		// search working dir
		try {
			String workDir = TinyUtils.getSystemProperty("user.dir");
			
			if(workDir != null) {
				File dir = new File(workDir);
				File[] files = dir.listFiles(THEMES_FILTER);
				
				if(files != null && files.length > 0) {
					for(int i = 0; i < files.length; i++) {
						ThemeDescription td = 
							new ThemeDescription(files[i].toURI());
						
						if(td.isValid()) {	// user.dir overrides user.home
							themes.add(td);
							
							if(DEBUG) {
								System.out.println(td.getName() + " found in user.dir: " + workDir);
							}
						}
					}
				}
			}
		}
		catch(SecurityException ignore) {}
		
		// search for known themes from inside tinylaf.jar
		addResourceTheme("/themes/Forest.theme", themes);
		addResourceTheme("/themes/Golden.theme", themes);
		addResourceTheme("/themes/Nightly.theme", themes);
		addResourceTheme("/themes/Plastic.theme", themes);
		addResourceTheme("/themes/Silver.theme", themes);
		addResourceTheme("/themes/Unicode.theme", themes);

		// Note: Creating this (non-existing) theme causes no error
		// because the URL is well-formed, a new ThemeDescription
		// will be created.
		// When trying to set this theme with Theme.loadTheme(ThemeDescription),
		// no exceptions will be thrown but Theme.loadTheme(ThemeDescription)
		// will return false to indicate that an error occured while trying
		// to load the theme.
//		try {
//			themes.add(new ThemeDescription(new URL("file:/D:/themes/Unknown.theme")));
//		}
//		catch(MalformedURLException ex) {}

//		printThemes(themes);
		
		if(themes.isEmpty()) {
			return new ThemeDescription[0];
		}
		
		return (ThemeDescription[])themes.toArray(new ThemeDescription[themes.size()]);
	}
	
	private static void addResourceTheme(String path, Vector themes) {
		URL url = TinyLookAndFeel.class.getResource(path);
		
		if(url != null) {
			ThemeDescription td = new ThemeDescription(url);
			
			if(td.isValid() && !themes.contains(td)) {
				themes.add(td);
				
				if(DEBUG) {
					System.out.println("addResourceTheme() path=" + path +
						", URL=" + td.getURL());
				}
			}
		}
	}
	
	/* Debugging code */
	private static void printThemes(Vector themes) {
		System.out.println(themes.size() + " themes:");
		
		Iterator ii = themes.iterator();
		while(ii.hasNext()) {
			ThemeDescription td = (ThemeDescription)ii.next();
			System.out.println("  " + td.getURL().toExternalForm());
			System.out.println("  '" + td.getName() + "' valid: " +
				td.isValid());
		}
	}
	
	public static String getPlatformFont(String fontFamily) {
		if(TinyUtils.isOSMac()) {
			String replacement = MAC_FONT_MAPPINGS.getProperty(fontFamily);
//			System.out.println("Mac: " + fontFamily + " => " + replacement);
			
			return (replacement != null ? replacement : fontFamily);
		}
		else if(TinyUtils.isOSLinux()) {
			String replacement = LINUX_FONT_MAPPINGS.getProperty(fontFamily);
//			System.out.println("Linux: " + fontFamily + " => " + replacement);
			
			return (replacement != null ? replacement : fontFamily);
		}
		
		return fontFamily;
	}
	
	private static void loadFontMappings() {
		URL url = Theme.class.getResource("/de/muntjak/tinylookandfeel/MacFontMappings.properties");
		
		if(url != null) {
			try {
				MAC_FONT_MAPPINGS.load(url.openStream());
//				System.out.println("MacFontMappings.properties loaded, size=" + MAC_FONT_MAPPINGS.size());
			}
			catch(IOException ex) {
				System.err.println(Theme.class.getName() +
					": Unable to locate MacFontMappings.properties.");
			}
		}
		else {
			System.err.println(Theme.class.getName() +
				": Unable to locate MacFontMappings.properties.");
		}
		
		url = Theme.class.getResource("/de/muntjak/tinylookandfeel/LinuxFontMappings.properties");
		
		if(url != null) {
			try {
				LINUX_FONT_MAPPINGS.load(url.openStream());
//				System.out.println("LinuxFontMappings.properties loaded, size=" + LINUX_FONT_MAPPINGS.size());
			}
			catch(IOException ex) {
				System.err.println(Theme.class.getName() +
					": Unable to locate LinuxFontMappings.properties.");
			}
		}
		else {
			System.err.println(Theme.class.getName() +
				": Unable to locate LinuxFontMappings.properties.");
		}
	}
	
	private static void initData() {
		loadFontMappings();
		
//		Colors
		mainColor = new SBReference(new Color(0, 106, 255), 0, 0, SBReference.ABS_COLOR, true);
		disColor = new SBReference(new Color(143, 142, 139), 0, 0, SBReference.ABS_COLOR, true);
		backColor = new SBReference(new Color(236, 233, 216), 0, 0, SBReference.ABS_COLOR, true);
		frameColor = new SBReference(new Color(0, 85, 255), 0, 0, SBReference.ABS_COLOR, true);
		sub1Color = new SBReference(new Color(197, 213, 252), 0, 0, SBReference.ABS_COLOR);
		sub2Color = new SBReference(new Color(34, 161, 34), 0, 0, SBReference.ABS_COLOR);
		sub3Color = new SBReference(new Color(231, 232, 245), 0, 0, SBReference.ABS_COLOR);
		sub4Color = new SBReference(new Color(227, 92, 60), 0, 0, SBReference.ABS_COLOR);
		sub5Color = new SBReference(new Color(120, 123, 189), 0, 0, SBReference.ABS_COLOR);
		sub6Color = new SBReference(new Color(248, 179, 48), 0, 0, SBReference.ABS_COLOR);
		sub7Color = new SBReference(new Color(175, 105, 125), 0, 0, SBReference.ABS_COLOR);
		sub8Color = new SBReference(new Color(255, 255, 255), 0, 0, SBReference.ABS_COLOR);
		
//		Font
		buttonFontColor = new SBReference();
		labelFontColor = new SBReference();
		menuFontColor = new SBReference();
		menuItemFontColor = new SBReference();
		radioFontColor = new SBReference();
		checkFontColor = new SBReference();
		tableFontColor = new SBReference();
		tableHeaderFontColor = new SBReference();
		titledBorderFontColor = new SBReference();
		tabFontColor = new SBReference();
		
		plainFont = new ColoredFont("Tahoma", Font.PLAIN, 11);
		boldFont = new ColoredFont("Tahoma", Font.BOLD, 11);
		buttonFont = new ColoredFont(buttonFontColor);
		labelFont = new ColoredFont(labelFontColor);
		passwordFont = new ColoredFont();
		comboFont = new ColoredFont();
		listFont = new ColoredFont();
		menuFont = new ColoredFont(menuFontColor);
		menuItemFont = new ColoredFont(menuItemFontColor);
		radioFont = new ColoredFont(radioFontColor);
		checkFont = new ColoredFont(checkFontColor);
		tableFont = new ColoredFont(tableFontColor);
		tableHeaderFont = new ColoredFont(tableHeaderFontColor);
		textAreaFont = new ColoredFont();
		textFieldFont = new ColoredFont();
		textPaneFont = new ColoredFont();
		titledBorderFont = new ColoredFont(titledBorderFontColor);
		toolTipFont = new ColoredFont();
		treeFont = new ColoredFont();
		
		tabFontColor = new SBReference();
		tabFont = new ColoredFont(tabFontColor);
		tabFont.setBoldFont(false);
		
		editorFont = new ColoredFont();
		frameTitleFont = new ColoredFont("Trebuchet MS", Font.BOLD, 13);
		internalFrameTitleFont = new ColoredFont("Trebuchet MS", Font.BOLD, 13);
		internalPaletteTitleFont = new ColoredFont("Trebuchet MS", Font.BOLD, 12);
		progressBarFont = new ColoredFont();
		
//		Progressbar
		progressColor = new SBReference(new Color(44, 212, 43), 43, 19, SBReference.SUB2_COLOR);
		progressTrackColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		progressBorderColor = new SBReference(new Color(104, 104, 104), -100, -54, SBReference.BACK_COLOR);
		progressDarkColor = new SBReference(new Color(190, 190, 190), -100, -16, SBReference.BACK_COLOR);
		progressLightColor = new SBReference(new Color(238, 238, 238), -100, 40, SBReference.BACK_COLOR);
		progressSelectForeColor = new SBReference(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		progressSelectBackColor = new SBReference(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		
//		Text	
		textBgColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		textPaneBgColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		editorPaneBgColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		desktopPaneBgColor = new SBReference(new Color(212, 210, 194), 0, -10, SBReference.BACK_COLOR);
		textTextColor = new SBReference(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		textCaretColor = new SBReference(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		textSelectedBgColor = new SBReference(new Color(43, 107, 197), -36, -6, SBReference.MAIN_COLOR);
		textSelectedTextColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		textDisabledBgColor = new SBReference(new Color(240, 237, 224), 0, 20, SBReference.BACK_COLOR);
		textNonEditableBgColor = new SBReference(new Color(240, 237, 224), 0, 20, SBReference.BACK_COLOR);
		textBorderColor = new SBReference(new Color(128, 152, 186), -70, 23, SBReference.MAIN_COLOR);
		textBorderDisabledColor = new SBReference(new Color(201, 198, 184), 0, -15, SBReference.BACK_COLOR);
		
		textInsets = new InsetsUIResource(2, 3, 2, 3);
		
//		Button    	
		buttonRolloverBorder = new BooleanReference(true);
		buttonFocus = new BooleanReference(false);
		buttonFocusBorder = new BooleanReference(true);
		buttonEnter = new BooleanReference(true);
		shiftButtonText = new BooleanReference(true);
		
		buttonNormalColor = new SBReference(new Color(231, 232, 245), 0, 0, SBReference.SUB3_COLOR);
		buttonRolloverBgColor = new SBReference(new Color(239, 240, 248), 0, 33, SBReference.SUB3_COLOR);
		buttonPressedColor = new SBReference(new Color(217, 218, 230), 0, -6, SBReference.SUB3_COLOR);
		buttonDisabledColor = new SBReference(new Color(245, 244, 235), 0, 48, SBReference.BACK_COLOR);
		buttonBorderColor = new SBReference(new Color(21, 61, 117), -30, -46, SBReference.MAIN_COLOR);
		buttonBorderDisabledColor = new SBReference(new Color(201, 198, 184), 0, -15, SBReference.BACK_COLOR);
		buttonDisabledFgColor = new SBReference(new Color(143, 142, 139), 0, 0, SBReference.DIS_COLOR);
		checkDisabledFgColor = new SBReference(new Color(143, 142, 139), 0, 0, SBReference.DIS_COLOR);
		radioDisabledFgColor = new SBReference(new Color(143, 142, 139), 0, 0, SBReference.DIS_COLOR);
		toggleSelectedBg = new SBReference(new Color(160, 182, 235), 38, -12, SBReference.SUB1_COLOR);
		
		buttonMargin = new InsetsUIResource(2, 12, 2, 12);

		buttonRolloverColor = new SBReference(new Color(248, 179, 48), 0, 0, SBReference.SUB6_COLOR);
		buttonDefaultColor = new SBReference(new Color(160, 182, 235), 38, -12, SBReference.SUB1_COLOR);
		buttonCheckColor = new SBReference(new Color(34, 161, 34), 0, 0, SBReference.SUB2_COLOR);
		buttonCheckDisabledColor = new SBReference(new Color(208, 205, 190), 0, -12, SBReference.BACK_COLOR);
		
		checkMargin = new InsetsUIResource(2, 2, 2, 2);
		
		buttonSpreadLight = new IntReference(20);
		buttonSpreadDark = new IntReference(3);
		buttonSpreadLightDisabled = new IntReference(20);
		buttonSpreadDarkDisabled = new IntReference(1);
		
//		Scrollbar
		scrollRollover = new BooleanReference(true);
		
		scrollSize = new IntReference(DEFAULT_SCROLL_SIZE);
		
		// Track
		scrollTrackColor = new SBReference(new Color(249, 249, 247), -50, 76, SBReference.BACK_COLOR);
		scrollTrackDisabledColor = new SBReference(new Color(249, 249, 247), -50, 76, SBReference.BACK_COLOR);
		scrollTrackBorderColor = new SBReference(new Color(234, 231, 218), -23, 0, SBReference.BACK_COLOR);
		scrollTrackBorderDisabledColor = new SBReference(new Color(234, 231, 218), -23, 0, SBReference.BACK_COLOR);
		
		// Thumb
		scrollThumbColor = new SBReference(new Color(197, 213, 252), 0, 0, SBReference.SUB1_COLOR);
		scrollThumbRolloverColor = new SBReference(new Color(226, 234, 254), 0, 50, SBReference.SUB1_COLOR);
		scrollThumbPressedColor = new SBReference(new Color(187, 202, 239), 0, -5, SBReference.SUB1_COLOR);
		scrollThumbDisabledColor = new SBReference(new Color(238, 238, 231), 0, -3, SBReference.SUB1_COLOR);
		
		// Grip
		scrollGripLightColor = new SBReference(new Color(238, 243, 254), 0, 71, SBReference.SUB1_COLOR);
		scrollGripDarkColor = new SBReference(new Color(171, 185, 219), 0, -13, SBReference.SUB1_COLOR);
		
		// Buttons
		scrollButtColor = new SBReference(new Color(197, 213, 252), 0, 0, SBReference.SUB1_COLOR);
		scrollButtRolloverColor = new SBReference(new Color(226, 234, 254), 0, 50, SBReference.SUB1_COLOR);
		scrollButtPressedColor = new SBReference(new Color(187, 202, 239), 0, -5, SBReference.SUB1_COLOR);
		scrollButtDisabledColor = new SBReference(new Color(238, 237, 231), -48, 29, SBReference.BACK_COLOR);
		
		scrollSpreadLight = new IntReference(20);
		scrollSpreadDark = new IntReference(2);
		scrollSpreadLightDisabled = new IntReference(20);
		scrollSpreadDarkDisabled = new IntReference(1);
		
		// Arrow
		scrollArrowColor = new SBReference(new Color(77, 100, 132), -74, -18, SBReference.MAIN_COLOR);
		scrollArrowDisabledColor = new SBReference(new Color(193, 193, 193), -100, -15, SBReference.BACK_COLOR);
		
		// Border
		scrollBorderColor = new SBReference(new Color(212, 210, 194), 0, -10, SBReference.SUB1_COLOR);
		scrollBorderLightColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.SUB1_COLOR);
		scrollBorderDisabledColor = new SBReference(new Color(232, 230, 220), -41, 0, SBReference.BACK_COLOR);
		scrollLightDisabledColor = new SBReference(new Color(232, 230, 220), -41, 0, SBReference.BACK_COLOR);
		
		// ScrollPane border
		scrollPaneBorderColor = new SBReference(new Color(201, 198, 184), 0, -15, SBReference.BACK_COLOR);
		
//		Tabbed
		tabPaneBorderColor = new SBReference(new Color(143, 160, 183), -78, 28, SBReference.MAIN_COLOR);
		tabNormalColor = new SBReference(new Color(242, 240, 238), 0, 69, SBReference.BACK_COLOR);
		tabSelectedColor = new SBReference(new Color(251, 251, 250), 0, 91, SBReference.BACK_COLOR);
		
		// since 1.3
		tabDisabledColor = new SBReference(new Color(244, 242, 232), 0, 40, SBReference.BACK_COLOR);
		tabDisabledSelectedColor = new SBReference(new Color(251, 251, 247), 0, 80, SBReference.BACK_COLOR);
		tabDisabledTextColor = new SBReference(new Color(188, 187, 185), 0, 40, SBReference.DIS_COLOR);
		// end since 1.3
		
		tabBorderColor = new SBReference(new Color(143, 160, 183), -78, 28, SBReference.MAIN_COLOR);
		tabRolloverColor = new SBReference(new Color(255, 199, 59), 0, 0, SBReference.SUB6_COLOR);
		
		// since 1.4.0
		tabPaneDisabledBorderColor = new SBReference(new Color(208, 205, 190), 0, -12, SBReference.BACK_COLOR);
		tabDisabledBorderColor = new SBReference(new Color(208, 205, 190), 0, -12, SBReference.BACK_COLOR);
		// end since 1.4.0

		tabRollover = new BooleanReference(true);
		
		// since 1.3.05
		tabFocus = new BooleanReference(true);
		
		ignoreSelectedBg = new BooleanReference(false);
		fixedTabs = new BooleanReference(true);
		
		tabInsets = new InsetsUIResource(1, 6, 4, 6);
		tabAreaInsets = new InsetsUIResource(4, 2, 0, 0);
		
//		Slider
		sliderRolloverEnabled = new BooleanReference(true);
		
		// since 1.3.05
		sliderFocusEnabled = new BooleanReference(true);

		// Thumb
		sliderThumbColor = new SBReference(new Color(245, 244, 235), 0, 49, SBReference.BACK_COLOR);
		sliderThumbRolloverColor = new SBReference(new Color(233, 166, 0), 100, -26, SBReference.SUB6_COLOR);
		sliderThumbPressedColor = new SBReference(new Color(244, 243, 239), -50, 50, SBReference.BACK_COLOR);
		sliderThumbDisabledColor = new SBReference(new Color(245, 243, 234), 0, 45, SBReference.BACK_COLOR);
		
		// Border
		sliderBorderColor = new SBReference(new Color(176, 189, 207), -76, 50, SBReference.MAIN_COLOR);
		sliderDarkColor = new SBReference(new Color(119, 130, 146), -89, 4, SBReference.MAIN_COLOR);
		sliderLightColor = new SBReference(new Color(27, 155, 27), 16, -7, SBReference.SUB2_COLOR);
		sliderBorderDisabledColor = new SBReference(new Color(214, 212, 198), -6, -9, SBReference.BACK_COLOR);
		
		// Track
		sliderTrackColor = new SBReference(new Color(240, 237, 224), 0, 20, SBReference.BACK_COLOR);
		sliderTrackBorderColor = new SBReference(new Color(157, 156, 150), -53, -32, SBReference.BACK_COLOR);
		sliderTrackDarkColor = new SBReference(new Color(242, 241, 232), -22, 39, SBReference.BACK_COLOR);
		sliderTrackLightColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		
		// Ticks
		sliderTickColor = new SBReference(new Color(118, 117, 108), 0, -50, SBReference.BACK_COLOR);
		sliderTickDisabledColor = new SBReference(new Color(174, 174, 171), 0, 28, SBReference.DIS_COLOR);
		
		// since 1.3.05
		sliderFocusColor = new SBReference(new Color(113, 112, 104), 0, -52, SBReference.BACK_COLOR);
		
//		Spinner
		spinnerRollover = new BooleanReference(false);
		
		// Button
		spinnerButtColor = new SBReference(new Color(198, 213, 250), 0, 0, SBReference.SUB1_COLOR);
		spinnerButtRolloverColor = new SBReference(new Color(232, 238, 254), 0, 60, SBReference.SUB1_COLOR);
		spinnerButtPressedColor = new SBReference(new Color(175, 190, 224), 0, -11, SBReference.SUB1_COLOR);
		spinnerButtDisabledColor = new SBReference(new Color(242, 240, 228), 0, 30, SBReference.BACK_COLOR);
		
		spinnerSpreadLight = new IntReference(20);
		spinnerSpreadDark = new IntReference(3);
		spinnerSpreadLightDisabled = new IntReference(20);
		spinnerSpreadDarkDisabled = new IntReference(1);
		
		spinnerBorderColor = new SBReference(new Color(128, 152, 186), -70, 23, SBReference.MAIN_COLOR);
		spinnerBorderDisabledColor = new SBReference(new Color(215, 212, 197), 0, -9, SBReference.BACK_COLOR);
		spinnerArrowColor = new SBReference(new Color(77, 100, 132), -74, -18, SBReference.MAIN_COLOR);
		spinnerArrowDisabledColor = new SBReference(new Color(212, 210, 194), 0, -10, SBReference.BACK_COLOR);
		
//		Combo
		comboBorderColor = new SBReference(new Color(128, 152, 186), -70, 23, SBReference.MAIN_COLOR);
		comboBorderDisabledColor = new SBReference(new Color(201, 198, 184), 0, -15, SBReference.BACK_COLOR);
		comboSelectedBgColor = new SBReference(new Color(43, 107, 197), -36, -6, SBReference.MAIN_COLOR);
		comboSelectedTextColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		comboFocusBgColor = new SBReference(new Color(43, 107, 197), 0, 0, SBReference.ABS_COLOR);
		comboBgColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		comboTextColor = new SBReference(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		
		// Button
		comboButtColor = new SBReference(new Color(197, 213, 252), 0, 0, SBReference.SUB1_COLOR);
		comboButtRolloverColor = new SBReference(new Color(226, 234, 254), 0, 50, SBReference.SUB1_COLOR);
		comboButtPressedColor = new SBReference(new Color(175, 190, 224), 0, -11, SBReference.SUB1_COLOR);
		comboButtDisabledColor = new SBReference(new Color(238, 237, 231), -48, 29, SBReference.BACK_COLOR);
		
		comboSpreadLight = new IntReference(20);
		comboSpreadDark = new IntReference(3);
		comboSpreadLightDisabled = new IntReference(20);
		comboSpreadDarkDisabled = new IntReference(1);
		
		// Button Border
		comboButtBorderColor = new SBReference(new Color(212, 210, 194), 0, -10, SBReference.SUB1_COLOR);
		comboButtBorderDisabledColor = new SBReference(new Color(232, 230, 220), -41, 0, SBReference.BACK_COLOR);
		
		// Arrow
		comboArrowColor = new SBReference(new Color(77, 100, 132), -74, -18, SBReference.MAIN_COLOR);
		comboArrowDisabledColor = new SBReference(new Color(203, 200, 186), 0, -14, SBReference.BACK_COLOR);

		comboInsets = new InsetsUIResource(2, 2, 2, 2);
		
		comboRollover = new BooleanReference(false);
		comboFocus = new BooleanReference(false);
		
//		Menu
		menuBarColor = new SBReference(new Color(238, 237, 230), -43, 28, SBReference.BACK_COLOR);
		menuItemSelectedTextColor = new SBReference(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		menuPopupColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		menuRolloverBgColor = new SBReference(new Color(189, 208, 234), -50, 66, SBReference.MAIN_COLOR);
		menuRolloverFgColor = new SBReference(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		menuDisabledFgColor = new SBReference(new Color(143, 142, 139), 0, 0, SBReference.DIS_COLOR);
		menuItemDisabledFgColor = new SBReference(new Color(143, 142, 139), 0, 0, SBReference.DIS_COLOR);
		menuItemRolloverColor = new SBReference(new Color(189, 208, 234), -50, 66, SBReference.MAIN_COLOR);
		menuBorderColor = new SBReference(new Color(173, 170, 153), 4, -28, SBReference.BACK_COLOR);
		menuInnerHilightColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		menuInnerShadowColor = new SBReference(new Color(213, 212, 207), -70, -7, SBReference.BACK_COLOR);
		menuOuterHilightColor = new SBReference(new Color(173, 170, 153), 4, -28, SBReference.BACK_COLOR);
		menuOuterShadowColor = new SBReference(new Color(173, 170, 153), 4, -28, SBReference.BACK_COLOR);
		menuIconColor = new SBReference(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		menuIconRolloverColor = new SBReference(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		menuIconDisabledColor = new SBReference(new Color(165, 163, 151), 0, -30, SBReference.BACK_COLOR);
		menuSeparatorColor = new SBReference(new Color(173, 170, 153), 4, -28, SBReference.BACK_COLOR);
		
		menuRollover = new BooleanReference(true);
		menuPopupShadow = new BooleanReference(false);
		menuAllowTwoIcons = new BooleanReference(false);
		
//		Toolbar
		toolBarColor = new SBReference(new Color(239, 237, 229), -35, 28, SBReference.BACK_COLOR);
		toolBarLightColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		toolBarDarkColor = new SBReference(new Color(214, 210, 187), 10, -11, SBReference.BACK_COLOR);
		toolButtColor = new SBReference(new Color(239, 237, 229), -35, 28, SBReference.BACK_COLOR);
		toolButtSelectedColor = new SBReference(new Color(243, 242, 239), -51, 52, SBReference.BACK_COLOR);
		toolButtRolloverColor = new SBReference(new Color(251, 251, 248), -30, 81, SBReference.BACK_COLOR);
		toolButtPressedColor = new SBReference(new Color(225, 224, 218), -58, -2, SBReference.BACK_COLOR);
		toolGripDarkColor = new SBReference(new Color(167, 167, 163), -70, -27, SBReference.BACK_COLOR);
		toolGripLightColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		toolSeparatorColor = new SBReference(new Color(167, 167, 163), -70, -27, SBReference.BACK_COLOR);
		toolBorderColor = new SBReference(new Color(239, 237, 229), -35, 28, SBReference.BACK_COLOR);
		toolBorderPressedColor = new SBReference(new Color(122, 144, 174), -76, 16, SBReference.MAIN_COLOR);
		toolBorderRolloverColor = new SBReference(new Color(122, 144, 174), -76, 16, SBReference.MAIN_COLOR);
		toolBorderSelectedColor = new SBReference(new Color(122, 144, 174), -76, 16, SBReference.MAIN_COLOR);
		
		toolMargin = new InsetsUIResource(5, 5, 5, 5);

		toolFocus = new BooleanReference(false);
		
		// (!) not adjustable
		toolRollover = new BooleanReference(true);
		
//		List
		listBgColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		listTextColor = new SBReference(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		listSelectedBgColor = new SBReference(new Color(43, 107, 197), -36, -6, SBReference.MAIN_COLOR);
		listSelectedTextColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		listFocusBorderColor = new SBReference(new Color(179, 211, 255), 100, 70, SBReference.MAIN_COLOR);
		
//		Tree
		treeBgColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		treeTextColor = new SBReference(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		treeTextBgColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		treeSelectedTextColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		treeSelectedBgColor = new SBReference(new Color(43, 107, 197), -36, -6, SBReference.MAIN_COLOR);
		treeLineColor = new SBReference(new Color(208, 205, 190), 0, -12, SBReference.BACK_COLOR);
		
//		Frame
		frameCaptionColor = new SBReference(new Color(13, 94, 255), 0, 5, SBReference.FRAME_COLOR);
		frameCaptionDisabledColor = new SBReference(new Color(122, 159, 223), -25, 41, SBReference.FRAME_COLOR);
		frameBorderColor = new SBReference(new Color(0, 60, 161), 0, -30, SBReference.FRAME_COLOR);
		frameLightColor = new SBReference(new Color(0, 68, 184), 0, -20, SBReference.FRAME_COLOR);
		frameBorderDisabledColor = new SBReference(new Color(74, 125, 212), -25, 20, SBReference.FRAME_COLOR);
		frameLightDisabledColor = new SBReference(new Color(99, 144, 233), -25, 30, SBReference.FRAME_COLOR);
		frameTitleColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.FRAME_COLOR);
		frameTitleShadowColor = new SBReference(new Color(0, 43, 128), 0, -50, SBReference.FRAME_COLOR);
		frameTitleDisabledColor = new SBReference(new Color(216, 226, 248), -29, 82, SBReference.FRAME_COLOR);
		
		// Button
		frameButtColor = new SBReference(new Color(13, 94, 255), 0, 5, SBReference.FRAME_COLOR);
		frameButtRolloverColor = new SBReference(new Color(51, 119, 255), 0, 20, SBReference.FRAME_COLOR);
		frameButtPressedColor = new SBReference(new Color(0, 68, 204), 0, -20, SBReference.FRAME_COLOR);
		frameButtDisabledColor = new SBReference(new Color(63, 120, 233), -21, 16, SBReference.FRAME_COLOR);
		
		frameButtSpreadLight = new IntReference(8);
		frameButtSpreadDark = new IntReference(2);
		frameButtSpreadLightDisabled = new IntReference(5);
		frameButtSpreadDarkDisabled = new IntReference(2);
		
		frameButtCloseColor = new SBReference(new Color(227, 92, 60), 0, 0, SBReference.SUB4_COLOR);
		frameButtCloseRolloverColor = new SBReference(new Color(233, 125, 99), 0, 20, SBReference.SUB4_COLOR);
		frameButtClosePressedColor = new SBReference(new Color(193, 78, 51), 0, -15, SBReference.SUB4_COLOR);
		frameButtCloseDisabledColor = new SBReference(new Color(175, 105, 125), 0, 0, SBReference.SUB7_COLOR);
		
		frameButtCloseSpreadLight = new IntReference(8);
		frameButtCloseSpreadDark = new IntReference(2);
		frameButtCloseSpreadLightDisabled = new IntReference(5);
		frameButtCloseSpreadDarkDisabled = new IntReference(2);
		
		// Button Border
		frameButtBorderColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.FRAME_COLOR);
		frameButtBorderDisabledColor = new SBReference(new Color(190, 206, 238), -42, 68, SBReference.FRAME_COLOR);
		
		// Symbol
		frameSymbolColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.FRAME_COLOR);
		frameSymbolPressedColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.FRAME_COLOR);
		frameSymbolDisabledColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.FRAME_COLOR);
		frameSymbolDarkColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.FRAME_COLOR);
		frameSymbolLightColor = new SBReference(new Color(13, 94, 255), 0, 5, SBReference.FRAME_COLOR);
		frameSymbolDarkDisabledColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.FRAME_COLOR);
		frameSymbolLightDisabledColor = new SBReference(new Color(63, 120, 233), -21, 16, SBReference.FRAME_COLOR);
		
		// Close Button
		frameButtCloseBorderColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.FRAME_COLOR);
		frameButtCloseDarkColor = new SBReference(new Color(174, 51, 20), 50, -32, SBReference.SUB4_COLOR);
		frameButtCloseLightColor = new SBReference(new Color(226, 88, 55), 11, -2, SBReference.SUB4_COLOR);
		frameButtCloseBorderDisabledColor = new SBReference(new Color(190, 206, 238), -42, 68, SBReference.FRAME_COLOR);

		// Close Symbol
		frameSymbolCloseColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.FRAME_COLOR);
		frameSymbolClosePressedColor = new SBReference(new Color(231, 180, 168), -24, 50, SBReference.SUB4_COLOR);
		frameSymbolCloseDisabledColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.FRAME_COLOR);
		frameSymbolCloseDarkColor = new SBReference(new Color(227, 92, 60), 0, 0, SBReference.SUB4_COLOR);
		frameSymbolCloseDarkDisabledColor = new SBReference(new Color(175, 105, 125), 0, 0, SBReference.SUB7_COLOR);

		frameSpreadDark = new IntReference(3);
		frameSpreadLight = new IntReference(2);
		frameSpreadDarkDisabled = new IntReference(2);
		frameSpreadLightDisabled = new IntReference(2);
		
//		Table
		tableBackColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		tableHeaderBackColor = new SBReference(new Color(236, 233, 216), 0, 0, SBReference.BACK_COLOR);
		tableHeaderRolloverBackColor = new SBReference(new Color(249, 248, 243), 0, 70, SBReference.BACK_COLOR);
		tableHeaderRolloverColor = new SBReference(new Color(248, 179, 48), 0, 0, SBReference.SUB6_COLOR);
		tableGridColor = new SBReference(new Color(167, 166, 160), -50, -28, SBReference.BACK_COLOR);
		tableHeaderArrowColor = new SBReference(new Color(167, 166, 160), -50, -28, SBReference.BACK_COLOR);
		tableSelectedBackColor = new SBReference(new Color(213, 211, 204), -50, -8, SBReference.BACK_COLOR);
		tableSelectedForeColor = new SBReference(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		tableBorderDarkColor = new SBReference(new Color(167, 166, 160), -50, -28, SBReference.BACK_COLOR);
		tableBorderLightColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		tableHeaderDarkColor = new SBReference(new Color(189, 186, 173), 0, -20, SBReference.BACK_COLOR);
		tableHeaderLightColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		tableFocusBorderColor = new SBReference(new Color(185, 184, 177), -50, -20, SBReference.BACK_COLOR);
		// New in 1.4.0 - Same as tableBackColor by default
		tableAlternateRowColor = new SBReference(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		
//		Icons
		for(int i = 0; i < 20; i++) {
			colorizer[i] = new HSBReference(hue, 25, 0, HSBReference.BACK_COLOR);
			colorize[i] = new BooleanReference(false);
		}
		
//		Separator
		separatorColor = new SBReference(new Color(167, 167, 163), -70, -27, SBReference.BACK_COLOR);
		
//		ToolTip
		tipBorderColor = new SBReference(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		tipBorderDis = new SBReference(new Color(143, 141, 139), 0, 0, SBReference.DIS_COLOR);
		tipBgColor = new SBReference(new Color(255, 255, 225), 0, 0, SBReference.ABS_COLOR);
		tipBgDis = new SBReference(new Color(236, 233, 216), 0, 0, SBReference.BACK_COLOR);
		tipTextColor = new SBReference(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		tipTextDis = new SBReference(new Color(143, 141, 139), 0, 0, SBReference.DIS_COLOR);
		
//		Misc
		titledBorderColor = new SBReference(new Color(165, 163, 151), 0, -30, SBReference.BACK_COLOR);
		splitPaneButtonColor = new SBReference(new Color(170, 168, 156), 0, -28, SBReference.BACK_COLOR);
	}
	
	/**
	 * Loads the default (hard-coded) YQ theme.
	 * (See the notes on <a href="#themeswitching">Theme Switching</a>).
	 */
	public static void loadYQTheme() {
//		Colors
		mainColor.update(new Color(0, 106, 255), 0, 0, SBReference.ABS_COLOR);
		disColor.update(new Color(143, 142, 139), 0, 0, SBReference.ABS_COLOR);
		backColor.update(new Color(236, 233, 216), 0, 0, SBReference.ABS_COLOR);
		frameColor.update(new Color(0, 85, 255), 0, 0, SBReference.ABS_COLOR);
		sub1Color.update(new Color(197, 213, 252), 0, 0, SBReference.ABS_COLOR);
		sub2Color.update(new Color(34, 161, 34), 0, 0, SBReference.ABS_COLOR);
		sub3Color.update(new Color(231, 232, 245), 0, 0, SBReference.ABS_COLOR);
		sub4Color.update(new Color(227, 92, 60), 0, 0, SBReference.ABS_COLOR);
		sub5Color.update(new Color(120, 123, 189), 0, 0, SBReference.ABS_COLOR);
		sub6Color.update(new Color(248, 179, 48), 0, 0, SBReference.ABS_COLOR);
		sub7Color.update(new Color(175, 105, 125), 0, 0, SBReference.ABS_COLOR);
		sub8Color.update(new Color(255, 255, 255), 0, 0, SBReference.ABS_COLOR);
		
//		Font
		buttonFontColor.update(Color.BLACK);
		labelFontColor.update(Color.BLACK);
		menuFontColor.update(Color.BLACK);
		menuItemFontColor.update(Color.BLACK);
		radioFontColor.update(Color.BLACK);
		checkFontColor.update(Color.BLACK);
		tableFontColor.update(Color.BLACK);
		tableHeaderFontColor.update(Color.BLACK);
		titledBorderFontColor.update(Color.BLACK);
		tabFontColor.update(Color.BLACK);
		
		plainFont.update("Tahoma", Font.PLAIN, 11);
		boldFont.update("Tahoma", Font.BOLD, 11);
		buttonFont.update(buttonFontColor);
		labelFont.update(labelFontColor);
		passwordFont.update((SBReference)null);
		comboFont.update((SBReference)null);
		listFont.update((SBReference)null);
		menuFont.update(menuFontColor);
		menuItemFont.update(menuItemFontColor);
		radioFont.update(radioFontColor);
		checkFont.update(checkFontColor);
		tableFont.update(tableFontColor);
		tableHeaderFont.update(tableHeaderFontColor);
		textAreaFont.update((SBReference)null);
		textFieldFont.update((SBReference)null);
		textPaneFont.update((SBReference)null);
		titledBorderFont.update(titledBorderFontColor);
		toolTipFont.update((SBReference)null);
		treeFont.update((SBReference)null);
		
		tabFontColor.update(Color.BLACK);
		tabFont.update(tabFontColor);
		tabFont.setBoldFont(false);
		
		editorFont.update((SBReference)null);
		frameTitleFont.update("Trebuchet MS", Font.BOLD, 13);
		internalFrameTitleFont.update("Trebuchet MS", Font.BOLD, 13);
		internalPaletteTitleFont.update("Trebuchet MS", Font.BOLD, 12);
		progressBarFont.update((SBReference)null);
		
//		Progressbar
		progressColor.update(new Color(44, 212, 43), 43, 19, SBReference.SUB2_COLOR);
		progressTrackColor.update(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		progressBorderColor.update(new Color(104, 104, 104), -100, -54, SBReference.BACK_COLOR);
		progressDarkColor.update(new Color(190, 190, 190), -100, -16, SBReference.BACK_COLOR);
		progressLightColor.update(new Color(238, 238, 238), -100, 40, SBReference.BACK_COLOR);
		progressSelectForeColor.update(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		progressSelectBackColor.update(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		
//		Text	
		textBgColor.update(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		textPaneBgColor.update(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		editorPaneBgColor.update(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		desktopPaneBgColor.update(new Color(212, 210, 194), 0, -10, SBReference.BACK_COLOR);
		textTextColor.update(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		textCaretColor.update(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		textSelectedBgColor.update(new Color(43, 107, 197), -36, -6, SBReference.MAIN_COLOR);
		textSelectedTextColor.update(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		textDisabledBgColor.update(new Color(240, 237, 224), 0, 20, SBReference.BACK_COLOR);
		textNonEditableBgColor.update(new Color(240, 237, 224), 0, 20, SBReference.BACK_COLOR);
		textBorderColor.update(new Color(128, 152, 186), -70, 23, SBReference.MAIN_COLOR);
		textBorderDisabledColor.update(new Color(201, 198, 184), 0, -15, SBReference.BACK_COLOR);
		
		textInsets.top = 2;
		textInsets.left = 3;
		textInsets.bottom = 2;
		textInsets.right = 3;
		
//		Button    	
		buttonRolloverBorder.setValue(true);
		buttonFocus.setValue(false);
		buttonFocusBorder.setValue(true);
		buttonEnter.setValue(true);
		shiftButtonText.setValue(true);
		
		buttonNormalColor.update(new Color(231, 232, 245), 0, 0, SBReference.SUB3_COLOR);
		buttonRolloverBgColor.update(new Color(239, 240, 248), 0, 33, SBReference.SUB3_COLOR);
		buttonPressedColor.update(new Color(217, 218, 230), 0, -6, SBReference.SUB3_COLOR);
		buttonDisabledColor.update(new Color(245, 244, 235), 0, 48, SBReference.BACK_COLOR);
		buttonBorderColor.update(new Color(21, 61, 117), -30, -46, SBReference.MAIN_COLOR);
		buttonBorderDisabledColor.update(new Color(201, 198, 184), 0, -15, SBReference.BACK_COLOR);
		buttonDisabledFgColor.update(new Color(143, 142, 139), 0, 0, SBReference.DIS_COLOR);
		checkDisabledFgColor.update(new Color(143, 142, 139), 0, 0, SBReference.DIS_COLOR);
		radioDisabledFgColor.update(new Color(143, 142, 139), 0, 0, SBReference.DIS_COLOR);
		toggleSelectedBg.update(new Color(160, 182, 235), 38, -12, SBReference.SUB1_COLOR);
		
		buttonMargin.top = 2;
		buttonMargin.left = 12;
		buttonMargin.bottom = 2;
		buttonMargin.right = 12;
		
		buttonRolloverColor.update(new Color(248, 179, 48), 0, 0, SBReference.SUB6_COLOR);
		buttonDefaultColor.update(new Color(160, 182, 235), 38, -12, SBReference.SUB1_COLOR);
		buttonCheckColor.update(new Color(34, 161, 34), 0, 0, SBReference.SUB2_COLOR);
		buttonCheckDisabledColor.update(new Color(208, 205, 190), 0, -12, SBReference.BACK_COLOR);
		
		checkMargin.top = 2;
		checkMargin.left = 2;
		checkMargin.bottom = 2;
		checkMargin.right = 2;
		
		buttonSpreadLight.setValue(20);
		buttonSpreadDark.setValue(3);
		buttonSpreadLightDisabled.setValue(20);
		buttonSpreadDarkDisabled.setValue(1);
		
//		Scrollbar
		scrollRollover.setValue(true);
		
		scrollSize.setValue(DEFAULT_SCROLL_SIZE);
		
		// Track
		scrollTrackColor.update(new Color(249, 249, 247), -50, 76, SBReference.BACK_COLOR);
		scrollTrackDisabledColor.update(new Color(249, 249, 247), -50, 76, SBReference.BACK_COLOR);
		scrollTrackBorderColor.update(new Color(234, 231, 218), -23, 0, SBReference.BACK_COLOR);
		scrollTrackBorderDisabledColor.update(new Color(234, 231, 218), -23, 0, SBReference.BACK_COLOR);
		
		// Thumb
		scrollThumbColor.update(new Color(197, 213, 252), 0, 0, SBReference.SUB1_COLOR);
		scrollThumbRolloverColor.update(new Color(226, 234, 254), 0, 50, SBReference.SUB1_COLOR);
		scrollThumbPressedColor.update(new Color(187, 202, 239), 0, -5, SBReference.SUB1_COLOR);
		scrollThumbDisabledColor.update(new Color(238, 238, 231), 0, -3, SBReference.SUB1_COLOR);
		
		// Grip
		scrollGripLightColor.update(new Color(238, 243, 254), 0, 71, SBReference.SUB1_COLOR);
		scrollGripDarkColor.update(new Color(171, 185, 219), 0, -13, SBReference.SUB1_COLOR);
		
		// Buttons
		scrollButtColor.update(new Color(197, 213, 252), 0, 0, SBReference.SUB1_COLOR);
		scrollButtRolloverColor.update(new Color(226, 234, 254), 0, 50, SBReference.SUB1_COLOR);
		scrollButtPressedColor.update(new Color(187, 202, 239), 0, -5, SBReference.SUB1_COLOR);
		scrollButtDisabledColor.update(new Color(238, 237, 231), -48, 29, SBReference.BACK_COLOR);
		
		scrollSpreadLight.setValue(20);
		scrollSpreadDark.setValue(2);
		scrollSpreadLightDisabled.setValue(20);
		scrollSpreadDarkDisabled.setValue(1);
		
		// Arrow
		scrollArrowColor.update(new Color(77, 100, 132), -74, -18, SBReference.MAIN_COLOR);
		scrollArrowDisabledColor.update(new Color(193, 193, 193), -100, -15, SBReference.BACK_COLOR);
		
		// Border
		scrollBorderColor.update(new Color(212, 210, 194), 0, -10, SBReference.SUB1_COLOR);
		scrollBorderLightColor.update(new Color(255, 255, 255), 0, 100, SBReference.SUB1_COLOR);
		scrollBorderDisabledColor.update(new Color(232, 230, 220), -41, 0, SBReference.BACK_COLOR);
		scrollLightDisabledColor.update(new Color(232, 230, 220), -41, 0, SBReference.BACK_COLOR);
		
		// ScrollPane border
		scrollPaneBorderColor.update(new Color(201, 198, 184), 0, -15, SBReference.BACK_COLOR);
		
//		Tabbed
		tabPaneBorderColor.update(new Color(143, 160, 183), -78, 28, SBReference.MAIN_COLOR);
		tabNormalColor.update(new Color(242, 240, 238), 0, 69, SBReference.BACK_COLOR);
		tabSelectedColor.update(new Color(251, 251, 250), 0, 91, SBReference.BACK_COLOR);
		
		// since 1.3
		tabDisabledColor.update(new Color(244, 242, 232), 0, 40, SBReference.BACK_COLOR);
		tabDisabledSelectedColor.update(new Color(251, 251, 247), 0, 80, SBReference.BACK_COLOR);
		tabDisabledTextColor.update(new Color(188, 187, 185), 0, 40, SBReference.DIS_COLOR);
		// end since 1.3
		
		tabBorderColor.update(new Color(143, 160, 183), -78, 28, SBReference.MAIN_COLOR);
		tabRolloverColor.update(new Color(255, 199, 59), 0, 0, SBReference.SUB6_COLOR);

		tabRollover.setValue(true);
		
		// since 1.3.05
		tabFocus.setValue(true);
		
		ignoreSelectedBg.setValue(false);
		fixedTabs.setValue(true);
		
		tabInsets.top = 1;
		tabInsets.left = 6;
		tabInsets.bottom = 4;
		tabInsets.right = 6;
		
		tabAreaInsets.top = 4;
		tabAreaInsets.left = 2;
		tabAreaInsets.bottom = 0;
		tabAreaInsets.right = 0;
		
//		Slider
		sliderRolloverEnabled.setValue(true);
		
		// since 1.3.05
		sliderFocusEnabled.setValue(true);

		// Thumb
		sliderThumbColor.update(new Color(245, 244, 235), 0, 49, SBReference.BACK_COLOR);
		sliderThumbRolloverColor.update(new Color(233, 166, 0), 100, -26, SBReference.SUB6_COLOR);
		sliderThumbPressedColor.update(new Color(244, 243, 239), -50, 50, SBReference.BACK_COLOR);
		sliderThumbDisabledColor.update(new Color(245, 243, 234), 0, 45, SBReference.BACK_COLOR);
		
		// Border
		sliderBorderColor.update(new Color(176, 189, 207), -76, 50, SBReference.MAIN_COLOR);
		sliderDarkColor.update(new Color(119, 130, 146), -89, 4, SBReference.MAIN_COLOR);
		sliderLightColor.update(new Color(27, 155, 27), 16, -7, SBReference.SUB2_COLOR);
		sliderBorderDisabledColor.update(new Color(214, 212, 198), -6, -9, SBReference.BACK_COLOR);
		
		// Track
		sliderTrackColor.update(new Color(240, 237, 224), 0, 20, SBReference.BACK_COLOR);
		sliderTrackBorderColor.update(new Color(157, 156, 150), -53, -32, SBReference.BACK_COLOR);
		sliderTrackDarkColor.update(new Color(242, 241, 232), -22, 39, SBReference.BACK_COLOR);
		sliderTrackLightColor.update(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		
		// Ticks
		sliderTickColor.update(new Color(118, 117, 108), 0, -50, SBReference.BACK_COLOR);
		sliderTickDisabledColor.update(new Color(174, 174, 171), 0, 28, SBReference.DIS_COLOR);
		
		// since 1.3.05
		sliderFocusColor.update(new Color(113, 112, 104), 0, -52, SBReference.BACK_COLOR);
		
//		Spinner
		spinnerRollover.setValue(false);
		
		// Button
		spinnerButtColor.update(new Color(198, 213, 250), 0, 0, SBReference.SUB1_COLOR);
		spinnerButtRolloverColor.update(new Color(232, 238, 254), 0, 60, SBReference.SUB1_COLOR);
		spinnerButtPressedColor.update(new Color(175, 190, 224), 0, -11, SBReference.SUB1_COLOR);
		spinnerButtDisabledColor.update(new Color(242, 240, 228), 0, 30, SBReference.BACK_COLOR);
		
		spinnerSpreadLight.setValue(20);
		spinnerSpreadDark.setValue(3);
		spinnerSpreadLightDisabled.setValue(20);
		spinnerSpreadDarkDisabled.setValue(1);
		
		spinnerBorderColor.update(new Color(128, 152, 186), -70, 23, SBReference.MAIN_COLOR);
		spinnerBorderDisabledColor.update(new Color(215, 212, 197), 0, -9, SBReference.BACK_COLOR);
		spinnerArrowColor.update(new Color(77, 100, 132), -74, -18, SBReference.MAIN_COLOR);
		spinnerArrowDisabledColor.update(new Color(212, 210, 194), 0, -10, SBReference.BACK_COLOR);
		
//		Combo
		comboBorderColor.update(new Color(128, 152, 186), -70, 23, SBReference.MAIN_COLOR);
		comboBorderDisabledColor.update(new Color(201, 198, 184), 0, -15, SBReference.BACK_COLOR);
		comboSelectedBgColor.update(new Color(43, 107, 197), -36, -6, SBReference.MAIN_COLOR);
		comboSelectedTextColor.update(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		comboFocusBgColor.update(new Color(43, 107, 197), 0, 0, SBReference.ABS_COLOR);
		comboBgColor.update(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		comboTextColor.update(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		
		// Button
		comboButtColor.update(new Color(197, 213, 252), 0, 0, SBReference.SUB1_COLOR);
		comboButtRolloverColor.update(new Color(226, 234, 254), 0, 50, SBReference.SUB1_COLOR);
		comboButtPressedColor.update(new Color(175, 190, 224), 0, -11, SBReference.SUB1_COLOR);
		comboButtDisabledColor.update(new Color(238, 237, 231), -48, 29, SBReference.BACK_COLOR);
		
		comboSpreadLight.setValue(20);
		comboSpreadDark.setValue(3);
		comboSpreadLightDisabled.setValue(20);
		comboSpreadDarkDisabled.setValue(1);
		
		// Button Border
		comboButtBorderColor.update(new Color(212, 210, 194), 0, -10, SBReference.SUB1_COLOR);
		comboButtBorderDisabledColor.update(new Color(232, 230, 220), -41, 0, SBReference.BACK_COLOR);
		
		// Arrow
		comboArrowColor.update(new Color(77, 100, 132), -74, -18, SBReference.MAIN_COLOR);
		comboArrowDisabledColor.update(new Color(203, 200, 186), 0, -14, SBReference.BACK_COLOR);

		comboInsets.top = 2;
		comboInsets.left = 2;
		comboInsets.bottom = 2;
		comboInsets.right = 2;
		
		comboRollover.setValue(false);
		comboFocus.setValue(false);
		
//		Menu
		menuBarColor.update(new Color(238, 237, 230), -43, 28, SBReference.BACK_COLOR);
		menuItemSelectedTextColor.update(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		menuPopupColor.update(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		menuRolloverBgColor.update(new Color(189, 208, 234), -50, 66, SBReference.MAIN_COLOR);
		menuRolloverFgColor.update(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		menuDisabledFgColor.update(new Color(143, 142, 139), 0, 0, SBReference.DIS_COLOR);
		menuItemDisabledFgColor.update(new Color(143, 142, 139), 0, 0, SBReference.DIS_COLOR);
		menuItemRolloverColor.update(new Color(189, 208, 234), -50, 66, SBReference.MAIN_COLOR);
		menuBorderColor.update(new Color(173, 170, 153), 4, -28, SBReference.BACK_COLOR);
		menuInnerHilightColor.update(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		menuInnerShadowColor.update(new Color(213, 212, 207), -70, -7, SBReference.BACK_COLOR);
		menuOuterHilightColor.update(new Color(173, 170, 153), 4, -28, SBReference.BACK_COLOR);
		menuOuterShadowColor.update(new Color(173, 170, 153), 4, -28, SBReference.BACK_COLOR);
		menuIconColor.update(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		menuIconRolloverColor.update(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		menuIconDisabledColor.update(new Color(165, 163, 151), 0, -30, SBReference.BACK_COLOR);
		menuSeparatorColor.update(new Color(173, 170, 153), 4, -28, SBReference.BACK_COLOR);
		
		menuRollover.setValue(true);
		menuPopupShadow.setValue(false);
		menuAllowTwoIcons.setValue(false);
		
//		Toolbar
		toolBarColor.update(new Color(239, 237, 229), -35, 28, SBReference.BACK_COLOR);
		toolBarLightColor.update(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		toolBarDarkColor.update(new Color(214, 210, 187), 10, -11, SBReference.BACK_COLOR);
		toolButtColor.update(new Color(239, 237, 229), -35, 28, SBReference.BACK_COLOR);
		toolButtSelectedColor.update(new Color(243, 242, 239), -51, 52, SBReference.BACK_COLOR);
		toolButtRolloverColor.update(new Color(251, 251, 248), -30, 81, SBReference.BACK_COLOR);
		toolButtPressedColor.update(new Color(225, 224, 218), -58, -2, SBReference.BACK_COLOR);
		toolGripDarkColor.update(new Color(167, 167, 163), -70, -27, SBReference.BACK_COLOR);
		toolGripLightColor.update(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		toolSeparatorColor.update(new Color(167, 167, 163), -70, -27, SBReference.BACK_COLOR);
		toolBorderColor.update(new Color(239, 237, 229), -35, 28, SBReference.BACK_COLOR);
		toolBorderPressedColor.update(new Color(122, 144, 174), -76, 16, SBReference.MAIN_COLOR);
		toolBorderRolloverColor.update(new Color(122, 144, 174), -76, 16, SBReference.MAIN_COLOR);
		toolBorderSelectedColor.update(new Color(122, 144, 174), -76, 16, SBReference.MAIN_COLOR);
		
		toolMargin.top = 5;
		toolMargin.left = 5;
		toolMargin.bottom = 5;
		toolMargin.right = 5;
		
		toolFocus.setValue(false);
		
		// (!) not adjustable
		toolRollover.setValue(true);
		
//		List
		listBgColor.update(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		listTextColor.update(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		listSelectedBgColor.update(new Color(43, 107, 197), -36, -6, SBReference.MAIN_COLOR);
		listSelectedTextColor.update(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		listFocusBorderColor.update(new Color(179, 211, 255), 100, 70, SBReference.MAIN_COLOR);
		
//		Tree
		treeBgColor.update(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		treeTextColor.update(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		treeTextBgColor.update(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		treeSelectedTextColor.update(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		treeSelectedBgColor.update(new Color(43, 107, 197), -36, -6, SBReference.MAIN_COLOR);
		treeLineColor.update(new Color(208, 205, 190), 0, -12, SBReference.BACK_COLOR);
		
//		Frame
		frameCaptionColor.update(new Color(13, 94, 255), 0, 5, SBReference.FRAME_COLOR);
		frameCaptionDisabledColor.update(new Color(122, 159, 223), -25, 41, SBReference.FRAME_COLOR);
		frameBorderColor.update(new Color(0, 60, 161), 0, -30, SBReference.FRAME_COLOR);
		frameLightColor.update(new Color(0, 68, 184), 0, -20, SBReference.FRAME_COLOR);
		frameBorderDisabledColor.update(new Color(74, 125, 212), -25, 20, SBReference.FRAME_COLOR);
		frameLightDisabledColor.update(new Color(99, 144, 233), -25, 30, SBReference.FRAME_COLOR);
		frameTitleColor.update(new Color(255, 255, 255), 0, 100, SBReference.FRAME_COLOR);
		frameTitleShadowColor.update(new Color(0, 43, 128), 0, -50, SBReference.FRAME_COLOR);
		frameTitleDisabledColor.update(new Color(216, 226, 248), -29, 82, SBReference.FRAME_COLOR);
		
		// Button
		frameButtColor.update(new Color(13, 94, 255), 0, 5, SBReference.FRAME_COLOR);
		frameButtRolloverColor.update(new Color(51, 119, 255), 0, 20, SBReference.FRAME_COLOR);
		frameButtPressedColor.update(new Color(0, 68, 204), 0, -20, SBReference.FRAME_COLOR);
		frameButtDisabledColor.update(new Color(63, 120, 233), -21, 16, SBReference.FRAME_COLOR);
		
		frameButtSpreadLight.setValue(8);
		frameButtSpreadDark.setValue(2);
		frameButtSpreadLightDisabled.setValue(5);
		frameButtSpreadDarkDisabled.setValue(2);
		
		frameButtCloseColor.update(new Color(227, 92, 60), 0, 0, SBReference.SUB4_COLOR);
		frameButtCloseRolloverColor.update(new Color(233, 125, 99), 0, 20, SBReference.SUB4_COLOR);
		frameButtClosePressedColor.update(new Color(193, 78, 51), 0, -15, SBReference.SUB4_COLOR);
		frameButtCloseDisabledColor.update(new Color(175, 105, 125), 0, 0, SBReference.SUB7_COLOR);
		
		frameButtCloseSpreadLight.setValue(8);
		frameButtCloseSpreadDark.setValue(2);
		frameButtCloseSpreadLightDisabled.setValue(5);
		frameButtCloseSpreadDarkDisabled.setValue(2);
		
		// Button Border
		frameButtBorderColor.update(new Color(255, 255, 255), 0, 100, SBReference.FRAME_COLOR);
		frameButtBorderDisabledColor.update(new Color(190, 206, 238), -42, 68, SBReference.FRAME_COLOR);
		
		// Symbol
		frameSymbolColor.update(new Color(255, 255, 255), 0, 100, SBReference.FRAME_COLOR);
		frameSymbolPressedColor.update(new Color(255, 255, 255), 0, 100, SBReference.FRAME_COLOR);
		frameSymbolDisabledColor.update(new Color(255, 255, 255), 0, 100, SBReference.FRAME_COLOR);
		frameSymbolDarkColor.update(new Color(255, 255, 255), 0, 100, SBReference.FRAME_COLOR);
		frameSymbolLightColor.update(new Color(13, 94, 255), 0, 5, SBReference.FRAME_COLOR);
		frameSymbolDarkDisabledColor.update(new Color(255, 255, 255), 0, 100, SBReference.FRAME_COLOR);
		frameSymbolLightDisabledColor.update(new Color(63, 120, 233), -21, 16, SBReference.FRAME_COLOR);
		
		// Close Button
		frameButtCloseBorderColor.update(new Color(255, 255, 255), 0, 100, SBReference.FRAME_COLOR);
		frameButtCloseDarkColor.update(new Color(174, 51, 20), 50, -32, SBReference.SUB4_COLOR);
		frameButtCloseLightColor.update(new Color(226, 88, 55), 11, -2, SBReference.SUB4_COLOR);
		frameButtCloseBorderDisabledColor.update(new Color(190, 206, 238), -42, 68, SBReference.FRAME_COLOR);

		// Close Symbol
		frameSymbolCloseColor.update(new Color(255, 255, 255), 0, 100, SBReference.FRAME_COLOR);
		frameSymbolClosePressedColor.update(new Color(231, 180, 168), -24, 50, SBReference.SUB4_COLOR);
		frameSymbolCloseDisabledColor.update(new Color(255, 255, 255), 0, 100, SBReference.FRAME_COLOR);
		frameSymbolCloseDarkColor.update(new Color(227, 92, 60), 0, 0, SBReference.SUB4_COLOR);
		frameSymbolCloseDarkDisabledColor.update(new Color(175, 105, 125), 0, 0, SBReference.SUB7_COLOR);

		frameSpreadDark.setValue(3);
		frameSpreadLight.setValue(2);
		frameSpreadDarkDisabled.setValue(2);
		frameSpreadLightDisabled.setValue(2);
		
//		Table
		tableBackColor.update(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		tableHeaderBackColor.update(new Color(236, 233, 216), 0, 0, SBReference.BACK_COLOR);
		tableHeaderRolloverBackColor.update(new Color(249, 248, 243), 0, 70, SBReference.BACK_COLOR);
		tableHeaderRolloverColor.update(new Color(248, 179, 48), 0, 0, SBReference.SUB6_COLOR);
		tableGridColor.update(new Color(167, 166, 160), -50, -28, SBReference.BACK_COLOR);
		tableHeaderArrowColor.update(new Color(167, 166, 160), -50, -28, SBReference.BACK_COLOR);
		tableSelectedBackColor.update(new Color(213, 211, 204), -50, -8, SBReference.BACK_COLOR);
		tableSelectedForeColor.update(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		tableBorderDarkColor.update(new Color(167, 166, 160), -50, -28, SBReference.BACK_COLOR);
		tableBorderLightColor.update(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		tableHeaderDarkColor.update(new Color(189, 186, 173), 0, -20, SBReference.BACK_COLOR);
		tableHeaderLightColor.update(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		tableFocusBorderColor.update(new Color(185, 184, 177), -50, -20, SBReference.BACK_COLOR);
		// New in 1.4.0 - Same as tableBackColor by default
		tableAlternateRowColor.update(new Color(255, 255, 255), 0, 100, SBReference.BACK_COLOR);
		
//		Icons
		for(int i = 0; i < 20; i++) {
			colorizer[i].setHue(hue);
			colorizer[i].setSaturation(25);
			colorizer[i].setBrightness(0);
			colorizer[i].setReference(HSBReference.BACK_COLOR);
			colorize[i].setValue(false);
		}
		
//		Separator
		separatorColor.update(new Color(167, 167, 163), -70, -27, SBReference.BACK_COLOR);
		
//		ToolTip
		tipBorderColor.update(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		tipBorderDis.update(new Color(143, 141, 139), 0, 0, SBReference.DIS_COLOR);
		tipBgColor.update(new Color(255, 255, 225), 0, 0, SBReference.ABS_COLOR);
		tipBgDis.update(new Color(236, 233, 216), 0, 0, SBReference.BACK_COLOR);
		tipTextColor.update(new Color(0, 0, 0), 0, -100, SBReference.BACK_COLOR);
		tipTextDis.update(new Color(143, 141, 139), 0, 0, SBReference.DIS_COLOR);
		
//		Misc
		titledBorderColor.update(new Color(165, 163, 151), 0, -30, SBReference.BACK_COLOR);
		splitPaneButtonColor.update(new Color(170, 168, 156), 0, -28, SBReference.BACK_COLOR);
	}
	
	/**
	 * Loads a theme from the specified file.
	 * (See the notes on <a href="#themeswitching">Theme Switching</a>).
	 * 
	 * @param f a non-null file specifying a TinyLaF theme
	 * @return <code>true</code> if the theme was successfully loaded,
	 * <code>false</code> otherwise. If <code>false</code> is returned
	 * then {@link #errorCode} is set to a value other than <code>ERROR_NONE</code>.
	 */
	public static boolean loadTheme(File f) {
		errorCode = ERROR_NONE;
		
		if(f == null) {
			errorCode = ERROR_NULL_ARGUMENT;
			return false;
		}
		
		try {
			return loadTheme(new FileInputStream(f));
		}
		catch(FileNotFoundException ex) {
			System.out.println("Theme.loadTheme(File) : " + ex);
			errorCode = ERROR_FILE_NOT_FOUND;
		}
		catch(IOException ex) {
			System.out.println("Theme.loadTheme(File) : " + ex);
			errorCode = ERROR_IO_EXCEPTION;
		}
		
		return false;
	}
	
	/**
	 * Loads the specified theme.
	 * (See the notes on <a href="#themeswitching">Theme Switching</a>).
	 * 
	 * @param td a non-null ThemeDescription
	 * @return <code>true</code> if the theme was successfully loaded,
	 * <code>false</code> otherwise. If <code>false</code> is returned
	 * then {@link #errorCode} is set to a value other than <code>ERROR_NONE</code>.
	 * 
	 */
	public static boolean loadTheme(ThemeDescription td) {
		if(td == null) {
			errorCode = ERROR_NULL_ARGUMENT;
			return false;
		}
		
		if(td.isValid()) {
			return loadTheme(td.getURL());
		}
		else {
			errorCode = ERROR_INVALID_THEME_DESCRIPTION;
			return false;
		}
	}
	
	/**
	 * Loads a theme from the specified URL.
	 * (See the notes on <a href="#themeswitching">Theme Switching</a>).
	 * 
	 * @param url a non-null URL specifying a TinyLaF theme
	 * @return <code>true</code> if the theme was successfully loaded,
	 * <code>false</code> otherwise. If <code>false</code> is returned
	 * then {@link #errorCode} is set to a value other than <code>ERROR_NONE</code>.
	 * 
	 */
	public static boolean loadTheme(URL url) {
		errorCode = ERROR_NONE;

		if(url == null) {
			errorCode = ERROR_NULL_ARGUMENT;
			return false;
		}
		
		if(YQ_URL != null && url.equals(YQ_URL)) {
			loadYQTheme();
			return true;
		}
		
		try {
			return loadTheme(url.openStream());
		}
		catch(FileNotFoundException ex) {
			// Because we end here if search for 'Default.theme'
			// fails, we don't print error msg.
			errorCode = ERROR_FILE_NOT_FOUND;
			
		}
		catch(IOException ex) {
			System.out.println("Theme.loadTheme(URL) : " + ex);
			errorCode = ERROR_IO_EXCEPTION;
		}
		
		return false;
	}
	
	private static boolean loadTheme(InputStream istream) throws IOException {
		DataInputStream in = null;
		
		try {
			in = new DataInputStream(
				new BufferedInputStream(istream));
			
			fileID = in.readInt();
			
			if(fileID != FILE_ID_3A &&
				fileID != FILE_ID_3B &&
				fileID != FILE_ID_3C &&
				fileID != FILE_ID_3D &&
				fileID != FILE_ID_3E &&
				fileID != FILE_ID_3F &&
				fileID != FILE_ID_4 &&
				fileID != FILE_ID_4B &&
				fileID != FILE_ID_4C &&
				fileID != FILE_ID_4D &&
				fileID != FILE_ID_4E &&
				fileID != FILE_ID_2 &&
				fileID != FILE_ID_1)
			{
				errorCode = ERROR_NO_TINYLAF_THEME;
				return false;
			}
			
//			System.out.println("fileID=" + Integer.toHexString(fileID));
			
			int derivedStyle = in.readInt();
			
			if(derivedStyle != YQ_STYLE) {
				errorCode = ERROR_WIN99_STYLE;
				return false;
			}
			
//			Colors
			mainColor.load(in);
			disColor.load(in);
			backColor.load(in);
			frameColor.load(in);
			sub1Color.load(in);
			sub2Color.load(in);
			sub3Color.load(in);
			sub4Color.load(in);
			sub5Color.load(in);
			sub6Color.load(in);
			sub7Color.load(in);
			sub8Color.load(in);
			
//			Font
			plainFont.load(in);
			boldFont.load(in);
			buttonFont.load(in);
			passwordFont.load(in);
			labelFont.load(in);
			comboFont.load(in);
			
			if(fileID == FILE_ID_1) {	// 1.0
				ColoredFont.loadDummyData(in);
			}
			
			listFont.load(in);
			menuFont.load(in);
			menuItemFont.load(in);
			radioFont.load(in);
			checkFont.load(in);
			tableFont.load(in);
			tableHeaderFont.load(in);
			textAreaFont.load(in);
			textFieldFont.load(in);
			textPaneFont.load(in);
			titledBorderFont.load(in);
			toolTipFont.load(in);
			treeFont.load(in);
			tabFont.load(in);
			editorFont.load(in);
			frameTitleFont.load(in);

			if(fileID >= FILE_ID_3A) {
				internalFrameTitleFont.load(in);
				internalPaletteTitleFont.load(in);
			}
			
			if(fileID != FILE_ID_1) {	// not for 1.0
				progressBarFont.load(in);
			}
			
			buttonFontColor.load(in);
			buttonFont.setSBReference(buttonFontColor);
			labelFontColor.load(in);
			labelFont.setSBReference(labelFontColor);
			menuFontColor.load(in);
			menuFont.setSBReference(menuFontColor);
			menuItemFontColor.load(in);
			menuItemFont.setSBReference(menuItemFontColor);
			radioFontColor.load(in);
			radioFont.setSBReference(radioFontColor);
			checkFontColor.load(in);
			checkFont.setSBReference(checkFontColor);
			tableFontColor.load(in);
			tableFont.setSBReference(tableFontColor);
			tableHeaderFontColor.load(in);
			tableHeaderFont.setSBReference(tableHeaderFontColor);
			tabFontColor.load(in);
			tabFont.setSBReference(tabFontColor);
			titledBorderFontColor.load(in);
			titledBorderFont.setSBReference(titledBorderFontColor);
			
			if(fileID < FILE_ID_3C) {
				SBReference.loadDummyData(in);
			}
			

//			Progressbar
			progressColor.load(in);
			progressTrackColor.load(in);
			progressBorderColor.load(in);
			progressDarkColor.load(in);
			progressLightColor.load(in);
			
			if(fileID != FILE_ID_1) {	// not 1.0
				progressSelectForeColor.load(in);
				progressSelectBackColor.load(in);
			}
			

//			Text	
			textBgColor.load(in);
			textTextColor.load(in);
			
			if(fileID >= FILE_ID_3A) {
				textCaretColor.load(in);
				editorPaneBgColor.load(in);
				textPaneBgColor.load(in);
				desktopPaneBgColor.load(in);
			}

			textSelectedBgColor.load(in);
			textSelectedTextColor.load(in);
			textDisabledBgColor.load(in);
			
			if(fileID < FILE_ID_4) {
				textNonEditableBgColor.update(textDisabledBgColor);
			}
			else {
				textNonEditableBgColor.load(in);
			}
			
			textBorderColor.load(in);
//			textBorderDarkColor.load(in);
//			textBorderLightColor.load(in);
			if(fileID < FILE_ID_4) {
				SBReference.loadDummyData(in);	// 99 only
				SBReference.loadDummyData(in);	// 99 only
			}
			textBorderDisabledColor.load(in);
//			textBorderDarkDisabledColor.load(in);
//			textBorderLightDisabledColor.load(in);
			if(fileID < FILE_ID_4) {
				SBReference.loadDummyData(in);	// 99 only
				SBReference.loadDummyData(in);	// 99 only
			}
			
			textInsets.top = in.readInt();
			textInsets.left = in.readInt();
			textInsets.bottom = in.readInt();
			textInsets.right = in.readInt();
			
//			Button    	
			buttonRolloverBorder.setValue(in.readBoolean());
			buttonFocus.setValue(in.readBoolean());
			
			if(fileID >= FILE_ID_3A) {
				buttonFocusBorder.setValue(in.readBoolean());
				buttonEnter.setValue(in.readBoolean());
			}
			
			if(fileID >= FILE_ID_3D) {
				shiftButtonText.setValue(in.readBoolean());
			}
			
			buttonNormalColor.load(in);
			buttonRolloverBgColor.load(in);
			buttonPressedColor.load(in);
			buttonDisabledColor.load(in);
			buttonBorderColor.load(in);
//			buttonDarkColor.load(in);
//			buttonLightColor.load(in);
			if(fileID < FILE_ID_4) {
				SBReference.loadDummyData(in);	// 99 only
				SBReference.loadDummyData(in);	// 99 only
			}
			buttonBorderDisabledColor.load(in);
//			buttonDarkDisabledColor.load(in);
//			buttonLightDisabledColor.load(in);
			if(fileID < FILE_ID_4) {
				SBReference.loadDummyData(in);	// 99 only
				SBReference.loadDummyData(in);	// 99 only
			}
			
			buttonMargin.top = in.readInt();
			buttonMargin.left = in.readInt();
			buttonMargin.bottom = in.readInt();
			buttonMargin.right = in.readInt();
			
			if(fileID >= FILE_ID_3B) {
				checkMargin.top = in.readInt();
				checkMargin.left = in.readInt();
				checkMargin.bottom = in.readInt();
				checkMargin.right = in.readInt();
			}
			else {
				checkMargin.top = 2;
				checkMargin.left = 2;
				checkMargin.bottom = 2;
				checkMargin.right = 2;
			}
			
			buttonRolloverColor.load(in);
			buttonDefaultColor.load(in);
			buttonCheckColor.load(in);
			buttonCheckDisabledColor.load(in);
			buttonDisabledFgColor.load(in);
			checkDisabledFgColor.load(in);
			radioDisabledFgColor.load(in);
			
			if(fileID >= FILE_ID_4B) {
				toggleSelectedBg.load(in);
			}
			else {
				toggleSelectedBg.update(buttonPressedColor);
			}
			
			buttonSpreadLight.setValue(in.readInt());
			buttonSpreadDark.setValue(in.readInt());
			buttonSpreadLightDisabled.setValue(in.readInt());
			buttonSpreadDarkDisabled.setValue(in.readInt());
			
			if(fileID < FILE_ID_3A) {
				// because I added (2, 2, 2, 2) insets for the border,
				// subtract it here
				buttonMargin.top = Math.max(0, buttonMargin.top - 2);
				buttonMargin.left = Math.max(0, buttonMargin.left - 2);
				buttonMargin.bottom = Math.max(0, buttonMargin.bottom - 2);
				buttonMargin.right = Math.max(0, buttonMargin.right - 2);
			}
			
//			Scrollbar
			scrollRollover.setValue(in.readBoolean());
			
			if(fileID >= FILE_ID_4D) {
				scrollSize.setValue(in.readInt());
			}
			else {
				scrollSize.setValue(DEFAULT_SCROLL_SIZE);
			}
			
			scrollTrackColor.load(in);
			scrollTrackDisabledColor.load(in);
			scrollTrackBorderColor.load(in);
			scrollTrackBorderDisabledColor.load(in);
			
			// Thumb
			scrollThumbColor.load(in);
			scrollThumbRolloverColor.load(in);
			scrollThumbPressedColor.load(in);
			scrollThumbDisabledColor.load(in);
			
			// Grip
			scrollGripLightColor.load(in);
			scrollGripDarkColor.load(in);
			
			// Buttons
			scrollButtColor.load(in);
			scrollButtRolloverColor.load(in);
			scrollButtPressedColor.load(in);
			scrollButtDisabledColor.load(in);
			
			scrollSpreadLight.setValue(in.readInt());
			scrollSpreadDark.setValue(in.readInt());
			scrollSpreadLightDisabled.setValue(in.readInt());
			scrollSpreadDarkDisabled.setValue(in.readInt());
			
			// Arrow
			scrollArrowColor.load(in);
			scrollArrowDisabledColor.load(in);
			
			// Border
			scrollBorderColor.load(in);
//			scrollDarkColor.load(in);
			if(fileID < FILE_ID_4) {
				SBReference.loadDummyData(in);
			}
			scrollBorderLightColor.load(in);
			scrollBorderDisabledColor.load(in);
//			scrollDarkDisabledColor.load(in);
			if(fileID < FILE_ID_4) {
				SBReference.loadDummyData(in);
			}
			scrollLightDisabledColor.load(in);
			
			// ScrollPane border
			scrollPaneBorderColor.load(in);
			
//			Tabbed
			tabPaneBorderColor.load(in);
//			tabPaneDarkColor.load(in);
//			tabPaneLightColor.load(in);
			if(fileID < FILE_ID_4) {
				SBReference.loadDummyData(in);
				SBReference.loadDummyData(in);
			}
			tabNormalColor.load(in);
			tabSelectedColor.load(in);
			
			if(fileID >= FILE_ID_3A) {
				tabDisabledColor.load(in);
				tabDisabledSelectedColor.load(in);
				tabDisabledTextColor.load(in);
			}

			tabBorderColor.load(in);
//			tabDarkColor.load(in);
//			tabLightColor.load(in);
			if(fileID < FILE_ID_4) {
				SBReference.loadDummyData(in);
				SBReference.loadDummyData(in);
			}
			
			tabRolloverColor.load(in);
			
			if(fileID >= FILE_ID_4) {
				tabDisabledBorderColor.load(in);
				tabPaneDisabledBorderColor.load(in);
			}
			else {
				tabDisabledBorderColor.update(tabBorderColor);
				tabPaneDisabledBorderColor.update(tabPaneBorderColor);
			}
			
			int leftInset = -1;
			if(fileID < FILE_ID_3A) {
				leftInset = in.readInt();	// was firstTabDistance
			}
			
			tabRollover.setValue(in.readBoolean());
			
			if(fileID >= FILE_ID_3E) {
				tabFocus.setValue(in.readBoolean());
			}
			else {
				tabFocus.setValue(true);
			}
			
			ignoreSelectedBg.setValue(in.readBoolean()); // was tabFocus
			
			if(fileID >= FILE_ID_3C) {
				fixedTabs.setValue(in.readBoolean());
			}
			
			if(fileID < FILE_ID_3A) {
				in.readInt();	// was tabContentBorderInsets
				in.readInt();
				in.readInt();
				in.readInt();
			}
			
			if(fileID >= FILE_ID_3A) {
				tabInsets.top = in.readInt();
				tabInsets.left = in.readInt();
				tabInsets.bottom = in.readInt();
				tabInsets.right = in.readInt();

				tabAreaInsets.top = in.readInt();
				tabAreaInsets.left = in.readInt();
				tabAreaInsets.bottom = in.readInt();
				tabAreaInsets.right = in.readInt();

				if(leftInset > -1) {
					tabAreaInsets.left = leftInset;
				}
			}
			
//			Slider
			sliderRolloverEnabled.setValue(in.readBoolean());
			
			if(fileID >= FILE_ID_3E) {
				sliderFocusEnabled.setValue(in.readBoolean());
			}
			else {
				sliderFocusEnabled.setValue(true);
			}
			
			// Thumb
			sliderThumbColor.load(in);
			sliderThumbRolloverColor.load(in);
			sliderThumbPressedColor.load(in);
			sliderThumbDisabledColor.load(in);
			sliderBorderColor.load(in);
			sliderDarkColor.load(in);
			sliderLightColor.load(in);
			
			if(fileID < FILE_ID_3A) {
				sliderLightColor.update(buttonCheckColor);
			}
			
			sliderBorderDisabledColor.load(in);
			
//			sliderDarkDisabledColor.load(in);
//			sliderLightDisabledColor.load(in);
			if(fileID < FILE_ID_4) {
				SBReference.loadDummyData(in);
				SBReference.loadDummyData(in);
			}
			
			sliderTrackColor.load(in);
			sliderTrackBorderColor.load(in);
			sliderTrackDarkColor.load(in);
			sliderTrackLightColor.load(in);
			sliderTickColor.load(in);
			sliderTickDisabledColor.load(in);
			
			if(fileID >= FILE_ID_3E) {
				sliderFocusColor.load(in);
			}
			
//			Spinner
			spinnerRollover.setValue(in.readBoolean());
			
			// Button
			spinnerButtColor.load(in);
			spinnerButtRolloverColor.load(in);
			spinnerButtPressedColor.load(in);
			spinnerButtDisabledColor.load(in);
			
			spinnerSpreadLight.setValue(in.readInt());
			spinnerSpreadDark.setValue(in.readInt());
			spinnerSpreadLightDisabled.setValue(in.readInt());
			spinnerSpreadDarkDisabled.setValue(in.readInt());
			
			spinnerBorderColor.load(in);
//			spinnerDarkColor.load(in);
//			spinnerLightColor.load(in);
			if(fileID < FILE_ID_4) {
				SBReference.loadDummyData(in);
				SBReference.loadDummyData(in);
			}
			spinnerBorderDisabledColor.load(in);
//			spinnerDarkDisabledColor.load(in);
//			spinnerLightDisabledColor.load(in);
			if(fileID < FILE_ID_4) {
				SBReference.loadDummyData(in);
				SBReference.loadDummyData(in);
			}
			
			// Arrow
			spinnerArrowColor.load(in);
			spinnerArrowDisabledColor.load(in);
			
//			Combo
			comboBorderColor.load(in);
//			comboDarkColor.load(in);
//			comboLightColor.load(in);
			if(fileID < FILE_ID_4) {
				SBReference.loadDummyData(in);
				SBReference.loadDummyData(in);
			}
			comboBorderDisabledColor.load(in);
//			comboDarkDisabledColor.load(in);
//			comboLightDisabledColor.load(in);
			if(fileID < FILE_ID_4) {
				SBReference.loadDummyData(in);
				SBReference.loadDummyData(in);
			}
			
			comboSelectedBgColor.load(in);
			comboSelectedTextColor.load(in);
			comboFocusBgColor.load(in);
			
			if(fileID >= FILE_ID_3A) {
				comboBgColor.load(in);
				comboTextColor.load(in);
			}
			else {
				comboBgColor.update(textBgColor);
				comboTextColor.update(textTextColor);
			}
			
			// Button
			comboButtColor.load(in);
			comboButtRolloverColor.load(in);
			comboButtPressedColor.load(in);
			comboButtDisabledColor.load(in);
			
			comboSpreadLight.setValue(in.readInt());
			comboSpreadDark.setValue(in.readInt());
			comboSpreadLightDisabled.setValue(in.readInt());
			comboSpreadDarkDisabled.setValue(in.readInt());
			
			// Button Border
			comboButtBorderColor.load(in);
//			comboButtDarkColor.load(in);
//			comboButtLightColor.load(in);
			if(fileID < FILE_ID_4) {
				SBReference.loadDummyData(in);
				SBReference.loadDummyData(in);
			}
			comboButtBorderDisabledColor.load(in);
//			comboButtDarkDisabledColor.load(in);
//			comboButtLightDisabledColor.load(in);
			if(fileID < FILE_ID_4) {
				SBReference.loadDummyData(in);
				SBReference.loadDummyData(in);
			}
			
			// Arrow
			comboArrowColor.load(in);
			comboArrowDisabledColor.load(in);
			
			comboInsets.top = in.readInt();
			comboInsets.left = in.readInt();
			comboInsets.bottom = in.readInt();
			comboInsets.right = in.readInt();
			
			comboRollover.setValue(in.readBoolean());
			comboFocus.setValue(in.readBoolean());
			
//			Menu
			menuBarColor.load(in);
			menuItemSelectedTextColor.load(in);
			menuPopupColor.load(in);
			menuRolloverBgColor.load(in);
			menuItemRolloverColor.load(in);
			menuBorderColor.load(in);
//			menuDarkColor.load(in);
//			menuLightColor.load(in);
			if(fileID < FILE_ID_4) {
				SBReference.loadDummyData(in);
				SBReference.loadDummyData(in);
			}

			menuIconColor.load(in);
			menuIconRolloverColor.load(in);
			menuIconDisabledColor.load(in);
//			menuIconShadowColor.load(in);
			if(fileID < FILE_ID_4) {
				SBReference.loadDummyData(in);
			}
			
			menuSeparatorColor.load(in);
//			menuSepLightColor.load(in);
			if(fileID < FILE_ID_4) {
				SBReference.loadDummyData(in);
				in.readInt();	// Menu
				in.readInt();	// border
				in.readInt();	// insets
				in.readInt();	// ...
			}
//			menuBorderInsets = new InsetsUIResource(
//			in.readInt(),
//			in.readInt(),
//			in.readInt(),
//			in.readInt());

			menuRollover.setValue(in.readBoolean());
			
			if(fileID >= FILE_ID_3A) {
				menuInnerHilightColor.load(in);
				menuInnerShadowColor.load(in);
				menuOuterHilightColor.load(in);
				menuOuterShadowColor.load(in);
				menuRolloverFgColor.load(in);
				menuDisabledFgColor.load(in);
			}
			else {
				menuRolloverFgColor.update(menuFont.getSBReference());
				menuDisabledFgColor.update(buttonDisabledFgColor);
			}
			
			if(fileID >= FILE_ID_4) {
				menuItemDisabledFgColor.load(in);
			}
			else {
				menuItemDisabledFgColor.update(menuDisabledFgColor);
			}
			
			// New in 1,4,0
			if(fileID > FILE_ID_4B) {
				menuPopupShadow.setValue(in.readBoolean());
				menuAllowTwoIcons.setValue(in.readBoolean());
			}
			else {
				menuPopupShadow.setValue(false);
				menuAllowTwoIcons.setValue(false);
			}

//			Toolbar
			toolBarColor.load(in);
			toolBarLightColor.load(in);
			toolBarDarkColor.load(in);
			
			if(fileID >= FILE_ID_3A) {
				toolButtColor.load(in);
				toolButtRolloverColor.load(in);
				toolButtPressedColor.load(in);
				toolButtSelectedColor.load(in);
			}
			else {
				toolButtSelectedColor.load(in);	// Note:
				toolButtRolloverColor.load(in);	// order differs
				toolButtPressedColor.load(in);	// from 1.3
				toolButtColor.update(toolButtSelectedColor);
			}
			
//			toolBorderDarkColor.load(in);
//			toolBorderLightColor.load(in);
			if(fileID < FILE_ID_4) {
				SBReference.loadDummyData(in);
				SBReference.loadDummyData(in);
			}
			toolBorderColor.load(in);
			if(fileID >= FILE_ID_3A) {
				toolBorderRolloverColor.load(in);
			}
			else {
				toolBorderRolloverColor.update(toolBorderColor);
			}
			toolBorderPressedColor.load(in);
			toolBorderSelectedColor.load(in);
			
			toolRollover.setValue(in.readBoolean());
			toolFocus.setValue(in.readBoolean());
			
			if(fileID >= FILE_ID_3A) {
				toolGripDarkColor.load(in);
				toolGripLightColor.load(in);
				toolSeparatorColor.load(in);
//				toolSepLightColor.load(in);
				if(fileID < FILE_ID_4) {
					SBReference.loadDummyData(in);
				}
				
				toolMargin.top = in.readInt();
				toolMargin.left = in.readInt();
				toolMargin.bottom = in.readInt();
				toolMargin.right = in.readInt();
			}
			else {
				toolMargin.top = 5;
				toolMargin.left = 5;
				toolMargin.bottom = 5;
				toolMargin.right = 5;
			}
			
//			List
			listSelectedBgColor.load(in);
			listSelectedTextColor.load(in);
			if(fileID >= FILE_ID_3A) {
				listBgColor.load(in);
				listTextColor.load(in);
			}
			
			if(fileID >= FILE_ID_4) {
				listFocusBorderColor.load(in);
			}
			else {
				// pre 1.4.0 default color
				listFocusBorderColor.update(new Color(213, 211, 209), 0, 0, SBReference.ABS_COLOR);
			}

//			Tree
			treeBgColor.load(in);
			treeTextColor.load(in);
			treeTextBgColor.load(in);
			treeSelectedTextColor.load(in);
			treeSelectedBgColor.load(in);
			
			if(fileID >= FILE_ID_3A) {
				treeLineColor.load(in);
			}
			
//			Frame
			frameCaptionColor.load(in);
			frameCaptionDisabledColor.load(in);
			frameBorderColor.load(in);
//			frameDarkColor.load(in);
			if(fileID < FILE_ID_4) {
				SBReference.loadDummyData(in);
			}
			frameLightColor.load(in);
			frameBorderDisabledColor.load(in);
//			frameDarkDisabledColor.load(in);
			if(fileID < FILE_ID_4) {
				SBReference.loadDummyData(in);
			}
			frameLightDisabledColor.load(in);
			frameTitleColor.load(in);
			
			if(fileID > FILE_ID_4B) {
				frameTitleShadowColor.load(in);
			}
			else {
				frameTitleShadowColor.update(frameCaptionColor);
				
				if(ColorRoutines.isColorDarker(frameTitleColor.getColor(), frameCaptionColor.getColor())) {
					frameTitleShadowColor.setBrightness(-8);
				}
				else {
					frameTitleShadowColor.setBrightness(-50);
				}
			}
			
			frameTitleDisabledColor.load(in);
			
			// Button
			frameButtColor.load(in);
			frameButtRolloverColor.load(in);
			frameButtPressedColor.load(in);
			frameButtDisabledColor.load(in);
			
			frameButtSpreadDark.setValue(in.readInt());
			frameButtSpreadLight.setValue(in.readInt());
			frameButtSpreadDarkDisabled.setValue(in.readInt());
			frameButtSpreadLightDisabled.setValue(in.readInt());
			
			frameButtCloseColor.load(in);
			frameButtCloseRolloverColor.load(in);
			frameButtClosePressedColor.load(in);
			frameButtCloseDisabledColor.load(in);
			
			frameButtCloseSpreadDark.setValue(in.readInt());
			frameButtCloseSpreadLight.setValue(in.readInt());
			frameButtCloseSpreadDarkDisabled.setValue(in.readInt());
			frameButtCloseSpreadLightDisabled.setValue(in.readInt());
			
			// Button Border
			frameButtBorderColor.load(in);
//			frameButtDarkColor.load(in);
//			frameButtLightColor.load(in);
			if(fileID < FILE_ID_4) {
				SBReference.loadDummyData(in);
				SBReference.loadDummyData(in);
			}
			frameButtBorderDisabledColor.load(in);
//			frameButtDarkDisabledColor.load(in);
//			frameButtLightDisabledColor.load(in);
			if(fileID < FILE_ID_4) {
				SBReference.loadDummyData(in);
				SBReference.loadDummyData(in);
			}
			
			// Symbol
			frameSymbolColor.load(in);
			frameSymbolPressedColor.load(in);
			frameSymbolDisabledColor.load(in);
			frameSymbolDarkColor.load(in);
			frameSymbolLightColor.load(in);
			
			if(fileID >= FILE_ID_4) {
				frameSymbolDarkDisabledColor.load(in);
				frameSymbolLightDisabledColor.load(in);
			}
			else {
				Color c = ColorRoutines.getAverage(
					frameSymbolDarkColor.getColor(),
					frameSymbolColor.getColor());
				frameSymbolDarkDisabledColor.update(c, 0, 0, SBReference.ABS_COLOR);
				
				c = ColorRoutines.getAverage(
					frameSymbolLightColor.getColor(),
					frameSymbolColor.getColor());
				frameSymbolLightDisabledColor.update(c, 0, 0, SBReference.ABS_COLOR);
			}
			
			// Close Button
			frameButtCloseBorderColor.load(in);
			frameButtCloseDarkColor.load(in);
			frameButtCloseLightColor.load(in);
			frameButtCloseBorderDisabledColor.load(in);
//			frameButtCloseDarkDisabledColor.load(in);
//			frameButtCloseLightDisabledColor.load(in);
			if(fileID < FILE_ID_4) {
				SBReference.loadDummyData(in);
				SBReference.loadDummyData(in);
			}
			
			// Close Symbol
			frameSymbolCloseColor.load(in);
			frameSymbolClosePressedColor.load(in);
			frameSymbolCloseDisabledColor.load(in);
			frameSymbolCloseDarkColor.load(in);
			
			if(fileID >= FILE_ID_4) {
				frameSymbolCloseDarkDisabledColor.load(in);
			}
			else {
				Color c = ColorRoutines.getAverage(
					frameSymbolCloseDarkColor.getColor(),
					frameSymbolCloseColor.getColor());
				frameSymbolCloseDarkDisabledColor.update(c, 0, 0, SBReference.ABS_COLOR);
			}
			
//			frameSymbolCloseLightColor.load(in);
			if(fileID < FILE_ID_4) {
				SBReference.loadDummyData(in);
			}
			
			frameSpreadDark.setValue(in.readInt());
			frameSpreadLight.setValue(in.readInt());
			frameSpreadDarkDisabled.setValue(in.readInt());
			frameSpreadLightDisabled.setValue(in.readInt());
			
//			Table
			tableBackColor.load(in);
			tableHeaderBackColor.load(in);
			
			if(fileID >= FILE_ID_3F) {
				tableHeaderArrowColor.load(in);
				tableHeaderRolloverBackColor.load(in);
				tableHeaderRolloverColor.load(in);
			}

			tableGridColor.load(in);
			tableSelectedBackColor.load(in);
			tableSelectedForeColor.load(in);
			
			if(fileID >= FILE_ID_3A) {
				tableBorderDarkColor.load(in);
				tableBorderLightColor.load(in);
				tableHeaderDarkColor.load(in);
				tableHeaderLightColor.load(in);
			}
			
			if(fileID >= FILE_ID_4) {
				tableFocusBorderColor.load(in);
			}
			else {
				tableFocusBorderColor.update(tableSelectedBackColor);
			}
			
			if(fileID >= FILE_ID_4E) {
				tableAlternateRowColor.load(in);
			}
			else {
				tableAlternateRowColor.update(tableBackColor);
			}
			
//			Icons
			if(fileID >= FILE_ID_3A) {
				for(int i = 0; i < colorizer.length; i++) {
					colorizer[i].load(in);
					colorize[i].setValue(in.readBoolean());
				}
			}
			else {
//				frameIconColor.load(in);
//				treeIconColor.load(in);
//				fileViewIconColor.load(in);
//				fileChooserIconColor.load(in);
//				optionPaneIconColor.load(in);
				SBReference.loadDummyData(in);
				SBReference.loadDummyData(in);
				SBReference.loadDummyData(in);
				SBReference.loadDummyData(in);
				SBReference.loadDummyData(in);
				
//				colorizeFrameIcon.setValue(in.readBoolean());
//				colorizeTreeIcon.setValue(in.readBoolean());
//				colorizeFileViewIcon.setValue(in.readBoolean());
//				colorizeFileChooserIcon.setValue(in.readBoolean());
//				colorizeOptionPaneIcon.setValue(in.readBoolean());
				in.readBoolean();
				in.readBoolean();
				in.readBoolean();
				in.readBoolean();
				in.readBoolean();
				
				for(int i = 0; i < 15; i++) {
//					colorize[i].setValue(in.readBoolean());
					in.readBoolean();
				}
				
				// no icons colorized
				for(int i = 0; i < colorizer.length; i++) {
					colorize[i].setValue(false);
				}
			}
			
//			Separator
			if(fileID >= FILE_ID_3A) {
				separatorColor.load(in);
				// sepLightColor.load(in);
				if(fileID < FILE_ID_4) {
					SBReference.loadDummyData(in);
				}
			}
			
//			ToolTip
			tipBorderColor.load(in);
			tipBgColor.load(in);
			
			if(fileID >= FILE_ID_3C) {
				tipBorderDis.load(in);
				tipBgDis.load(in);
				tipTextColor.load(in);
				tipTextDis.load(in);
			}
			
//			Misc
			titledBorderColor.load(in);
			
			if(fileID >= FILE_ID_4) {
				splitPaneButtonColor.load(in);
			}
			else {
				// pre 1.4.0 default
				splitPaneButtonColor.update(scrollArrowColor);
			}
			
			in.close();
		}
		catch (IOException ex) {
			ex.printStackTrace();
			throw ex;
		}
		finally {
			if(in != null) {
				try {
					in.close();
				}
				catch(IOException ignore) {}
			}
		}
		
		return true;
	}
	
	/**
	 * Saves the current theme.
	 * @param fileName a non-null file path
	 * @return <code>true</code> if the theme was successfully saved,
	 * <code>false</code> otherwise.
	 * @throws IllegalArgumentException if <code>fileName</code> is
	 * <code>null</code>.
	 */
	public static boolean saveTheme(String fileName) {
		if(fileName == null) {
			throw new IllegalArgumentException(
				"Argument to Theme.saveTheme(String) is null");
		}
		
		return saveTheme(new File(fileName));
	}
	
	private static boolean saveTheme(File f) {
		DataOutputStream out = null;
		
		try {
			out = new DataOutputStream(new FileOutputStream(f));
			
			out.writeInt(FILE_ID_4E);
			out.writeInt(YQ_STYLE);
			
//			Colors
			mainColor.save(out);
			disColor.save(out);
			backColor.save(out);
			frameColor.save(out);
			sub1Color.save(out);
			sub2Color.save(out);
			sub3Color.save(out);
			sub4Color.save(out);
			sub5Color.save(out);
			sub6Color.save(out);
			sub7Color.save(out);
			sub8Color.save(out);
			
//			Font
			plainFont.save(out);
			boldFont.save(out);
			buttonFont.save(out);
			passwordFont.save(out);
			labelFont.save(out);
			comboFont.save(out);
			listFont.save(out);
			menuFont.save(out);
			menuItemFont.save(out);
			radioFont.save(out);
			checkFont.save(out);
			tableFont.save(out);
			tableHeaderFont.save(out);
			textAreaFont.save(out);
			textFieldFont.save(out);
			textPaneFont.save(out);
			titledBorderFont.save(out);
			toolTipFont.save(out);
			treeFont.save(out);
			tabFont.save(out);
			editorFont.save(out);
			frameTitleFont.save(out);
			// since 1.3
			internalFrameTitleFont.save(out);
			// since 1.3
			internalPaletteTitleFont.save(out);
			
			progressBarFont.save(out);
			buttonFontColor.save(out);
			labelFontColor.save(out);
			menuFontColor.save(out);
			menuItemFontColor.save(out);
			radioFontColor.save(out);
			checkFontColor.save(out);
			tableFontColor.save(out);
			tableHeaderFontColor.save(out);
			tabFontColor.save(out);
			titledBorderFontColor.save(out);

//			Progressbar
			progressColor.save(out);
			progressTrackColor.save(out);
			progressBorderColor.save(out);
			progressDarkColor.save(out);
			progressLightColor.save(out);
			// since 1.1
			progressSelectForeColor.save(out);
			progressSelectBackColor.save(out);
			
//			Text	
			textBgColor.save(out);
			textTextColor.save(out);
			// since 1.3
			textCaretColor.save(out);
			editorPaneBgColor.save(out);
			textPaneBgColor.save(out);
			desktopPaneBgColor.save(out);
			
			textSelectedBgColor.save(out);
			textSelectedTextColor.save(out);
			textDisabledBgColor.save(out);
			
			// since 1.4.0
			textNonEditableBgColor.save(out);
			
			textBorderColor.save(out);
			// not needed since 1. 4
//			textBorderDarkColor.save(out);
//			textBorderLightColor.save(out);
			textBorderDisabledColor.save(out);
			// not needed since 1. 4
//			textBorderDarkDisabledColor.save(out);
//			textBorderLightDisabledColor.save(out);
			out.writeInt(textInsets.top);
			out.writeInt(textInsets.left);
			out.writeInt(textInsets.bottom);
			out.writeInt(textInsets.right);
			
//			Button    	
			out.writeBoolean(buttonRolloverBorder.getValue());
			out.writeBoolean(buttonFocus.getValue());
			out.writeBoolean(buttonFocusBorder.getValue());	// new in 1.3
			out.writeBoolean(buttonEnter.getValue());		// new in 1.3
			out.writeBoolean(shiftButtonText.getValue());	// new in 1.3.04
			
			buttonNormalColor.save(out);
			buttonRolloverBgColor.save(out);
			buttonPressedColor.save(out);
			buttonDisabledColor.save(out);
			buttonBorderColor.save(out);
			// not needed since 1. 4
//			buttonDarkColor.save(out);
//			buttonLightColor.save(out);
			buttonBorderDisabledColor.save(out);
			// not needed since 1. 4
//			buttonDarkDisabledColor.save(out);
//			buttonLightDisabledColor.save(out);
			
			out.writeInt(buttonMargin.top);
			out.writeInt(buttonMargin.left);
			out.writeInt(buttonMargin.bottom);
			out.writeInt(buttonMargin.right);
			// since 1.3
			out.writeInt(checkMargin.top);
			out.writeInt(checkMargin.left);
			out.writeInt(checkMargin.bottom);
			out.writeInt(checkMargin.right);
			
			buttonRolloverColor.save(out);
			buttonDefaultColor.save(out);
			buttonCheckColor.save(out);
			buttonCheckDisabledColor.save(out);
			buttonDisabledFgColor.save(out);
			checkDisabledFgColor.save(out);
			radioDisabledFgColor.save(out);
			// new in 1.4.0
			toggleSelectedBg.save(out);
			
			out.writeInt(buttonSpreadLight.getValue());
			out.writeInt(buttonSpreadDark.getValue());
			out.writeInt(buttonSpreadLightDisabled.getValue());
			out.writeInt(buttonSpreadDarkDisabled.getValue());
			
//			Scrollbar
			out.writeBoolean(scrollRollover.getValue());
			// New in 1.4.0
			out.writeInt(scrollSize.getValue());
			
			scrollTrackColor.save(out);
			scrollTrackDisabledColor.save(out);
			scrollTrackBorderColor.save(out);
			scrollTrackBorderDisabledColor.save(out);
			
			// Thumb
			scrollThumbColor.save(out);
			scrollThumbRolloverColor.save(out);
			scrollThumbPressedColor.save(out);
			scrollThumbDisabledColor.save(out);
			
			// Grip
			scrollGripLightColor.save(out);
			scrollGripDarkColor.save(out);
			
			// Buttons
			scrollButtColor.save(out);
			scrollButtRolloverColor.save(out);
			scrollButtPressedColor.save(out);
			scrollButtDisabledColor.save(out);
			
			out.writeInt(scrollSpreadLight.getValue());
			out.writeInt(scrollSpreadDark.getValue());
			out.writeInt(scrollSpreadLightDisabled.getValue());
			out.writeInt(scrollSpreadDarkDisabled.getValue());
			
			// Arrow
			scrollArrowColor.save(out);
			scrollArrowDisabledColor.save(out);
			
			// Border
			scrollBorderColor.save(out);
			// not needed since 1.4
//			scrollDarkColor.save(out);
			scrollBorderLightColor.save(out);
			scrollBorderDisabledColor.save(out);
			// not needed since 1.4
//			scrollDarkDisabledColor.save(out);
			scrollLightDisabledColor.save(out);
			
			// ScrollPane border
			scrollPaneBorderColor.save(out);
			
//			Tabbed
			tabPaneBorderColor.save(out);
			// not needed since 1.4
//			tabPaneDarkColor.save(out);
//			tabPaneLightColor.save(out);
			tabNormalColor.save(out);
			tabSelectedColor.save(out);
			
			// since 1.3
			tabDisabledColor.save(out);
			tabDisabledSelectedColor.save(out);
			tabDisabledTextColor.save(out);
			
			tabBorderColor.save(out);
			// not needed since 1.4
//			tabDarkColor.save(out);
//			tabLightColor.save(out);
			tabRolloverColor.save(out);
			
			// new in 1.4.0
			tabDisabledBorderColor.save(out);
			tabPaneDisabledBorderColor.save(out);
			
			out.writeBoolean(tabRollover.getValue());
			
			// since 1.3.05
			out.writeBoolean(tabFocus.getValue());
			
			out.writeBoolean(ignoreSelectedBg.getValue());
			
			// since 1.3
			out.writeBoolean(fixedTabs.getValue());
			
			// since 1.3
			out.writeInt(tabInsets.top);
			out.writeInt(tabInsets.left);
			out.writeInt(tabInsets.bottom);
			out.writeInt(tabInsets.right);
			
			out.writeInt(tabAreaInsets.top);
			out.writeInt(tabAreaInsets.left);
			out.writeInt(tabAreaInsets.bottom);
			out.writeInt(tabAreaInsets.right);
			
//			Slider
			out.writeBoolean(sliderRolloverEnabled.getValue());
			// since 1.3.05
			out.writeBoolean(sliderFocusEnabled.getValue());
			
			// Thumb
			sliderThumbColor.save(out);
			sliderThumbRolloverColor.save(out);
			sliderThumbPressedColor.save(out);
			sliderThumbDisabledColor.save(out);
			sliderBorderColor.save(out);
			sliderDarkColor.save(out);
			sliderLightColor.save(out);
			sliderBorderDisabledColor.save(out);
			sliderTrackColor.save(out);
			sliderTrackBorderColor.save(out);
			sliderTrackDarkColor.save(out);
			sliderTrackLightColor.save(out);
			sliderTickColor.save(out);
			sliderTickDisabledColor.save(out);
			// since 1.3.05
			sliderFocusColor.save(out);
			
//			Spinner
			out.writeBoolean(spinnerRollover.getValue());

			// Button
			spinnerButtColor.save(out);
			spinnerButtRolloverColor.save(out);
			spinnerButtPressedColor.save(out);
			spinnerButtDisabledColor.save(out);
			
			out.writeInt(spinnerSpreadLight.getValue());
			out.writeInt(spinnerSpreadDark.getValue());
			out.writeInt(spinnerSpreadLightDisabled.getValue());
			out.writeInt(spinnerSpreadDarkDisabled.getValue());
			
			spinnerBorderColor.save(out);
			// not needed since 1.4
//			spinnerDarkColor.save(out);
//			spinnerLightColor.save(out);
			spinnerBorderDisabledColor.save(out);
			// not needed since 1.4
//			spinnerDarkDisabledColor.save(out);
//			spinnerLightDisabledColor.save(out);
			
			// Arrow
			spinnerArrowColor.save(out);
			spinnerArrowDisabledColor.save(out);
			
//			Combo
			comboBorderColor.save(out);
			// not needed since 1.4
//			comboDarkColor.save(out);
//			comboLightColor.save(out);
			comboBorderDisabledColor.save(out);
			// not needed since 1.4
//			comboDarkDisabledColor.save(out);
//			comboLightDisabledColor.save(out);
			comboSelectedBgColor.save(out);
			comboSelectedTextColor.save(out);
			comboFocusBgColor.save(out);
			comboBgColor.save(out);
			comboTextColor.save(out);
			
			// Button
			comboButtColor.save(out);
			comboButtRolloverColor.save(out);
			comboButtPressedColor.save(out);
			comboButtDisabledColor.save(out);
			out.writeInt(comboSpreadLight.getValue());
			out.writeInt(comboSpreadDark.getValue());
			out.writeInt(comboSpreadLightDisabled.getValue());
			out.writeInt(comboSpreadDarkDisabled.getValue());

			// Button Border
			comboButtBorderColor.save(out);
			// not needed since 1.4
//			comboButtDarkColor.save(out);
//			comboButtLightColor.save(out);
			comboButtBorderDisabledColor.save(out);
			// not needed since 1.4
//			comboButtDarkDisabledColor.save(out);
//			comboButtLightDisabledColor.save(out);
			
			// Arrow
			comboArrowColor.save(out);
			comboArrowDisabledColor.save(out);
			out.writeInt(comboInsets.top);
			out.writeInt(comboInsets.left);
			out.writeInt(comboInsets.bottom);
			out.writeInt(comboInsets.right);
			out.writeBoolean(comboRollover.getValue());
			out.writeBoolean(comboFocus.getValue());
			
//			Menu
			menuBarColor.save(out);
			menuItemSelectedTextColor.save(out);
			menuPopupColor.save(out);
			menuRolloverBgColor.save(out);
			menuItemRolloverColor.save(out);
			menuBorderColor.save(out);
			// not needed since 1.4
//			menuDarkColor.save(out);
//			menuLightColor.save(out);
			
			menuIconColor.save(out);
			menuIconRolloverColor.save(out);
			menuIconDisabledColor.save(out);
			// not needed since 1.4
//			menuIconShadowColor.save(out);
			
			menuSeparatorColor.save(out);
			// not needed since 1.4
//			menuSepLightColor.save(out);
//			out.writeInt(menuBorderInsets.top);
//			out.writeInt(menuBorderInsets.left);
//			out.writeInt(menuBorderInsets.bottom);
//			out.writeInt(menuBorderInsets.right);

			out.writeBoolean(menuRollover.getValue());
			
			// since 1.3
			menuInnerHilightColor.save(out);
			menuInnerShadowColor.save(out);
			menuOuterHilightColor.save(out);
			menuOuterShadowColor.save(out);
			menuRolloverFgColor.save(out);
			menuDisabledFgColor.save(out);
			// since 1.4.0
			menuItemDisabledFgColor.save(out);
			
			// since 1.4.0
			out.writeBoolean(menuPopupShadow.getValue());
			out.writeBoolean(menuAllowTwoIcons.getValue());
			
//			Toolbar
			toolBarColor.save(out);
			toolBarLightColor.save(out);
			toolBarDarkColor.save(out);
			toolButtColor.save(out);	// since 1.3
			toolButtRolloverColor.save(out);
			toolButtPressedColor.save(out);
			toolButtSelectedColor.save(out);
			// not needed since 1.4
//			toolBorderDarkColor.save(out);
//			toolBorderLightColor.save(out);
			toolBorderColor.save(out);
			toolBorderRolloverColor.save(out);	// since 1.3
			toolBorderPressedColor.save(out);
			toolBorderSelectedColor.save(out);
			out.writeBoolean(toolRollover.getValue());
			out.writeBoolean(toolFocus.getValue());

			// since 1.3
			toolGripDarkColor.save(out);
			toolGripLightColor.save(out);
			toolSeparatorColor.save(out);
			// not needed since 1.4
//			toolSepLightColor.save(out);
			out.writeInt(toolMargin.top);
			out.writeInt(toolMargin.left);
			out.writeInt(toolMargin.bottom);
			out.writeInt(toolMargin.right);
			
//			List
			listSelectedBgColor.save(out);
			listSelectedTextColor.save(out);
			// since 1.3
			listBgColor.save(out);
			listTextColor.save(out);
			// since 1.4.0
			listFocusBorderColor.save(out);
			
//			Tree
			treeBgColor.save(out);
			treeTextColor.save(out);
			treeTextBgColor.save(out);
			treeSelectedTextColor.save(out);
			treeSelectedBgColor.save(out);
			treeLineColor.save(out);
			
//			Frame
			frameCaptionColor.save(out);
			frameCaptionDisabledColor.save(out);
			frameBorderColor.save(out);
			// not needed since 1.4
//			frameDarkColor.save(out);
			frameLightColor.save(out);
			frameBorderDisabledColor.save(out);
			// not needed since 1.4
//			frameDarkDisabledColor.save(out);
			frameLightDisabledColor.save(out);
			frameTitleColor.save(out);
			// Since 1,4,0
			frameTitleShadowColor.save(out);
			frameTitleDisabledColor.save(out);
			
			// Button
			frameButtColor.save(out);
			frameButtRolloverColor.save(out);
			frameButtPressedColor.save(out);
			frameButtDisabledColor.save(out);
			
			out.writeInt(frameButtSpreadDark.getValue());
			out.writeInt(frameButtSpreadLight.getValue());
			out.writeInt(frameButtSpreadDarkDisabled.getValue());
			out.writeInt(frameButtSpreadLightDisabled.getValue());
			
			frameButtCloseColor.save(out);
			frameButtCloseRolloverColor.save(out);
			frameButtClosePressedColor.save(out);
			frameButtCloseDisabledColor.save(out);
			
			out.writeInt(frameButtCloseSpreadDark.getValue());
			out.writeInt(frameButtCloseSpreadLight.getValue());
			out.writeInt(frameButtCloseSpreadDarkDisabled.getValue());
			out.writeInt(frameButtCloseSpreadLightDisabled.getValue());

			// Button Border
			frameButtBorderColor.save(out);
			frameButtBorderDisabledColor.save(out);
			frameSymbolColor.save(out);
			frameSymbolPressedColor.save(out);
			frameSymbolDisabledColor.save(out);
			frameSymbolDarkColor.save(out);
			frameSymbolLightColor.save(out);
			// since 1.4.0
			frameSymbolDarkDisabledColor.save(out);
			frameSymbolLightDisabledColor.save(out);
			
			// Close Button
			frameButtCloseBorderColor.save(out);
			frameButtCloseDarkColor.save(out);
			frameButtCloseLightColor.save(out);
			frameButtCloseBorderDisabledColor.save(out);
			frameSymbolCloseColor.save(out);
			frameSymbolClosePressedColor.save(out);
			frameSymbolCloseDisabledColor.save(out);
			frameSymbolCloseDarkColor.save(out);
			// since 1.4.0
			frameSymbolCloseDarkDisabledColor.save(out);
			
			out.writeInt(frameSpreadDark.getValue());
			out.writeInt(frameSpreadLight.getValue());
			out.writeInt(frameSpreadDarkDisabled.getValue());
			out.writeInt(frameSpreadLightDisabled.getValue());
			
//			Table
			tableBackColor.save(out);
			tableHeaderBackColor.save(out);
			// since 1.3.6
			tableHeaderArrowColor.save(out);
			tableHeaderRolloverBackColor.save(out);
			tableHeaderRolloverColor.save(out);
			// end since 1.3.6
			tableGridColor.save(out);
			tableSelectedBackColor.save(out);
			tableSelectedForeColor.save(out);
			tableBorderDarkColor.save(out);
			tableBorderLightColor.save(out);
			tableHeaderDarkColor.save(out);
			tableHeaderLightColor.save(out);
			// since 1.4.0
			tableFocusBorderColor.save(out);
			tableAlternateRowColor.save(out);
			
//			Icons
			for(int i = 0; i < colorizer.length; i++) {
				colorizer[i].save(out);
				out.writeBoolean(colorize[i].getValue());
			}
			
//			Separator - since 1.3
			separatorColor.save(out);
			
//			ToolTip
			tipBorderColor.save(out);
			tipBgColor.save(out);
			
			// since 1.3C
			tipBorderDis.save(out);
			tipBgDis.save(out);
			tipTextColor.save(out);
			tipTextDis.save(out);
			
//			Misc
			titledBorderColor.save(out);
			// new in 1.4.0
			splitPaneButtonColor.save(out);
			
			return true;
		}
		catch (IOException ex) {
			ex.printStackTrace();
		}
		finally {
			if(out != null) {
				try {
					out.close();
				}
				catch(IOException ignore) {}
			}
		}
		
		return false;
	}
}