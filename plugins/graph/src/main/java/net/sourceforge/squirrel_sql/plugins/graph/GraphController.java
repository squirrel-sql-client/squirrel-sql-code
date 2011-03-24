package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.objecttree.ObjectTreeNode;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.plugins.graph.window.TabToWindowHandler;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.GraphControllerXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.GraphXmlSerializer;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.TableFrameControllerXmlBean;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.util.*;
import java.util.List;


public class GraphController implements GraphControllerAccessor
{
   private ISession _session;
   private GraphPanelController _panelController;

   private TableFramesModel _tableFramesModel = new TableFramesModel();
   private static final int BORDER_X = ConstraintView.STUB_LENGTH + 10;
   private static final int BORDER_Y = 10;
   private AddTableListener _addTableListener;
   private GraphDesktopListener _graphDesktopListener;
   private GraphPlugin _plugin;
   private TabToWindowHandler _tabToWindowHandler;
   private GraphXmlSerializer _xmlSerializer;

   public GraphController(ISession session, GraphPlugin plugin, GraphXmlSerializer xmlSerializer)
   {
      _session = session;
      _plugin = plugin;

      _graphDesktopListener = new GraphDesktopListener()
      {
         public void saveGraphRequested()
         {
            saveGraph();
         }

         public void renameRequest(String newName)
         {
            renameGraph(newName);
         }

         public void removeRequest()
         {
            removeGraph();
         }

         public void refreshAllTablesRequested()
         {
            refreshAllTables();
         }

         public void scriptAllTablesRequested()
         {
            scriptAllTables();
         }

         public void allTablesPkConstOrderRequested()
         {
            _tableFramesModel.allTablesPkConstOrder();
         }

         public void allTablesByNameOrderRequested()
         {
            _tableFramesModel.allTablesByNameOrder();
         }

         public void allTablesDbOrderRequested()
         {
            _tableFramesModel.allTablesDbOrder();
         }

         @Override
         public void allTablesFilteredSelectedOrderRequested()
         {
            _tableFramesModel.allTablesFilteredSelectedOrder();
         }

         public void showQualifiedTableNamesRequested()
         {
            _tableFramesModel.refreshTableNames();
         }

         @Override
         public void tablesDropped(List<ITableInfo> tis, Point dropPoint)
         {
            onTablesDropped(tis, dropPoint);
         }

         @Override
         public void toggleWindowTab()
         {
            _tabToWindowHandler.toggleWindowTab();
         }
      };

      _panelController = new GraphPanelController(_tableFramesModel, _graphDesktopListener, _session, _plugin);
      _tabToWindowHandler = new TabToWindowHandler(_panelController, _session, _plugin);


      if(null == xmlSerializer)
      {
         _xmlSerializer = new GraphXmlSerializer(_plugin, _session, _tabToWindowHandler.getTitle(), null);
      }
      else
      {
         _xmlSerializer = xmlSerializer;
      }

      _addTableListener = new AddTableListener()
      {
         public void addTablesRequest(String[] tablenames, String schema, String catalog)
         {
            onAddTablesRequest(tablenames, schema, catalog);
         }
      };

      GraphControllerXmlBean graphControllerXmlBean = null;
      if(null != xmlSerializer)
      {
         graphControllerXmlBean = _xmlSerializer.read();
         _tabToWindowHandler.setTitle(graphControllerXmlBean.getTitle());
         _panelController.initMode(Mode.getForIndex(graphControllerXmlBean.getModeIndex()), graphControllerXmlBean.getZoomerXmlBean(), graphControllerXmlBean.getPrintXmlBean());
         _panelController.getDesktopController().setShowConstraintNames(graphControllerXmlBean.isShowConstraintNames());
         _panelController.getDesktopController().setShowQualifiedTableNames(graphControllerXmlBean.isShowQualifiedTableNames());
      }
      else
      {
         _tabToWindowHandler.setTitle(_plugin.patchName(_tabToWindowHandler.getTitle(), _session));
         _panelController.initMode(Mode.DEFAULT, null, null);
      }

      _tabToWindowHandler.showGraph();

      if(null != graphControllerXmlBean)
      {
         TableFrameControllerXmlBean[] tableFrameControllerXmls = graphControllerXmlBean.getTableFrameControllerXmls();
         for (int i = 0; i < tableFrameControllerXmls.length; i++)
         {
            addTableIntern(new Positioner(), null, null, null, tableFrameControllerXmls[i]);
         }
      }
   }

   private void onTablesDropped(List<ITableInfo> tis, Point dropPoint)
   {
      Positioner positioner = new Positioner(dropPoint);
      for (ITableInfo ti : tis)
      {
         addTableIntern(positioner, ti.getSimpleName(), ti.getSchemaName(), ti.getCatalogName(), null);
      }
   }



