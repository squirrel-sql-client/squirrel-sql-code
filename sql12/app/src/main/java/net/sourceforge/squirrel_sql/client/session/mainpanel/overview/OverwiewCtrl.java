package net.sourceforge.squirrel_sql.client.session.mainpanel.overview;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale.*;
import net.sourceforge.squirrel_sql.fw.datasetviewer.*;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import javax.swing.table.DefaultTableColumnModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

public class OverwiewCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(OverwiewCtrl.class);

   private OverviewHolder _overviewHolder = new OverviewHolder();
   private OverwiewPanel _overwiewPanel;
   private IApplication _app;

   public OverwiewCtrl(final ISession session)
   {
      _app = session.getApplication();
      _overwiewPanel = new OverwiewPanel(_app.getResources());

      _overwiewPanel.btnNext.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onNext();
         }
      });

      _overwiewPanel.btnPrev.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onPrev();
         }
      });

      _overwiewPanel.btnShowInTable.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onShowInTable();
         }
      });


      _overwiewPanel.btnShowInTableWin.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onShowInTableWin();
         }
      });

      _overwiewPanel.btnSaveColumnWidth.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onSaveColumnWidths();
         }
      });

   }

   private void onSaveColumnWidths()
   {
      _overviewHolder.getDataScaleTable().saveColumnWidths();
   }


   private void onShowInTableWin()
   {
      DataSetViewerTablePanel simpleTable = createSimpleTable();
      openWindowForTable(simpleTable);
   }

   private void openWindowForTable(DataSetViewerTablePanel simpleTable)
   {
      OverviewFrame overviewFrame = new OverviewFrame(simpleTable, _app);
      _app.getMainFrame().addWidget(overviewFrame);
      overviewFrame.setLayer(JLayeredPane.PALETTE_LAYER);
      overviewFrame.setVisible(true);
      DialogWidget.centerWithinDesktop(overviewFrame);
   }

   private void onShowInTable()
   {
      _overviewHolder.setOverview(createSimpleTable());
      initGui();
   }


   private void onShowIntervalInTable(Interval interval)
   {
      List<Object[]> rows = interval.getResultRows();
      ColumnDisplayDefinition[] columnDisplayDefinitions = _overviewHolder.getDataScaleTable().getColumnDisplayDefinitions();

      _overviewHolder.setOverview(createSimpleTable(rows, columnDisplayDefinitions));
      initGui();
   }

   private void onShowIntervalInTableWin(Interval interval)
   {
      List<Object[]> rows = interval.getResultRows();
      ColumnDisplayDefinition[] columnDisplayDefinitions = _overviewHolder.getDataScaleTable().getColumnDisplayDefinitions();

      openWindowForTable(createSimpleTable(rows, columnDisplayDefinitions));
   }


   private DataSetViewerTablePanel createSimpleTable()
   {
      List<Object[]> allRows = _overviewHolder.getDataScaleTable().getAllRows();
      ColumnDisplayDefinition[] columnDisplayDefinitions = _overviewHolder.getDataScaleTable().getColumnDisplayDefinitions();

      return createSimpleTable(allRows, columnDisplayDefinitions);
   }

   private DataSetViewerTablePanel createSimpleTable(List<Object[]> allRows, ColumnDisplayDefinition[] columnDisplayDefinitions)
   {
      try
      {
         OverviewDataSet ods = new OverviewDataSet(allRows, columnDisplayDefinitions);

         DataSetViewerTablePanel dsv = new DataSetViewerTablePanel();
         dsv.init(null);
         dsv.show(ods);
         return dsv;
      }
      catch (DataSetException e)
      {
         throw new RuntimeException(e);
      }
   }


   private void onPrev()
   {
      _overviewHolder.setParent();
      initGui();

   }

   private void onNext()
   {
      _overviewHolder.setKid();
      initGui();
   }

   public String getTitle()
   {
      return s_stringMgr.getString("OverwiewCtrl.title");
   }

   public Component getPanel()
   {
      return _overwiewPanel;
   }

   public void init(ResultSetDataSet rsds)
   {
      if(false == _overviewHolder.isEmpty())
      {
         return;
      }


      List<Object[]> rows = rsds.getAllDataForReadOnly();
      DataSetDefinition dataSetDefinition = rsds.getDataSetDefinition();
      ColumnDisplayDefinition[] columnDefinitions = dataSetDefinition.getColumnDefinitions();

      initScales(rows, columnDefinitions);
   }

   private void onIntervalSelected(Interval interval, ColumnDisplayDefinition[] columnDefinitions)
   {
      if (false == interval.containsAllRows())
      {
         List<Object[]> rows = interval.getResultRows();
         initScales(rows, columnDefinitions);
      }
   }


   private void initScales(List<Object[]> rows, final ColumnDisplayDefinition[] columnDefinitions)
   {
      if(0 == rows.size())
      {
         initScaleTable(new DataScale[0], rows, columnDefinitions);
         return;
      }


      DataScale[] scales = new DataScale[columnDefinitions.length];

      DataScaleListener dataScaleListener = new DataScaleListener()
      {
         @Override
         public void intervalSelected(Interval interval)
         {
            onIntervalSelected(interval, columnDefinitions);
         }

         @Override
         public void showInTableWin(Interval interval)
         {
            onShowIntervalInTableWin(interval);
         }

         @Override
         public void showInTable(Interval interval)
         {
            onShowIntervalInTable(interval);
         }
      };


      for (int i = 0; i < columnDefinitions.length; i++)
      {
         scales[i] = new ScaleFactory(rows, i, columnDefinitions[i]).createScale(dataScaleListener);
      }

      initScaleTable(scales, rows, columnDefinitions);
   }

   private void initScaleTable(DataScale[] scales, List<Object[]> rows, ColumnDisplayDefinition[] columnDefinitions)
   {
      DataScaleTableModel dataScaleTableModel = new DataScaleTableModel(scales);
      DataScaleTable dataScaleTable = new DataScaleTable(dataScaleTableModel, rows, columnDefinitions);

      TableColumnModel tcm = new DefaultTableColumnModel();
      dataScaleTable.setColumnModel(tcm);

      for (int i = 0; i < DataScaleTableModel.getColumnNames().length; i++)
      {
         TableColumn col = new TableColumn(i);
         col.setHeaderValue(DataScaleTableModel.getColumnNames()[i]);
         if (DataScaleTableModel.COL_NAME_COLUMN.equals(DataScaleTableModel.getColumnNames()[i]))
         {
            col.setPreferredWidth(dataScaleTableModel.getColumnWidthForColName(DataScaleTableModel.COL_NAME_COLUMN));
         }
         else if (DataScaleTableModel.COL_NAME_DATA.equals(DataScaleTableModel.getColumnNames()[i]))
         {
            col.setPreferredWidth(dataScaleTableModel.getColumnWidthForColName(DataScaleTableModel.COL_NAME_DATA));
         }
         tcm.addColumn(col);
      }

      _overviewHolder.setOverview(dataScaleTable);
      initGui();

   }

   private void initGui()
   {
      _overwiewPanel.btnPrev.setEnabled(_overviewHolder.hasParent());
      _overwiewPanel.btnNext.setEnabled(_overviewHolder.hasKid());

      _overwiewPanel.btnShowInTable.setEnabled(_overviewHolder.canShowInSimpleTable());
      _overwiewPanel.btnShowInTableWin.setEnabled(_overviewHolder.canShowInSimpleTable());

      _overwiewPanel.btnSaveColumnWidth.setEnabled(_overviewHolder.isScaleTable());

      _overwiewPanel.scrollPane.setViewportView(_overviewHolder.getComponent());
   }
}
