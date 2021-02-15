package net.sourceforge.squirrel_sql.plugins.graph;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.gui.GUIUtils;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.plugins.graph.link.CopyGraphAction;
import net.sourceforge.squirrel_sql.plugins.graph.querybuilder.WhereTreeNodeStructure;
import net.sourceforge.squirrel_sql.plugins.graph.window.TabToWindowHandler;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.GraphControllerXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.GraphXmlSerializer;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.OrderStructureXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.PrintXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.SelectStructureXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.TableFrameControllerXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.ZoomerXmlBean;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.SwingUtilities;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;
import java.awt.geom.Rectangle2D;
import java.util.List;
import java.util.Vector;


public class GraphController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(GraphController.class);


   private ISession _session;
   private GraphPanelController _panelController;

   private TableFramesModel _tableFramesModel = new TableFramesModel();
   private static final int BORDER_X = ConstraintView.STUB_LENGTH + 10;
   private static final int BORDER_Y = 10;
   private AddTableRequestListener _addTableRequestListener;
   private GraphDesktopChannel _graphDesktopChannel;
   private GraphPlugin _plugin;
   private TabToWindowHandler _tabToWindowHandler;
   private GraphXmlSerializer _xmlSerializer;
   private boolean _lazyLoadDone;

   public GraphController(ISession session, GraphPlugin plugin, final GraphXmlSerializer xmlSerializer, boolean showDndDesktopImageAtStartup, boolean selectTab)
   {
      _session = session;
      _plugin = plugin;

      _graphDesktopChannel = new GraphDesktopChannel()
      {
         public void saveGraphRequested()
         {
            saveGraph();
         }

         @Override
         public void renameRequest(String newName)
         {
            renameGraph(newName);
         }

         @Override
         public void removeRequest()
         {
            removeGraph();
         }

         @Override
         public void refreshAllTablesRequested()
         {
            refreshAllTables();
         }

         @Override
         public void scriptAllTablesRequested()
         {
            scriptAllTables();
         }

         @Override
         public void allTablesPkConstOrderRequested()
         {
            _tableFramesModel.allTablesPkConstOrder();
         }

         @Override
         public void allTablesByNameOrderRequested()
         {
            _tableFramesModel.allTablesByNameOrder();
         }

         @Override
         public void allTablesDbOrderRequested()
         {
            _tableFramesModel.allTablesDbOrder();
         }

         @Override
         public void allTablesFilteredSelectedOrderRequested()
         {
            _tableFramesModel.allTablesFilteredSelectedOrder();
         }

         @Override
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

         @Override
         public boolean isLink()
         {
            return null != xmlSerializer && xmlSerializer.isLink();
         }

         @Override
         public void saveLinkAsLocalCopy()
         {
            onSaveLinkAsLocalCopy();
         }

         @Override
         public void saveLinkedGraph()
         {
            saveGraph();
         }

         @Override
         public void removeLink()
         {
            onRemoveLink();
         }

         @Override
         public void showLinkDetails()
         {
            onShowLinkDetails();
         }

         @Override
         public void copyGraph()
         {
            onCopyGraph();
         }

         @Override
         public String getGraphName()
         {
            return _tabToWindowHandler.getTitle();
         }
      };


      if(null == xmlSerializer)
      {
         _xmlSerializer = new GraphXmlSerializer(_plugin, _session, null);
      }
      else
      {
         _xmlSerializer = xmlSerializer;
      }

      _panelController = new GraphPanelController(_tableFramesModel, _graphDesktopChannel, _session, _plugin, showDndDesktopImageAtStartup);

      _tabToWindowHandler = new TabToWindowHandler(_panelController, _session, _plugin, _xmlSerializer.isLink());


      _addTableRequestListener = new AddTableRequestListener()
      {
         public void addTablesRequest(String[] tablenames, String schema, String catalog)
         {
            onAddTablesRequest(tablenames, schema, catalog);
         }
      };

      GraphControllerXmlBean graphControllerXmlBean = null;
      if(_xmlSerializer.isLoadable())
      {
         graphControllerXmlBean = _xmlSerializer.read();
         _tabToWindowHandler.setTitle(graphControllerXmlBean.getTitle());
         Mode modeIndex = Mode.getForIndex(graphControllerXmlBean.getModeIndex());

         ZoomerXmlBean zoomerXmlBean = graphControllerXmlBean.getZoomerXmlBean();
         PrintXmlBean printXmlBean = graphControllerXmlBean.getPrintXmlBean();
         boolean queryHideNoJoins = graphControllerXmlBean.isQueryHideNoJoins();
         WhereTreeNodeStructure whereTreeNodeStructure = graphControllerXmlBean.getWhereTreeNodeStructure();
         OrderStructureXmlBean orderStructure = graphControllerXmlBean.getOrderStructure();
         SelectStructureXmlBean selectStructure = graphControllerXmlBean.getSelectStructure();

         _panelController.initMode(modeIndex, zoomerXmlBean, printXmlBean, queryHideNoJoins, selectStructure, whereTreeNodeStructure, orderStructure);

         _panelController.getDesktopController().setShowConstraintNames(graphControllerXmlBean.isShowConstraintNames());
         _panelController.getDesktopController().setShowQualifiedTableNames(graphControllerXmlBean.isShowQualifiedTableNames());
      }
      else
      {
         _tabToWindowHandler.setTitle(_xmlSerializer.getTitle());
         _panelController.initMode(Mode.DEFAULT, null, null, false, null, null, null);
      }


      final GraphControllerXmlBean finalGraphControllerXmlBean = graphControllerXmlBean;

      LazyLoadListener lazyLoadListener = new LazyLoadListener()
      {
         @Override
         public void lazyLoadTables()
         {
            onLazyLoadTables(finalGraphControllerXmlBean);
         }
      };
      _tabToWindowHandler.showGraph(lazyLoadListener, selectTab);
   }

   private void onCopyGraph()
   {
      CopyGraphAction.copyGraph(this);
   }

   private void onShowLinkDetails()
   {
      JPanel pnl = new JPanel(new GridBagLayout());
      GridBagConstraints gbc;
      
      
      // Link {0} points to Graph {1} in file:   
      String linkName = _xmlSerializer.getLinkXmlBean().getLinkName();
      String nameOfLinkedGraph = _xmlSerializer.getLinkXmlBean().getNameOfLinkedGraph();
      String msg = s_stringMgr.getString("graph.link.linkDetailsMsg", linkName, nameOfLinkedGraph);

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(0,0,0,0),0,0);
      pnl.add(new JLabel(msg), gbc);

      gbc = new GridBagConstraints(0,1,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,0),0,0);
      JTextField textField = new JTextField(_xmlSerializer.getLinkXmlBean().getFilePathOfLinkedGraph());
      textField.setEditable(false);
      pnl.add(textField, gbc);


      String msg2 = s_stringMgr.getString("graph.link.linkDetailsMsg2");
      gbc = new GridBagConstraints(0,2,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(15,0,0,0),0,0);
      pnl.add(new JLabel(msg2), gbc);

      gbc = new GridBagConstraints(0,3,1,1,0,0,GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(5,0,0,0),0,0);
      JTextField textField2 = new JTextField(_xmlSerializer.getLinkFile());
      textField2.setEditable(false);
      pnl.add(textField2, gbc);



      JOptionPane.showMessageDialog(GUIUtils.getOwningFrame(_tabToWindowHandler.getComponent()), pnl);
   }

   private void onLazyLoadTables(GraphControllerXmlBean graphControllerXmlBean)
   {
      if(null != graphControllerXmlBean && false == _lazyLoadDone)
      {
         _lazyLoadDone = true;
         TableFrameControllerXmlBean[] tableFrameControllerXmls = graphControllerXmlBean.getTableFrameControllerXmls();
         for (int i = 0; i < tableFrameControllerXmls.length; i++)
         {
            addTableIntern(new Positioner(), null, null, null, tableFrameControllerXmls[i]);
         }

         final GraphControllerXmlBean finalGraphControllerXmlBean = graphControllerXmlBean;
         SwingUtilities.invokeLater(new Runnable()
         {
            public void run()
            {
               _tableFramesModel.hideNoJoins(finalGraphControllerXmlBean.isQueryHideNoJoins());
               _tableFramesModel.replaceColumnClonesInConstraintsByRefrences();
            }
         });
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

   private void onRemoveLink()
   {
      _xmlSerializer.removeLink();
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

   private void onSaveLinkAsLocalCopy()
   {
      _xmlSerializer.saveLinkAsLocalCopy(createXmlBean());
      _panelController.changedFromLinkToLocalCopy();
      _tabToWindowHandler.changedFromLinkToLocalCopy();
   }


   public void saveGraph()
   {
      GraphControllerXmlBean xmlBean = createXmlBean();
      _xmlSerializer.write(xmlBean);
   }

   public GraphControllerXmlBean createXmlBean()
   {
      GraphControllerXmlBean xmlBean = new GraphControllerXmlBean();
      xmlBean.setTitle(_tabToWindowHandler.getTitle());
      xmlBean.setShowConstraintNames(_panelController.getDesktopController().isShowConstraintNames());
      xmlBean.setShowQualifiedTableNames(_panelController.getDesktopController().isShowQualifiedTableNames());
      xmlBean.setZoomerXmlBean(_panelController.getDesktopController().getZoomer().getXmlBean());
      xmlBean.setPrintXmlBean(_panelController.getDesktopController().getZoomPrintController().getPrintXmlBean());
      xmlBean.setModeIndex(_panelController.getModeManager().getMode().getIndex());
      xmlBean.setQueryHideNoJoins(_panelController.getModeManager().isQueryHideNoJoins());
      xmlBean.setWhereTreeNodeStructure(_panelController.getModeManager().getWhereTreeNodeStructure());
      xmlBean.setOrderStructure(_panelController.getModeManager().getOrderStructure());
      xmlBean.setSelectStructure(_panelController.getModeManager().getSelectStructure());

      Vector<TableFrameController> tblCtrls = _tableFramesModel.getTblCtrls();

      TableFrameControllerXmlBean[] frameXmls = new TableFrameControllerXmlBean[tblCtrls.size()];


      for (int i = 0; i < tblCtrls.size(); i++)
      {
         TableFrameController tableFrameController = tblCtrls.get(i);
         frameXmls[i] = tableFrameController.getXmlBean();
      }
      xmlBean.setTableFrameControllerXmls(frameXmls);
      return xmlBean;
   }


   private void onAddTablesRequest(String[] tablenames, String schema, String catalog)
   {
      Positioner positioner = new Positioner();

      for (int i = 0; i < tablenames.length; i++)
      {
         addTableIntern(positioner, tablenames[i], schema, catalog, null);
      }
   }



   public void addTable(TableToAddWrapper toAddWrapper, final Positioner positioner)
   {
      String catalog = toAddWrapper.getCatalogName();
      String schema = toAddWrapper.getSchemaName();
      String table = toAddWrapper.getSimpleName();

      addTableIntern(positioner, table, schema, catalog,  null);
   }


   private void addTableIntern(final Positioner positioner, String tableName, String schemaName, String catalogName, final TableFrameControllerXmlBean xmlBean)
   {
      final TableFrameController tfc;

      if(null == xmlBean)
      {
         tfc = new TableFrameController(_plugin, _session, _panelController.getDesktopController(), _addTableRequestListener, tableName, schemaName, catalogName, null);
      }
      else
      {
         tfc = new TableFrameController(_plugin, _session, _panelController.getDesktopController(), _addTableRequestListener, null, null, null, xmlBean);
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
            boolean readingXml = (null != xmlBean);
            _panelController.getDesktopController().addFrame(tfc.getFrame());
            _tableFramesModel.addTable(tfc, readingXml);
            if (false == readingXml)
            {
               calcPosition(tfc, positioner);
            }

         }
      });

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

   public boolean isMyGraphMainPanelTab(GraphMainPanelTab graphMainPanelTab)
   {
      return _tabToWindowHandler.isMyGraphMainPanelTab(graphMainPanelTab);
   }
}

