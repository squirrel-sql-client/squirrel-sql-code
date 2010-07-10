package net.sourceforge.squirrel_sql.plugins.laf;
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
public class LAFPreferences implements Serializable {
    private String _lafClassName;
    private String _themePackName;

    public LAFPreferences() {
        super();
        _lafClassName = UIManager.getCrossPlatformLookAndFeelClassName();
        _themePackName = "";
    }

    public String getLookAndFeelClassName() {
        return _lafClassName;
    }

    public String getThemePackName() {
        return _themePackName;
    }

    public void setLookAndFeelClassName(String data) {
        _lafClassName = data;
    }

    public void setThemePackName(String data) {
        _themePackName = data;
    }
}
