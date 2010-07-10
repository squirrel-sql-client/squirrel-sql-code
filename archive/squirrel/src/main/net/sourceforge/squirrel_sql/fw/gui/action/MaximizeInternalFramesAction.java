package net.sourceforge.squirrel_sql.fw.gui.action;
/*
 * Copyright (C) 2001 Colin Bell
 * colbell@users.sourceforge.net
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
import java.awt.event.ActionEvent;
import java.beans.PropertyVetoException;

import javax.swing.DesktopManager;
import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

import net.sourceforge.squirrel_sql.fw.gui.MaximizeInternalFramePositioner;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;

/**
 * This class will cascade all internal frames owned by a
 * <CODE>JDesktopPane</CODE>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class MaximizeInternalFramesAction extends BaseAction
                                            implements IHasJDesktopPane {

    /**
     * This interface defines locale specific strings. This should be
     * replaced with a property file.
     */
    private interface i18n {
        String TITLE = "Maximize";
    }

    /**
     * The <CODE>JDesktopPane</CODE> that owns the internal frames to be
     * cascaded.
     */
    private JDesktopPane _desktop;

    /**
     * Default constructor.
     */
    public MaximizeInternalFramesAction() {
        this(null);
    }

    /**
     * Constructor specifying the <CODE>JDesktopPane</CODE> that owns the
     * internal frames to be maximized.
     *
     * @param   desktop     the <CODE>JDesktopPane</CODE> that owns the
     *                      internal frames to be maximized.
     */
    public MaximizeInternalFramesAction(JDesktopPane desktop) {
        super(i18n.TITLE);
        setJDesktopPane(desktop);
    }

    /**
     * Set the <CODE>JDesktopPane</CODE> that owns the internal frames to be
     * maximized.
     *
     * @param   desktop     the <CODE>JDesktopPane</CODE> that owns the
     *                      internal frames to be maximized.
     */
    public void setJDesktopPane(JDesktopPane value) {
        _desktop = value;
    }

    /**
     * Maximize the internal frames.
     *
     * @param   evt     Specifies the event being proceessed.
     */
    public void actionPerformed(ActionEvent evt) {
        if (_desktop != null) {
            MaximizeInternalFramePositioner pos = new MaximizeInternalFramePositioner();
            JInternalFrame[] children = GUIUtils.getOpenNonToolWindows(_desktop.getAllFrames());
            for (int i = children.length - 1; i >= 0; --i) {
                pos.positionInternalFrame(children[i]);
            }
        }
    }
}