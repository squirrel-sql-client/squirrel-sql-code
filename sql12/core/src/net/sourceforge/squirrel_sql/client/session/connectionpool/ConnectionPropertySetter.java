package net.sourceforge.squirrel_sql.client.session.connectionpool;

import net.sourceforge.squirrel_sql.fw.sql.ISQLConnection;

import java.sql.SQLException;

@FunctionalInterface
public interface ConnectionPropertySetter
{
   void setProperty(ISQLConnection con) throws SQLException;
}
