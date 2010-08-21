package com.digitprop.tonic;


import java.awt.*;

import java.net.*;


import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.*;
import javax.swing.plaf.metal.*;



/**	Represents the Tonic Look and Feel, and provides the UIManager with
 * 	the settings for Tonic.
 * 
 * 	@version	1.0.5
 * 
 * 	@author		Markus Fischer
 *
 *  	<p>This software is under the <a href="http://www.gnu.org/copyleft/lesser.html" target="_blank">GNU Lesser General Public License</a>
 */

/*
 * ------------------------------------------------------------------------
 * Copyright (C) 2004 Markus Fischer
 * 
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License version 2.1 as published by the Free Software Foundation.
 * 
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free 
 * Software Foundation, Inc., 59 Temple Place, Suite 330, Boston, 
 * MA 02111-1307  USA
 * 
 * You can contact the author at:
 *    Markus Fischer
 *    www.digitprop.com
 *    info@digitprop.com
 * ------------------------------------------------------------------------
 */
public class TonicLookAndFeel extends MetalLookAndFeel
{
	private static final boolean				isSlim=false;
	
	/**	The text foreground color */
	private static final ColorUIResource 	textFg=new ColorUIResource(Color.BLACK);
	
	/** 	The window background color */
	private static final ColorUIResource	windowBg=new ColorUIResource(220, 220, 220);
	
	/** 	The background for inactive windows */
	private static final ColorUIResource 	inactiveBg=new ColorUIResource(180, 180, 180);
	
	/**	The background for active windows */
	private static final ColorUIResource	activeBg=new ColorUIResource(220, 220, 220);
	
	/**	The highlight for active resources */
	private static final ColorUIResource 	activeHighlight=new ColorUIResource(Color.WHITE);
	
	/** 	The shadow for active resources */
	private static final ColorUIResource	activeShadow=new ColorUIResource(150, 150, 150);
	
	/**	The light shadow for active resources */
	private static final ColorUIResource 	activeLightShadow=new ColorUIResource(200, 200, 200);
	
	/**	The foreground color for selections */
	private static final ColorUIResource 	selFg=new ColorUIResource(Color.WHITE);
	
	/**	The background color for selections */
	private static final ColorUIResource	selBg=new ColorUIResource(0, 0, 153);
	
	/**	The background color for active ToolButtons */
	private static final ColorUIResource	toolButtonActiveBg=new ColorUIResource(173,173,209);
	
	/**	The border color for active ToolButtons */
	private static final ColorUIResource	toolButtonBorder=selBg;
	
	/**	The color of the focus */
	private static final ColorUIResource	focusColor=selBg; //focusColor=new ColorUIResource(100, 90, 130);
	
	/**	The color of inactive borders */
	private static final ColorUIResource	inactiveBorderColor=new ColorUIResource(100, 90, 90);
	
	/**	The border of components */
	private static final ColorUIResource	borderColor=new ColorUIResource(100, 90, 90);
	
	/**	The background for fields */
	private static final ColorUIResource	fieldBg=new ColorUIResource(Color.WHITE);
	
	/**	The color for tree lines */
	private static final ColorUIResource	treeLineColor=new ColorUIResource(220, 220, 220);
	
	/**	Inactive window gradient color 1 */
	private static final ColorUIResource	inactiveGradColor1=new ColorUIResource(200, 200, 200);
	
	/**	Inactive window gradient color 2 */
	private static final ColorUIResource	inactiveGradColor2=new ColorUIResource(Color.WHITE);

	/**	Active window gradient color 1 */	
	private static final ColorUIResource 	activeGradColor1=selBg;
	
	/**	Active window gradient color 2 */
	private static final ColorUIResource	activeGradColor2=new ColorUIResource(Color.WHITE);
	
	/**	Background for tool tips */	
	private static final ColorUIResource	toolTipBg=new ColorUIResource(255, 255, 240);
	
	/**	Standard font */
	private static FontUIResource				stdFont=new FontUIResource("Tahoma", Font.PLAIN, 11);
	
	/**	Standard bold font */
	private static final FontUIResource		stdBoldFont=new FontUIResource("Tahoma", Font.BOLD, 11);
	
	/**	Helper border to be used in compound border */
	private static final Border 				hb5=BorderFactory.createLineBorder(fieldBg, 1);
	
