package net.sourceforge.squirrel_sql.client.session.mainpanel;
/*
 * Copyright (C) 2003 Colin Bell
 * colbell@users.sourceforge.net
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
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboBoxRenderer;

import net.sourceforge.squirrel_sql.fw.gui.MemoryComboBox;
/**
 * This combobox holds the history of SQL statments executed.
 * 
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLHistoryComboBox extends MemoryComboBox
{
	public SQLHistoryComboBox(boolean useSharedModel)
	{
		super();
		setModel(new SQLHistoryComboBoxModel(useSharedModel));
		setRenderer(new Renderer());

		addActionListener(new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				updateToolTip();
			}
		});
	}

	public SQLHistoryComboBoxModel getTypedModel()
	{
		return (SQLHistoryComboBoxModel)getModel();
	}

	public void setUseSharedModel(boolean use)
	{
		getTypedModel().setUseSharedModel(use);
	}

	public boolean isUsingSharedDataModel()
	{
		return getTypedModel().isUsingSharedDataModel();
	}

	/**
	 * Ensure that the tooltip is still displayed when the combobox is
	 * collapsed.
	 */
	private void updateToolTip()
	{
		String tt = " ";
		SQLHistoryItem shi = (SQLHistoryItem)getSelectedItem();
		if (shi != null)
		{
			tt = formatToolTip(shi.getSQL());
		}
		Component[] comps = SQLHistoryComboBox.this.getComponents();
		for (int i = 0; i < comps.length; ++i)
		{
			if (comps[i] instanceof JComponent)
			{
				((JComponent)comps[i]).setToolTipText(tt);
			}
		}
	}

	private String formatToolTip(String tt)
	{
      final StringBuffer buf = new StringBuffer();
      buf.append("<HTML><PRE>");

      if (200 < tt.length())
      {
         buf.append(tt.substring(0, 200)).append(" ...");
      }
      else
      {
         buf.append(tt);
      }

      buf.append("</PRE></HTML>");
      return buf.toString();
   }

	public void dispose()
	{
		// The SQLHistoryComboBoxModel has a static member that prevents garbage collection.
		// Therefore we need to remove the model. 
		setModel(new DefaultComboBoxModel());
	}

	/**
	 * Renderer for this combobox. It displays the entire SQL for the current
	 * line as the tooltip. We use the HTML <PRE> tag in order to linebreak the
	 * SQL in the tooltip.
	 */
	private final class Renderer extends BasicComboBoxRenderer
	{
		public Component getListCellRendererComponent(JList list,
																	 Object value, int index, boolean isSelected,
																	 boolean cellHasFocus)
		{
			if (isSelected)
			{
				setBackground(list.getSelectionBackground());
				setForeground(list.getSelectionForeground());
				if (index != -1)
				{
					final String tt = ((SQLHistoryItem)value).getSQL();
					final String text = formatToolTip(tt);
					list.setToolTipText(text);
				}
			}
			else
			{
				setBackground(list.getBackground());
				setForeground(list.getForeground());
			}

			setFont(list.getFont());
			setText((value == null) ? "" : value.toString());

			return this;
		}
	}
}
