/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*  Tiny Look and Feel - a pluggable look and feel for java				   	   *
*  Copyright 2003 - 2008  Hans Bickel									   	   *
*  TinyLaF Home: http://www.muntjak.de/hans/java/tinylaf/					   *
*                                                                              *
*  This program is free software: you can redistribute it and/or modify        *
*  it under the terms of the GNU Lesser General Public License as published by *
*  the Free Software Foundation, either version 3 of the License, or           *
*  (at your option) any later version.                                         *
*                                                                              *
*  This program is distributed in the hope that it will be useful,             *
*  but WITHOUT ANY WARRANTY; without even the implied warranty of              *
*  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the               *
*  GNU Lesser General Public License for more details.                         *
*                                                                              *
*  You should have received a copy of the GNU Lesser General Public License    *
*  along with this program.  If not, see <http://www.gnu.org/licenses/>.       *
*                                                                              *
*                                                                              *
* The starting point for Tiny Look and Feel was the XP Look and Feel written   *
* by Stefan Krause.  														   *
* The original header of this file was:                                        *
** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*	XP Look and Feel														   *
*															                   *
*  (C) Copyright 2002, by Stefan Krause, Taufik Romdhane and Contributors      *
*                                                                              *
*                                                                              *
* The XP Look and Feel started as as extension to the Metouia Look and Feel.   *
* The original header of this file was:                                        *
** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
** * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
*        Metouia Look And Feel: a free pluggable look and feel for java        *
*                         http://mlf.sourceforge.net                           *
*          (C) Copyright 2002, by Taoufik Romdhane and Contributors.           *
*                                                                              *
*   This library is free software; you can redistribute it and/or modify it    *
*   under the terms of the GNU Lesser General Public License as published by   *
*   the Free Software Foundation; either version 2.1 of the License, or (at    *
*   your option) any later version.                                            *
*                                                                              *
*   This library is distributed in the hope that it will be useful,            *
*   but WITHOUT ANY WARRANTY; without even the implied warranty of             *
*   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.                       *
*   See the GNU Lesser General Public License for more details.                *
*                                                                              *
*   You should have received a copy of the GNU General Public License along    *
*   with this program; if not, write to the Free Software Foundation, Inc.,    *
*   59 Temple Place, Suite 330, Boston, MA 02111-1307, USA.                    *
*                                                                              *
*   Original Author:  Taoufik Romdhane                                         *
* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.KeyboardFocusManager;
import java.awt.Robot;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.AccessControlException;
import java.security.AccessController;
import java.security.PrivilegedAction;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.basic.BasicBorders;
import javax.swing.plaf.metal.MetalLookAndFeel;

import de.muntjak.tinylookandfeel.borders.TinyButtonBorder;
import de.muntjak.tinylookandfeel.borders.TinyFrameBorder;
import de.muntjak.tinylookandfeel.borders.TinyInternalFrameBorder;
import de.muntjak.tinylookandfeel.borders.TinyMenuBarBorder;
import de.muntjak.tinylookandfeel.borders.TinyPopupMenuBorder;
import de.muntjak.tinylookandfeel.borders.TinyProgressBarBorder;
import de.muntjak.tinylookandfeel.borders.TinyScrollPaneBorder;
import de.muntjak.tinylookandfeel.borders.TinySpinnerBorder;
import de.muntjak.tinylookandfeel.borders.TinyTableHeaderBorder;
import de.muntjak.tinylookandfeel.borders.TinyTableHeaderRolloverBorder;
import de.muntjak.tinylookandfeel.borders.TinyTableScrollPaneBorder;
import de.muntjak.tinylookandfeel.borders.TinyTextFieldBorder;
import de.muntjak.tinylookandfeel.borders.TinyToolBarBorder;
import de.muntjak.tinylookandfeel.borders.TinyToolTipBorder;

/**
 * The Tiny Look and Feel implementation.

 * @version 1.4.0
 * @author Hans Bickel
 */
public class TinyLookAndFeel extends MetalLookAndFeel {
	
	/** Signals if we are run from the ControlPanel. */
	public static boolean controlPanelInstantiated = false;
	
	/** If <code>true</code>, each drawing cache reports its
	 * size as new entries are added. Should be false for production build.
	 */
	public static final boolean PRINT_CACHE_SIZES = false;
	