	/** 	Helper border to be used in compound border */
	private static final Border 				hb6=BorderFactory.createLineBorder(borderColor);
	
	/**	Helper border to be used in compound border */
	private static final Border				hb7=BorderFactory.createLineBorder(borderColor);
	
	/**	Helper border to be used in compound border */
	private static final Border				hb8=BorderFactory.createCompoundBorder(hb5, hb6);
	
	/**	Helper border to be used in compound border */
	private static final BorderUIResource.CompoundBorderUIResource internalFrameBorder=new BorderUIResource.CompoundBorderUIResource(hb7, hb8);
	
	/**	Helper border to be used in compound border */		
	private static final BorderUIResource	internalFrameBorder2=new BorderUIResource(new InternalFrameBorder(borderColor, inactiveBg, Color.WHITE));
	
	private static final BorderUIResource.LineBorderUIResource	plainDialogBorder=new BorderUIResource.LineBorderUIResource(Color.BLACK);	

	/**	Helper border to be used in compound border */
	private static final Border				hb1=BorderFactory.createMatteBorder(0, 0, 1, 0, activeHighlight);
	
	/**	Helper border to be used in compound border */
	private static final Border 				hb2=BorderFactory.createMatteBorder(0, 0, 1, 0, activeShadow);
	
	/**	Border for separating the menu (and toolbars) from the main panel */
	private static final BorderUIResource.CompoundBorderUIResource menuBarBorder=new BorderUIResource.CompoundBorderUIResource(hb1, hb2);
	
	/**	Helper border to be used in compound border */
	private static final Border				hb9=BorderFactory.createMatteBorder(0, 0, 0, 1, activeHighlight);
	
	/**	Helper border to be used in compound border */
	private static final Border 				hb10=BorderFactory.createMatteBorder(0, 0, 0, 1, activeShadow);
	
	/**	Border for separating the menu (and toolbars) from the main panel */
	private static final BorderUIResource.CompoundBorderUIResource toolBarVerticalBorder=new BorderUIResource.CompoundBorderUIResource(hb9, hb10);	

	private static final BorderUIResource.EmptyBorderUIResource	menuBorder=new BorderUIResource.EmptyBorderUIResource(2, 5, 2, 5);
	
	/**	Helper border to be used in compound border */
	private static final Border				hb3=BorderFactory.createEmptyBorder(2, 2, 2, 2);
	
	/**	Helper border to be used in compound border */
	private static final Border				hb4=BorderFactory.createLineBorder(borderColor);
	
	/**	Border for progress bars */
	private static final BorderUIResource.CompoundBorderUIResource progressBarBorder=new BorderUIResource.CompoundBorderUIResource(hb4, hb3);

	/**	Simple, single pixel border */
	private static final BorderUIResource.LineBorderUIResource popupMenuBorder=new BorderUIResource.LineBorderUIResource(focusColor);
	
	/**	Simple, single pixel border */
	private static final BorderUIResource.LineBorderUIResource simpleBorder=new BorderUIResource.LineBorderUIResource(borderColor);
	
