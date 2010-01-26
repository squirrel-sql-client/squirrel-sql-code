package com.digitprop.tonic;


import java.awt.Toolkit;
import java.awt.event.*;
import javax.swing.*;
import sun.awt.dnd.SunDragSourceContextPeer;


/**	Default drag gesture recognition for drag operations performed by classses
 * 	that have a <code>dragEnabled</code> property.  The gesture for a drag in
 * 	this package is a mouse press over a selection followed by some movement
 * 	by enough pixels to keep it from being treated as a click.
 * 
 * 	@author  Markus Fischer
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
class TonicDragGestureRecognizer implements MouseListener, MouseMotionListener
{

	private MouseEvent dndArmedEvent= null;

	private static int motionThreshold;

	private static boolean checkedMotionThreshold= false;

	private static int getMotionThreshold()
	{
		if (checkedMotionThreshold)
		{
			return motionThreshold;
		}
		else
		{
			checkedMotionThreshold= true;
			try
			{
				motionThreshold=
					((Integer) Toolkit
						.getDefaultToolkit()
						.getDesktopProperty("DnD.gestureMotionThreshold"))
						.intValue();
			}
			catch (Exception e)
			{
				motionThreshold= 5;
			}
		}
		return motionThreshold;
	}

	protected int mapDragOperationFromModifiers(MouseEvent e)
	{
		int mods= e.getModifiersEx();

		if ((mods & InputEvent.BUTTON1_DOWN_MASK)
			!= InputEvent.BUTTON1_DOWN_MASK)
		{
			return TransferHandler.NONE;
		}

		JComponent c= getComponent(e);
		TransferHandler th= c.getTransferHandler();
		return SunDragSourceContextPeer.convertModifiersToDropAction(
			mods,
			th.getSourceActions(c));
	}

	public void mouseClicked(MouseEvent e)
	{
	}

	public void mousePressed(MouseEvent e)
	{
		dndArmedEvent= null;

		if (isDragPossible(e)
			&& mapDragOperationFromModifiers(e) != TransferHandler.NONE)
		{
			dndArmedEvent= e;
			e.consume();
		}
	}

	public void mouseReleased(MouseEvent e)
	{
		dndArmedEvent= null;
	}

	public void mouseEntered(MouseEvent e)
	{
		//dndArmedEvent = null;
	}

	public void mouseExited(MouseEvent e)
	{
		//if (dndArmedEvent != null && mapDragOperationFromModifiers(e) == TransferHandler.NONE) {
		//    dndArmedEvent = null;
		//}
	}

	public void mouseDragged(MouseEvent e)
	{
		if (dndArmedEvent != null)
		{
			e.consume();

			int action= mapDragOperationFromModifiers(e);

			if (action == TransferHandler.NONE)
			{
				return;
			}

			int dx= Math.abs(e.getX() - dndArmedEvent.getX());
			int dy= Math.abs(e.getY() - dndArmedEvent.getY());
			if ((dx > getMotionThreshold()) || (dy > getMotionThreshold()))
			{
				// start transfer... shouldn't be a click at this point
				JComponent c= getComponent(e);
				TransferHandler th= c.getTransferHandler();
				th.exportAsDrag(c, dndArmedEvent, action);
				dndArmedEvent= null;
			}
		}
	}

	public void mouseMoved(MouseEvent e)
	{
	}

	private TransferHandler getTransferHandler(MouseEvent e)
	{
		JComponent c= getComponent(e);
		return c == null ? null : c.getTransferHandler();
	}

	/**
	 * Determines if the following are true:
	 * <ul>
	 * <li>the press event is located over a selection
	 * <li>the dragEnabled property is true
	 * <li>A TranferHandler is installed
	 * </ul>
	 * <p>
	 * This is implemented to check for a TransferHandler.
	 * Subclasses should perform the remaining conditions.
	 */
	protected boolean isDragPossible(MouseEvent e)
	{
		JComponent c= getComponent(e);
		return (c == null) ? true : (c.getTransferHandler() != null);
	}

	protected JComponent getComponent(MouseEvent e)
	{
		Object src= e.getSource();
		if (src instanceof JComponent)
		{
			JComponent c= (JComponent) src;
			return c;
		}
		return null;
	}

}