	// The minimum width for TitlePane.TitlePaneLayout,
	// from interest only if a frame or dialog is decorated.
	static final int MINIMUM_FRAME_WIDTH = 104;
	
	// The minimum width for internal frames and palettes
	static final int MINIMUM_INTERNAL_FRAME_WIDTH = 32;

	/** The current TinyLaF version as a string. */
	public static final String VERSION_STRING = "1.4.0";
	
	/** The release date of this TinyLaF release as a string. */
	public static final String DATE_STRING = "2009/8/25";

	/* The installation state of the TinyLaF Look and Feel. */
	private static boolean isInstalled = false;
	
	/**
	 * A global <code>Robot</code> instance used for the ControlPanel
	 * magnifier, to antialias frame caption edges and to capture
	 * backgrounds for popup menu shadows.
	 */
	public static Robot ROBOT;
	
	static {
		if(ROBOT == null) {
			try {
				ROBOT = new Robot();
			}
			catch(Exception ignore) {
				ROBOT = null;
			}
		}
	}

	/**
     * Initializes the look and feel.
     */
	public void initialize() {
		super.initialize();
		
		if(!isInstalled) {
			isInstalled = true;

			searchDefaultTheme();
			UIManager.installLookAndFeel(new UIManager.LookAndFeelInfo(
				"TinyLookAndFeel", "de.muntjak.tinylookandfeel.TinyLookAndFeel"));
		}
		
		TinyPopupFactory.install();
		
		// Execute this one even if isInstalled is true
		// New in 1.3.6
		KeyboardFocusManager.getCurrentKeyboardFocusManager().
        	addKeyEventPostProcessor(TinyMenuUI.ALT_PROCESSOR);
		
		clearAllCaches();
	}

	/**
	 * Clears all caches used to speed up drawing of
	 * components. This method is called automatically
	 * if new themes are loaded.
	 *
	 */
	public static void clearAllCaches() {
		TinyTitlePane.clearCache();
		TinyInternalFrameBorder.clearCache();
		TinyButtonUI.clearCache();
		TinyCheckBoxIcon.clearCache();
		TinyComboBoxButton.clearCache();
		TinyProgressBarUI.clearCache();
		TinyRadioButtonIcon.clearCache();
		TinyScrollBarUI.clearCache();
		TinyScrollButton.clearCache();
		TinySpinnerButtonUI.clearCache();
		TinyWindowButtonUI.clearCache();
		MenuItemIconFactory.clearCache();
	}

	/**
	 * Uninitializes the look and feel.
	 */
	public void uninitialize() {
		super.uninitialize();
		
		TinyPopupFactory.uninstall();

		KeyboardFocusManager.getCurrentKeyboardFocusManager().
        	removeKeyEventPostProcessor(TinyMenuUI.ALT_PROCESSOR);
	}
	
	private void searchDefaultTheme() {
		// only if running without the control panel
		if(controlPanelInstantiated) return;

		String loadedFrom = null;
		URL defaultURL = TinyLookAndFeel.class.getResource("/" + Theme.DEFAULT_THEME);
		
		if(Theme.loadTheme(defaultURL)) {
			loadedFrom = defaultURL.toExternalForm();
		}
		else {
			defaultURL = Thread.currentThread().getContextClassLoader().getResource(Theme.DEFAULT_THEME);

			
			if(Theme.loadTheme(defaultURL)) {
				loadedFrom = defaultURL.toExternalForm();
			}
			else {
				try {
					defaultURL = new File(TinyUtils.getSystemProperty("user.home"), Theme.DEFAULT_THEME).toURI().toURL();
					
					if(Theme.loadTheme(defaultURL)) {
						loadedFrom = defaultURL.toExternalForm();
					}
					else {
						defaultURL = new File(TinyUtils.getSystemProperty("user.dir"), Theme.DEFAULT_THEME).toURI().toURL();
					
						if(Theme.loadTheme(defaultURL)) {
							loadedFrom = defaultURL.toExternalForm();
						}
						// else we give up
					}
				}
				catch (MalformedURLException ignore) {}
				
				// AccessControlException is thrown when running
				// with Java Web Start
				catch(AccessControlException ignore) {}
			}
		}
		
		String info = "TinyLaF v" + VERSION_STRING + "\n";
		if(loadedFrom == null) {
			System.out.println(info + "'Default.theme' not found - using YQ default theme.");
		}
		else {
			System.out.println(info + "Theme: " + loadedFrom);
		}
	}

