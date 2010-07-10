package net.sourceforge.squirrel_sql.client.resources;
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
import java.net.URL;
import net.sourceforge.squirrel_sql.fw.util.Resources;

public class SquirrelResources extends Resources {

    public final static int S_SPLASH_IMAGE_BACKGROUND = 0xADAFC4;

    private final String _defaultsPath;
    public interface ImageNames {
        String PERFORMANCE_WARNING = "PerformanceWarning";
        String SPLASH_SCREEN = "SplashScreen";
    }

    public SquirrelResources(String rsrcBundleBaseName)
            throws IllegalArgumentException {
        super(rsrcBundleBaseName, SquirrelResources.class.getClassLoader());
        _defaultsPath = getBundle().getString("path.defaults");
    }

    public URL getDefaultDriversUrl() {
        return getClass().getResource(_defaultsPath + "default_drivers.xml");
    }
}

