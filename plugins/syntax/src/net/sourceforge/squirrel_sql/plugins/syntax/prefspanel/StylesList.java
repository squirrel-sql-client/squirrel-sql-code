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
package net.sourceforge.squirrel_sql.plugins.syntax.prefspanel;

import java.awt.Color;
import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.ListCellRenderer;
import javax.swing.ListSelectionModel;
import javax.swing.UIManager;

import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxPreferences;
import net.sourceforge.squirrel_sql.plugins.syntax.SyntaxStyle;
/**
 * This control is a listbox containing SyntaxStyle objects.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class StylesList extends JList
{
	/** Defines indices in the Styles list for the individual styles. */
	public interface IStylesListIndices
	{
		int COMMENTS = 0;
		int DATA_TYPES = 1;
		int ERRORS = 2;
		int FUNCTIONS = 3;
		int IDENTIFIERS = 4;
		int LITERALS = 5;
		int OPERATORS = 6;
		int RESERVED_WORDS = 7;
		int SEPARATORS = 8;
		int WHITE_SPACE = 9;

		int LIST_SIZE = 10;
	}

// TODO: columns, tables 
	/** Titles for each style in the styles list. */
	private final static String[] s_styleTitles = new String[]
	{
		"Comments",
		"Data Types",
		"Errors",
		"Functions",
		"Identifiers",
		"Literals",
		"Operators",
		"Reserved Words",
		"Separators",
		"White Space",
	};

	private SyntaxStyle[] _styles = new SyntaxStyle[IStylesListIndices.LIST_SIZE];

	public StylesList()
	{
		super(new DefaultListModel());
		setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		setCellRenderer(new ListRenderer());
		setBorder(BorderFactory.createLineBorder(Color.gray));
	}

	public void loadData(SyntaxPreferences prefs)
	{
		removeAll();

		_styles[IStylesListIndices.COMMENTS] = new SyntaxStyle(prefs.getCommentStyle());
		_styles[IStylesListIndices.DATA_TYPES] = new SyntaxStyle(prefs.getDataTypeStyle());
		_styles[IStylesListIndices.ERRORS] = new SyntaxStyle(prefs.getErrorStyle());
		_styles[IStylesListIndices.FUNCTIONS] = new SyntaxStyle(prefs.getFunctionStyle());
		_styles[IStylesListIndices.IDENTIFIERS] = new SyntaxStyle(prefs.getIdentifierStyle());
		_styles[IStylesListIndices.LITERALS] = new SyntaxStyle(prefs.getLiteralStyle());
		_styles[IStylesListIndices.OPERATORS] = new SyntaxStyle(prefs.getOperatorStyle());
		_styles[IStylesListIndices.RESERVED_WORDS] = new SyntaxStyle(prefs.getReservedWordStyle());
		_styles[IStylesListIndices.SEPARATORS] = new SyntaxStyle(prefs.getSeparatorStyle());
		_styles[IStylesListIndices.WHITE_SPACE] = new SyntaxStyle(prefs.getWhiteSpaceStyle());

		final DefaultListModel model = (DefaultListModel)getModel();
		for (int i = 0; i < _styles.length; ++i)
		{
			model.addElement(_styles[i]);
		}

		setSelectedIndex(0);
	}

	public SyntaxStyle getSelectedSyntaxStyle()
	{
		return (SyntaxStyle)getSelectedValue();
	}

	public SyntaxStyle getSyntaxStyleAt(int idx)
	{
		return (SyntaxStyle)getModel().getElementAt(idx);
	}

	/**
	 * Renderer for this list.
	 */
	private static final class ListRenderer extends JLabel
											implements ListCellRenderer
	{
		ListRenderer()
		{
			super();
			setOpaque(true);
		}

		public Component getListCellRendererComponent(JList list,
							Object value, int idx, boolean isSelected,
							boolean cellHasFocus)
		{
			final SyntaxStyle style = (SyntaxStyle)value;
			setForeground(new Color(style.getTextRGB()));
			setBackground(new Color(style.getBackgroundRGB()));

//TODO:			setFont(style.createStyledFont(getFont()));
			setText(s_styleTitles[idx]);

			if (isSelected)
			{
				setBorder(UIManager.getBorder("Table.focusCellHighlightBorder"));
			}
			else
			{
				setBorder(BorderFactory.createEmptyBorder());
			}

			return this;
		}
	}
}