	/**
	 * Returns &quot;TinyLookAndFeel&quot;.
	 *
	 * @return &quot;TinyLookAndFeel&quot;.
	 */
	public String getID() {
		return "TinyLookAndFeel";
	}

	/**
	 * Returns &quot;TinyLookAndFeel&quot;.
	 *
	 * @return &quot;TinyLookAndFeel&quot;.
	 */
	public String getName() {
		return "TinyLookAndFeel";
	}

	/**
	 * Returns &quot;TinyLookAndFeel&quot;.
	 *
	 * @return &quot;TinyLookAndFeel&quot;
	 */
	public String getDescription() {
		return "TinyLookAndFeel";
	}

	/**
	 * Returns <code>false</code>.
	 * @return <code>false</code>
	 */
	public boolean isNativeLookAndFeel() {
		return false;
	}

	/**
	 * Returns <code>true</code>.
	 * @return <code>true</code>
	 */
	public final boolean isSupportedLookAndFeel() {
		return true;
	}

	/**
	 * Returns <code>true</code>.
	 * @return <code>true</code>
	 */
	public boolean getSupportsWindowDecorations() {
		return true;
	}

	/**
	 * Initializes the uiClassID to BasicComponentUI mapping.
	 * The JComponent classes define their own uiClassID constants. This table
	 * must map those constants to a BasicComponentUI class of the appropriate
	 * type.
	 *
	 * @param table The ui defaults table.
	 */
	protected void initClassDefaults(UIDefaults table) {
		super.initClassDefaults(table);

		table.putDefaults(new Object[] {
			"ButtonUI", "de.muntjak.tinylookandfeel.TinyButtonUI",
			"CheckBoxUI", "de.muntjak.tinylookandfeel.TinyCheckBoxUI",
			"TextFieldUI", "de.muntjak.tinylookandfeel.TinyTextFieldUI",
			"TextAreaUI", "de.muntjak.tinylookandfeel.TinyTextAreaUI",
			/* New in 1.3.6: Removed entry for FormattedTextFieldUI */
			// Removing the entry wasn't a good idea because text field
			// properties were not propagated, introduced TinyFormattedTextFieldUI
			// in v1.4.0
			"FormattedTextFieldUI", "de.muntjak.tinylookandfeel.TinyFormattedTextFieldUI",
			"PasswordFieldUI", "de.muntjak.tinylookandfeel.TinyPasswordFieldUI",
			"EditorPaneUI", "de.muntjak.tinylookandfeel.TinyEditorPaneUI",
			"TextPaneUI", "de.muntjak.tinylookandfeel.TinyTextPaneUI",
			"SliderUI", "de.muntjak.tinylookandfeel.TinySliderUI",
			"SpinnerUI", "de.muntjak.tinylookandfeel.TinySpinnerUI",
			"ToolBarUI", "de.muntjak.tinylookandfeel.TinyToolBarUI",			
			"ToolBarSeparatorUI", "de.muntjak.tinylookandfeel.TinyToolBarSeparatorUI",
			"MenuBarUI", "de.muntjak.tinylookandfeel.TinyMenuBarUI",
			"MenuUI", "de.muntjak.tinylookandfeel.TinyMenuUI",
			"MenuItemUI", "de.muntjak.tinylookandfeel.TinyMenuItemUI",
			"CheckBoxMenuItemUI", "de.muntjak.tinylookandfeel.TinyCheckBoxMenuItemUI",
			"RadioButtonMenuItemUI", "de.muntjak.tinylookandfeel.TinyRadioButtonMenuItemUI",
			"ScrollBarUI", "de.muntjak.tinylookandfeel.TinyScrollBarUI",
			"TabbedPaneUI", "de.muntjak.tinylookandfeel.TinyTabbedPaneUI",
			"ToggleButtonUI", "de.muntjak.tinylookandfeel.TinyButtonUI",
			"ScrollPaneUI", "de.muntjak.tinylookandfeel.TinyScrollPaneUI",
			"ProgressBarUI", "de.muntjak.tinylookandfeel.TinyProgressBarUI",
			"InternalFrameUI", "de.muntjak.tinylookandfeel.TinyInternalFrameUI",
			"RadioButtonUI", "de.muntjak.tinylookandfeel.TinyRadioButtonUI",
			"ComboBoxUI", "de.muntjak.tinylookandfeel.TinyComboBoxUI",
			"PopupMenuSeparatorUI", "de.muntjak.tinylookandfeel.TinyPopupMenuSeparatorUI",
			"SeparatorUI", "de.muntjak.tinylookandfeel.TinySeparatorUI",
			"SplitPaneUI", "de.muntjak.tinylookandfeel.TinySplitPaneUI",
			"FileChooserUI", "de.muntjak.tinylookandfeel.TinyFileChooserUI",
			"ListUI", "de.muntjak.tinylookandfeel.TinyListUI",
			"TreeUI", "de.muntjak.tinylookandfeel.TinyTreeUI",
			"LabelUI", "de.muntjak.tinylookandfeel.TinyLabelUI",
			"TableUI", "de.muntjak.tinylookandfeel.TinyTableUI",
			"TableHeaderUI", "de.muntjak.tinylookandfeel.TinyTableHeaderUI",
			"ToolTipUI", "de.muntjak.tinylookandfeel.TinyToolTipUI",
			"RootPaneUI", "de.muntjak.tinylookandfeel.TinyRootPaneUI",
			"DesktopPaneUI", "de.muntjak.tinylookandfeel.TinyDesktopPaneUI"
			});
	}

