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
import java.util.Iterator;

import net.sourceforge.squirrel_sql.fw.id.IHasIdentifier;
import net.sourceforge.squirrel_sql.fw.id.IIdentifier;

/**
 * This interface defines a the behaviour of an object cache.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface IObjectCache {
    /**
     * Retrieve a stored object.
     *
     * @param   objClass    The class of the object to be retrieved.
     * @param   id          The <CODE>IIdentifier</CODE> that identifies
     *                      the object to be retrieved.
     *
     * @return  The <CODE>IHasIdentifier</CODE> retrieved or <CODE>null</CODE>
     *          if no object exists for <CODE>id</CODE>.
     */
    IHasIdentifier get(Class objClass, IIdentifier id);

    /**
     * Store an object.
     *
     * @param   obj     Object to be stored.
     *
     * @exception   DuplicateObjectException
     *              Thrown if an object of the same class as <CODE>obj</CODE>
     *              and with the same identifier is already in the cache.
     */
    void add(IHasIdentifier obj) throws DuplicateObjectException;

    /**
     * Remove an object.
     *
     * @param   objClass    Class of object to be removed.
     * @param   id          Identifier for object to be removed.
     */
    void remove(Class objClass, IIdentifier id);

    /**
     * Return an array of <CODE>Class</CODE objects that represent all the
     * different types of objects stored.
     *
     * @return  Class[] of all classes stored.
     */
    Class[] getAllClasses();

    /**
     * Return an <CODE>Iterator</CODE> of all objects stored for the
     * passed class.
     *
     * @param   objClass    Class to return objects for.
     *
     * @return  <CODE>Iterator</CODE> over all objects.
     */
    Iterator getAllForClass(Class objClass);

    /**
     * Adds a listener for changes to the cache entry for the passed class.
     *
     * @param   lis         a ObjectCacheChangeListener that will be notified
     *                      when objects are added or removed from this cache
     *                      entry.
     * @param   objClass    The class of objects whose cache we want to listen
     *                      to.
     */
    void addChangesListener(ObjectCacheChangeListener lis, Class objClass);

    /**
     * Removes a listener for changes to the cache entry for the passed class.
     *
     * @param   lis         a ObjectCacheChangeListener that will be notified
     *                      when objects are added or removed from this cache
     *                      entry.
     * @param   objClass    The class of objects whose cache we want to listen
     *                      to.
     */
    void removeChangesListener(ObjectCacheChangeListener lis, Class objClass);
}