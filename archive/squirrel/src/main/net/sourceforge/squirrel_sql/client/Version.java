package net.sourceforge.squirrel_sql.client;
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

/**
 * Application version information.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class Version {
    private static final String APP_NAME = "Squirrel SQL Client";
    private static final int MAJOR_VERSION = 1;
    private static final int MINOR_VERSION = 1;
    private static final String TYPE = "alpha";
//  private static final String TYPE = "beta";
//  private static final String TYPE = "final";
    //private static final int RELEASE = 7;
    private static final int RELEASE = 1;

    private static final String COPYRIGHT = "Copyright (c) 2001 Colin Bell, colbell@users.sourceforge.net";

    private static final String WEB_SITE = "http://sourceforge.net/projects/squirrel-sql/";

    public static String getVersion() {
        StringBuffer buf = new StringBuffer();
        buf.append(APP_NAME).append(" Version ").append(MAJOR_VERSION)
            .append('.').append(MINOR_VERSION).append(TYPE);
        if (RELEASE != 0) {
            buf.append(RELEASE);
        }
        return buf.toString();
    }

    public static String getCopyrightStatement() {
        return COPYRIGHT;
    }

    public static String getWebSite() {
        return WEB_SITE;
    }
}
