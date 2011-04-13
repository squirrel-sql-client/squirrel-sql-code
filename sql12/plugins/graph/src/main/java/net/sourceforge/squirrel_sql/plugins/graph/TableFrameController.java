package net.sourceforge.squirrel_sql.plugins.graph;

import java.awt.*;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ObjectTreeSearch;
import net.sourceforge.squirrel_sql.client.session.schemainfo.ObjFilterMatcher;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.graph.nondbconst.DndCallback;
import net.sourceforge.squirrel_sql.plugins.graph.nondbconst.DndEvent;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.ColumnInfoXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.TableFrameControllerXmlBean;


public class TableFrameController
{
	private static final StringManager s_stringMgr =
		StringManagerFactory.getStringManager(TableFrameController.class);

    /** Logger for this class. */
    private final static ILogger s_log =
        LoggerController.createLogger(TableFrameController.class);


   ////////////////////////////////////////
   // Serialized attributes
   private String _schema;
   private String _catalog;
   private String _tableName;
   private TableFrame _frame;

   private ColumnInfoModel _colInfoModel = new ColumnInfoModel();
   private ConstraintViewsModel _constraintViewsModel;
   private String[] _tablesExportedTo;
   //
   ///////////////////////////////////////////


   private ISession _session;
   private Rectangle _startSize;
   private GraphDesktopController _desktopController;
   private Vector<TableFrameControllerListener> _listeners = new Vector<TableFrameControllerListener>();
   private Vector<TableFrameController> _openFramesConnectedToMe = new Vector<TableFrameController>();
   private Hashtable<TableFrameController, ComponentAdapter> _compListenersToOtherFramesByFrameCtrlr = 
       new Hashtable<TableFrameController, ComponentAdapter>();
   private Hashtable<TableFrameController, AdjustmentListener> _scrollListenersToOtherFramesByFrameCtrlr = 
       new Hashtable<TableFrameController, AdjustmentListener>();
   private Hashtable<TableFrameController, ColumnSortListener> _columnSortListenersToOtherFramesByFrameCtrlr = 
       new Hashtable<TableFrameController, ColumnSortListener>();

   private Vector<ColumnSortListener> _mySortListeners = 
       new Vector<ColumnSortListener>();

   private JPopupMenu _popUp;
   private JMenuItem _mnuAddTableForForeignKey;
   private JMenuItem _mnuAddChildTables;
   private JMenuItem _mnuAddParentTables;
   private JMenuItem _mnuAddAllRelatedTables;
   private JMenuItem _mnuRefreshTable;
   private JMenuItem _mnuScriptTable;
   private JMenuItem _mnuCopyTableName;
   private JMenuItem _mnuCopyQualifiedTableName;
   private JMenuItem _mnuViewTableInObjectTree;
   private JCheckBoxMenuItem _mnuOrderByName;
   private JCheckBoxMenuItem _mnuPksAndConstraintsOnTop;
   private JCheckBoxMenuItem _mnuDbOrder;
   private JCheckBoxMenuItem _mnuQueryFiltersAndSelectedOnTop;
   private JMenuItem _mnuQuerySelectAll;
   private JMenuItem _mnuQueryUnselectAll;
   private JMenuItem _mnuQueryClearAllFilters;
   private JMenuItem _mnuClose;
   private AddTableRequestListener _addTablelRequestListener;
   private ConstraintViewListener _constraintViewListener;

   private OrderType _columnOrderType = OrderType.ORDER_DB;


   private static final String MNU_PROP_COLUMN_INFO = "MNU_PROP_COLUMN_INFO";
   private ModeManagerListener _modeManagerListener;
   private ZoomerListener _zoomerListener;
   private Rectangle _adjustBeginBounds;
   private double _adjustBeginZoom;
   private TableFramesModelListener _tableFramesModelListener;
   private ComponentAdapter _componentListener;
   private InternalFrameAdapter _internalFrameListener;


