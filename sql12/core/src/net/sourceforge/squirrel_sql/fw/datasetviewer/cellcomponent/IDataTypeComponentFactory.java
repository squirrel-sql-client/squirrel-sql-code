/*
 * Copyright (C) 2007 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.fw.dialects.DialectType;

/**
 * Interface that describes a class that creates instances of a particular 
 * IDataTypeComponent. This avoids the need to use ClassLoaders to load/create 
 * instances of custom IDataTpeComponents that are installed by plugins.
 * This should be implemented by plugins and registered with the 
 * CellComponentFactory. 
 * 
 * @author manningr
 *
 */
public interface IDataTypeComponentFactory {

   /** 
    * Builds and returns an IDataTypeComponent implementation that handles some
    * plugin-specific data type, or overrides the behavior provided by the 
    * defaut datatype components.
    * 
    * @return a class that implements IDataTypeComponent  
    */
   IDataTypeComponent constructDataTypeComponent();

   /**
    * Returns the dialect type that this datatype component factory corresponds
    * to.  This allows SQuirreL to distinguish which IDataTypeComponent to use
    * when there are multiple ones registered for the same datatype.
    * 
    * @return a DialectType from the DialectType enumeration.
    */
   DialectType getDialectType();
   
   /**
    * Check if given factory is suitable for requested type
    * 
    * @param dialectType
    *           the type of dialect that describes the session that is in use.
    *           This is an important component in making the key because it
    *           allows plugins for example to provide IDataTypeComponents for
    *           standard types that are only used when a session that the plugin
    *           is interested in is in use.
    * @param sqlType
    *           the JDBC type code supplied by the driver
    * @param sqlTypeName
    *           the JDBC type name supplied by the driver
    * @return
    */
   public abstract boolean matches(DialectType dialectType, int sqlType,
			String sqlTypeName);
}
