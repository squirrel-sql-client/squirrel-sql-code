package net.sourceforge.squirrel_sql.fw.gui.action;
/*
 * Copyright (C) 2001 Colin Bell
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
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;

import javax.swing.AbstractAction;
import javax.swing.DesktopManager;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

/**
 * This class will tile all internal frames owned by a
 * <CODE>JDesktopPane</CODE>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class TileInternalFramesAction extends BaseAction implements IHasJDesktopPane {
	/**
	 * This interface defines locale specific strings. This should be
	 * replaced with a property file.
	 */
	private interface i18n {
		String TITLE = "Tile";
	}

	/**
	 * The <CODE>JDesktopPane</CODE> that owns the internal frames to be
	 * tiled.
	 */
	private JDesktopPane _desktop;

	/**
	 * Default constructor.
	 */
	public TileInternalFramesAction() {
		this(null);
	}

	/**
	 * Constructor specifying the <CODE>JDesktopPane</CODE> that owns the
	 * internal frames to be tiled.
	 *
	 * @param   desktop	 the <CODE>JDesktopPane</CODE> that owns the
	 *					  internal frames to be cascaded.
	 */
	public TileInternalFramesAction(JDesktopPane desktop) {
		super(i18n.TITLE);
		setJDesktopPane(desktop);
	}

	/**
	 * Set the <CODE>JDesktopPane</CODE> that owns the internal frames to be
	 * cascaded.
	 *
	 * @param   desktop	 the <CODE>JDesktopPane</CODE> that owns the
	 *					  internal frames to be cascaded.
	 */
	public void setJDesktopPane(JDesktopPane value) {
		_desktop = value;
	}

	/**
	 * Tile the internal frames.
	 *
	 * @param   evt	 Specifies the event being proceessed.
	 */
	public void actionPerformed(ActionEvent evt) {
		if (_desktop != null) {
			JInternalFrame[] children = GUIUtils.getOpenNonToolWindows(_desktop.getAllFrames());
			final int cells = children.length;
			if (cells > 0) {
				// Determine size of grid to tile into. e.g 3X3 for 9 cells.
				int rows = (int)Math.sqrt(cells);
				int cols = rows;
				while (rows * cols < cells) {
					++cols;
					if (rows * cols < cells) {
						++rows;
					}
				}

//?? Extract this out into a class like CascadeInternalFramePositioner.

				final Dimension desktopSize = _desktop.getSize();
				final int width = desktopSize.width / cols;
				final int height = desktopSize.height / rows;
				int xPos = 0;
				int yPos = 0;

//				DesktopManager mgr = _desktop.getDesktopManager();
				for (int y = 0; y < rows; ++y) {
					for (int x = 0; x < cols; ++x) {
						final int idx = y + (x * rows);
						if (idx >= cells) {
							break;
						}
						JInternalFrame frame = children[idx];
						if (!frame.isClosed()) {
							if (frame.isIcon()) {
								try {
									frame.setIcon(false);
								} catch (PropertyVetoException ignore) {
								}
							} else if (frame.isMaximum()) {
								try {
									frame.setMaximum(false);
								} catch (PropertyVetoException ignore) {
								}
							}

							frame.reshape(xPos, yPos, width, height);
							xPos += width;
						}
					}
					xPos = 0;
					yPos += height;
				}
			}
		}
	}
}