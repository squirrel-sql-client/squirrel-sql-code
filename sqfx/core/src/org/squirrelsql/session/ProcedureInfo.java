package org.squirrelsql.session;

import java.io.Serializable;

public class ProcedureInfo implements Serializable
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

   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      ProcedureInfo that = (ProcedureInfo) o;

      if (_procedureType != that._procedureType) return false;
      if (_name != null ? !_name.equals(that._name) : that._name != null) return false;
      if (_catalog != null ? !_catalog.equals(that._catalog) : that._catalog != null) return false;
      return !(_schema != null ? !_schema.equals(that._schema) : that._schema != null);

   }

   @Override
   public int hashCode()
   {
      int result = _name != null ? _name.hashCode() : 0;
      result = 31 * result + (_catalog != null ? _catalog.hashCode() : 0);
      result = 31 * result + (_schema != null ? _schema.hashCode() : 0);
      result = 31 * result + _procedureType;
      return result;
   }
}
