package net.sourceforge.squirrel_sql.client.mainframe;
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
import java.awt.Point;

import net.sourceforge.squirrel_sql.fw.gui.WindowState;
import net.sourceforge.squirrel_sql.fw.util.beanwrapper.PointWrapper;

/**
 * This bean describes the state of the main window.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class MainFrameWindowState extends WindowState {

    public interface IPropertyNames {
        String ALIASES_WINDOW_LOCATION = "aliasesWindowLocation";
        String DRIVERS_WINDOW_LOCATION = "driversWindowLocation";
    }
    private PointWrapper _driversWindowLocation = new PointWrapper(new Point(2, 0));
    private PointWrapper _aliasesWindowLocation = new PointWrapper(new Point(2, 200));

    private MainFrame _frame;

    public MainFrameWindowState() {
        super();
    }

    public MainFrameWindowState(MainFrame frame) {
        super(frame);
        _frame = frame;
    }

    /**
     * This bean is about to be written out to XML so load its values from its
     * window.
     */
    public void aboutToBeWritten() {
        super.aboutToBeWritten();
        refresh();
    }
    public PointWrapper getAliasesWindowLocation() {
        refresh();
        return _aliasesWindowLocation;
    }

    public PointWrapper getDriversWindowLocation() {
        refresh();
        return _driversWindowLocation;
    }
    public void setAliasesWindowLocation(PointWrapper value) {
        _aliasesWindowLocation = value;
    }

    public void setDriversWindowLocation(PointWrapper value) {
        _driversWindowLocation = value;
    }

    private void refresh() {
        if (_frame != null) {
            if (_aliasesWindowLocation == null) {
                _aliasesWindowLocation = new PointWrapper();
            }
            if (_driversWindowLocation == null) {
                _driversWindowLocation = new PointWrapper();
            }
            _aliasesWindowLocation.setFrom(_frame.getAliasesWindowLocation());
            _driversWindowLocation.setFrom(_frame.getDriversWindowLocation());
        }
    }
}
