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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyVetoException;

import javax.swing.AbstractAction;
import javax.swing.JInternalFrame;

import net.sourceforge.squirrel_sql.fw.gui.action.SelectInternalFrameCommand;

public class SelectInternalFrameAction extends AbstractAction
            implements PropertyChangeListener {

    /**
     * This interface defines locale specific strings. This should be
     * replaced with a property file.
     */
    private interface i18n {
        String SHORT_DESCRIPTION = "Active window";
    }

    private static final String FRAME_PTR = "FRAME_PTR";

    public SelectInternalFrameAction(JInternalFrame child) {
        this(child, null);
    }

    public SelectInternalFrameAction(JInternalFrame child, String title) {
        super(getTitle(child, title));
        putValue(FRAME_PTR, child);
        putValue(SHORT_DESCRIPTION, i18n.SHORT_DESCRIPTION);
        if (title != null && title.length() > 0) {
            child.addPropertyChangeListener(JInternalFrame.TITLE_PROPERTY, this);
        }
    }

    public void actionPerformed(ActionEvent evt) {
        JInternalFrame fr = getInternalFrame();
        if (fr != null) {
            new SelectInternalFrameCommand(fr).execute();
        }
    }

    public void propertyChange(PropertyChangeEvent evt) {
        putValue(AbstractAction.NAME, getInternalFrame().getTitle());
    }

    private JInternalFrame getInternalFrame() throws IllegalStateException {
        JInternalFrame fr = (JInternalFrame)getValue(FRAME_PTR);
        if (fr == null) {
            throw new IllegalStateException("No JInternalFrame associated with SelectInternalFrameAction");
        }
        return fr;
    }

    private static String getTitle(JInternalFrame child, String title) {
        if (child == null) {
            throw new IllegalArgumentException("null JInternalFrame passed");
        }
        if (title != null && title.length() > 0) {
            return title;
        }
        return child.getTitle();
    }
}