	/**
	 * Creates the default theme and installs it.
	 * The TinyDefaultTheme is used as default.
	 */
	protected void createDefaultTheme() {
		// Note: Up to 1.3.02, the if-clause below prevented
		// the theme from being re-loaded when refreshing an Applet page
			
//		if(!themeHasBeenSet) {
			setCurrentTheme(new TinyDefaultTheme());
//		}
	}

	/**
	 * Initializes the default values for many ui widgets and puts them in the
	 * given ui defaults table.
	 * Here is the place where borders can be changed.
	 *
	 * @param table The ui defaults table.
	 */
	protected void initComponentDefaults(UIDefaults table) {
		super.initComponentDefaults(table);

		// Replace Metal borders:
		Border border = new EmptyBorder(0, 0, 0, 0);
		table.put("Button.border",
			new TinyButtonBorder.CompoundBorderUIResource(
				new TinyButtonBorder(),
				new BasicBorders.MarginBorder()));
		Border textFieldBorder = new TinyTextFieldBorder();
		table.put("FormattedTextField.border", textFieldBorder);
		table.put("TextField.border", textFieldBorder);
		table.put("PasswordField.border", textFieldBorder);
		table.put("ComboBox.border", border);
		table.put("Table.scrollPaneBorder", new TinyTableScrollPaneBorder());
		table.put("TableHeader.cellBorder", new TinyTableHeaderBorder());
		table.put("TableHeader.cellRolloverBorder", new TinyTableHeaderRolloverBorder());
		
		// New in 1.4.0
		table.put("Table.alternateRowColor", new ColorUIResource(228, 230, 236));
		
		// new in 1.4.0 - previously spinner border was textfield border
		table.put("Spinner.border", new TinySpinnerBorder());
		table.put("ProgressBar.border", new TinyProgressBarBorder());
		table.put("ToolBar.border", new TinyToolBarBorder());
		table.put("ToolTip.border", new BorderUIResource(new TinyToolTipBorder(true)));
		table.put("ToolTip.borderInactive", new BorderUIResource(new TinyToolTipBorder(false)));

		border = new TinyInternalFrameBorder();		
		table.put("InternalFrame.border", border);
		table.put("InternalFrame.paletteBorder", border);
		table.put("InternalFrame.optionDialogBorder", border);
		
		table.put("MenuBar.border", new TinyMenuBarBorder());

		border = new EmptyBorder(2, 4, 2, 4);
		// Changed with 1.4.0 so the distance between adjacent
		// top menus is equal to XP
//		table.put("Menu.border", border);
		table.put("Menu.border", new EmptyBorder(2, 5, 2, 6));
		table.put("MenuItem.border", border);
		table.put("CheckBoxMenuItem.border", border);
		table.put("RadioButtonMenuItem.border", border);

		table.put("PopupMenu.border", new TinyPopupMenuBorder());
		table.put("ScrollPane.border", new TinyScrollPaneBorder());
		table.put("Slider.trackWidth", new Integer(4));
		
		// Note: Margins correspond to borders. In TinyLaF 1.2 checkboxes
		// and radio buttons had the Metal border which itself adds insets
		// of (2, 2, 2, 2) - so a 1.3 checkbox/radio button has the same
		// visible margin as a 1.2 checkbox/radio button because margins
		// in 1.2 where (0, 0, 0, 0)
		table.put("CheckBox.border", new BasicBorders.MarginBorder());
		table.put("RadioButton.border", new BasicBorders.MarginBorder());
		table.put("RadioButton.margin", new InsetsUIResource(2, 2, 2, 2));
		table.put("CheckBox.margin", new InsetsUIResource(2, 2, 2, 2));

		// Tweak some subtle values:
		table.put("SplitPane.dividerSize", new Integer(7));
		
		// New in 1.3.7 - value is evaluated at
		// javax.swing.plaf.basic.BasicFileChooserUI.installDefaults(JFileChooser)
		if(TinyUtils.isOSLinux()) {
			table.put("FileChooser.readOnly", Boolean.TRUE);
		}

		table.put("TabbedPane.tabInsets", new Insets(1, 6, 4, 6));
		table.put("TabbedPane.selectedTabPadInsets", new Insets(2, 2, 1, 2));
		table.put("TabbedPane.tabAreaInsets", new Insets(6, 2, 0, 0));
		table.put("TabbedPane.contentBorderInsets", new Insets(1, 1, 3, 3));

		table.put("PopupMenu.foreground", new Color(255, 0, 0));

		table.put("RootPane.colorChooserDialogBorder", TinyFrameBorder.getInstance());
		table.put("RootPane.errorDialogBorder", TinyFrameBorder.getInstance());
		table.put("RootPane.fileChooserDialogBorder", TinyFrameBorder.getInstance());
		table.put("RootPane.frameBorder", TinyFrameBorder.getInstance());
		table.put("RootPane.informationDialogBorder", TinyFrameBorder.getInstance());
		table.put("RootPane.plainDialogBorder", TinyFrameBorder.getInstance());
		table.put("RootPane.questionDialogBorder", TinyFrameBorder.getInstance());
		table.put("RootPane.warningDialogBorder", TinyFrameBorder.getInstance());

		table.put("CheckBoxMenuItem.checkIcon", MenuItemIconFactory.getCheckBoxMenuItemIcon());
		table.put("RadioButtonMenuItem.checkIcon", MenuItemIconFactory.getRadioButtonMenuItemIcon());
		table.put("Menu.arrowIcon", MenuItemIconFactory.getMenuArrowIcon());
		
		table.put("InternalFrame.frameTitleHeight", new Integer(25));
		table.put("InternalFrame.paletteTitleHeight", new Integer(16));
		table.put("InternalFrame.icon", loadIcon("InternalFrameIcon.png"));

		table.put("Tree.expandedIcon", loadIcon("TreeMinusIcon.png"));
		table.put("Tree.collapsedIcon", loadIcon("TreePlusIcon.png"));
		table.put("Tree.openIcon", loadIcon("TreeFolderOpenedIcon.png"));
		table.put("Tree.closedIcon", loadIcon("TreeFolderClosedIcon.png"));
		table.put("Tree.leafIcon", loadIcon("TreeLeafIcon.png"));
		
		table.put("FileView.directoryIcon", loadIcon("DirectoryIcon.png"));
		table.put("FileView.computerIcon", loadIcon("ComputerIcon.png"));
		table.put("FileView.fileIcon", loadIcon("FileIcon.png"));
		table.put("FileView.floppyDriveIcon", loadIcon("FloppyIcon.png"));
		table.put("FileView.hardDriveIcon", loadIcon("HarddiskIcon.png"));
		
		table.put("FileChooser.detailsViewIcon", loadIcon("FileDetailsIcon.png"));
		table.put("FileChooser.homeFolderIcon", loadIcon("HomeFolderIcon.png"));
		table.put("FileChooser.listViewIcon", loadIcon("FileListIcon.png"));
		table.put("FileChooser.newFolderIcon", loadIcon("NewFolderIcon.png"));
		table.put("FileChooser.upFolderIcon", loadIcon("ParentDirectoryIcon.png"));
	
		table.put("OptionPane.errorIcon", loadIcon("ErrorIcon.png"));
		table.put("OptionPane.informationIcon", loadIcon("InformationIcon.png"));
		table.put("OptionPane.warningIcon", loadIcon("WarningIcon.png"));
		table.put("OptionPane.questionIcon", loadIcon("QuestionIcon.png"));
	}

