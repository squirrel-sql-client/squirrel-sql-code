package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.graph.nondbconst.DndEvent;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.ConstraintViewXmlBean;

import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;

public class ConstraintViewsModel
{
   private final static ILogger s_log = LoggerController.createLogger(ConstraintViewsModel.class);
   static final StringManager s_stringMgr = StringManagerFactory.getStringManager(ConstraintViewsModel.class);


   private ConstraintView[] _constraintViews = new ConstraintView[0];
   private ISession _session;
   private ArrayList<ConstraintViewsModelListener> _listeners = new ArrayList<ConstraintViewsModelListener>();
   private ConstraintIconHandlerListener _constraintIconHandlerListener;

   public ConstraintViewsModel(ISession session)
   {
      _session = session;

      _constraintIconHandlerListener = new ConstraintIconHandlerListener()
      {
         @Override
         public void constraintTypeChanged()
         {
            fireListeners();
         }
      };

   }


   public ConstraintView[] getConstViews()
   {
      return _constraintViews;
   }

   public void initByXmlBeans(ConstraintViewXmlBean[] constraintViewXmlBeans, GraphDesktopController desktopController, ColumnInfo[] colInfos)
   {
      _constraintViews = new ConstraintView[constraintViewXmlBeans.length];
      for (int i = 0; i < _constraintViews.length; i++)
      {
         _constraintViews[i] = new ConstraintView(constraintViewXmlBeans[i], desktopController, _session, _constraintIconHandlerListener);
         _constraintViews[i].replaceCopiedColsByReferences(colInfos, false);
      }
   }

   public void addConst(ConstraintView constView)
   {
      ArrayList<ConstraintView> buf = new ArrayList<ConstraintView>();
      buf.addAll(Arrays.asList(_constraintViews));
      buf.add(constView);
      _constraintViews = buf.toArray(new ConstraintView[buf.size()]);
      fireListeners();
   }

   public void removeConst(ConstraintView constraintView)
   {
      constraintView.clearColumnImportData();
      ArrayList<ConstraintView> buf = new ArrayList<ConstraintView>();
      buf.addAll(Arrays.asList(_constraintViews));
      buf.remove(constraintView);
      _constraintViews = buf.toArray(new ConstraintView[buf.size()]);
      fireListeners();
   }

   private void fireListeners()
   {
      ConstraintViewsModelListener[] listeners = _listeners.toArray(new ConstraintViewsModelListener[_listeners.size()]);

      for (ConstraintViewsModelListener listener : listeners)
      {
         listener.constraintsChanged();
      }
   }

   public void initFromDB(DatabaseMetaData metaData, String catalog, String schema, String tableName, ColumnInfoModel colInfoModel, GraphDesktopController desktopController)
   {
      Hashtable<String, ConstraintData> dbConstraintInfosByConstraintName = new Hashtable<String, ConstraintData>();


      ResultSet res = null;
      try
      {
         res = metaData.getImportedKeys(catalog, schema, tableName);
         while (res.next())
         {
            String pkTable = res.getString(3);   // PKTABLE_NAME
            String pkColName = res.getString(4); // PKCOLUMN_NAME
            String fkColName = res.getString(8); // FKCOLUMN_NAME
            String fkName = res.getString(12);   // FK_NAME

            ColumnInfo colInfo = colInfoModel.findColumnInfo(fkColName);
            colInfo.setImportData(pkTable, pkColName, fkName, false);

            ConstraintData dbConstraintData = dbConstraintInfosByConstraintName.get(fkName);

            if (null == dbConstraintData)
            {
               dbConstraintData = new ConstraintData(pkTable, tableName, fkName);
               dbConstraintInfosByConstraintName.put(fkName, dbConstraintData);
            }
            dbConstraintData.addColumnInfo(colInfo);
         }
      }
      catch (SQLException e)
      {
         s_log.error("Unable to get Foriegn Key info", e);
      }
      finally
      {
         if (res != null)
         {
            try
            {
               res.close();
            }
            catch (SQLException e)
            {
            }
         }
      }


      ConstraintData[] newDBconstraintData = dbConstraintInfosByConstraintName.values().toArray(new ConstraintData[0]);
      Hashtable<String, ConstraintView> oldDBConstraintViewsByConstraintName = new Hashtable<String, ConstraintView>();
      ArrayList<ConstraintView> oldNonDBConstraintViews = new ArrayList<ConstraintView>();

      desktopController.removeConstraintViews(_constraintViews, true);


      for (int i = 0; i < _constraintViews.length; i++)
      {
         if (_constraintViews[i].getData().isNonDbConstraint())
         {
            _constraintViews[i].replaceCopiedColsByReferences(colInfoModel.getAll(), true);
            oldNonDBConstraintViews.add(_constraintViews[i]);
         }
         else
         {
            String constraintName = _constraintViews[i].getData().getConstraintName();
            oldDBConstraintViewsByConstraintName.put(constraintName, _constraintViews[i]);
         }

      }

      ArrayList<ConstraintView> newConstraintViewsBuf = new ArrayList<ConstraintView>();
      for (int i = 0; i < newDBconstraintData.length; i++)
      {
         ConstraintView oldCV =
             oldDBConstraintViewsByConstraintName.get(newDBconstraintData[i].getConstraintName());

         if(null != oldCV)
         {
            // The old view is preserved to eventually preserve folding points
            oldCV.setData(newDBconstraintData[i]);
            newConstraintViewsBuf.add(oldCV);
         }
         else
         {
            newConstraintViewsBuf.add(new ConstraintView(newDBconstraintData[i], desktopController, _session, _constraintIconHandlerListener));
         }
      }

      removeOverlappingConstraints(newConstraintViewsBuf, oldNonDBConstraintViews);


      newConstraintViewsBuf.addAll(oldNonDBConstraintViews);

      _constraintViews = newConstraintViewsBuf.toArray(new ConstraintView[newConstraintViewsBuf.size()]);
   }

