package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import net.sourceforge.squirrel_sql.fw.dialects.DialectType;

import java.sql.Statement;

public class JDBCResultSetExportData
{
   public String _sql;
   public Statement _stmt;
   public DialectType _dialect;
   public boolean _exportComplete;
   public int _maxRows;
}
