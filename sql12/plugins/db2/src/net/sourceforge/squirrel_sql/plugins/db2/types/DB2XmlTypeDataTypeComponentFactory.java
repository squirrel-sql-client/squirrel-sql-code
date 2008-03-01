/*
 * Copyright (C) 2008 Lars Heller lhe@cedros.com
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
package net.sourceforge.squirrel_sql.plugins.db2.types;

import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponentFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;

/**
 * A factory that creates DB2XmlTypeDataTypeComponents for rendering columns of
 * {@code DB2Types.XML}.
 * 
 * @author Lars Heller
 * 
 */
public class DB2XmlTypeDataTypeComponentFactory implements
		IDataTypeComponentFactory {

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponentFactory#constructDataTypeComponent()
	 */
	public IDataTypeComponent constructDataTypeComponent() {
		return new DB2XmlTypeDataTypeComponent();
	}

	/**
	 * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponentFactory#getDialectType()
	 */
	public DialectType getDialectType() {
		return DialectType.DB2;
	}

}