	/**
	 * This method is not intended for public use.
	 * @param index
	 * @return an icon
	 */
	public static Icon getUncolorizedSystemIcon(int index) {
		switch(index) {
			case 0:
				return loadIcon("InternalFrameIcon.png");
			case 1:
				return loadIcon("TreeFolderClosedIcon.png");
			case 2:
				return loadIcon("TreeFolderOpenedIcon.png");
			case 3:
				return loadIcon("TreeLeafIcon.png");
			case 4:
				return loadIcon("TreeMinusIcon.png");
			case 5:
				return loadIcon("TreePlusIcon.png");
			case 6:
				return loadIcon("ComputerIcon.png");
			case 7:
				return loadIcon("FloppyIcon.png");
			case 8:
				return loadIcon("HarddiskIcon.png");
			case 9:
				return loadIcon("DirectoryIcon.png");
			case 10:
				return loadIcon("FileIcon.png");
			case 11:
				return loadIcon("ParentDirectoryIcon.png");
			case 12:
				return loadIcon("HomeFolderIcon.png");
			case 13:
				return loadIcon("NewFolderIcon.png");
			case 14:
				return loadIcon("FileListIcon.png");
			case 15:
				return loadIcon("FileDetailsIcon.png");
			case 16:
				return loadIcon("InformationIcon.png");
			case 17:
				return loadIcon("QuestionIcon.png");
			case 18:
				return loadIcon("WarningIcon.png");
			default:
				return loadIcon("ErrorIcon.png");
		}
	}

