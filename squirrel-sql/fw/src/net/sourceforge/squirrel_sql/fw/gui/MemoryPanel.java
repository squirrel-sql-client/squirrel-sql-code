package net.sourceforge.squirrel_sql.fw.gui;
/*
 * Copyright (C) 2002 Colin Bell
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
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.JPopupMenu;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;

import net.sourceforge.squirrel_sql.fw.util.Utilities;
/**
 * Memory panel. This will show the used/total memory in the heap.
 * A timer to update the memory status is started when the component
 * is added to its parent and stopped when removed from its parent.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class MemoryPanel extends JLabel
{
	/** Timer that updates memory status. */
	private Timer _timer;

	/**
	 * Default ctor.
	 */
	public MemoryPanel()
	{
		super("", JLabel.CENTER);
	}

	/**
	 * Add component to its parent. Start the timer for auto-update.
	 */
	public void addNotify()
	{
		super.addNotify();
		updateMemoryStatus();
		ToolTipManager.sharedInstance().registerComponent(this);
		_timer = new Timer(2000, new TimerListener());
		_timer.start();
	}

	/**
	 * Remove component from its parent. Stop the timer.
	 */
	public void removeNotify()
	{
		ToolTipManager.sharedInstance().unregisterComponent(this);
		if (_timer != null)
		{
			_timer.stop();
			_timer = null;
		}
		super.removeNotify();
	}

	/**
	 * Return tooltip for this component.
	 * 
	 * @return	tooltip for this component.
	 */
	public String getToolTipText()
	{
		final Runtime rt = Runtime.getRuntime();
		final long totalMemory = rt.totalMemory();
		final long freeMemory = rt.freeMemory();
		final long usedMemory = totalMemory - freeMemory;
		StringBuffer buf = new StringBuffer();
		buf.append(Utilities.formatSize(usedMemory))
			.append(" used from ")
			.append(Utilities.formatSize(totalMemory))
			.append(" total leaving ")
			.append(Utilities.formatSize(freeMemory))
			.append(" free");
		return buf.toString();
	}

	/**
	 * Return the preferred size of this component.
	 * 
	 * @return	the preferred size of this component.
	 */
	public Dimension getPreferredSize()
	{
		Dimension dim = super.getPreferredSize();
		FontMetrics fm = getFontMetrics(getFont());
		dim.width = fm.stringWidth("99.9MB/99.9MB");
		Border border = getBorder();
		if (border != null)
		{
			Insets ins = border.getBorderInsets(this);
			if (ins != null)
			{
				dim.width += (ins.left + ins.right);
			}
		}
		Insets ins = getInsets();
		if (ins != null)
		{
			dim.width += (ins.left + ins.right);
		}
		return dim;
	}

	/**
	 * Update component with the current memory status.
	 * 
	 * @param	evt		The current event.
	 */
	private void updateMemoryStatus()
	{
		final Runtime rt = Runtime.getRuntime();
		final long totalMemory = rt.totalMemory();
		final long freeMemory = rt.freeMemory();
		final long usedMemory = totalMemory - freeMemory;
		StringBuffer buf = new StringBuffer();
		buf.append(Utilities.formatSize(usedMemory, 1)).append("/")
			.append(Utilities.formatSize(totalMemory, 1));
		setText(buf.toString());
	}

	/**
	 * Update component with the current memory status.
	 */
	private class TimerListener implements ActionListener
	{
		public void actionPerformed(ActionEvent evt)
		{
			updateMemoryStatus();
		}
	}
}