	/**	Border for combo boxes */
	private static final BorderUIResource.LineBorderUIResource comboBoxBorder=new BorderUIResource.LineBorderUIResource(borderColor);
	
	
	/**	Is called by the UIManager.setLookAndFeel() method and
	 * 	creates the look and feel specific defaults table.
	 * 
	 * 	@see	UIManager#setLookAndFeel(LookAndFeel)
	 */ 
	public UIDefaults getDefaults()
	{
		UIDefaults result=super.getDefaults();	
			
		// Bind the UI classes to the respective components
		String packageName="com.digitprop.tonic.";		
		result.put("ButtonUI", 					packageName+"ButtonUI");
		result.put("FileChooserUI", 			packageName+"FileChooserUI");
		result.put("LabelUI",					packageName+"LabelUI");
		result.put("MenuBarUI", 				packageName+"MenuBarUI");
		result.put("MenuItemUI", 				packageName+"MenuItemUI");
		result.put("MenuUI", 					packageName+"MenuUI");
		result.put("SeparatorUI", 				packageName+"SeparatorUI");
		result.put("PopupMenuSeparatorUI", 	packageName+"PopupMenuSeparatorUI");
		result.put("CheckBoxMenuItemUI", 	packageName+"CheckBoxMenuItemUI");
		result.put("DesktopIconUI", 			packageName+"DesktopIconUI");
		result.put("RadioButtonMenuItemUI", packageName+"RadioButtonMenuItemUI");
		result.put("ComboBoxUI", 				packageName+"ComboBoxUI");
		result.put("ScrollBarUI", 				packageName+"ScrollBarUI");
		result.put("ToggleButtonUI", 			packageName+"ToggleButtonUI");
		result.put("RadioButtonUI", 			packageName+"RadioButtonUI");
		result.put("CheckBoxUI", 				packageName+"CheckBoxUI");
		result.put("InternalFrameUI", 		packageName+"InternalFrameUI");
		result.put("OptionPaneUI", 			packageName+"OptionPaneUI");
		result.put("RootPaneUI",				packageName+"RootPaneUI");		
		result.put("TabbedPaneUI", 			packageName+"TabbedPaneUI");
		result.put("TableUI", 					packageName+"TableUI");
		result.put("TableHeaderUI", 			packageName+"TableHeaderUI");
		result.put("ToolBarUI", 				packageName+"ToolBarUI");
		result.put("ToolButtonUI", 			packageName+"ToolButtonUI");
		result.put("ProgressBarUI", 			packageName+"ProgressBarUI");
		result.put("SliderUI", 					packageName+"SliderUI");
		result.put("SplitPaneUI", 				packageName+"SplitPaneUI");
		result.put("ScrollPaneUI", 			packageName+"ScrollPaneUI");
		result.put("SpinnerUI", 				packageName+"SpinnerUI");
		
		// Set parameters for visual appearance
		result.put("Button.font", stdFont);
		result.put("Button.borderColor", borderColor);
		result.put("Button.border", null);
		result.put("Button.focusBorderColor", focusColor);
		result.put("Button.disabledBorderColor", inactiveBorderColor);
		result.put("Button.background", windowBg); //new ColorUIResource(Color.WHITE));
		result.put("Button.highlight", activeHighlight);
		result.put("Button.textShiftOffset", new Integer(5));
		result.put("Button.margin", new InsetsUIResource(0, 7, 0, 7));

		result.put("CheckBox.background", windowBg);
		result.put("CheckBox.font", stdFont);

		result.put("CheckBoxMenuItem.background", windowBg);
		result.put("CheckBoxMenuItem.font", stdFont);
		result.put("CheckBoxMenuItem.selectionBackground", selBg);
		result.put("CheckBoxMenuItem.selectionForeground", selFg);
		result.put("CheckBoxMenuItem.acceleratorFont", stdFont);
		result.put("CheckBoxMenuItem.acceleratorForeground", textFg);
		result.put("CheckBoxMenuItem.acceleratorSelectionForeground", selFg);
		result.put("CheckBoxMenuItem.border", menuBorder);
		
		result.put("ComboBox.font", stdFont);
		result.put("ComboBox.background", fieldBg);
		result.put("ComboBox.selectionBackground", selBg);
		result.put("ComboBox.selectionForeground", selFg);
		result.put("ComboBox.border", comboBoxBorder);
		
		result.put("control", windowBg);
		result.put("controlHighlight", activeHighlight);
		result.put("controlShadow", activeShadow);

		result.put("Desktop.background", Color.WHITE);

		result.put("FileChooser.newFolderIcon", makeIcon(getClass(),"icons/filechooser_newfolder.gif"));
		result.put("FileChooser.upFolderIcon", makeIcon(getClass(),"icons/filechooser_back.gif"));
		result.put("FileChooser.homeFolderIcon", makeIcon(getClass(),"icons/home.gif"));
		result.put("FileChooser.detailsViewIcon", makeIcon(getClass(),"icons/filechooser_details.gif"));
		result.put("FileChooser.listViewIcon", makeIcon(getClass(),"icons/filechooser_list.gif"));
		result.put("FileChooser.folderIcon", makeIcon(getClass(),"icons/tree_closed.gif"));
		
		result.put("FormattedTextField.font", stdFont);
		result.put("FormattedTextField.border", new BorderUIResource.MatteBorderUIResource(1, 1, 1, 0, borderColor));
		result.put("FormattedTextField.selectionBackground", selBg);
		result.put("FormattedTextField.selectionForeground", selFg);

		result.put("InternalFrame.optionDialogBorder", internalFrameBorder); //new BorderUIResource.LineBorderUIResource(borderColor, 2));
		result.put("InternalFrame.paletteBorder", internalFrameBorder); //new BorderUIResource.LineBorderUIResource(borderColor, 2));
		result.put("InternalFrame.border", internalFrameBorder2); //new BorderUIResource.LineBorderUIResource(borderColor, 2));
	
		result.put("InternalFrame.closeIcon", LookAndFeel.makeIcon(getClass(),"icons/frame_close.gif"));
		result.put("InternalFrame.icon", LookAndFeel.makeIcon(getClass(),"icons/frame_icon.gif"));
		result.put("InternalFrame.maximizeIcon", LookAndFeel.makeIcon(getClass(),"icons/frame_max.gif"));
		result.put("InternalFrame.minimizeIcon", LookAndFeel.makeIcon(getClass(),"icons/frame_max.gif"));
		result.put("InternalFrame.iconifyIcon", LookAndFeel.makeIcon(getClass(),"icons/frame_min.gif"));
		result.put("InternalFrame.inactiveTitleBackground", inactiveGradColor1);
		result.put("InternalFrame.inactiveTitleGradientColor", inactiveGradColor2);
		result.put("InternalFrame.activeTitleBackground", activeGradColor1);
		result.put("InternalFrame.activeTitleGradientColor", activeGradColor2);
		result.put("InternalFrame.activeTitleForeground", selFg); 

		result.put("Label.font", stdFont);

		result.put("List.selectionForeground", selFg);
		result.put("List.selectionBackground", selBg);
		result.put("List.font", stdFont);
		result.put("List.focusCellHighlightBorder", new BorderUIResource.LineBorderUIResource(selBg));

		result.put("Menu.font", stdFont);
		result.put("Menu.background", windowBg);
		result.put("Menu.selectionBackground", selBg);
		result.put("Menu.selectionForeground", selFg);
		result.put("Menu.border", menuBorder);
				
		result.put("MenuBar.background", windowBg);
		result.put("MenuBar.border", menuBarBorder);
	
		result.put("MenuItem.font", stdFont);
		result.put("MenuItem.background", windowBg);
		result.put("MenuItem.selectionBackground", selBg);
		result.put("MenuItem.selectionForeground", selFg);
		result.put("MenuItem.acceleratorFont", stdFont);
		result.put("MenuItem.acceleratorForeground", textFg);
		result.put("MenuItem.acceleratorSelectionForeground", selFg);
		result.put("MenuItem.border", menuBorder);
		
		result.put("OptionPane.background", windowBg);
		result.put("OptionPane.border", new BorderUIResource.LineBorderUIResource(windowBg, 10));
		result.put("OptionPane.buttonAreaBorder", null);
		result.put("OptionPane.errorDialog.border.background", windowBg);
		result.put("OptionPane.errorDialog.titlePane.background", windowBg);
		result.put("OptionPane.questionDialog.border.background", windowBg);
		result.put("OptionPane.questionDialog.titlePane.background", windowBg);
		result.put("OptionPane.warningDialog.border.background", windowBg);
		result.put("OptionPane.warningDialog.titlePane.background", windowBg);	

		result.put("Panel.background", windowBg);
		
		result.put("PopupMenuSeparator.foreground", borderColor);
		result.put("PopupMenuSeparator.background", windowBg);
		result.put("PopupMenu.border", popupMenuBorder);
		
		result.put("ProgressBar.foreground", selBg);
		result.put("ProgressBar.cellLength", new Integer(10));
		result.put("ProgressBar.cellSpacing", new Integer(2));
		result.put("ProgressBar.border", progressBarBorder);
		
		result.put("RadioButton.background", windowBg);
		result.put("RadioButton.font", stdFont);
		result.put("RadioButton.focusColor", focusColor);

		result.put("RadioButtonMenuItem.background", windowBg);
		result.put("RadioButtonMenuItem.font", stdFont);
		result.put("RadioButtonMenuItem.selectionBackground", selBg);
		result.put("RadioButtonMenuItem.selectionForeground", selFg);
		result.put("RadioButtonMenuItem.acceleratorFont", stdFont);
		result.put("RadioButtonMenuItem.acceleratorForeground", textFg);
		result.put("RadioButtonMenuItem.acceleratorSelectionForeground", selFg);
		result.put("RadioButtonMenuItem.border", menuBorder);

		result.put("RootPane.frameBorder", internalFrameBorder2);
		result.put("RootPane.plainDialogBorder", plainDialogBorder);
		result.put("RootPane.informationDialogBorder", plainDialogBorder);
		result.put("RootPane.errorDialogBorder", plainDialogBorder);
		result.put("RootPane.colorChooserDialogBorder", plainDialogBorder);
		result.put("RootPane.fileChooserDialogBorder", plainDialogBorder);
		result.put("RootPane.questionDialogBorder", plainDialogBorder);
		result.put("RootPane.warningDialogBorder", plainDialogBorder);
		
		if(isSlim)
			result.put("ScrollBar.width", new Integer(14)); // activeShadow); //windowBg);
			
		result.put("ScrollBar.background", activeBg); // activeShadow); //windowBg);
		result.put("ScrollBar.thumb", activeHighlight);
		result.put("ScrollBar.thumbHighlight", activeHighlight);
		result.put("ScrollBar.thumbShadow", activeLightShadow);
		result.put("ScrollBar.thumbStripes", activeLightShadow);

		result.put("ScrollPane.viewportBorder", null);
		result.put("ScrollPane.border", new BorderUIResource.LineBorderUIResource(borderColor));

		result.put("Separator.foreground", borderColor);
		result.put("Separator.background", windowBg);

		result.put("Slider.background", windowBg);
		result.put("Slider.foreground", textFg);
		result.put("Slider.trackColor", activeBg);

		result.put("SplitPane.dividerSize", new Integer(4));
		result.put("SplitPane.oneTouchDividerSize", new Integer(8));
		result.put("SplitPane.background", windowBg);
		result.put("SplitPane.border", new BorderUIResource.MatteBorderUIResource(5, 5, 2, 2, windowBg));

		result.put("Spinner.border", null);

		result.put("TabbedPane.selectedTabPadInsets", new InsetsUIResource(2, 0, 0, 0));
		
		if(isSlim)
			result.put("TabbedPane.tabInsets", new InsetsUIResource(0, 6, 0, 6));
		else
			result.put("TabbedPane.tabInsets", new InsetsUIResource(3, 6, 2, 6));
			
		result.put("TabbedPane.font", stdFont);
		result.put("TabbedPane.thickBorders", new Boolean(false));
		result.put("TabbedPane.selectedFont", stdBoldFont);
		result.put("TabbedPane.selected", windowBg);
		result.put("TabbedPane.background", inactiveBg);

		result.put("Table.scrollPaneBorder", new BorderUIResource.LineBorderUIResource(borderColor));
		result.put("Table.selectionBackground", selBg);
		result.put("Table.selectionForeground", selFg);
		result.put("Table.focusCellBackground", selBg);
		result.put("Table.focusCellForeground", selFg);
		result.put("Table.focusCellHighlightBorder", null);
		result.put("TableHeader.cellBorder", new BorderUIResource.MatteBorderUIResource(0, 0, 1, 1, borderColor));

		result.put("TextArea.selectionBackground", selBg);
		result.put("TextArea.selectionForeground", selFg);

		result.put("TextField.selectionBackground", selBg);
		result.put("TextField.selectionForeground", selFg);
		result.put("TextField.border", simpleBorder);
						
		result.put("ToggleButton.border", null);
		result.put("ToggleButton.background", activeBg);

		result.put("ToolBar.background", windowBg);
		result.put("ToolBar.border", menuBarBorder);
		result.put("ToolBar.verticalBorder", toolBarVerticalBorder);
		
		result.put("ToolButton.activeBackground", toolButtonActiveBg);
		result.put("ToolButton.activeForeground", selFg);
		result.put("ToolButton.activeBorderColor", toolButtonBorder);
		
		result.put("ToolTip.background", toolTipBg);
		result.put("ToolTip.border", new BorderUIResource.LineBorderUIResource(borderColor));
		
		result.put("Tree.expandedIcon", LookAndFeel.makeIcon(getClass(),"icons/tree_expanded.gif"));
		result.put("Tree.leafIcon", LookAndFeel.makeIcon(getClass(),"icons/tree_leaf.gif"));
		result.put("Tree.collapsedIcon", LookAndFeel.makeIcon(getClass(),"icons/tree_collapsed.gif"));
		result.put("Tree.closedIcon", LookAndFeel.makeIcon(getClass(),"icons/tree_closed.gif"));
		result.put("Tree.openIcon", LookAndFeel.makeIcon(getClass(),"icons/tree_open.gif"));
		result.put("Tree.selectionBackground", selBg);
		result.put("Tree.selectionBorderColor", selBg);
		result.put("Tree.selectionForeground", selFg);
		result.put("Tree.textBackground", fieldBg);
		result.put("Tree.textForeground", textFg);
		result.put("Tree.font", stdFont);
		result.put("Tree.foreground", textFg);
		result.put("Tree.line", treeLineColor);
		result.put("Tree.hash", treeLineColor);
		result.put("Tree.rowHeight", new Integer(17));
		
		// Set constants for NetBeans (workaround for buggy NB)
		result.put("controlFont", new FontUIResource(new Font("Dialog", Font.PLAIN, 11)));
		
		// Associate the icons with their components
		setIcon(result, "Menu.arrowIcon", "icons/arrow.gif");
		setIcon(result, "Menu.invArrowIcon", "icons/invarrow.gif");
		setIcon(result, "CheckBoxMenuItem.uncheckIcon", "icons/menucheckbox0.gif");
		setIcon(result, "CheckBoxMenuItem.checkIcon", "icons/menucheckbox1.gif");
		setIcon(result, "RadioButtonMenuItem.uncheckIcon", "icons/menuradiobutton0.gif");
		setIcon(result, "RadioButtonMenuItem.checkIcon", "icons/menuradiobutton1.gif");
		setIcon(result, "ComboBox.icon", "icons/downarrow.gif");
		setIcon(result, "Arrow.down", "icons/downarrow.gif");
		setIcon(result, "Arrow.up", "icons/uparrow.gif");
		setIcon(result, "Arrow.left", "icons/leftarrow.gif");
		setIcon(result, "Arrow.right", "icons/rightarrow.gif");
		setIcon(result, "RadioButton.unselectedEnabledIcon", "icons/radiobutton0.gif");
		setIcon(result, "RadioButton.selectedEnabledIcon", "icons/radiobutton1.gif");
		setIcon(result, "RadioButton.unselectedDisabledIcon", "icons/radiobutton0.gif");
		setIcon(result, "RadioButton.selectedDisabledIcon", "icons/radiobutton1.gif");
		setIcon(result, "CheckBox.unselectedEnabledIcon", "icons/checkbox0.gif");
		setIcon(result, "CheckBox.selectedEnabledIcon", "icons/checkbox1.gif");
		setIcon(result, "CheckBox.unselectedDisabledIcon", "icons/checkbox0.gif");
		setIcon(result, "CheckBox.selectedDisabledIcon", "icons/checkbox1.gif");
		setIcon(result, "Slider.horizontalThumbIcon", "icons/sliderdown.gif");
		setIcon(result, "Slider.verticalThumbIcon", "icons/sliderright.gif");
		
		return result;
	}
	
	
	/**	Adds to the specified UIDefaults an association of the specified key with
	 * 	an icon made from the specified icon file name. The icon is created
	 * 	by calling makeIcon().
	 * 
	 * 	@param	uiDefaults			The UI defaults table for which the specified
	 * 										icon will be added
	 * 	@param	key					The key under which the icon will be added
	 * 	@param	iconFileName		The file name of the icon to be associated
	 * 										with the key
	 * 	@see		#makeIcon()
	 */
	private void setIcon(UIDefaults uiDefaults, String key, String iconFileName)
	{
		Object icon=LookAndFeel.makeIcon(getClass(), iconFileName);
		uiDefaults.put(key, icon);
	}
	
	
	/**	Returns a predefined icon associated with the specified key. This method
	 * 	grants access to Tonic themed icons fitting the visual appearance of the
	 * 	whole look and feel.
	 * 
	 * 	@param	key			The key for the icon to be returned
	 * 
	 * 	@return					The icon associated with the specified key, or
	 * 								null, if the key was invalid.
	 */
	public static ImageIcon getTonicIcon(String key)
	{
		URL url=TonicLookAndFeel.class.getResource("icons/collection/"+key);
		if(url!=null)
			return new ImageIcon(url);
		else
		{
			url=TonicLookAndFeel.class.getResource("icons/collection/"+key+".gif");
			if(url!=null)
				return new ImageIcon(url);
		}

		return null;
	}
	
	
	/**	Returns the color for the focus */
	public static ColorUIResource getFocusColor()
	{
		return focusColor;
	}


