package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2002 Johan Compagner
 * jcompagner@j-com.nl
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
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JTable;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Image;
import java.awt.Insets;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.net.URL;

/**
 * @version 	$Id: ButtonTableHeader.java,v 1.6 2002-03-26 12:15:34 colbell Exp $
 * @author		Johan Compagner
 */
public class ButtonTableHeader extends JTableHeader
{
	protected boolean _bDragged;
	protected boolean _bPressed;
	protected int _iPressed;
	private static ImageIcon desc;
	private static ImageIcon asc;

	protected ImageIcon current;
	static
	{
		// HARDWIRED path names!! Should be in Resource/Settings file??
		try
		{
			URL url = ClassLoader.getSystemResource("net/sourceforge/squirrel_sql/fw/gui/image/stock_down_arrow.png");
			desc = new ImageIcon(url);
		} catch(Exception e){}
		try
		{
			URL url = ClassLoader.getSystemResource("net/sourceforge/squirrel_sql/fw/gui/image/stock_up_arrow.png");
			asc = new ImageIcon(url);
		} catch(Exception e){}
	}
	/**
	 * Constructor for ButtonTableHeader.
	 * @param cm
	 */
	public ButtonTableHeader()
	{
		super();
		_bPressed = false;
		_bDragged = false;
		_iPressed = -1;

		setDefaultRenderer(new ButtonTableRenderer(getFont()));

		HeaderListener hl = new HeaderListener();
		addMouseListener(hl);
		addMouseMotionListener(hl);
	}

	class HeaderListener extends MouseAdapter implements MouseMotionListener
	{
		/*
		 * @see MouseListener#mousePressed(MouseEvent)
		 */
		public void mousePressed(MouseEvent e)
		{
			_bPressed = true;
			_iPressed = columnAtPoint(e.getPoint());
			current = null;
			repaint();
		}

		/*
		* @see MouseListener#mouseReleased(MouseEvent)
		*/
		public void mouseReleased(MouseEvent e)
		{
			_bPressed = false;
			if(!_bDragged)
			{
				int column = getTable().convertColumnIndexToModel(_iPressed);
				TableModel tm = table.getModel();

				if(column > -1 && column < tm.getColumnCount() && tm instanceof SortableTableModel)
				{
					((SortableTableModel)tm).sortByColumn(column);
					if(((SortableTableModel)tm)._bAscending)
					{
						current = asc;
					}
					else
					{
						current = desc;
					}
				}
				repaint();
			}
			_bDragged = false;
		}

		/*
		 * @see MouseMotionListener#mouseDragged(MouseEvent)
		 */
		public void mouseDragged(MouseEvent e)
		{
			_bDragged = true;
			if(_bPressed)
			{
				current = null;
				_bPressed = false;
				repaint();
			}
		}

		/*
		 * @see MouseMotionListener#mouseMoved(MouseEvent)
		 */
		public void mouseMoved(MouseEvent e)
		{
			_bDragged = false;
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

			buttonLowered.setMinimumSize(new Dimension(50,25));
			buttonRaised.setMinimumSize(new Dimension(50,25));
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
			if(_iPressed == column && _bPressed)
			{
				buttonLowered.setText(value.toString());
				if(current != null) buttonLowered.setIcon(current);
				return buttonLowered;
			}
			else
			{
				buttonRaised.setText(value.toString());
				if(current != null && column == _iPressed) buttonRaised.setIcon(current);
				else buttonRaised.setIcon(null);
				return buttonRaised;
			}
		}

	}

}