package net.sourceforge.squirrel_sql.plugins.sqlscript;
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
import java.io.Serializable;

import javax.swing.UIManager;

/**
 * This JavaBean class represents the user specific
 * preferences for this plugin.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class SQLScriptPreferences implements Serializable {
    private boolean _openInPreviousDirectory = true;
    private boolean _openInSpecifiedDirectory = false;
    private String _specifiedDirectory = "";
    private String _previousDirectory = "";

    public SQLScriptPreferences() {
        super();
    }

    public boolean getOpenInSpecifiedDirectory() {
        return _openInSpecifiedDirectory;
    }

    public boolean getOpenInPreviousDirectory() {
        return _openInPreviousDirectory;
    }

    public String getSpecifiedDirectory() {
        return _specifiedDirectory;
    }

    public String getPreviousDirectory() {
        return _previousDirectory;
    }

    public void setOpenInSpecifiedDirectory(boolean data) {
        _openInSpecifiedDirectory = data;
    }

    public void setOpenInPreviousDirectory(boolean data) {
        _openInPreviousDirectory = data;
    }

    public void setSpecifiedDirectory(String data) {
        _specifiedDirectory = data != null ? data : "";
    }

    public void setPerviousDirectory(String data) {
        _previousDirectory = data != null ? data : "";
    }
}
