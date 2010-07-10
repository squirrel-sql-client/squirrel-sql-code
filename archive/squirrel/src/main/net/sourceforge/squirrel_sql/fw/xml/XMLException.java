package net.sourceforge.squirrel_sql.fw.xml;
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
import net.sourceforge.squirrel_sql.fw.util.BaseException;

/**
 * This exception indicates that a problem has occured in XML processing.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class XMLException extends BaseException {
    /*
     * Ctor.
     *
     * @param   msg     Message describing the error.
     */
    public XMLException(String msg) {
        super(msg);
    }

    /*
     * Ctor. Wraps this exception around another.
     *
     * @param   wrapee  The exception that this one is wrapped around.
     */
    public XMLException(Exception wrapee) {
        super(wrapee);
    }
}