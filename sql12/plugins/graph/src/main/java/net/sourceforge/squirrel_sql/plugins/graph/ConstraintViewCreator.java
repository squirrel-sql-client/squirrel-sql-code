package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

public class ConstraintViewCreator
{

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ConstraintViewCreator.class);


   public static ConstraintView createConstraintView(DndEvent e, TableFrameController sourceTable, ColumnInfo sourceColumnInfo, GraphDesktopController desktopController, ISession session)
   {
      ColumnInfo targetColumnInfo = e.getColumnInfo();
      TableFrameController targetTable = e.getTableFrameController();

      if(null == targetColumnInfo || null == sourceColumnInfo || targetTable == sourceTable)
      {
         return null;
      }

      if(null != targetColumnInfo.getImportedColumnName())
      {
         // i18n[graph.nonDbConstraintCreationError_already_points=A column cannot reference more than one other column. Non DB constraint could not be created.]
         session.getApplication().getMessageHandler().showErrorMessage(s_stringMgr.getString("graph.nonDbConstraintCreationError_already_points"));
         return null;
      }

      String constName = "SquirrelGeneratedConstraintName";

      ConstraintData data = new ConstraintData(
         sourceTable.getTableInfo().getSimpleName(),
         targetTable.getTableInfo().getSimpleName(),
         constName,
         true);



      targetColumnInfo.setImportData(sourceTable.getTableInfo().getSimpleName(), sourceColumnInfo.getName(), constName, true);


      data.addColumnInfo(targetColumnInfo);
      ConstraintView ret = new ConstraintView(data, desktopController, session);

      return ret;
   }
}
