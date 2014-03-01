/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
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

package net.sourceforge.squirrel_sql.fw.gui.debug;

import java.awt.AWTEvent;
import java.awt.Container;
import java.awt.Toolkit;
import java.awt.event.AWTEventListener;
import java.awt.event.MouseEvent;

import javax.swing.AbstractButton;
import javax.swing.JComponent;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;

import org.apache.commons.lang.StringUtils;


/**
 *  This code was developed originally by Romain Guy.  The original - and simpler - version is here:
 *  
 *  http://www.jroller.com/gfx/entry/dynamic_debugging_with_swing
 *  
 *  This is an event listener that listens for mouse events (enter, exit and drag) and installs a 
 *  highlighting border in the component that has been entered and restores the original border in the 
 *  component that is exited.  This will also set the tooltip text of the component to the name or class
 *  to help with finding the source code for the component being highlighted.
 */
public class DebugEventListener implements AWTEventListener
{
	public void setEnabled(boolean enable) {
		Toolkit kit = Toolkit.getDefaultToolkit();
		if (enable) {
			/* register as a listener for mouse events */
			kit.addAWTEventListener(this, AWTEvent.MOUSE_EVENT_MASK|AWTEvent.MOUSE_MOTION_EVENT_MASK);
			
			/* show all tooltips for ten seconds before hiding */
			ToolTipManager.sharedInstance().setDismissDelay(10000);			
		} else {
			kit.removeAWTEventListener(this);
		}
	}
	
	public void eventDispatched(AWTEvent event)
	{
		Object o = event.getSource();
		if (o instanceof JComponent && o != null)
		{
			JComponent source = (JComponent) o;
			switch (event.getID())
			{
			case MouseEvent.MOUSE_DRAGGED:
				printDebugInfo(source, event);
				break;
			case MouseEvent.MOUSE_ENTERED:
				printDebugInfo(source, event);
				setToolTipText(source, event);
				setBorder(source, event);
				break;
			case MouseEvent.MOUSE_EXITED:
				printDebugInfo(source, event);
				setBorder(source, event);
				break;
			}
		}
	}

	private void setBorder(JComponent source, AWTEvent event) {
		Border border = source.getBorder();
		switch (event.getID())
		{
		case MouseEvent.MOUSE_ENTERED:
			if (border != null)
			{
				source.setBorder(new DebugBorder(border));
			}
			break;			
		case MouseEvent.MOUSE_EXITED:
			if (border != null && border instanceof DebugBorder)
			{
				source.setBorder(((DebugBorder) border).getDelegate());
			}
			break;			
		}
	}
	
	private void setToolTipText(JComponent source, AWTEvent event) {
		
		Container parent = source.getParent();
		String sourceName = source.getName();
		String sourceClassName = source.getClass().toString();
		String parentClassName = parent == null ? null : parent.getClass().toString();
		
		StringBuilder toolTipText = new StringBuilder(getEventMessagePrefix(event));
		
		if (source instanceof AbstractButton) { 
			toolTipText.append("Button with parentClass=");
			toolTipText.append(parentClassName);
		} else {
			if (!StringUtils.isEmpty(sourceName)) {
				toolTipText.append(sourceName);
			} else if (!StringUtils.isEmpty(sourceClassName)) {
				toolTipText.append(sourceClassName);
			} 
		}
		source.setToolTipText(toolTipText.toString());
	}

	private void printDebugInfo(JComponent source, AWTEvent event)
	{
		Container parent = source.getParent();
		String sourceName = source.getName();
		String sourceClassName = source.getClass().toString();			
		String parentName = parent == null ? null : parent.getName();
		String parentClassName = parent == null ? null : parent.getClass().toString();
		
		StringBuilder msg = new StringBuilder(getEventMessagePrefix(event));
		msg.append("\n");
		msg.append("\t sourceName:").append(sourceName).append("\n");
		msg.append("\t sourceClassName:").append(sourceClassName).append("\n");
		msg.append("\t parentName:").append(parentName).append("\n");
		msg.append("\t parentClassName:").append(parentClassName);
		System.out.println(msg.toString());
	}
	
	private String getEventMessagePrefix(AWTEvent event) {
		String result = null;
		switch (event.getID()) {
		case MouseEvent.MOUSE_DRAGGED:
			result = "Mouse dragged: ";
			break;
		case MouseEvent.MOUSE_ENTERED:
			result = "Mouse entered: ";
			break;
		case MouseEvent.MOUSE_EXITED:
			result = "Mouse exited: ";
			break;
		default:
			result = "Unknown EventType: ";
		}
		return result;
	}
}