	public static ColorUIResource getPrimaryControlDarkShadow()
	{
		return selBg;
	}


	/**	Returns a one line description of this look and feel */
	public String getDescription()
	{
		return "Tonic look and feel";
	}
	

	/**	Returns a String that identifies this look and feel */
	public String getID()
	{
		return "TonicLF";
	}
	

	/**	Returns the name of this look and feel */
	public String getName()
	{
		return "Tonic Look and Feel";
	}
	

	/**	Returns true if the LookAndFeel returned RootPaneUI instances support 
	 * 	providing Window decorations in a JRootPane.
	 */
	public boolean getSupportsWindowDecorations()
	{
		return true;
	}
	

	/**	This is called before the first (and usually only) call to getDefaults.
	 * 
	 * 	@see	#getDefaults()
	 */
	public void initialize()
	{
		super.initialize();
	}
	

	/**	Returns true if this is the native look and feel of the current
	 * 	operating system.
	 */ 
	public boolean isNativeLookAndFeel()
	{
		return false;
	}
	

	/**	Returns true if the underlying platform supports or allows
	 * 	this look and feel
	 */
	public boolean isSupportedLookAndFeel()
	{
		return true;
	}
	

	/**	Invoked when the user attempts an invalid operation, such as pasting 
	 * 	into an uneditable JTextField that has focus.
	 */
	public void provideErrorFeedback(Component component)
	{
		super.provideErrorFeedback(component);
	}
	

