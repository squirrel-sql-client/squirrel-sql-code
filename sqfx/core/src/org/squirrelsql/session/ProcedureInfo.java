package org.squirrelsql.session;

public class ProcedureInfo
{
   private String _name;
   private String _catalog;
   private String _schema;
   private int _procedureType;


   public ProcedureInfo(String catalog, String schema, String name, int procedureType)
   {
      _catalog = catalog;
      _schema = schema;
      _name = name;
      _procedureType = procedureType;
   }

   public String getName()
   {
      return _name;
   }

   public String getCatalog()
   {
      return _catalog;
   }

   public String getSchema()
   {
      return _schema;
   }
}
