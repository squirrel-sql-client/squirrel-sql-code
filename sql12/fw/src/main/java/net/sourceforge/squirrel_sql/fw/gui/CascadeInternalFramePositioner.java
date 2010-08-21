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
import java.awt.*;
import java.beans.PropertyVetoException;

import javax.swing.JInternalFrame;
/**
 * This class will position an internal frame slightly below and to the
 * right of the internal frame that it previously positioned.
 */
public class CascadeInternalFramePositioner implements IInternalFramePositioner
{
	private int _x = INITIAL_POS;
	private int _y = INITIAL_POS;

	private static final int MOVE = 20;
	private static final int INITIAL_POS = 5;

	public CascadeInternalFramePositioner()
	{
		super();
	}

	public void positionInternalFrame(JInternalFrame child)
	{
		if (child == null)
		{
			throw new IllegalArgumentException("null JInternalFrame passed");
		}

      boolean toInitialPos = false;

		if (!child.isClosed())
		{
			if (child.getParent() != null)
			{
            Dimension childSize = child.getSize();

            if(0 == childSize.width || 0 == childSize.height)
            {
               toInitialPos = true;
            }
            else
            {
               Rectangle parentBounds = child.getParent().getBounds();
               if (_x + MOVE  + childSize.width >= parentBounds.width)
               {
                  _x = INITIAL_POS;
               }
               if (_y + MOVE + childSize.height >= parentBounds.height)
               {
                  _y = INITIAL_POS;
               }
            }
			}
			if (child.isIcon())
			{
				try
				{
					child.setIcon(false);
				}
				catch (PropertyVetoException ignore)
				{
					// Ignore.
				}
			}
			else if (child.isMaximum())
			{
				try
				{
					child.setMaximum(false);
				}
				catch (PropertyVetoException ignore)
				{
					// Ignore.
				}
			}

         if(toInitialPos)
         {
            child.setBounds(INITIAL_POS, INITIAL_POS, child.getWidth(), child.getHeight());
         }
         else
         {
            child.setBounds(_x, _y, child.getWidth(), child.getHeight());
            _x += MOVE;
            _y += MOVE;
         }
		}
	}
}
