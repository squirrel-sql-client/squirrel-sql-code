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
import java.text.DecimalFormat;

import javax.swing.JLabel;
import javax.swing.Timer;
import javax.swing.ToolTipManager;
import javax.swing.border.Border;
/**
 * Memory panel. This will show the used/total memory in the heap.
 * A timer to update the memory status is started when the component
 * is added to its parent and stopped when removed from its parent.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class MemoryPanel extends JLabel implements ActionListener {
	/** Timer that updates memory status. */
	private Timer _timer;

	/** Formatter used to format memory size. */
	private DecimalFormat _fmt = new DecimalFormat("##0.0");

	/**
	 * Default ctor.
	 */
	public MemoryPanel() {
		super("", JLabel.CENTER);
	}

	/**
	 * Add component to its parent. Start the timer for auto-update.
	 */
	public void addNotify() {
		super.addNotify();
		updateMemoryStatus();
		ToolTipManager.sharedInstance().registerComponent(this);
		_timer = new Timer(2000, this);
		_timer.start();
	}

	/**
	 * Remove component from its parent. Stop the timer.
	 */
	public void removeNotify() {
		ToolTipManager.sharedInstance().unregisterComponent(this);
		if (_timer != null) {
			_timer.stop();
			_timer = null;
		}
		super.removeNotify();
	}

	/**
	 * Update component with the current memory status.
	 * 
	 * @param	evt		The current event.
	 */
	public void actionPerformed(ActionEvent evt) {
		updateMemoryStatus();
	}

	/**
	 * Return tooltip for this component.
	 * 
	 * @return	tooltip for this component.
	 */
	public String getToolTipText() {
		final Runtime rt = Runtime.getRuntime();
		final long totalMemory = rt.totalMemory() / 1024;
		final long freeMemory = rt.freeMemory() / 1024;
		final long usedMemory = totalMemory - freeMemory;
		StringBuffer buf = new StringBuffer();
		buf.append(usedMemory).append("KB used from ")
			.append(totalMemory).append("KB total leaving ")
			.append(freeMemory).append(" KB free");
		return buf.toString();
	}

	/**
	 * Return the preferred size of this component.
	 * 
	 * @return	the preferred size of this component.
	 */
	public Dimension getPreferredSize() {
		Dimension dim = super.getPreferredSize();
		FontMetrics fm = getFontMetrics(getFont());
		dim.width = fm.stringWidth("99.9MB/99.9MB");
		Border border = getBorder();
		if (border != null) {
			Insets ins = border.getBorderInsets(this);
			if (ins != null) {
				dim.width += (ins.left + ins.right);
			}
		}
		Insets ins = getInsets();
		if (ins != null) {
			dim.width += (ins.left + ins.right);
		}
		return dim;
	}

	/**
	 * Update component with the current memory status.
	 * 
	 * @param	evt		The current event.
	 */
	private void updateMemoryStatus() {
		final Runtime rt = Runtime.getRuntime();
		final long totalMemory = rt.totalMemory();// / (1024 * 1024);
		final long freeMemory = rt.freeMemory();// / (1024 * 1024);
		final long usedMemory = totalMemory - freeMemory;
		StringBuffer buf = new StringBuffer();
		buf.append(formatSize(usedMemory)).append("/")
			.append(formatSize(totalMemory));
		setText(buf.toString());
	}

	/**
	 * Format the passed number of bytes for display.
	 * 
	 * @param	nbrBytes	Nbr of bytes to be displayed.
	 * 
	 * @return	the formatted version of <TT>nbrBytes</TT>.
	 */
	private String formatSize(long nbrBytes) {
		double size = nbrBytes;
		double val = size / (1024 * 1024);
		return _fmt.format(val).concat("MB");
	}

}