	/**	UIManager.setLookAndFeel calls this method just before we're 
	 * 	replaced by a new default look and feel.
	 */
	public void uninitialize()
	{
		super.uninitialize();
	}
	
	
	public static void setStandardFont(Font font)
	{
		stdFont=new FontUIResource(font);
	}
	
	
//	/**
//		* Utility method that creates a UIDefaults.LazyValue that creates
//		* an ImageIcon UIResource for the specified <code>gifFile</code>
//		* filename.
//		*/
//	public static Object makeIcon(final Class baseClass, final String gifFile)
//	{
//		return new UIDefaults.LazyValue()
//		{
//			public Object createValue(UIDefaults table)
//			{
//				/* Copy resource into a byte array.  This is
//				 * necessary because several browsers consider
//				 * Class.getResource a security risk because it
//				 * can be used to load additional classes.
//				 * Class.getResourceAsStream just returns raw
//				 * bytes, which we can convert to an image.
//				 */
//				final byte[][] buffer= new byte[1][];
//				
//				try
//				{
//				SwingUtilities.invokeAndWait(new Runnable()
//				{
//					public void run()
//					{
//						try
//						{
//							InputStream resource=
//								baseClass.getResourceAsStream(gifFile);
//							if (resource == null)
//							{
//								return;
//							}
//							BufferedInputStream in= new BufferedInputStream(resource);
//							ByteArrayOutputStream out= new ByteArrayOutputStream(1024);
//							buffer[0]= new byte[1024];
//							int n;
//							while ((n= in.read(buffer[0])) > 0)
//							{
//								out.write(buffer[0], 0, n);
//							}
//							in.close();
//							out.flush();
//							buffer[0]= out.toByteArray();
//						}
//						catch (IOException ioe)
//						{
//							System.err.println(ioe.toString());
//							return;
//						}
//					}
//				});
//				}
//				catch(InterruptedException e)
//				{
//					System.err.println("Could not load icon - interrupted: "+e.getMessage());
//				} 
//				catch (InvocationTargetException e)
//				{
//					e.printStackTrace(System.err);
//				}
//
//				if (buffer[0] == null)
//				{
//					System.err.println(
//						baseClass.getName() + "/" + gifFile + " not found.");
//					return null;
//				}
//				if (buffer[0].length == 0)
//				{
//					System.err.println("warning: " + gifFile + " is zero-length");
//					return null;
//				}
//
//				return new ImageIcon(buffer[0]);
//			}
//		};
//	}
}
