package net.sourceforge.squirrel_sql.fw.datasetviewer;
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
import java.util.EventObject;

/**
 * Event associated with the <TT>DataSetModelListener</TT>.
 *
 * @author  <A HREF="mailto:colbell@users.sourceforge.net">Colin Bell</A>
 */
public class DataSetModelEvent extends EventObject {
	private IDataSetModel _src;

    DataSetModelEvent(IDataSetModel src) throws IllegalArgumentException {
        super(validateSource(src));
        _src = src;
    }
    
    private static IDataSetModel validateSource(IDataSetModel src)
    		throws IllegalArgumentException {
    	if (src == null) {
    		throw new IllegalArgumentException("Null IDataSetModel passed");
    	}
    	return src;
    }
}
