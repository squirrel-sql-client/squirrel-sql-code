package net.sourceforge.squirrel_sql.plugins.syntax.prefspanel;
/*
 * Copyright (C) 2003 Colin Bell
 * colbell@users.sourceforge.net
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
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JColorChooser;
import javax.swing.JToggleButton;
import javax.swing.JToolBar;

import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPluginResources;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxStyle;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

/**
 * This panel allows maintenance of the selected Syntax Style.
 *
 * @author <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class StyleMaintenancePanel extends JToolBar
{

	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(StyleMaintenancePanel.class);

	private final StylesList _list;
	private final JToggleButton _boldChk;
	private final JToggleButton _italicChk;
	private final JButton _fontColorBtn;
	private final JButton _backgroundColorBtn;
	private FontColorButtonListener _fontColorBtnLis;
	private BackgroundColorButtonListener _backgroundColorBtnLis;
	private ActionListener _toggleLis;

	private SyntaxStyle _style;

	public StyleMaintenancePanel(StylesList list, SyntaxPluginResources rsrc)
	{
		super();
		_list = list;

		this.setFloatable(false);

		_boldChk = new JToggleButton(rsrc.getIcon(SyntaxPluginResources.IKeys.BOLD_IMAGE));
		//i18n[syntax.bold=Bold]
		_boldChk.setToolTipText(s_stringMgr.getString("syntax.bold"));
		_italicChk = new JToggleButton(rsrc.getIcon(SyntaxPluginResources.IKeys.ITALIC_IMAGE));
		//i18n[syntax.italic=Italic]
		_italicChk.setToolTipText(s_stringMgr.getString("syntax.italic"));

		_fontColorBtn = new JButton(rsrc.getIcon(SyntaxPluginResources.IKeys.FOREGROUND_IMAGE));
		//i18n[syntax.font=Select font color]
		_fontColorBtn.setToolTipText(s_stringMgr.getString("syntax.font"));
		_backgroundColorBtn = new JButton(rsrc.getIcon(SyntaxPluginResources.IKeys.BACKGROUND_IMAGE));
		//i18n[syntax.background=Select background color]
		_backgroundColorBtn.setToolTipText(s_stringMgr.getString("syntax.background"));

		add(_boldChk);
		add(_italicChk);
		add(_fontColorBtn);
		add(_backgroundColorBtn);
	}

	/**
	 * Component has been added to its parent so setup listeners etc.
	 */
	public void addNotify()
	{
		super.addNotify();

		if (_fontColorBtnLis == null)
		{
			_fontColorBtnLis = new FontColorButtonListener(_list);
			_fontColorBtn.addActionListener(_fontColorBtnLis);
			_backgroundColorBtnLis = new BackgroundColorButtonListener(_list);
			_backgroundColorBtn.addActionListener(_backgroundColorBtnLis);
		}

		if (_toggleLis == null)
		{
			_toggleLis = new ToggleButtonListener();
			_boldChk.addActionListener(_toggleLis);
			_italicChk.addActionListener(_toggleLis);
		}
	}

	/**
	 * Component has been removed from its parent so remove listeners etc.
	 */
	public void removeNotify()
	{
		if (_fontColorBtnLis != null)
		{
			_fontColorBtn.removeActionListener(_fontColorBtnLis);
			_backgroundColorBtn.removeActionListener(_backgroundColorBtnLis);
			_fontColorBtnLis = null;
			_backgroundColorBtnLis = null;
		}
		if (_toggleLis != null)
		{
			_boldChk.removeActionListener(_toggleLis);
			_italicChk.removeActionListener(_toggleLis);
			_toggleLis = null;
		}

		super.removeNotify();
	}

	public void setEnabled(boolean enable)
	{
		_boldChk.setEnabled(enable);
		_italicChk.setEnabled(enable);
		_fontColorBtn.setEnabled(enable);
		_backgroundColorBtn.setEnabled(enable);
	}

	public void setStyle(SyntaxStyle style)
	{
		_boldChk.setSelected(style.isBold());
		_italicChk.setSelected(style.isItalic());
		_style = style;
	}

	private final class ToggleButtonListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			_style.setBold(_boldChk.isSelected());
			_style.setItalic(_italicChk.isSelected());
			_list.repaint();
		}
	}

	/**
	 * Listener for the Font Color selection button. Show a Color selection
	 * dialog and if the user selects a color update the current style with
	 * that color.
	 */
	private static class FontColorButtonListener implements ActionListener
	{
		private final StylesList _list;

		FontColorButtonListener(StylesList list)
		{
			super();
			_list = list;
		}

		public void actionPerformed(ActionEvent evt)
		{
			final SyntaxStyle style = _list.getSelectedSyntaxStyle();
			final int origRGB = style.getTextRGB();
			final Color color = JColorChooser.showDialog(null,
				//i18n[syntax.selColor=Select Color]
												s_stringMgr.getString("syntax.selColor"), new Color(origRGB));
			if (color != null)
			{
				style.setTextRGB(color.getRGB());
			}
		}
	}

	/**
	 * Listener for the Background Color selection button. Show a Color
	 * selection dialog and if the user selects a color update the current
	 * style with that color.
	 */
	private static class BackgroundColorButtonListener implements ActionListener
	{
		private final StylesList _list;

		BackgroundColorButtonListener(StylesList list)
		{
			super();
			_list = list;
		}

		public void actionPerformed(ActionEvent evt)
		{
			final SyntaxStyle style = _list.getSelectedSyntaxStyle();
			final int origRGB = style.getBackgroundRGB();
			final Color color = JColorChooser.showDialog(null,
				//i18n[syntax.selColor2=Select Color]
												s_stringMgr.getString("syntax.selColor2"), new Color(origRGB));
			if (color != null)
			{
				style.setBackgroundRGB(color.getRGB());
			}

		}
	}
}
