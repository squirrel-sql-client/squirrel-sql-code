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
import java.util.EventListener;

/**
 * This interface defines a listener to changes in <CODE>ObjectCache</CODE>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public interface ObjectCacheChangeListener extends EventListener {
    /**
     * An object has been added to the cache.
     *
     * @param   evt     Describes the event.
     */
    void objectAdded(ObjectCacheChangeEvent evt);

    /**
     * An object has been removed from the cache.
     *
     * @param   evt     Describes the event.
     */
    void objectRemoved(ObjectCacheChangeEvent evt);
}