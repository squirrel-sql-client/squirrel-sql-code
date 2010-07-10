package net.sourceforge.squirrel_sql.fw.util;
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
import java.text.MessageFormat;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

/**
 * This exception is thrown if an attempt is made to add an object
 * to a <CODE>IObjectCache</CODE> and an object for the same class and with the
 * same ID is already in the cache.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DuplicateObjectException extends BaseException {
    /** Object that couldn't be added to the cache. */
    private IHasIdentifier _obj;

    /**
     * This interface defines locale specific strings. This should be
     * replaced with a property file.
     */
    private interface i18n {
        String MSG = "An object of class {0} with an ID of {1} already exists in the cache";
    }

    /**
     * Ctor.
     *
     * @param   obj     The object that we tried to add to into the cache.
     */
    public DuplicateObjectException(IHasIdentifier obj) {
        super(generateMessage(obj));
    }

    /**
     * Return the object that couldn't be added to the cache.
     */
    IHasIdentifier getObject() {
        return _obj;
    }

    /**
     * Generate error message. Help function for ctor.
     */
    private static String generateMessage(IHasIdentifier obj) {
        Object[] args = {obj.getClass().getName(), obj.getIdentifier().toString()};
        return MessageFormat.format(i18n.MSG, args);
    }
}