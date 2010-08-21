package net.sourceforge.squirrel_sql.plugins.graph;

public interface AddTableListener
{
   void addTablesRequest(String[] tablenames, String schema, String catalog);
}
