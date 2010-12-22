package net.sourceforge.squirrel_sql.plugins.oracle.expander;

import net.sourceforge.squirrel_sql.fw.sql.DatabaseObjectType;
import net.sourceforge.squirrel_sql.plugins.oracle.ObjectTypes;
import net.sourceforge.squirrel_sql.plugins.oracle.OraclePlugin;
import net.sourceforge.squirrel_sql.plugins.oracle.OraclePluginResources;

import javax.swing.*;

public class ObjectTypesMock extends ObjectTypes
{
   public ObjectTypesMock()
   {
      super(new OracleResourcesMock());
   }

   @Override
   public DatabaseObjectType getUserParent()
   {
      return DatabaseObjectType.createNewDatabaseObjectType("DUM", new ImageIcon());
   }

   private static class OracleResourcesMock extends OraclePluginResources
   {
      private OracleResourcesMock()
      {
         super(OraclePlugin.class.getName(), new OraclePlugin());
      }
   }
}
