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
import java.awt.Rectangle;
import java.awt.Window;

import net.sourceforge.squirrel_sql.fw.util.beanwrapper.RectangleWrapper;
import net.sourceforge.squirrel_sql.fw.xml.IXMLAboutToBeWritten;

/**
 * This bean will store the state of a window object.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class WindowState implements IXMLAboutToBeWritten {
    private Window _window;
    private RectangleWrapper _bounds = new RectangleWrapper(new Rectangle(600, 400));

    public interface IPropertyNames {
        String BOUNDS = "bounds";
    }

    /**
     * Default ctor.
     */
    public WindowState() {
        super();
    }

    /**
     * Ctor storing the state of the passed <CODE>Window</CODE>.
     */
    public WindowState(Window window) {
        super();
        _window = window;
    }

    /**
     * This bean is about to be written out to XML so load its values from its
     * window.
     */
    public void aboutToBeWritten() {
        refresh();
    }

    public RectangleWrapper getBounds() {
        refresh();
        return _bounds;
    }

    public void setBounds(RectangleWrapper value) {
        _bounds = value;
    }

    private void refresh() {
        if (_window != null) {
            if (_bounds == null) {
                _bounds = new RectangleWrapper();
            }
            _bounds.setFrom(_window.getBounds());
        }
    }
}