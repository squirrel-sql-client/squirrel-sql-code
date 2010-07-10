package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2001 Johan Compagner
 * jcompagner@j-com.nl
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
import java.util.Comparator;

/**
 * This class compares does a case-insensitive comparison of two
 * <TT>IDatabaseObjectInfo</TT> objects using their simple names.
 */
public class DatabaseObjectSimpleNameInfoComparator implements Comparator {
    public int compare(Object o1, Object o2) {
        return ((IDatabaseObjectInfo) o1).getSimpleName().compareToIgnoreCase(
            ((IDatabaseObjectInfo) o2).getSimpleName());
    }
}