   public TableFrameController(GraphPlugin plugin,
                               ISession session,
                               GraphDesktopController desktopController,
                               AddTableRequestListener listener,
                               String tableName, String schemaName,
                               String catalogName,
                               TableFrameControllerXmlBean xmlBean)
   {
      try
      {
         _session = session;
         _constraintViewsModel = new ConstraintViewsModel(_session);
         _desktopController = desktopController;
         _addTablelRequestListener = listener;

         TableToolTipProvider toolTipProvider = new TableToolTipProvider()
         {
            public String getToolTipText(MouseEvent event)
            {
               return onGetToolTipText(event);
            }
         };

         if(null == xmlBean)
         {
            _catalog = catalogName;
            _schema = schemaName;
            _tableName = tableName;
            _frame = new TableFrame(_session, plugin, getDisplayName(), null, toolTipProvider, _desktopController.getModeManager(), createDndCallback());


            initFromDB();

         }
         else
         {
            _catalog = xmlBean.getCatalog();
            _schema = xmlBean.getSchema();
            _tableName = xmlBean.getTablename();
            _frame = new TableFrame(_session, plugin, getDisplayName(), xmlBean.getTableFrameXmlBean(), toolTipProvider, _desktopController.getModeManager(), createDndCallback());
            _columnOrderType = OrderType.getByIx(xmlBean.getColumOrder());
            ColumnInfo[] colInfos = new ColumnInfo[xmlBean.getColumnIfoXmlBeans().length];
            for (int i = 0; i < colInfos.length; i++)
            {
               colInfos[i] = new ColumnInfo(xmlBean.getColumnIfoXmlBeans()[i]);
            }
            _colInfoModel.initCols(colInfos, _columnOrderType);

            _constraintViewsModel.initByXmlBeans(xmlBean.getConstraintViewXmlBeans(), _desktopController);

         }
         _frame.txtColumsFactory.setColumnInfoModel(_colInfoModel);



         _constraintViewListener = new ConstraintViewListener()
         {
            @Override
            public void foldingPointMoved(ConstraintView source)
            {
               onFoldingPointMoved(source);
            }

            @Override
            public void removeNonDbConstraint(ConstraintView constraintView)
            {
               onRemoveNonDbConstraint(constraintView);
            }
         };

         _componentListener = new ComponentAdapter()
         {
            public void componentMoved(ComponentEvent e)
            {
               recalculateAllConnections(false);
            }

            public void componentResized(ComponentEvent e)
            {
               recalculateAllConnections(false);
            }

            public void componentShown(ComponentEvent e)
            {
               recalculateAllConnections(false);
            }
         };


         _internalFrameListener = new InternalFrameAdapter()
         {
            public void internalFrameClosing(InternalFrameEvent e)
            {
               onClose();
            }
         };

         _frame.addInternalFrameListener(_internalFrameListener);

         _frame.addComponentListener(_componentListener);

         _frame.scrollPane.getVerticalScrollBar().addAdjustmentListener(new AdjustmentListener()
         {
            public void adjustmentValueChanged(AdjustmentEvent e)
            {
               recalculateAllConnections(false);
            }
         });

         createPopUp();

         orderColumns();

         _zoomerListener = new ZoomerListener()
         {
            public void zoomChanged(double newZoom, double oldZoom, boolean adjusting)
            {
               onZoomChanged(newZoom, oldZoom, adjusting);
            }

            public void setHideScrollBars(boolean b)
            {
               onHideScrollBars(b);
            }
         };

         _modeManagerListener = new ModeManagerListener()
         {
            @Override
            public void modeChanged(Mode newMode)
            {
               onModeChanged(newMode);
            }
         };

         _tableFramesModelListener = new TableFramesModelListener()
         {
            @Override
            public void modelChanged(TableFramesModelChangeType changeType)
            {
               onTableFramesModelChanged(changeType);
            }
         };


         _desktopController.getModeManager().addModeManagerListener(_modeManagerListener);
         _desktopController.getZoomer().addZoomListener(_zoomerListener);
         _desktopController.getTableFramesModel().addTableFramesModelListener(_tableFramesModelListener);

         if (Mode.ZOOM_PRINT == _desktopController.getModeManager().getMode())
         {
            onHideScrollBars(_desktopController.getZoomer().isHideScrollbars() );
         }

      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   private void onTableFramesModelChanged(TableFramesModelChangeType changeType)
   {
      try
      {
         if(TableFramesModelChangeType.TABLE == changeType)
         {
            DatabaseMetaData metaData = _session.getSQLConnection().getConnection().getMetaData();
            completeConstraints(metaData);
         }
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }


   private DndCallback createDndCallback()
   {
      return new DndCallback()
      {
         @Override
         public void dndImportDone(DndEvent e, Point dropPoint)
         {
            onDndImportDone(e, dropPoint);
         }

         @Override
         public DndEvent createDndEvent(MouseEvent lastDndExportedMousePressedEvent)
         {
            return onCreateDndEvent(lastDndExportedMousePressedEvent);
         }
      };
   }

   private DndEvent onCreateDndEvent(MouseEvent lastDndExportedMousePressedEvent)
   {
      return new DndEvent(this, getColumnInfoForPoint(lastDndExportedMousePressedEvent.getPoint()));
   }

   private void onDndImportDone(DndEvent e, Point dropPoint)
   {
      final TableFrameController fkTable = e.getTableFrameController();

      final ConstraintView constraintView = fkTable._constraintViewsModel.createConstraintView(
            e,
            this,
            getColumnInfoForPoint(dropPoint),
            _desktopController,
            _session
      );

      if (null != constraintView)
      {
//         SwingUtilities.invokeLater(new Runnable()
//         {
//            public void run()
//            {
               fkTable.recalculateAllConnections(true);
               constraintView.generateFoldingPointIfLinesWouldCoverEachOther();
//            }
//         });
      }

   }

   private void onRemoveNonDbConstraint(ConstraintView constraintView)
   {
      _constraintViewsModel.removeConst(constraintView);

      _desktopController.removeConstraintViews(new ConstraintView[]{constraintView}, false);

      recalculateAllConnections(true);
      _desktopController.repaint();
   }



   private String getDisplayName()
   {
      if(_desktopController.isShowQualifiedTableNames())
      {
         return getQualifiedName();
      }
      else
      {
         return _tableName;
      }
   }

   private String getQualifiedName()
   {
      String ret = "";

      if(null !=_catalog)
      {
         ret += _catalog + ".";
      }

      if(null !=_schema)
      {
         ret += _schema + ".";
      }

      ret += _tableName;

      return ret;
   }

   /**
    * @return false if table doesnt exist anymore.
    */
   private boolean initFromDB()
      throws SQLException
   {
      DatabaseMetaData metaData = _session.getSQLConnection().getConnection().getMetaData();

      ArrayList<ColumnInfo> colInfosBuf = GraphUtil.createColumnInfos(_session, _catalog, _schema, _tableName);

      _colInfoModel.initCols(colInfosBuf.toArray(new ColumnInfo[colInfosBuf.size()]), _columnOrderType);


      if(0 == _colInfoModel.getColCount())
      {
         // Table was deleted from DB
         _desktopController.removeConstraintViews(_constraintViewsModel.getConstViews(), false);
         return false;
      }

      completeConstraints(metaData);

      return true;
   }

   private void completeConstraints(DatabaseMetaData metaData)
   {
      _constraintViewsModel.initFromDB(metaData, _catalog, _schema, _tableName, _colInfoModel, _desktopController);
   }


   private void onModeChanged(Mode mode)
   {
      if(Mode.ZOOM_PRINT == mode)
      {
         onHideScrollBars(_desktopController.getZoomer().isHideScrollbars());
      }
      else
      {
         onHideScrollBars(false);
      }
   }

   private void onHideScrollBars(boolean b)
   {
      if(b)
      {
         _frame.scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
         _frame.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
      }
      else
      {
         _frame.scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
         _frame.scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
      }
   }

   private void onZoomChanged(double newZoom, double oldZoom, boolean adjusting)
   {
      if(null == _adjustBeginBounds)
      {
         _adjustBeginZoom = oldZoom;
         _adjustBeginBounds = new Rectangle(_frame.getBounds());
      }

      Rectangle bounds = new Rectangle();
      bounds.x = (int) (_adjustBeginBounds.x * newZoom / _adjustBeginZoom + 0.5);
      bounds.y = (int) (_adjustBeginBounds.y * newZoom / _adjustBeginZoom + 0.5);
      bounds.width = (int) (_adjustBeginBounds.width * newZoom / _adjustBeginZoom + 0.5);
      bounds.height = (int) (_adjustBeginBounds.height * newZoom / _adjustBeginZoom + 0.5);;
      _frame.setBounds(bounds);
      recalculateAllConnections(false);

      if(false == adjusting)
      {
         _adjustBeginZoom = newZoom;
         _adjustBeginBounds = null;
      }

   }

   private String onGetToolTipText(MouseEvent event)
   {
      ColumnInfo ci = getColumnInfoForPoint(event.getPoint());

      if(null == ci)
      {
         return null;
      }

      return ci.getConstraintToolTipText();

   }

   private ColumnInfo getColumnInfoForPoint(Point point)
   {
      int zoomedFontHeight = (int)(  _frame.txtColumsFactory.getColumnHeight() * _desktopController.getZoomer().getZoom()  + 0.5);
      for (int i = 0; i < _colInfoModel.getColCount(); i++)
      {
         int unscrolledHeight = _colInfoModel.getColAt(i).getIndex() * zoomedFontHeight;
         if(unscrolledHeight <= point.y &&  point.y  <= unscrolledHeight +  zoomedFontHeight)
         {
            return _colInfoModel.getColAt(i);
         }
      }

      return null;
   }

   public TableFrameControllerXmlBean getXmlBean()
   {
      TableFrameControllerXmlBean ret = new TableFrameControllerXmlBean();
      ret.setSchema(_schema);
      ret.setCatalog(_catalog);
      ret.setTablename(_tableName);
      ret.setTableFrameXmlBean(_frame.getXmlBean());
      ret.setColumOrder(_columnOrderType.getIx());

      ColumnInfoXmlBean[] colXmlBeans = new ColumnInfoXmlBean[_colInfoModel.getColCount()];
      for (int i = 0; i < _colInfoModel.getColCount(); i++)
      {
         colXmlBeans[i] = _colInfoModel.getColAt(i).getXmlBean();
      }
      ret.setColumnIfoXmlBeans(colXmlBeans);

      ret.setConstraintViewXmlBeans(_constraintViewsModel.getXmlBeans());

      ret.setTablesExportedTo(_tablesExportedTo);


      return ret;
   }


   private void createPopUp()
   {
      _popUp = new JPopupMenu();

      _mnuAddTableForForeignKey = new JMenuItem();
      _mnuAddTableForForeignKey.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onAddTableForForeignKey((ColumnInfo)_mnuAddTableForForeignKey.getClientProperty(MNU_PROP_COLUMN_INFO));
         }
      });

		// i18n[graph.addChildTables=Add child tables]
		_mnuAddChildTables = new JMenuItem(s_stringMgr.getString("graph.addChildTables"));
      _mnuAddChildTables.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onAddChildTables();
         }
      });

		// i18n[graph.addParentTables=Add parent tables]
		_mnuAddParentTables = new JMenuItem(s_stringMgr.getString("graph.addParentTables"));
      _mnuAddParentTables.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onAddParentTables();
         }
      });

		// i18n[graph.addRelTables=Add all related tables]
		_mnuAddAllRelatedTables = new JMenuItem(s_stringMgr.getString("graph.addRelTables"));
      _mnuAddAllRelatedTables.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onAddAllRelatedTables();
         }
      });

		// i18n[graph.refreshTable=Refresh table]
		_mnuRefreshTable = new JMenuItem(s_stringMgr.getString("graph.refreshTable"));
      _mnuRefreshTable.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onRefresh();
         }
      });

		// i18n[graph.scriptTable=Script table]
		_mnuScriptTable = new JMenuItem(s_stringMgr.getString("graph.scriptTable"));
      _mnuScriptTable.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onScriptTable();
         }
      });

      _mnuCopyTableName = new JMenuItem(s_stringMgr.getString("graph.copyTableName"));
      _mnuCopyTableName.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onCopyTableName(false);
         }
      });

      _mnuCopyQualifiedTableName = new JMenuItem(s_stringMgr.getString("graph.copyQualifiedTableName"));
      _mnuCopyQualifiedTableName.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onCopyTableName(true);
         }
      });



      // i18n[graph.viewTableInObjectTree=View table in Object tree]
		_mnuViewTableInObjectTree = new JMenuItem(s_stringMgr.getString("graph.viewTableInObjectTree"));
      _mnuViewTableInObjectTree.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onViewTableInObjectTree();
         }
      });

		// i18n[graph.dbOrder=db order]
		_mnuDbOrder = new JCheckBoxMenuItem(s_stringMgr.getString("graph.dbOrder"));
      _mnuDbOrder.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            dbOrder();
         }
      });

		// i18n[graph.orderyName=order by name]
		_mnuOrderByName = new JCheckBoxMenuItem(s_stringMgr.getString("graph.orderyName"));
      _mnuOrderByName.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            nameOrder();
         }
      });

		// i18n[graph.orderPksConstr=order PKs/constraints on top]
		_mnuPksAndConstraintsOnTop = new JCheckBoxMenuItem(s_stringMgr.getString("graph.orderPksConstr"));
      _mnuPksAndConstraintsOnTop.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            pkConstraintOrder();
         }
      });

		// i18n[graph.orderPksConstr=order PKs/constraints on top]
		_mnuQueryFiltersAndSelectedOnTop = new JCheckBoxMenuItem(s_stringMgr.getString("graph.orderFilteredSelected"));
      _mnuQueryFiltersAndSelectedOnTop.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            filteredSelectedOrder();
         }
      });

		// i18n[graph.close=close]
		_mnuClose = new JMenuItem(s_stringMgr.getString("graph.close"));
      _mnuClose.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onClose();
            _frame.setVisible(false);
            _frame.dispose();
         }
      });


