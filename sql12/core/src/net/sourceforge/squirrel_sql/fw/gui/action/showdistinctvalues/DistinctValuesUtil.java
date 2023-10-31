package net.sourceforge.squirrel_sql.fw.gui.action.showdistinctvalues;

import java.sql.Types;

import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class DistinctValuesUtil
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DistinctValuesUtil.class);

   public static ColumnDisplayDefinition createFrequencyColumn()
   {
      final ColumnDisplayDefinition dispDef = new ColumnDisplayDefinition(200, s_stringMgr.getString("DistinctValuesUtil.count.column.name"));
      dispDef.setSqlType(Types.INTEGER);
      dispDef.setSqlTypeName("INTEGER");
      return dispDef;
   }
}