   public ConstraintView createConstraintView(DndEvent e, TableFrameController sourceTable, ColumnInfo sourceColumnInfo, GraphDesktopController desktopController, ISession session)
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
      ConstraintView ret = new ConstraintView(data, desktopController, session, _constraintIconHandlerListener);

      addConst(ret);
      return ret;
   }



   private void removeOverlappingConstraints(ArrayList<ConstraintView> master, ArrayList<ConstraintView> toRemoveFrom)
   {
      ArrayList<ConstraintView> removeBuf = new ArrayList<ConstraintView>();

      for (ConstraintView cvRemoveCand : toRemoveFrom)
      {
         for (ConstraintView cvMaster : master)
         {
            if(cvMaster.hasOverlap(cvRemoveCand))
            {
               removeBuf.add(cvRemoveCand);
               break;
            }
         }
      }

      toRemoveFrom.removeAll(removeBuf);

   }

   public ConstraintViewXmlBean[] getXmlBeans()
   {
      ConstraintViewXmlBean[] constViewXmlBeans = new ConstraintViewXmlBean[_constraintViews.length];
      for (int i = 0; i < _constraintViews.length; i++)
      {
         constViewXmlBeans[i] = _constraintViews[i].getXmlBean();
      }

      return constViewXmlBeans;
   }

   public ConstraintView[] removeConstraintsForTable(String tableName)
   {
      List<ConstraintView> newConstraintData = new ArrayList<ConstraintView>();

      List<ConstraintView> constraintDataToRemove = new ArrayList<ConstraintView>();

      for (int i = 0; i < _constraintViews.length; i++)
      {
         if(_constraintViews[i].getData().getPkTableName().equals(tableName))
         {
            constraintDataToRemove.add(_constraintViews[i]);
         }
         else
         {
            newConstraintData.add(_constraintViews[i]);
         }
      }

      return constraintDataToRemove.toArray(new ConstraintView[constraintDataToRemove.size()]);
   }

   public ConstraintView[] findConstraintViews(String tableName)
   {
      List<ConstraintView> ret = new ArrayList<ConstraintView>();
      for (int i = 0; i < _constraintViews.length; i++)
      {
         if(_constraintViews[i].getData().getPkTableName().equals(tableName))
         {
            ret.add(_constraintViews[i]);
         }
      }
      return ret.toArray(new ConstraintView[ret.size()]);
   }

   public void addListener(ConstraintViewsModelListener l)
   {
      _listeners.remove(l);
      _listeners.add(l);

   }

   public void hideNoJoins(boolean b)
   {
      for (ConstraintView constraintView : _constraintViews)
      {
         constraintView.setHideIfNoJoin(b);
      }
   }

   public boolean containsUniddenNoJoins()
   {
      for (ConstraintView constraintView : _constraintViews)
      {
         if(constraintView.isUniddenNoJoin())
         {
            return true;
         }
      }
      return false;
   }
}
