package net.sourceforge.squirrel_sql.client.session.schemainfo;

import net.sourceforge.squirrel_sql.fw.sql.IDatabaseObjectInfo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class DatabaseUpdateInfos implements Serializable
{
   private Set<IDatabaseObjectInfo> _updateDatabaseObjectInfos =  new HashSet<>();
   private Set<String> _dropTableSimpleNames = new HashSet<>();
   private Set<String> _dropProcedureSimpleNames = new HashSet<>();

   public Set<IDatabaseObjectInfo> getUpdateDatabaseObjectInfos()
   {
      return _updateDatabaseObjectInfos;
   }

   public void setUpdateDatabaseObjectInfos(Set<IDatabaseObjectInfo> updateDatabaseObjectInfos)
   {
      _updateDatabaseObjectInfos = updateDatabaseObjectInfos;
   }

   public Set<String> getDropTableSimpleNames()
   {
      return _dropTableSimpleNames;
   }

   public void setDropTableSimpleNames(Set<String> dropTableSimpleNames)
   {
      _dropTableSimpleNames = dropTableSimpleNames;
   }

   public Set<String> getDropProcedureSimpleNames()
   {
      return _dropProcedureSimpleNames;
   }

   public void setDropProcedureSimpleNames(Set<String> dropProcedureSimpleNames)
   {
      _dropProcedureSimpleNames = dropProcedureSimpleNames;
   }

   public void clear()
   {
      _updateDatabaseObjectInfos.clear();
      _dropTableSimpleNames.clear();
      _dropProcedureSimpleNames.clear();
   }
}