	/**
	 * This method is not intended for public use.
	 * @param index 
	 * @return the name of a system icon, for example
	 * &quot;Tree.closedIcon&quot;
	 */
	public static String getSystemIconName(int index) {
		switch(index) {
			case 0:
			return "InternalFrame.icon";
			case 1:
			return "Tree.closedIcon";
			case 2:
			return "Tree.openIcon";
			case 3:
			return "Tree.leafIcon";
			case 4:
			return "Tree.expandedIcon";
			case 5:
			return "Tree.collapsedIcon";
			case 6:
			return "FileView.computerIcon";
			case 7:
			return "FileView.floppyDriveIcon";
			case 8:
			return "FileView.hardDriveIcon";
			case 9:
			return "FileView.directoryIcon";
			case 10:
			return "FileView.fileIcon";
			case 11:
			return "FileChooser.upFolderIcon";
			case 12:
			return "FileChooser.homeFolderIcon";
			case 13:
			return "FileChooser.newFolderIcon";
			case 14:
			return "FileChooser.listViewIcon";
			case 15:
			return "FileChooser.detailsViewIcon";
			case 16:
			return "OptionPane.informationIcon";
			case 17:
			return "OptionPane.questionIcon";
			case 18:
			return "OptionPane.warningIcon";
			default:
			return "OptionPane.errorIcon";
		}
	}

	/**
	 * This method is not intended for public use.
	 *
	 * @param fileName The icon's file name.
	 * @return the specified icon or <code>null</code> if the icon
	 * resource doesn't exist.
	 */
	public static ImageIcon loadIcon(final String fileName) {
		// This should work for both applications and applets
		URL url = null;
		
		if(fileName.indexOf("/") != -1) {
			url = Thread.currentThread().getContextClassLoader().getResource(
				"de/muntjak/tinylookandfeel/" + fileName);
		}
		else {
			url = Thread.currentThread().getContextClassLoader().getResource(
				"de/muntjak/tinylookandfeel/icons/" + fileName);
		}
		
		if(url == null) {
			// Another try
			if(fileName.indexOf("/") != -1) {
				url = TinyLookAndFeel.class.getResource(
					"/de/muntjak/tinylookandfeel/" + fileName);
			}
			else {
				url = TinyLookAndFeel.class.getResource(
					"/de/muntjak/tinylookandfeel/icons/" + fileName);
			}
			
			if(url == null) {
				System.err.println("TinyLaF: Icon directory could not be resolved." +
					" fileName argument:\"" + fileName + "\"");
				return null;
			}
		}
		
		return new ImageIcon(url);
	}
}