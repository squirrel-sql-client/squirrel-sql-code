package net.sourceforge.squirrel_sql.fw.gui;
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
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;

public class ScrollableDesktopPane extends JDesktopPane {
// make serializable safe ??
    private MyComponentListener _listener = new MyComponentListener();
    /**
     * Default ctor.
     */
    public ScrollableDesktopPane() {
        super();
    }

    public void paint(Graphics g) {
        setPreferredSize(getRequiredSize());
        super.paint(g);
    }

    public void remove(Component comp) {
        if (comp != null) {
            comp.removeComponentListener(_listener);
        }
        super.remove(comp);
        revalidate();
        repaint();
    }

    protected void addImpl(Component comp, Object constraints, int index) {
        if (comp != null) {
            comp.addComponentListener(_listener);
            revalidate();
        }
        super.addImpl(comp, constraints, index);
    }

    /**
     * Calculate the required size of this desktop pane so that
     * all visible intenal frames will be fully shown.
     *
     * @return  <TT>Dimension</TT> required size.
     */
    protected Dimension getRequiredSize() {
        JInternalFrame [] frames = getAllFrames();
        int maxX = 0;
        int maxY = 0;
        for (int i = 0; i < frames.length; ++i) {
            if (frames[i].isVisible()) {
                JInternalFrame frame = frames[i];
                int x = frame.getX() + frame.getWidth();
                if (x > maxX) {
                    maxX = x;
                }
                int y = frame.getY() + frame.getHeight();
                if (y > maxY) {
                    maxY = y;
                }
            }
        }
        return new Dimension(maxX, maxY);
    }

    private final class MyComponentListener implements ComponentListener {
        public void componentHidden(ComponentEvent evt) {
            ScrollableDesktopPane.this.revalidate();
        }

        public void componentMoved(ComponentEvent evt) {
            ScrollableDesktopPane.this.revalidate();
        }

        public void componentResized(ComponentEvent evt) {
            ScrollableDesktopPane.this.revalidate();
        }

        public void componentShown(ComponentEvent evt) {
            ScrollableDesktopPane.this.revalidate();
        }
    }
}

