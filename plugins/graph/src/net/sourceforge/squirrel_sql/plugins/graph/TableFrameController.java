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
import java.util.List;

import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.SwingUtilities;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.ObjectTreeSearch;
import net.sourceforge.squirrel_sql.fw.sql.ITableInfo;
import net.sourceforge.squirrel_sql.fw.sql.PrimaryKeyInfo;
import net.sourceforge.squirrel_sql.fw.sql.SQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.ColumnInfoXmlBean;
import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.ConstraintViewXmlBean;
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

   private ColumnInfo[] _colInfos;
   private ConstraintView[] _constraintViews;
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
   private JMenuItem _mnuClose;
   private AddTableListener _addTablelListener;
   private ConstraintViewListener _constraintViewListener;

   private int _columnOrder = ORDER_DB;
   private static final int ORDER_DB = 0;
   private static final int ORDER_NAME = 1;
   private static final int ORDER_PK_CONSTRAINT = 2;
   private ColumnInfo[] _orderedColumnInfos;
   private static final String MNU_PROP_COLUMN_INFO = "MNU_PROP_COLUMN_INFO";
   private ZoomerListener _zoomerListener;
   private Rectangle _adjustBeginBounds;
   private double _adjustBeginZoom;
   private GraphControllerAccessor _graphControllerAccessor;


   public TableFrameController(ISession session, GraphDesktopController desktopController, GraphControllerAccessor graphControllerAccessor, AddTableListener listener, String tableName, String schemaName, String catalogName, TableFrameControllerXmlBean xmlBean)
   {
      try
      {
         _session = session;
         _desktopController = desktopController;
         _addTablelListener = listener;
         _graphControllerAccessor = graphControllerAccessor;

         TableToolTipProvider toolTipProvider = new TableToolTipProvider()
         {
            public String getToolTipText(MouseEvent event)
            {
               return onGetToolTipText(event);
            }
         };

         if(null == xmlBean)
         {
            _tableName = tableName;
            _frame = new TableFrame(_tableName, null, toolTipProvider, _desktopController.getZoomer());

            _catalog = catalogName;
            _schema = schemaName;

            initFromDB();

         }
         else
         {
            _tableName = xmlBean.getTablename();
            _frame = new TableFrame(_tableName, xmlBean.getTableFrameXmlBean(), toolTipProvider, _desktopController.getZoomer());
            _catalog = xmlBean.getCatalog();
            _schema = xmlBean.getSchema();
            _columnOrder = xmlBean.getColumOrder();
            _colInfos = new ColumnInfo[xmlBean.getColumnIfoXmlBeans().length];
            for (int i = 0; i < _colInfos.length; i++)
            {
               _colInfos[i] = new ColumnInfo(xmlBean.getColumnIfoXmlBeans()[i]);
            }

            _constraintViews = new ConstraintView[xmlBean.getConstraintViewXmlBeans().length];
            for (int i = 0; i < _constraintViews.length; i++)
            {
               _constraintViews[i] = new ConstraintView(xmlBean.getConstraintViewXmlBeans()[i], _desktopController, _session);
               _constraintViews[i].replaceCopiedColsByReferences(_colInfos);
            }
         }


         _constraintViewListener = new ConstraintViewListener()
         {
            public void foldingPointMoved(ConstraintView source)
            {
               onFoldingPointMoved(source);
            }
         };



         _frame.addInternalFrameListener(new InternalFrameAdapter()
         {
            public void internalFrameClosing(InternalFrameEvent e)
            {
               onClose();
            }
         });

         _frame.addComponentListener(new ComponentAdapter()
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
         });

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

            public void zoomEnabled(boolean b)
            {
               onZoomEnabled(b);
            }

            public void setHideScrollBars(boolean b)
            {
               onHideScrollBars(b);
            }
         };

         _desktopController.getZoomer().addZoomListener(_zoomerListener);
         onHideScrollBars(_desktopController.getZoomer().isHideScrollbars() );

      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }

   /**
    * @return false if table doesnt exist anymore.
    */
   private boolean initFromDB()
      throws SQLException
   {
      DatabaseMetaData metaData = _session.getSQLConnection().getConnection().getMetaData();
      SQLDatabaseMetaData md = _session.getSQLConnection().getSQLMetaData();
      Hashtable<String, ConstraintData> constaintInfosByConstraintName = 
          new Hashtable<String, ConstraintData>();
      Vector<ColumnInfo> colInfosBuf = new Vector<ColumnInfo>();
      if (s_log.isDebugEnabled()) {
          s_log.debug("initFromDB: _catalog="+_catalog+" _schema="+_schema+
                      " _tableName="+_tableName);
      }

          
      TableColumnInfo[] infos = md.getColumnInfo(_catalog, _schema, _tableName);
      for (int i = 0; i < infos.length; i++) {
         TableColumnInfo info = infos[i];
         String columnName = info.getColumnName();
         String columnType = info.getTypeName();
         int columnSize = info.getColumnSize();
         int decimalDigits = info.getDecimalDigits();
         boolean nullable = "YES".equalsIgnoreCase(info.isNullable());

         ColumnInfo colInfo = new ColumnInfo(columnName, 
                                             columnType, 
                                             columnSize, 
                                             decimalDigits,
                                             nullable);
         colInfosBuf.add(colInfo);
         
      }
      
      _colInfos = colInfosBuf.toArray(new ColumnInfo[colInfosBuf.size()]);

      if(0 == _colInfos.length)
      {
         // Table was deleted from DB
         return false;
      }

      try {
          PrimaryKeyInfo[] pkinfos = 
              md.getPrimaryKey(_catalog, _schema, _tableName);
          for (int i = 0; i < pkinfos.length; i++) {
             PrimaryKeyInfo info = pkinfos[i];
             for (int c = 0; c < _colInfos.length; c++)
             {
                if(_colInfos[c].getName().equals(info.getColumnName()))
                {
                   _colInfos[c].markPrimaryKey();
                }
             }
          }          
      } catch (SQLException e) {
          s_log.error("Unable to get Primary Key info", e);
      } 
      
      ResultSet res = null;
      try {
          res = metaData.getImportedKeys(_catalog, _schema, _tableName);
          while(res.next())
          {
             String pkTable = res.getString(3);   // PKTABLE_NAME
             String pkColName = res.getString(4); // PKCOLUMN_NAME         
             String fkColName = res.getString(8); // FKCOLUMN_NAME
             String fkName = res.getString(12);   // FK_NAME
             
             ColumnInfo colInfo = findColumnInfo(fkColName);
             colInfo.setImportData(pkTable, pkColName, fkName);
    
             ConstraintData  constraintData = constaintInfosByConstraintName.get(fkName);
    
             if(null == constraintData)
             {
                constraintData = new ConstraintData(pkTable, _tableName, fkName);
                constaintInfosByConstraintName.put(fkName, constraintData);
             }
             constraintData.addColumnInfo(colInfo);
          }
      } catch (SQLException e) {
          s_log.error("Unable to get Foriegn Key info", e);
      } finally {
          if (res != null) {
              try { res.close(); } catch (SQLException e) {}
          }
      }

      ConstraintData[] constraintData = 
          constaintInfosByConstraintName.values().toArray(new ConstraintData[0]);

      Hashtable<String, ConstraintView> oldConstraintViewsByConstraintName = 
          new Hashtable<String, ConstraintView>();

      if(null != _constraintViews)
      {
         _desktopController.removeConstraintViews(_constraintViews, true);
         for (int i = 0; i < _constraintViews.length; i++)
         {
            String constraintName = 
                _constraintViews[i].getData().getConstraintName();
            oldConstraintViewsByConstraintName.put(constraintName, _constraintViews[i]);
         }
      }

      _constraintViews = new ConstraintView[constraintData.length];
      for (int i = 0; i < constraintData.length; i++)
      {
         ConstraintView oldCV = 
             oldConstraintViewsByConstraintName.get(constraintData[i].getConstraintName());

         if(null != oldCV)
         {
            // The old view is preserved to eventually preserve folding points
            oldCV.setData(constraintData[i]);
            _constraintViews[i] = oldCV;
         }
         else
         {
            _constraintViews[i] = new ConstraintView(constraintData[i], _desktopController, _session);
         }
      }

      return true;
   }

   private void onZoomEnabled(boolean b)
   {
      if(false == b)
      {
         onHideScrollBars(false);
      }
      else
      {
         onHideScrollBars(_desktopController.getZoomer().isHideScrollbars());
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
      FontMetrics fm = _frame.txtColumsFactory.getGraphics().getFontMetrics(_frame.txtColumsFactory.getFont());

      int zoomedFontHeight = (int)(  fm.getHeight() * _desktopController.getZoomer().getZoom()  + 0.5);
      for (int i = 0; i < _colInfos.length; i++)
      {
         int unscrolledHeight = _colInfos[i].getIndex() * zoomedFontHeight;
         if(unscrolledHeight <= point.y &&  point.y  <= unscrolledHeight +  zoomedFontHeight)
         {
            return _colInfos[i];
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
      ret.setColumOrder(_columnOrder);

      ColumnInfoXmlBean[] colXmlBeans = new ColumnInfoXmlBean[_colInfos.length];
      for (int i = 0; i < _colInfos.length; i++)
      {
         colXmlBeans[i] = _colInfos[i].getXmlBean();
      }
      ret.setColumnIfoXmlBeans(colXmlBeans);

      ConstraintViewXmlBean[] constViewXmlBeans = new ConstraintViewXmlBean[_constraintViews.length];
      for (int i = 0; i < _constraintViews.length; i++)
      {
         constViewXmlBeans[i] =_constraintViews[i].getXmlBean();
      }
      ret.setConstraintViewXmlBeans(constViewXmlBeans);

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
      _popUp.add(_mnuDbOrder);
      _popUp.add(_mnuOrderByName);
      _popUp.add(_mnuPksAndConstraintsOnTop);
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

   private void onCopyTableName(boolean qualified)
   {
      String toCopy = "";

      if(qualified)
      {
         if(null != _catalog)
         {
            toCopy += _catalog + ".";
         }

         if(null != _schema)
         {
            toCopy += _schema + ".";
         }

         toCopy += _tableName;

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
      SqlScriptAcessor.scriptTablesToSQLEntryArea(_session, new ITableInfo[]{getTableInfo()});
   }

   ITableInfo getTableInfo()
   {
      return _session.getSchemaInfo().getITableInfos(_catalog, _schema, _tableName, new String[]{"TABLE"})[0];
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
      _addTablelListener.addTablesRequest(new String[]{columnInfo.getImportedTableName()}, _schema, _catalog);
   }

   void pkConstraintOrder()
   {
      _columnOrder = ORDER_PK_CONSTRAINT;
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

   void nameOrder()
   {
      _columnOrder = ORDER_NAME;
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

   void dbOrder()
   {
      _columnOrder = ORDER_DB;
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
      Comparator<ColumnInfo> comp;

      switch(_columnOrder)
      {
         case ORDER_DB:
            _orderedColumnInfos = _colInfos;
            _mnuDbOrder.setSelected(true);
            _mnuOrderByName.setSelected(false);
            _mnuPksAndConstraintsOnTop.setSelected(false);
            break;
         case ORDER_NAME:
            _orderedColumnInfos = new ColumnInfo[_colInfos.length];
            System.arraycopy(_colInfos, 0, _orderedColumnInfos, 0, _colInfos.length);

            comp = new Comparator<ColumnInfo>()
            {
               public int compare(ColumnInfo c1, ColumnInfo c2)
               {
                  return c1.getName().compareTo(c2.getName());
               }
            };
            Arrays.sort(_orderedColumnInfos, comp);

            _mnuDbOrder.setSelected(false);
            _mnuOrderByName.setSelected(true);
            _mnuPksAndConstraintsOnTop.setSelected(false);
            break;
         case ORDER_PK_CONSTRAINT:
            _orderedColumnInfos = new ColumnInfo[_colInfos.length];
            System.arraycopy(_colInfos, 0, _orderedColumnInfos, 0, _colInfos.length);

            comp = new Comparator<ColumnInfo>()
            {
               public int compare(ColumnInfo c1, ColumnInfo c2)
               {
                  if(c1.isPrimaryKey() && false == c2.isPrimaryKey())
                  {
                     return -1;
                  }
                  else if(false == c1.isPrimaryKey() && c2.isPrimaryKey())
                  {
                     return 1;
                  }
                  else
                  {
                     if(null != c1.getConstraintName() && null == c2.getConstraintName())
                     {
                        return -1;
                     }
                     else if(null == c1.getConstraintName() && null != c2.getConstraintName())
                     {
                        return 1;
                     }
                     else
                     {
                        if(null != c1.getConstraintName() && null != c2.getConstraintName())
                        {
                           String s1 = c1.getConstraintName() + "_" + c1.getName();
                           String s2 = c2.getConstraintName() + "_" + c2.getName();
                           return s1.compareTo(s2);
                        }
                        else
                        {
                           return c1.getName().compareTo(c2.getName());
                        }
                     }
                  }
               }
            };
            Arrays.sort(_orderedColumnInfos, comp);

            _mnuDbOrder.setSelected(false);
            _mnuOrderByName.setSelected(false);
            _mnuPksAndConstraintsOnTop.setSelected(true);
            break;
         default:
            throw new IllegalStateException("Unknown order " + _columnOrder);
      }

      _frame.txtColumsFactory.setColumns(_orderedColumnInfos);

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
         _addTablelListener.addTablesRequest(_tablesExportedTo, _schema, _catalog);

      }
      catch (SQLException e)
      {
         throw new RuntimeException(e);
      }
   }



   private void onAddParentTables()
   {
      HashSet<String> tablesToAdd = new HashSet<String>();

      for (int i = 0; i < _constraintViews.length; i++)
      {
         tablesToAdd.add(_constraintViews[i].getData().getPkTableName());
      }

      _addTablelListener.addTablesRequest(tablesToAdd.toArray(new String[tablesToAdd.size()]), _schema, _catalog);
   }



   private void maybeShowPopup(MouseEvent e)
   {
      if (e.isPopupTrigger())
      {

         ColumnInfo ci = getColumnInfoForPoint(e.getPoint());
         if(null == ci || null == ci.getImportedTableName())
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

         _popUp.show(e.getComponent(), e.getX(), e.getY());
      }
      else if(2 == e.getClickCount() && e.getID() == MouseEvent.MOUSE_PRESSED)
      {
         ColumnInfo ci = getColumnInfoForPoint(e.getPoint());
         if(null != ci && null != ci.getImportedTableName())
         {
            _addTablelListener.addTablesRequest(new String[]{ci.getImportedTableName()}, _schema, _catalog);
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
      _desktopController.removeConstraintViews(_constraintViews, false);
      _desktopController.getZoomer().removeZoomListener(_zoomerListener);


      for (int i = 0; i < _listeners.size(); i++)
      {
         TableFrameControllerListener tableFrameControllerListener = _listeners.elementAt(i);
         tableFrameControllerListener.closed(this);
      }

      for(TableFrameController tfc : _compListenersToOtherFramesByFrameCtrlr.keySet()) {
         ComponentAdapter listenerToRemove = _compListenersToOtherFramesByFrameCtrlr.get(tfc);
         tfc._frame.removeComponentListener(listenerToRemove);
      }

      for(TableFrameController tfc : _scrollListenersToOtherFramesByFrameCtrlr.keySet()) {
         AdjustmentListener listenerToRemove = _scrollListenersToOtherFramesByFrameCtrlr.get(tfc);
         tfc._frame.scrollPane.getVerticalScrollBar().removeAdjustmentListener(listenerToRemove);
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


   private void recalculateAllConnections(boolean checkForNewConnections)
   {
      if(checkForNewConnections)
      {
         Vector<TableFrameController> openTableFrameControllers = _graphControllerAccessor.getOpenTableFrameControllers();

         for (int i = 0; i < openTableFrameControllers.size(); i++)
         {
            boolean found = false;
            for (int j = 0; j < _openFramesConnectedToMe.size(); j++)
            {
               if(openTableFrameControllers.get(i) == _openFramesConnectedToMe.get(j))
               {
                  found = true;
                  break;
               }
            }

            if(false == found)
            {
               tableFrameOpen(openTableFrameControllers.get(i));
            }
         }
      }



      for (int i = 0; i < _openFramesConnectedToMe.size(); i++)
      {
         TableFrameController tableFrameController = _openFramesConnectedToMe.elementAt(i);
         recalculateConnectionsTo(tableFrameController);
      }
   }


   public void tableFrameRemoved(TableFrameController tfc)
   {
      _openFramesConnectedToMe.remove(tfc);

      List<ConstraintView> constraintDataToRemove = new ArrayList<ConstraintView>();
      List<ConstraintView> newConstraintData = new ArrayList<ConstraintView>();

      for (int i = 0; i < _constraintViews.length; i++)
      {
         if(_constraintViews[i].getData().getPkTableName().equals(tfc._tableName))
         {
            constraintDataToRemove.add(_constraintViews[i]);
         }
         else
         {
            newConstraintData.add(_constraintViews[i]);
         }
      }

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

      ConstraintView[] buf = 
          constraintDataToRemove.toArray(new ConstraintView[constraintDataToRemove.size()]);
      _desktopController.removeConstraintViews(buf, false);
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
         ColumnInfo[] colInfos = constraintView[i].getData().getColumnInfos();

         FoldingPoint firstFoldingPoint = constraintView[i].getFirstFoldingPoint();
         FoldingPoint lastFoldingPoint = constraintView[i].getLastFoldingPoint();

         ConnectionPoints fkPoints = getConnectionPoints(colInfos, this, other, firstFoldingPoint);

         ColumnInfo[] othersColInfos = new ColumnInfo[colInfos.length];
         for (int j = 0; j < othersColInfos.length; j++)
         {
            othersColInfos[j] = other.findColumnInfo(colInfos[j].getImportedColumnName());
         }

         ConnectionPoints pkPoints = getConnectionPoints(othersColInfos, other, this, lastFoldingPoint);

         constraintView[i].setConnectionPoints(fkPoints, pkPoints, other, _constraintViewListener);
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



   private ConstraintView[] findConstraintViews(String tableName)
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


   private int[] calculateRelativeConnectionPointHeights(ColumnInfo[] colInfos)
   {
      Hashtable<Integer, Integer> buf = new Hashtable<Integer, Integer>();
      FontMetrics fm = _frame.txtColumsFactory.getGraphics().getFontMetrics(_frame.txtColumsFactory.getFont());

      for (int i = 0; i < colInfos.length; i++)
      {
         double zoom = _desktopController.getZoomer().getZoom();

         int unscrolledHeight = (int)((colInfos[i].getIndex() * fm.getHeight() + fm.getHeight() / 2) * zoom + 0.5);
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
      for (int i = 0; i < _colInfos.length; i++)
      {
         if(_colInfos[i].getName().equals(colName))
         {
            return _colInfos[i];
         }
      }

      throw new IllegalArgumentException("Column " + colName + " not found");
   }

   private void calculateStartSize()
   {
      int maxViewingCols = 15;

      FontMetrics fm = _frame.txtColumsFactory.getGraphics().getFontMetrics(_frame.txtColumsFactory.getFont());
      int width = getMaxSize(_colInfos, fm) + 30;
      int height = Math.min(_colInfos.length, maxViewingCols) * (fm.getHeight()) + 47;
      _startSize = new Rectangle(width, height);
   }

   private int getMaxSize(ColumnInfo[] infos, FontMetrics fontMetrics)
   {
      int maxSize = 0;
      for (int i = 0; i < infos.length; i++)
      {
         int buf = fontMetrics.stringWidth(infos[i].toString());
         if(maxSize < buf)
         {
            maxSize = buf;
         }
      }
      return maxSize;

   }


   TableFrame getFrame()
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

}
