package org.squirrelsql.session.schemainfo;

import java.util.ArrayList;

public class DatabaseStructure extends StructItem
{
   private String _aliasName;

   public DatabaseStructure(String aliasName)
   {
      _aliasName = aliasName;
   }

   public ArrayList<StructItem> getLeaves()
   {
      ArrayList<StructItem> ret = new ArrayList<>();

      fillLeaves(ret);

      return ret;
   }

   @Override
   public boolean equals(Object o)
   {
      if (this == o) return true;
      if (o == null || getClass() != o.getClass()) return false;

      DatabaseStructure that = (DatabaseStructure) o;

      if (_aliasName != null ? !_aliasName.equals(that._aliasName) : that._aliasName != null) return false;

      return true;
   }

   @Override
   public int hashCode()
   {
      return _aliasName != null ? _aliasName.hashCode() : 0;
   }

   public <T> T visitTopToBottom(DatabaseStructureVisitor<T> databaseStructureVisitor)
   {
      T parent =  databaseStructureVisitor.visit(null, this);

      visit(databaseStructureVisitor, parent);

      return parent;
   }

}
