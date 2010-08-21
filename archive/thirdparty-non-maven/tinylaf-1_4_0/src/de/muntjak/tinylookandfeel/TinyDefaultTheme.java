/* * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *	This file is part of the Tiny Look and Feel                                *
 *  Copyright 2003 - 2008  Hans Bickel                                         *
 *                                                                             *
 *  For licensing information and credits, please refer to the                 *
 *  comment in file de.muntjak.tinylookandfeel.TinyLookAndFeel                 *
 *                                                                             *
 * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * */

package de.muntjak.tinylookandfeel;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.*;
import javax.swing.plaf.BorderUIResource;
import javax.swing.plaf.ColorUIResource;
import javax.swing.plaf.FontUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.metal.DefaultMetalTheme;

import de.muntjak.tinylookandfeel.controlpanel.*;
import de.muntjak.tinylookandfeel.util.ColorRoutines;
import de.muntjak.tinylookandfeel.util.DrawRoutines;
import de.muntjak.tinylookandfeel.util.HSBReference;

/**
 * TinyDefaultTheme
 *
 * @version 1.1
 * @author Hans Bickel
 */
public class TinyDefaultTheme extends DefaultMetalTheme {

	/**
	 * Adds some custom values to the defaults table.
	 *
	 * @param table The UI defaults table.
	 */
	public void addCustomEntriesToTable(UIDefaults table) {
		super.addCustomEntriesToTable(table);

		table.put("Button.margin", Theme.buttonMargin);
		table.put("CheckBox.margin", Theme.checkMargin);
		table.put("RadioButton.margin", Theme.checkMargin);
		table.put("Button.background", Theme.buttonNormalColor.getColor());
		table.put("Button.font", Theme.buttonFont.getFont());
		table.put("CheckBox.font", Theme.checkFont.getFont());
		table.put("CheckBoxMenuItem.font", Theme.menuItemFont.getFont());
		table.put("ComboBox.font", Theme.comboFont.getFont());
		table.put("Label.font", Theme.labelFont.getFont());
		table.put("List.font", Theme.listFont.getFont());
		table.put("Menu.font", Theme.menuFont.getFont());
		table.put("MenuItem.font", Theme.menuItemFont.getFont());
		table.put("ProgressBar.font", Theme.progressBarFont.getFont());
		table.put("RadioButton.font", Theme.radioFont.getFont());
		table.put("RadioButtonMenuItem.font", Theme.menuItemFont.getFont());
		table.put("Table.font", Theme.tableFont.getFont());
		table.put("TableHeader.font", Theme.tableHeaderFont.getFont());
		table.put("TitledBorder.font", Theme.titledBorderFont.getFont());
		table.put("ToolTip.font", Theme.toolTipFont.getFont());
		table.put("Tree.font", Theme.treeFont.getFont());
		table.put("PasswordField.font", Theme.passwordFont.getFont());
		table.put("TextArea.font", Theme.textAreaFont.getFont());
		table.put("TextField.font", Theme.textFieldFont.getFont());
		table.put("FormattedTextField.font", Theme.textFieldFont.getFont());
		table.put("TextPane.font", Theme.textPaneFont.getFont());
		table.put("EditorPane.font", Theme.editorFont.getFont());
		table.put("InternalFrame.font", Theme.editorFont.getFont());
		// font for internal frames and palettes
		table.put("InternalFrame.normalTitleFont", Theme.internalFrameTitleFont.getFont());
		table.put("InternalFrame.paletteTitleFont", Theme.internalPaletteTitleFont.getFont());
		// font for (decorized) frame
		table.put("Frame.titleFont", Theme.frameTitleFont.getFont());
		
		table.put("TabbedPane.font", Theme.tabFont.getFont());

		table.put("Button.foreground", Theme.buttonFontColor.getColor());
		table.put("CheckBox.foreground", Theme.checkFontColor.getColor());
		table.put("Menu.foreground", Theme.menuFontColor.getColor());
		table.put("MenuItem.foreground", Theme.menuItemFontColor.getColor());
		table.put("CheckBoxMenuItem.foreground", Theme.menuItemFontColor.getColor());
		table.put("RadioButtonMenuItem.foreground", Theme.menuItemFontColor.getColor());
		table.put("RadioButton.foreground", Theme.radioFontColor.getColor());
		table.put("TabbedPane.foreground", Theme.tabFontColor.getColor());
		table.put("TitledBorder.titleColor", Theme.titledBorderFontColor.getColor());
		table.put("Label.foreground", Theme.labelFontColor.getColor());
		table.put("TableHeader.foreground", Theme.tableHeaderFontColor.getColor());
		table.put("TableHeader.background", Theme.tableHeaderBackColor.getColor());
		table.put("Table.foreground", Theme.tableFontColor.getColor());
		table.put("Table.background", Theme.tableBackColor.getColor());
		table.put("Table.selectionForeground", Theme.tableSelectedForeColor.getColor());
		table.put("Table.selectionBackground", Theme.tableSelectedBackColor.getColor());
		table.put("Table.gridColor", Theme.tableGridColor.getColor());
		
		// New in 1.4.0
		table.put("Table.focusCellHighlightBorder", new BorderUIResource(
			new LineBorder(Theme.tableFocusBorderColor.getColor())));
		
		// New in 1.4.0
		table.put("Table.alternateRowColor", Theme.tableAlternateRowColor.getColor());
		
		table.put("ProgressBar.foreground", Theme.progressColor.getColor());
		table.put("ProgressBar.background", Theme.progressTrackColor.getColor());
		table.put("ProgressBar.selectionForeground", Theme.progressSelectForeColor.getColor());
		table.put("ProgressBar.selectionBackground", Theme.progressSelectBackColor.getColor());
		table.put("PopupMenu.background", Theme.menuPopupColor);

		// Note: TabbedPane.background is the default background color of unselected tabs,
		// whereas TabbedPane.unselectedBackground has no effect.
		table.put("TabbedPane.background", Theme.tabNormalColor.getColor());
		table.put("TabbedPane.tabAreaInsets", Theme.tabAreaInsets);
		table.put("TabbedPane.tabInsets", Theme.tabInsets);

		table.put("MenuBar.background", Theme.menuBarColor.getColor());
		table.put("ToolBar.background", Theme.toolBarColor.getColor());

		table.put("EditorPane.caretForeground", Theme.textCaretColor.getColor());
		table.put("PasswordField.caretForeground", Theme.textCaretColor.getColor());
		table.put("TextArea.caretForeground", Theme.textCaretColor.getColor());
		table.put("TextField.caretForeground", Theme.textCaretColor.getColor());
		table.put("FormattedTextField.caretForeground", Theme.textCaretColor.getColor());

		table.put("List.foreground", Theme.listTextColor.getColor());
		table.put("List.background", Theme.listBgColor.getColor());
		table.put("ComboBox.foreground", Theme.comboTextColor.getColor());
		table.put("ComboBox.background", Theme.comboBgColor.getColor());
		table.put("ComboBox.disabledBackground", Theme.textDisabledBgColor.getColor());
		table.put("EditorPane.background", Theme.textBgColor.getColor());
		table.put("EditorPane.foreground", Theme.textTextColor.getColor());
		table.put("PasswordField.background", Theme.textBgColor.getColor());
		table.put("PasswordField.foreground", Theme.textTextColor.getColor());
		table.put("PasswordField.inactiveBackground", Theme.textDisabledBgColor.getColor());
		table.put("TextArea.background", Theme.textBgColor.getColor());
		table.put("TextArea.foreground", Theme.textTextColor.getColor());
		table.put("TextArea.inactiveBackground", Theme.textDisabledBgColor.getColor());
		table.put("TextField.background", Theme.textBgColor.getColor());
		table.put("TextField.foreground", Theme.textTextColor.getColor());		
		table.put("TextField.inactiveBackground", Theme.textDisabledBgColor.getColor());
		table.put("FormattedTextField.background", Theme.textBgColor.getColor());
		table.put("FormattedTextField.foreground", Theme.textTextColor.getColor());
		table.put("FormattedTextField.inactiveBackground", Theme.textDisabledBgColor.getColor());
		table.put("TextPane.background", Theme.textPaneBgColor.getColor());
		table.put("EditorPane.background", Theme.editorPaneBgColor.getColor());
		table.put("OptionPane.messageForeground", Theme.textTextColor.getColor());
		
		table.put("PasswordField.selectionBackground", Theme.textSelectedBgColor.getColor());
		table.put("PasswordField.selectionForeground", Theme.textSelectedTextColor.getColor());
		table.put("TextField.selectionBackground", Theme.textSelectedBgColor.getColor());
		table.put("TextField.selectionForeground", Theme.textSelectedTextColor.getColor());
		table.put("FormattedTextField.selectionBackground", Theme.textSelectedBgColor.getColor());
		table.put("FormattedTextField.selectionForeground", Theme.textSelectedTextColor.getColor());
		table.put("TextArea.selectionBackground", Theme.textSelectedBgColor.getColor());
		table.put("TextArea.selectionForeground", Theme.textSelectedTextColor.getColor());
		table.put("TextPane.selectionBackground", Theme.textSelectedBgColor.getColor());
		table.put("TextPane.selectionForeground", Theme.textSelectedTextColor.getColor());

		table.put("ComboBox.selectionBackground", Theme.comboSelectedBgColor.getColor());
		table.put("ComboBox.selectionForeground", Theme.comboSelectedTextColor.getColor());
		table.put("ComboBox.focusBackground", Theme.comboSelectedBgColor.getColor());

		table.put("List.selectionForeground", Theme.listSelectedTextColor.getColor());
		table.put("List.selectionBackground", Theme.listSelectedBgColor.getColor());
		// new in 1.4.0
		table.put("List.focusCellHighlightBorder", new BorderUIResource(
			new LineBorder(Theme.listFocusBorderColor.getColor())));

		table.put("Tree.background", Theme.treeBgColor.getColor());
		table.put("Tree.textBackground", Theme.treeTextBgColor.getColor());
		table.put("Tree.textForeground", Theme.treeTextColor.getColor());
		table.put("Tree.selectionBackground", Theme.treeSelectedBgColor.getColor());
		table.put("Tree.selectionForeground", Theme.treeSelectedTextColor.getColor());
		table.put("Tree.hash", Theme.treeLineColor.getColor());
		table.put("Tree.line", Theme.treeLineColor.getColor());

		table.put("Button.disabledText", Theme.buttonDisabledFgColor.getColor());
		table.put("CheckBox.disabledText", Theme.checkDisabledFgColor.getColor());
		table.put("RadioButton.disabledText", Theme.radioDisabledFgColor.getColor());
		table.put("ToggleButton.disabledText", Theme.disColor.getColor());
		table.put("ToggleButton.disabledSelectedText", Theme.disColor.getColor());
		table.put("TextArea.inactiveForeground", Theme.disColor.getColor());
		table.put("TextField.inactiveForeground", Theme.disColor.getColor());
		table.put("FormattedTextField.inactiveForeground", Theme.disColor.getColor());
		table.put("TextPane.inactiveForeground", Theme.disColor.getColor());
		table.put("PasswordField.inactiveForeground", Theme.disColor.getColor());
		table.put("ComboBox.disabledForeground", Theme.disColor.getColor());
		table.put("Label.disabledForeground", Theme.disColor.getColor());
		table.put("textInactiveText", Theme.disColor.getColor());

		table.put("Desktop.background", Theme.desktopPaneBgColor.getColor());
		table.put("Separator.background", Theme.separatorColor.getColor());
		// not needed since 1.4 (XP separator has only background)
//		table.put("Separator.foreground", Theme.sepLightColor.getColor());

		table.put("TitledBorder.border", new LineBorder(
			Theme.titledBorderColor.getColor()));

		table.put("ToolTip.background", Theme.tipBgColor.getColor());
		table.put("ToolTip.backgroundInactive", Theme.tipBgDis.getColor());
		table.put("ToolTip.foreground", Theme.tipTextColor.getColor());
		table.put("ToolTip.foregroundInactive", Theme.tipTextDis.getColor());

		table.put("Panel.background", Theme.backColor.getColor());

		// set default icons and colorize selected icons
		Icon icon = null;
		
		for(int i = 0; i < 20; i++) {
			if(Theme.colorize[i].getValue()) {
				icon = TinyLookAndFeel.getUncolorizedSystemIcon(i);
				
				if(icon != null && (icon instanceof ImageIcon)) {
					table.put(TinyLookAndFeel.getSystemIconName(i),
						DrawRoutines.colorizeIcon(
							((ImageIcon)icon).getImage(), Theme.colorizer[i]));
				}
				else {
					table.put(TinyLookAndFeel.getSystemIconName(i), icon);
				}
			}
		}
	}

	public String getName() {
		return "TinyLaF Default Theme";
	}

	/**
	 * Returns the third secondary color. This is the panel background color.
	 *
	 * @return The third secondary color
	 */
	protected ColorUIResource getSecondary3() {
		return Theme.backColor.getColor();
	}
	
	/**
	 * Returns the control text font, used for slider labels.
	 * @return the control text font
	 */
	public FontUIResource getControlTextFont() { 
        return Theme.labelFont.getFont();
    }
}