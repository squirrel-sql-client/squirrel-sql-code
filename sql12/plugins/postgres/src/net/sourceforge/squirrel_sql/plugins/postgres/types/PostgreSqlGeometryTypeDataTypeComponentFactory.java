/*
 * Copyright (C) 2008 Rob Manning manningr@users.sourceforge.net This library is free software; you can redistribute it
 * and/or modify it under the terms of the GNU Lesser General Public License as published by the Free Software Foundation;
 * either version 2.1 of the License, or (at your option) any later version. This library is distributed in the hope that it
 * will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR
 * PURPOSE. See the GNU Lesser General Public License for more details. You should have received a copy of the GNU Lesser
 * General Public License along with this library; if not, write to the Free Software Foundation, Inc., 59 Temple Place,
 * Suite 330, Boston, MA 02111-1307 USA
 */
package net.sourceforge.squirrel_sql.plugins.postgres.types;

import net.sourceforge.squirrel_sql.client.session.SessionManager;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponent;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponentFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import org.apache.commons.lang3.builder.EqualsBuilder;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;

/**
 * A factory that creates PostgreSqlGeometryTypeDataTypeComponents for rendering "geometry" columns.
 * 
 * @author manningr
 */
public class PostgreSqlGeometryTypeDataTypeComponentFactory implements IDataTypeComponentFactory {

    private final SessionManager sessionManager;

	/**
     * @param sessionManager 
     * @param typeName
     */
    public PostgreSqlGeometryTypeDataTypeComponentFactory(SessionManager sessionManager) {
		this.sessionManager = sessionManager;
    }

    /**
     * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponentFactory#constructDataTypeComponent()
     */
    @Override
    public IDataTypeComponent constructDataTypeComponent() {
        return new PostgreSqlGeometryTypeDataTypeComponent(this);
    }

    /**
     * @see net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.IDataTypeComponentFactory#getDialectType()
     */
    @Override
    public DialectType getDialectType() {
        return DialectType.POSTGRES;
    }
    
    int fetchSrid(ColumnDisplayDefinition _colDef) throws SQLException {
    	String[] parts = _colDef.getFullTableColumnName().split(":");
		String sql = "SELECT ST_SRID("+_colDef.getColumnName()+") FROM "+ parts[3]+" LIMIT 1;";
		ResultSet resultSet = sessionManager.getActiveSession().getSQLConnection().createStatement().executeQuery(sql);
    	if (resultSet.next()) {
    		return resultSet.getInt(1);
    	}
    	return 0;
    }

	@Override
	public boolean matches(DialectType dialectType, int sqlType,
			String sqlTypeName) {
		return new EqualsBuilder().append(getDialectType(), dialectType)
				.append(Types.OTHER, sqlType)
				.append("geometry", sqlTypeName).isEquals();
	}
}