//      _mnuQuerySelectAll
//      _mnuQueryUnselectAll
//      _mnuQueryClearAllFilters

		// i18n[graph.QuerySelectAll=Check all columns for SQL-SELECT clause]
		_mnuQuerySelectAll = new JMenuItem(s_stringMgr.getString("graph.QuerySelectAll"));
      _mnuQuerySelectAll.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onQuerySelectAll(true);
         }
      });

		// i18n[graph.QueryUnselectAll=Uncheck all columns for SQL-SELECT clause]
		_mnuQueryUnselectAll = new JMenuItem(s_stringMgr.getString("graph.QueryUnselectAll"));
      _mnuQueryUnselectAll.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onQuerySelectAll(false);
         }
      });

		// i18n[graph.QueryClearAllFilters=Clear all filters]
		_mnuQueryClearAllFilters = new JMenuItem(s_stringMgr.getString("graph.QueryClearAllFilters"));
      _mnuQueryClearAllFilters.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onQueryClearAllFilters();
         }
      });



      _popUp.add(_mnuAddTableForForeignKey);
      _popUp.add(_mnuAddChildTables);
      _popUp.add(_mnuAddParentTables);
      _popUp.add(_mnuAddAllRelatedTables);
      _popUp.add(new JSeparator());
      _popUp.add(_mnuCopyTableName);
      _popUp.add(_mnuCopyQualifiedTableName);
      _popUp.add(new JSeparator());
      _popUp.add(_mnuRefreshTable);
      _popUp.add(_mnuScriptTable);
      _popUp.add(_mnuViewTableInObjectTree);
      _popUp.add(new JSeparator());


      ButtonGroup bg = new ButtonGroup();
      _popUp.add(_mnuDbOrder);
      bg.add(_mnuDbOrder);
      _popUp.add(_mnuOrderByName);
      bg.add(_mnuOrderByName);
      _popUp.add(_mnuPksAndConstraintsOnTop);
      bg.add(_mnuPksAndConstraintsOnTop);
      _popUp.add(_mnuQueryFiltersAndSelectedOnTop);
      bg.add(_mnuQueryFiltersAndSelectedOnTop);


      _popUp.add(new JSeparator());
      _popUp.add(_mnuQuerySelectAll);
      _popUp.add(_mnuQueryUnselectAll);
      _popUp.add(_mnuQueryClearAllFilters);
      _popUp.add(new JSeparator());
      _popUp.add(_mnuClose);



      _frame.txtColumsFactory.addMouseListener(new MouseAdapter()
      {
         public void mousePressed(MouseEvent e)
         {
            maybeShowPopup(e);
         }

         public void mouseReleased(MouseEvent e)
         {
            maybeShowPopup(e);
         }
      });


   }

   private void onQueryClearAllFilters()
   {

      Window parent = SwingUtilities.windowForComponent(_frame);
      int opt = JOptionPane.showConfirmDialog(parent, s_stringMgr.getString("graph.tableFrameController.queryClearAllFilters"));

      if (JOptionPane.YES_OPTION != opt)
      {
         return;
      }

      _colInfoModel.clearAllFilters();
   }

   private void onQuerySelectAll(boolean b)
   {
      if(false == b)
      {
         Window parent = SwingUtilities.windowForComponent(_frame);
         int opt = JOptionPane.showConfirmDialog(parent, s_stringMgr.getString("graph.tableFrameController.queryUnselectAll"));

         if(JOptionPane.YES_OPTION != opt)
         {
            return;
         }
      }

      _colInfoModel.querySelectAll(b);
   }


   private void onCopyTableName(boolean qualified)
   {
      String toCopy = "";

      if(qualified)
      {
         toCopy = getQualifiedName();
      }
      else
      {
         toCopy = _tableName;
      }

      final StringSelection ss = new StringSelection(toCopy);
      Toolkit.getDefaultToolkit().getSystemClipboard().setContents(ss, ss);
   }

   private void onViewTableInObjectTree()
   {
      new ObjectTreeSearch().viewObjectInObjectTree(getTableInfo().getQualifiedName(), _session);
   }

   private void onScriptTable()
   {
      Window parent = SwingUtilities.windowForComponent(_frame);
      SqlScriptAcessor.scriptTablesToSQLEntryArea(parent, _session, new ITableInfo[]{getTableInfo()});
   }

   public ITableInfo getTableInfo()
   {
      _session.getSchemaInfo().waitTillTablesLoaded();
      return _session.getSchemaInfo().getITableInfos(_catalog, _schema, new ObjFilterMatcher(_tableName), new String[]{"TABLE"})[0];
   }


   private void onRefresh()
   {
      refresh();
   }

   void refresh()
   {
      try
      {
         if(initFromDB())
         {
            orderColumns();
            recalculateAllConnections(true);
            _desktopController.repaint();
         }
         else
         {
            onClose();
            _frame.setVisible(false);
            _frame.dispose();
         }
      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   private void onAddTableForForeignKey(ColumnInfo columnInfo)
   {
      _addTablelRequestListener.addTablesRequest(new String[]{columnInfo.getDBImportedTableName()}, _schema, _catalog);
   }

   void filteredSelectedOrder()
   {
      orderBy(OrderType.ORDER_FILTERED_SELECTED);
   }


   void pkConstraintOrder()
   {
      orderBy(OrderType.ORDER_PK_CONSTRAINT);
   }

   void nameOrder()
   {
      orderBy(OrderType.ORDER_NAME);
   }

   void dbOrder()
   {
      orderBy(OrderType.ORDER_DB);
   }

   private void orderBy(OrderType orderType)
   {
      _columnOrderType = orderType;
      orderColumns();
      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            recalculateAllConnections(false);
            fireSortListeners();
         }
      });
   }

   public void refreshTableName()
   {
      _frame.setTitle(getDisplayName());

   }


   private void fireSortListeners()
   {
      ColumnSortListener[] listeners = 
          _mySortListeners.toArray(new ColumnSortListener[_mySortListeners.size()]);

      for (int i = 0; i < listeners.length; i++)
      {
         listeners[i].columnOrderChanged();
      }
   }

   private void orderColumns()
   {
      _colInfoModel.orderBy(_columnOrderType);

      switch (_columnOrderType)
      {
         case ORDER_DB:
            _mnuDbOrder.setSelected(true);
            break;
         case ORDER_NAME:
            _mnuOrderByName.setSelected(true);
            break;
         case ORDER_PK_CONSTRAINT:
            _mnuPksAndConstraintsOnTop.setSelected(true);
            break;
         case ORDER_FILTERED_SELECTED:
            _mnuQueryFiltersAndSelectedOnTop.setSelected(true);
            break;
      }

      SwingUtilities.invokeLater(new Runnable()
      {
         public void run()
         {
            JComponent bestReadyComponent = _frame.txtColumsFactory.getBestReadyComponent();
            if(null != bestReadyComponent)
            {
               _frame.txtColumsFactory.getBestReadyComponent().scrollRectToVisible(new Rectangle(0,0,1,1));
            }
         }
      });
   }



   private void onAddAllRelatedTables()
   {
      onAddChildTables();
      onAddParentTables();
   }


   private void onAddChildTables()
   {
      try
      {
         DatabaseMetaData metaData = _session.getSQLConnection().getConnection().getMetaData();


         if(null == _tablesExportedTo)
         {
            Hashtable<String, String> exportBuf = new Hashtable<String, String>();
            ResultSet res = metaData.getExportedKeys(_catalog, _schema, _tableName);
            while(res.next())
            {
               String tableName = res.getString("FKTABLE_NAME");
               exportBuf.put(tableName, tableName);
            }
            _tablesExportedTo = exportBuf.keySet().toArray(new String[0]);
         }
         _addTablelRequestListener.addTablesRequest(_tablesExportedTo, _schema, _catalog);

      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }



   private void onAddParentTables()
   {
      HashSet<String> tablesToAdd = new HashSet<String>();

//      ConstraintView[] constViews = _constraintViewsModel.getConstViews();
//      for (int i = 0; i < constViews.length; i++)
//      {
//         tablesToAdd.add(constViews[i].getData().getPkTableName());
//      }

      for (ColumnInfo columnInfo : _colInfoModel.getAll())
      {
         if (null != columnInfo.getDBImportedTableName())
         {
            tablesToAdd.add(columnInfo.getDBImportedTableName());
         }
      }

      _addTablelRequestListener.addTablesRequest(tablesToAdd.toArray(new String[tablesToAdd.size()]), _schema, _catalog);
   }



   private void maybeShowPopup(MouseEvent e)
   {
      if (e.isPopupTrigger())
      {

         ColumnInfo ci = getColumnInfoForPoint(e.getPoint());
         if(null == ci || null == ci.getDBImportedTableName())
         {
            _mnuAddTableForForeignKey.setEnabled(false);
				// i18n[graph.addTableRefByNoHit=add table referenced by (no hit on FK)]
				_mnuAddTableForForeignKey.setText(s_stringMgr.getString("graph.addTableRefByNoHit"));
         }
         else
         {
            _mnuAddTableForForeignKey.setEnabled(true);
				// i18n[graph.addTableRefBy=add table referenced by {0}]
				_mnuAddTableForForeignKey.setText(s_stringMgr.getString("graph.addTableRefBy",ci.getName()));
            _mnuAddTableForForeignKey.putClientProperty(MNU_PROP_COLUMN_INFO, ci);
         }

         _mnuQueryFiltersAndSelectedOnTop.setEnabled(_desktopController.getModeManager().getMode().isQueryBuilder());
         _mnuQuerySelectAll.setEnabled(_desktopController.getModeManager().getMode().isQueryBuilder());
         _mnuQueryUnselectAll.setEnabled(_desktopController.getModeManager().getMode().isQueryBuilder());
         _mnuQueryClearAllFilters.setEnabled(_desktopController.getModeManager().getMode().isQueryBuilder());

         _popUp.show(e.getComponent(), e.getX(), e.getY());
      }
      else if(2 == e.getClickCount() && e.getID() == MouseEvent.MOUSE_PRESSED)
      {
         ColumnInfo ci = getColumnInfoForPoint(e.getPoint());
         if(null != ci && null != ci.getDBImportedTableName())
         {
            _addTablelRequestListener.addTablesRequest(new String[]{ci.getDBImportedTableName()}, _schema, _catalog);
         }
      }


   }


   void initAfterAddedToDesktop(TableFrameController[] openFrames, boolean resetBounds)
   {
      calculateStartSize();
      for (int i = 0; i < openFrames.length; i++)
      {
         tableFrameOpen(openFrames[i]);
      }

      if(resetBounds)
      {
         _frame.setBounds(_startSize);
      }
      _frame.setVisible(true);

      _frame.txtColumsFactory.getBestReadyComponent().scrollRectToVisible(new Rectangle(0,0,1,1));
   }

   private void onClose()
   {
      _desktopController.removeConstraintViews(_constraintViewsModel.getConstViews(), false);
      _desktopController.getModeManager().removeModeManagerListener(_modeManagerListener);
      _desktopController.getZoomer().removeZoomListener(_zoomerListener);
      _desktopController.getTableFramesModel().removeTableFramesModelListener(_tableFramesModelListener);

      ///////////////////////////////////////////////////////////////////
      // Is needed in case the frame was selected before closing
      // the grouping featur might send one more of these events.
      _frame.removeComponentListener(_componentListener);
      _frame.removeInternalFrameListener(_internalFrameListener);
      //
      ////////////////////////////////////////////////////////////////////

      for(TableFrameController tfc : _compListenersToOtherFramesByFrameCtrlr.keySet())
      {
         ComponentAdapter listenerToRemove = _compListenersToOtherFramesByFrameCtrlr.get(tfc);
         tfc._frame.removeComponentListener(listenerToRemove);
      }

      for(TableFrameController tfc : _scrollListenersToOtherFramesByFrameCtrlr.keySet())
      {
         AdjustmentListener listenerToRemove = _scrollListenersToOtherFramesByFrameCtrlr.get(tfc);
         tfc._frame.scrollPane.getVerticalScrollBar().removeAdjustmentListener(listenerToRemove);
      }

      for (int i = 0; i < _listeners.size(); i++)
      {
         TableFrameControllerListener tableFrameControllerListener = _listeners.elementAt(i);
         tableFrameControllerListener.closed(this);
      }
   }

   void tableFrameOpen(final TableFrameController tfc)
   {
      if(_openFramesConnectedToMe.contains(tfc))
      {
         return;
      }


      if(false == recalculateConnectionsTo(tfc))
      {
         return;
      }

      _openFramesConnectedToMe.add(tfc);

      ComponentAdapter compListener =
         new ComponentAdapter()
         {
            public void componentMoved(ComponentEvent e)
            {
               recalculateConnectionsTo(tfc);
            }
            public void componentResized(ComponentEvent e)
            {
               recalculateConnectionsTo(tfc);
            }
            public void componentShown(ComponentEvent e)
            {
               recalculateConnectionsTo(tfc);
            }
         };

      _compListenersToOtherFramesByFrameCtrlr.put(tfc, compListener);
      tfc._frame.addComponentListener(compListener);

      AdjustmentListener adjListener = new AdjustmentListener()
      {
         public void adjustmentValueChanged(AdjustmentEvent e)
         {
            recalculateConnectionsTo(tfc);
         }
      };
      tfc._frame.scrollPane.getVerticalScrollBar().addAdjustmentListener(adjListener);
      _scrollListenersToOtherFramesByFrameCtrlr.put(tfc, adjListener);

      ColumnSortListener sortListener = new ColumnSortListener()
      {
         public void columnOrderChanged()
         {
            recalculateConnectionsTo(tfc);
         }
      };

      tfc.addSortListener(sortListener);
      _columnSortListenersToOtherFramesByFrameCtrlr.put(tfc, sortListener);

   }

   private void addSortListener(ColumnSortListener sortListener)
   {
      _mySortListeners.add(sortListener);
   }

   private void removeSortListener(ColumnSortListener sortListener)
   {
      _mySortListeners.remove(sortListener);
   }

   public void recalculateConnections()
   {
      recalculateAllConnections(true);
   }

   private void recalculateAllConnections(boolean checkConnections)
   {
      if(checkConnections)
      {
         Vector<TableFrameController> openTableFrameControllers = _desktopController.getModeManager().getTableFramesModel().getTblCtrls();

         for (int i = 0; i < openTableFrameControllers.size(); i++)
         {
            boolean found = false;
            TableFrameController tfc = openTableFrameControllers.get(i);

            if(this == tfc)
            {
               continue;
            }

            for (int j = 0; j < _openFramesConnectedToMe.size(); j++)
            {
               if(tfc == _openFramesConnectedToMe.get(j))
               {
                  found = true;
                  break;
               }
            }

            if(found)
            {
               if(0 == findConstraintViews(tfc.getTableInfo().getSimpleName()).length)
               {
                  disconnectTableFrame(tfc);
               }
            }
            else
            {
               tableFrameOpen(tfc);
            }
         }
      }



      for (int i = 0; i < _openFramesConnectedToMe.size(); i++)
      {
         TableFrameController tableFrameController = _openFramesConnectedToMe.elementAt(i);
         recalculateConnectionsTo(tableFrameController);
      }
   }


   public void disconnectTableFrame(TableFrameController tfc)
   {
      _openFramesConnectedToMe.remove(tfc);


      ConstraintView[] constraintDataToRemove = _constraintViewsModel.removeConstraintsForTable(tfc._tableName);


      ComponentAdapter compListenerToRemove = 
          _compListenersToOtherFramesByFrameCtrlr.remove(tfc);
      if(null != compListenerToRemove)
      {
         tfc._frame.removeComponentListener(compListenerToRemove);
      }


      AdjustmentListener adjListenerToRemove = 
          _scrollListenersToOtherFramesByFrameCtrlr.remove(tfc);
      if(null != adjListenerToRemove)
      {
         tfc._frame.scrollPane.getVerticalScrollBar().removeAdjustmentListener(adjListenerToRemove);
      }

      ColumnSortListener columnSortListener = 
         _columnSortListenersToOtherFramesByFrameCtrlr.get(tfc);
      if(null != columnSortListener)
      {
         tfc.removeSortListener(columnSortListener);
      }

      _desktopController.removeConstraintViews(constraintDataToRemove, false);
   }

   private boolean recalculateConnectionsTo(TableFrameController other)
   {
      ConstraintView[] constraintView = findConstraintViews(other._tableName);

      if(0 == constraintView.length)
      {
         return false;
      }

      for (int i = 0; i < constraintView.length; i++)
      {
         ColumnInfo[] colInfos = constraintView[i].getData().getFkColumnInfos();

         FoldingPoint firstFoldingPoint = constraintView[i].getFirstFoldingPoint();
         FoldingPoint lastFoldingPoint = constraintView[i].getLastFoldingPoint();

         ConnectionPoints fkPoints = getConnectionPoints(colInfos, this, other, firstFoldingPoint);

         ColumnInfo[] othersColInfos = new ColumnInfo[colInfos.length];

         ColumnInfo[] pkColumnInfos = constraintView[i].getData().getPkColumnInfos();
         for (int j = 0; j < pkColumnInfos.length; j++)
         {
            othersColInfos[j] = other.findColumnInfo(pkColumnInfos[j].getColumnName());
         }

         ConnectionPoints pkPoints = getConnectionPoints(othersColInfos, other, this, lastFoldingPoint);

         constraintView[i].setConnectionPoints(fkPoints, pkPoints, this, other, _constraintViewListener);
      }

      _desktopController.putConstraintViews(constraintView);
      _desktopController.repaint();

      return true;

   }

   private void onFoldingPointMoved(ConstraintView source)
   {
      recalculateConnectionsTo(source.getPkFramePointingTo());
   }



   private static ConnectionPoints getConnectionPoints(ColumnInfo[] colInfos, TableFrameController me, TableFrameController other, FoldingPoint myNextFoldingPoint)
   {
      int[] relPointHeights = me.calculateRelativeConnectionPointHeights(colInfos);

      Rectangle myBounds = me._frame.getBounds();
      Rectangle othersBounds = other._frame.getBounds();

      ConnectionPoints ret = new ConnectionPoints();
      ret.points = new Point[relPointHeights.length];

      for (int i = 0; i < ret.points.length; i++)
      {

         if(null == myNextFoldingPoint)
         {
            if(myBounds.x + myBounds.width *3/4 < othersBounds.x)
            {
               ret.points[i] = new Point(myBounds.x + myBounds.width, myBounds.y + relPointHeights[i]);
               ret.pointsAreLeftOfWindow = false;
            }
            else
            {
               ret.points[i] = new Point(myBounds.x , myBounds.y + relPointHeights[i]);
               ret.pointsAreLeftOfWindow = true;
            }
         }
         else
         {
            if(myBounds.x + myBounds.width / 2 < myNextFoldingPoint.getZoomedPoint().x)
            {
               ret.points[i] = new Point(myBounds.x + myBounds.width, myBounds.y + relPointHeights[i]);
               ret.pointsAreLeftOfWindow = false;
            }
            else
            {
               ret.points[i] = new Point(myBounds.x , myBounds.y + relPointHeights[i]);
               ret.pointsAreLeftOfWindow = true;
            }
         }
      }
      return ret;
   }



   public ConstraintView[] findConstraintViews(String tableName)
   {
      return _constraintViewsModel.findConstraintViews(tableName);
   }


   private int[] calculateRelativeConnectionPointHeights(ColumnInfo[] colInfos)
   {
      Hashtable<Integer, Integer> buf = new Hashtable<Integer, Integer>();

      int height = _frame.txtColumsFactory.getColumnHeight();

      for (int i = 0; i < colInfos.length; i++)
      {
         double zoom = _desktopController.getZoomer().getZoom();

         int unscrolledHeight = (int)((colInfos[i].getIndex() * height + height / 2) * zoom + 0.5);
         int scrolledHeight;
         Rectangle viewRect = _frame.scrollPane.getViewport().getViewRect();

         scrolledHeight = unscrolledHeight - viewRect.y;
         if(scrolledHeight < 0)
         {
            scrolledHeight = 0;
         }
         if(scrolledHeight > viewRect.height)
         {
            scrolledHeight = viewRect.height;
         }

         scrolledHeight += + _frame.getTitlePane().getSize().height + 2;//  + 6;

         buf.put(Integer.valueOf(scrolledHeight), Integer.valueOf(scrolledHeight));
      }

      int[] ret = new int[buf.size()];

      int i=0;
      for (Integer key: buf.keySet()) {
          ret[i++] = key.intValue();
      }
      return ret;
   }

   private ColumnInfo findColumnInfo(String colName)
   {
      return _colInfoModel.findColumnInfo(colName);
   }

   private void calculateStartSize()
   {
      int maxViewingCols = 15;

      int height1 = _frame.txtColumsFactory.getColumnHeight();

      int width = _frame.txtColumsFactory.getMaxWidht() + 30;
      int height = Math.min(_colInfoModel.getColCount(), maxViewingCols) * height1 + 47;
      _startSize = new Rectangle(width, height);
   }

   public TableFrame getFrame()
   {
      return _frame;
   }

   public void addTableFrameControllerListener(TableFrameControllerListener l)
   {
      _listeners.add(l);
   }

   public boolean equals(Object obj)
   {
      if(obj instanceof TableFrameController)
      {
         TableFrameController other = (TableFrameController) obj;

         return other._tableName.equals(_tableName);
      }
      else
      {
         return false;
      }

   }

   public int hashCode()
   {
      return _tableName.hashCode();
   }

   public ColumnInfo[] getColumnInfos()
   {
      return _colInfoModel.getAll();
   }

   public ConstraintViewsModel getConstraintViewsModel()
   {
      return _constraintViewsModel;
   }

   public ColumnInfoModel getColumnInfoModel()
   {
      return _colInfoModel;
   }

}
