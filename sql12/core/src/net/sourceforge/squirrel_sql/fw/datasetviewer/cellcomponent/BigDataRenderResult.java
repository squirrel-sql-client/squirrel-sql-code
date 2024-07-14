package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

public class BigDataRenderResult
{

   public static final int MAX_BYTES_IN_CELL_DETAIL_DISPLAY = 10000;

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DataTypeBinary.class);
   private static ILogger s_log = LoggerController.createLogger(BigDataRenderResult.class);


   private final String _renderResult;
   private final boolean _maxBytesReached;

   public BigDataRenderResult(String renderResult, boolean maxBytesReached)
   {
      _renderResult = renderResult;
      _maxBytesReached = maxBytesReached;
   }

   public String getRenderResult()
   {
      return _renderResult;
   }

   public boolean isMaxBytesReached()
   {
      return _maxBytesReached;
   }

   public static void showMaxBytesReachedMessage(ColumnDisplayDefinition colDef)
   {
      String msg = s_stringMgr.getString("BinaryTypeRenderResult.MaxBytesReached", MAX_BYTES_IN_CELL_DETAIL_DISPLAY, colDef.getFullTableColumnName(), colDef.getSqlTypeName());
      s_log.warn(msg);
      Main.getApplication().getMessageHandler().showWarningMessage(msg);
   }

   public static void showStringLenReachedMessage(ColumnDisplayDefinition colDef)
   {
      String msg = s_stringMgr.getString("BinaryTypeRenderResult.StringLenReached", MAX_BYTES_IN_CELL_DETAIL_DISPLAY, colDef.getFullTableColumnName(), colDef.getSqlTypeName());
      s_log.warn(msg);
      Main.getApplication().getMessageHandler().showWarningMessage(msg);
   }

}