   private void scriptAllTables()
   {
      Vector<TableFrameController> tblCtrls = _tableFramesModel.getTblCtrls();

      ITableInfo[] tableInfos = new ITableInfo[tblCtrls.size()];

      for (int i = 0; i < tblCtrls.size(); i++)
      {
         TableFrameController tableFrameController = tblCtrls.get(i);
         tableInfos[i] = tableFrameController.getTableInfo();
      }

      Window parent = SwingUtilities.windowForComponent(_panelController.getGraphPanel());
      SqlScriptAcessor.scriptTablesToSQLEntryArea(parent, _session, tableInfos);
   }

   private void refreshAllTables()
   {
      _tableFramesModel.refreshAllTables();
   }

   private void removeGraph()
   {
      _xmlSerializer.remove();
      _tabToWindowHandler.removeGraph();
      _plugin.removeGraphController(this, _session);
   }

   private void renameGraph(String newName)
   {
      if(newName.equals(_tabToWindowHandler.getTitle()))
      {
         return;
      }

      newName = _plugin.patchName(newName, _session);
      _xmlSerializer.rename(newName);

      _tabToWindowHandler.renameGraph(newName);

      saveGraph();
   }

   public void saveGraph()
   {
      GraphControllerXmlBean xmlBean = new GraphControllerXmlBean();
      xmlBean.setTitle(_tabToWindowHandler.getTitle());
      xmlBean.setShowConstraintNames(_panelController.getDesktopController().isShowConstraintNames());
      xmlBean.setZoomerXmlBean(_panelController.getDesktopController().getZoomer().getXmlBean());
      xmlBean.setPrintXmlBean(_panelController.getDesktopController().getZoomPrintController().getPrintXmlBean());
      xmlBean.setModeIndex(_panelController.getModeManager().getMode().getIndex());

      Vector<TableFrameController> tblCtrls = _tableFramesModel.getTblCtrls();

      TableFrameControllerXmlBean[] frameXmls = new TableFrameControllerXmlBean[tblCtrls.size()];


      for (int i = 0; i < tblCtrls.size(); i++)
      {
         TableFrameController tableFrameController = tblCtrls.get(i);
         frameXmls[i] = tableFrameController.getXmlBean(); 
      }
      xmlBean.setTableFrameControllerXmls(frameXmls);

      _xmlSerializer.write(xmlBean);
   }




   private void onAddTablesRequest(String[] tablenames, String schema, String catalog)
   {
      Positioner positioner = new Positioner();

      for (int i = 0; i < tablenames.length; i++)
      {
         addTableIntern(positioner, tablenames[i], schema, catalog, null);
      }
   }



   public void addTable(ObjectTreeNode selectedNode, final Positioner positioner)
   {
      String catalog = selectedNode.getDatabaseObjectInfo().getCatalogName();
      String schema = selectedNode.getDatabaseObjectInfo().getSchemaName();
      String table = selectedNode.getDatabaseObjectInfo().getSimpleName();

      addTableIntern(positioner, table, schema, catalog,  null);
   }


