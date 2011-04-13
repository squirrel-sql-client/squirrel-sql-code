package net.sourceforge.squirrel_sql.plugins.graph;

public interface AddTableRequestListener
{
   void addTablesRequest(String[] tablenames, String schema, String catalog);
}
