package net.sourceforge.squirrel_sql.fw.sql.databasemetadata;

import net.sourceforge.squirrel_sql.fw.datasetviewer.BlockMode;
import net.sourceforge.squirrel_sql.fw.dialects.DialectFactory;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.sql.*;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProcedureAndFunctionMetaData
{
   private final static ILogger s_log = LoggerController.createLogger(SQLDatabaseMetaData.class);

   static List<IProcedureInfo> getProcedureInfos(String catalog, String schemaPattern, String procedureNamePattern, ProgressCallBack progressCallBack, SQLDatabaseMetaData md) throws SQLException
   {
      ArrayList<IProcedureInfo> list = new ArrayList<>();
      ResultSet rs = md.getJDBCMetaData().getProcedures(catalog, schemaPattern, procedureNamePattern);
      if (rs == null)
      {
         return list;
      }

      int count = 0;
      try
      {
         final int[] cols = new int[]
               {
                     1, // PROCEDURE_CAT
                     2, // PROCEDURE_SCHEM
                     3, // PROCEDURE_NAME
                     7, // REMARKS
                     8  // PROCEDURE_TYPE
               };

         DialectType dialectType = DialectFactory.getDialectType(md);
         final ResultSetReader rdr = new ResultSetReader(rs, cols, dialectType);
         Object[] row;
         while ((row = rdr.readRow(BlockMode.INDIFFERENT)) != null)
         {
            // Sybase IQ using jdbc3 driver returns null for some procedure return types - this is probably
            // outside the JDBC spec.
            // The safest solution seems to be to set it to Unknown result type.
            if (row[4] == null || false == row[4] instanceof Number)
            {
               if (row[4] != null)
               {
                  s_log.warn("Error reading procedure meta data for column 8 (PROCEDURE_TYPE): " +
                        "According to the API of java.sql.DatabaseMetaData.getProcedures(...) " +
                        "the type should be int but is " + row[4].getClass().getName() + " with value " + row[4]);
               }

               row[4] = DatabaseMetaData.procedureResultUnknown;

            }
            final int type = ((Number) row[4]).intValue();
            ProcedureInfo pi = new ProcedureInfo(SQLDatabaseMetaDataUtil.getAsString(row[0]),
                                                 SQLDatabaseMetaDataUtil.getAsString(row[1]),
                                                 SQLDatabaseMetaDataUtil.getAsString(row[2]),
                                                 SQLDatabaseMetaDataUtil.getAsString(row[3]),
                                                 type, ProcedureInfoOrigin.GET_PROCEDURES, md);

            list.add(pi);

            if (null != progressCallBack)
            {
               if (0 == count++ % 200)
               {
                  progressCallBack.currentlyLoading(pi.getSimpleName());
               }
            }
         }
      }
      finally
      {
         SQLUtilities.closeResultSet(rs);
      }
      return list;
   }

   static List<IProcedureInfo> getFunctionInfos(String catalog, String schemaPattern, String funtionNamePattern, ProgressCallBack progressCallBack, SQLDatabaseMetaData md) throws SQLException
   {
      try
      {
         return _getFunctionInfos(catalog, schemaPattern, funtionNamePattern, progressCallBack, md);
      }
      catch (Exception e)
      {
         // As java.sql.DataBaseMetaData.getFunctions() was introduced much later than java.sql.DataBaseMetaData.getProcedures()
         // we don't expect all Drivers to support it.
         s_log.error(e);
         return Collections.emptyList();
      }
   }

   private static ArrayList<IProcedureInfo> _getFunctionInfos(String catalog, String schemaPattern, String funtionNamePattern, ProgressCallBack progressCallBack, SQLDatabaseMetaData md) throws SQLException
   {
      ArrayList<IProcedureInfo> list = new ArrayList<>();
      ResultSet rs = md.getJDBCMetaData().getFunctions(catalog, schemaPattern, funtionNamePattern);
      if (rs == null)
      {
         return list;
      }

      int count = 0;
      try
      {
         final int[] cols = new int[]
               {
                     1, // FUNCTION_CAT
                     2, // FUNCTION_SCHEM
                     3, // FUNCTION_NAME
                     4, // REMARKS
                     5  // FUNCTION_TYPE
               };

         DialectType dialectType = DialectFactory.getDialectType(md);
         final ResultSetReader rdr = new ResultSetReader(rs, cols, dialectType);
         Object[] row;
         while ((row = rdr.readRow(BlockMode.INDIFFERENT)) != null)
         {
            // Sybase IQ using jdbc3 driver returns null for some procedure return types - this is probably
            // outside the JDBC spec.
            // The safest solution seems to be to set it to Unknown result type.
            if (row[4] == null || false == row[4] instanceof Number)
            {
               if (row[4] != null)
               {
                  s_log.warn("Error reading procedure meta data for column 8 (PROCEDURE_TYPE): " +
                        "According to the API of java.sql.DatabaseMetaData.getProcedures(...) " +
                        "the type should be int but is " + row[4].getClass().getName() + " with value " + row[4]);
               }

               row[4] = DatabaseMetaData.functionResultUnknown;

            }
            final int type = ((Number) row[4]).intValue();
            ProcedureInfo pi = new ProcedureInfo(SQLDatabaseMetaDataUtil.getAsString(row[0]),
                                                 SQLDatabaseMetaDataUtil.getAsString(row[1]),
                                                 SQLDatabaseMetaDataUtil.getAsString(row[2]),
                                                 SQLDatabaseMetaDataUtil.getAsString(row[3]),
                                                 type, ProcedureInfoOrigin.GET_FUNCTIONS, md);

            list.add(pi);

            if (null != progressCallBack)
            {
               if (0 == count++ % 200)
               {
                  progressCallBack.currentlyLoading(pi.getSimpleName());
               }
            }
         }
      }
      finally
      {
         SQLUtilities.closeResultSet(rs);
      }
      return list;
   }
}