   private void addTableIntern(final Positioner positioner, String tableName, String schemaName, String catalogName, final TableFrameControllerXmlBean xmlBean)
   {
      final TableFrameController tfc;

      if(null == xmlBean)
      {
         tfc = new TableFrameController(_plugin, _session, _panelController.getDesktopController(), this, _addTableListener, tableName, schemaName, catalogName, null);
      }
      else
      {
         tfc = new TableFrameController(_plugin, _session, _panelController.getDesktopController(), this ,_addTableListener, null, null, null, xmlBean);
      }

      if (_tableFramesModel.containsTable(tfc))
      {
         return;
      }


      //tfc.addTableFrameControllerListener(_tableFrameControllerListener);

      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
             _panelController.getDesktopController().addFrame(tfc.getFrame());
             _tableFramesModel.addTable(tfc);
            initsAfterFrameAdded(tfc, positioner, null == xmlBean);
         }
      });

   }


   public TableFramesModel getTableFrameModel()
   {
      return _tableFramesModel;
   }

   private void calcPosition(final TableFrameController tfc, Positioner positioner)
   {
      Dimension frmSize = tfc.getFrame().getSize();


      if (null == positioner.getDropPointClone())
      {
         ///////////////////////////////////////////////////////////////////////////
         // We try to find a completely free space for the new table frame.
         for (int y = 0; y < _panelController.getDesktopController().getDesktopPane().getSize().getHeight(); y += frmSize.height + BORDER_Y)
         {
            for (int x = 0; x < _panelController.getDesktopController().getDesktopPane().getSize().getWidth(); x += frmSize.width + BORDER_X)
            {
               Point leftUp = new Point(x, y);
               Point rightDown = new Point(x + frmSize.width, y + frmSize.height);

               if (isInDesktop(leftUp) && isInDesktop(rightDown) && false == isRectangleOccupied(leftUp, rightDown, tfc))
               {
                  tfc.getFrame().setBounds(leftUp.x, leftUp.y, tfc.getFrame().getBounds().width, tfc.getFrame().getBounds().height);
                  return;
               }
            }
         }
         //
         //////////////////////////////////////////////////////////////////////////
      }


      ////////////////////////////////////////////////////////////////////////////////
      // We try to cascade
      int cascadeIndent = tfc.getFrame().getTitlePane().getSize().height;

      if (null == positioner.getRefPoint())
      {
         if (null != positioner.getDropPointClone())
         {
            positioner.setRefPoint(positioner.getDropPointClone());
         }
         else
         {
            positioner.setRefPoint(new Point(cascadeIndent, cascadeIndent));
         }
      }
      else
      {
         Point refPoint = positioner.getRefPoint();
         positioner.setRefPoint(new Point(refPoint .x + cascadeIndent, refPoint .y + cascadeIndent));
      }

      for (int x = positioner.getRefPoint().x; x < _panelController.getDesktopController().getDesktopPane().getSize().getWidth(); x += cascadeIndent)
      {
         for (int y = positioner.getRefPoint().y; y < _panelController.getDesktopController().getDesktopPane().getSize().getHeight(); y += cascadeIndent)
         {
            Point leftUp = new Point(x, y);
            Point rightDown = new Point(x + frmSize.width, y + frmSize.height);

            if (isInDesktop(leftUp) && isInDesktop(rightDown))
            {
               tfc.getFrame().setBounds(leftUp.x, leftUp.y, tfc.getFrame().getBounds().width, tfc.getFrame().getBounds().height);
               positioner.getRefPoint().y += cascadeIndent;
               return;
            }
         }
         positioner.getRefPoint().y = cascadeIndent;
      }
      //
      //////////////////////////////////////////////////////////////////////////////////


      // If we reach here we could not calculate a place to add the new table frame.
      // Now it will automatically be added at (0,0).


   }

   private boolean isRectangleOccupied(Point leftUp, Point rightDown, TableFrameController toExclude)
   {
      Vector<TableFrameController> tblCtrls = _tableFramesModel.getTblCtrls();

      for (int i = 0; i < tblCtrls.size(); i++)
      {
         TableFrameController tfc = tblCtrls.get(i);

         if (tfc.equals(toExclude))
         {
            continue;
         }

         Rectangle rectTfc = tfc.getFrame().getBounds();

         Rectangle rectParam = new Rectangle(leftUp.x - BORDER_X, leftUp.y - BORDER_Y, rightDown.x - leftUp.x + BORDER_X, rightDown.y - leftUp.y + BORDER_Y);

         Rectangle2D interSect = rectParam.createIntersection(rectTfc);

         if (0 < interSect.getWidth() && 0 < interSect.getHeight())
         {
            return true;
         }
      }
      return false;

   }


   private boolean isInDesktop(Point p)
   {
      if (_panelController.getDesktopController().getDesktopPane().getSize().width >= p.x && _panelController.getDesktopController().getDesktopPane().getSize().height >= p.y)
      {
         return true;
      }
      return false;
   }

   private void initsAfterFrameAdded(TableFrameController tfc, Positioner positioner, boolean resetBounds)
   {

      Vector<TableFrameController> tblCtrls = _tableFramesModel.getTblCtrls();

      for (int i = 0; i < tblCtrls.size(); i++)
      {
         TableFrameController buf = tblCtrls.get(i);
         if (false == buf.equals(tfc))
         {
            buf.tableFrameOpen(tfc);
         }
      }

      Vector<TableFrameController> others = new  Vector<TableFrameController>(_tableFramesModel.getTblCtrls());
      others.remove(tfc);
      TableFrameController[] othersArr = others.toArray(new TableFrameController[others.size()]);
      tfc.initAfterAddedToDesktop(othersArr, resetBounds);

      if(resetBounds)
      {
         calcPosition(tfc, positioner);
      }

   }

   public String getTitle()
   {
      return _tabToWindowHandler.getTitle();
   }

   public String toString()
   {
      return getTitle();
   }

   public void sessionEnding()
   {
      _panelController.sessionEnding();
   }

   public void showQueryBuilderInWindowBesidesObjectTree()
   {
      _panelController.getModeManager().setMode(Mode.QUERY_BUILDER);
      _tabToWindowHandler.showInWindowBesidesObjectTree();
   }
}

