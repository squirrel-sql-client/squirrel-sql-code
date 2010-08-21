package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2001-2003 Colin Bell
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
import javax.swing.*;

public class MemoryComboBox extends JComboBox
{
    private static final long serialVersionUID = -1059386228875239787L;

    public final static int NO_MAX = -1;

	private int _maxMemoryCount = NO_MAX;

	public MemoryComboBox()
	{
		this(NO_MAX);
	}

	public MemoryComboBox(int maxMemoryCount)
	{
		super();
		setMaxMemoryCount(maxMemoryCount);
	}

	public void setMaxMemoryCount(int value)
	{
		_maxMemoryCount = (value > NO_MAX) ? value : NO_MAX;
	}

	public void addItem(Object item)
	{
		if (item != null)
		{
			removeItem(item);
			insertItemAt(item, 0);
			setSelectedIndex(0);
			if (_maxMemoryCount > NO_MAX && getItemCount() > _maxMemoryCount)
			{
				removeItemAt(getItemCount() - 1);
			}
		}
	}

   public void insertItemAt(Object anObject, int index)
   {
      super.insertItemAt(anObject, index);

      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            while (_maxMemoryCount > NO_MAX && getItemCount() > _maxMemoryCount)
            {
               removeItemAt(0);
            }
         }
      });
   }

}
