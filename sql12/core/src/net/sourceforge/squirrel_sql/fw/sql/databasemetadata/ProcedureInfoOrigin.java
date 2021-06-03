package net.sourceforge.squirrel_sql.fw.sql.databasemetadata;

public enum ProcedureInfoOrigin
{
   GET_PROCEDURES("Read by java.sql.DataBaseMeta.getProcedures(...)"),
   GET_FUNCTIONS("Read by java.sql.DataBaseMeta.getFunctions(...)");

   private String _toString;

   ProcedureInfoOrigin(String toString)
   {
      _toString = toString;
   }

   @Override
   public String toString()
   {
      return _toString;
   }
}
