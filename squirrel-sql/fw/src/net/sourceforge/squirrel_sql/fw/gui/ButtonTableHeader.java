package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2002 Johan Compagner
 * jcompagner@j-com.nl
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

import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import java.awt.Component;
import java.awt.Font;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * @version 	1.0
 * @author
 */
public class ButtonTableHeader extends JTableHeader
{
	protected int _iPressed;

	/**
	 * Constructor for ButtonTableHeader.
	 * @param cm
	 */
	public ButtonTableHeader(TableColumnModel cm)
	{
		super(cm);
		_iPressed = -1;
		setDefaultRenderer(new ButtonTableRenderer(getFont()));
		addMouseListener(new HeaderListener());
	}

	class HeaderListener extends MouseAdapter
	{
		/*
		 * @see MouseListener#mousePressed(MouseEvent)
		 */
		public void mousePressed(MouseEvent e)
		{
			_iPressed = columnAtPoint(e.getPoint());
			repaint();
		}

		/*
		* @see MouseListener#mouseReleased(MouseEvent)
		*/
		public void mouseReleased(MouseEvent e)
		{
			_iPressed = -1;
			int column = columnAtPoint(e.getPoint());
			TableModel tm = table.getModel();

			if(column > -1 && column < tm.getColumnCount() && tm instanceof SortableTableModel)
			{
				((SortableTableModel)tm).sortByColumn(column);
			}
			repaint();
		}

	}
	protected class ButtonTableRenderer implements TableCellRenderer
	{
		JButton buttonRaised;
		JButton buttonLowered;

		ButtonTableRenderer(Font font)
		{
			buttonRaised = new JButton();
			buttonRaised.setMargin(new Insets(0,0,0,0));
			buttonRaised.setFont(font);
			buttonLowered = new JButton();
			buttonLowered.setMargin(new Insets(0,0,0,0));
			buttonLowered.setFont(font);
			buttonLowered.getModel().setArmed(true);
			buttonLowered.getModel().setPressed(true);
		}
		/*
		 * @see TableCellRenderer#getTableCellRendererComponent(JTable, Object, boolean, boolean, int, int)
		 */
		public Component getTableCellRendererComponent(
			JTable table,
			Object value,
			boolean isSelected,
			boolean hasFocus,
			int row,
			int column)
		{
			if (value == null)
				value = "";
			if(_iPressed == column)
			{
				buttonLowered.setText(value.toString());
				return buttonLowered;
			}
			else
			{
				buttonRaised.setText(value.toString());
				return buttonRaised;
			}
		}

	}

}