package net.sourceforge.squirrel_sql.client.session.mainpanel.overview;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.gui.desktopcontainer.DialogWidget;
import net.sourceforge.squirrel_sql.client.session.DefaultDataModelImplementationDetails;
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
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

public class OverviewCtrl
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(OverviewCtrl.class);

   private OverviewHolder _overviewHolder = new OverviewHolder();
   private OverviewPanel _overviewPanel;
   private IApplication _app;
   private ISession _session;

   public OverviewCtrl(final ISession session)
   {
      _session = session;
      _app = session.getApplication();
      _overviewPanel = new OverviewPanel(_app.getResources());

      _overviewPanel.btnNext.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onNext();
         }
      });

      _overviewPanel.btnPrev.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onPrev();
         }
      });

      _overviewPanel.btnShowInTable.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onShowInTable();
         }
      });


      _overviewPanel.btnShowInTableWin.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onShowInTableWin();
         }
      });


      _overviewPanel.btnReport.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onReport();
         }
      });

      _overviewPanel.btnChart.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            onChart();
         }
      });

      _overviewPanel.cboCallDepth.setModel(new DefaultComboBoxModel(CallDepthComboModel.createModels()));
      _overviewPanel.cboCallDepth.setSelectedItem(CallDepthComboModel.getDefaultSelected());

      _overviewPanel.cboCallDepth.addItemListener(new ItemListener()
      {
         @Override
         public void itemStateChanged(ItemEvent e)
         {
            onCallDepthSelected(e);
         }
      });


   }

   private void onCallDepthSelected(ItemEvent e)
   {
      if(ItemEvent.SELECTED == e.getStateChange())
      {
         CallDepthComboModel.saveSelection((CallDepthComboModel) _overviewPanel.cboCallDepth.getSelectedItem());
      }

   }

   private void onChart()
   {
      int index = _overviewPanel.cboColumns.getSelectedIndex();
      DataScale dataScale = _overviewHolder.getDataScaleTable().getDataScaleTableModel().getDataScaleAt(index);

      CallDepthComboModel selItem = (CallDepthComboModel) _overviewPanel.cboCallDepth.getSelectedItem();

      ChartHandler.doChart(dataScale, selItem.getCallDepth(), _app);
   }

   private void onReport()
   {
      try
      {
         DataScaleTableModel dstm = _overviewHolder.getDataScaleTable().getDataScaleTableModel();

         StringWriter sw = new StringWriter();
         PrintWriter pw = new PrintWriter(sw);


         pw.println("<html>");
         pw.println("<body>");

         for( int i = 0; i < dstm.getRowCount(); i++ )
         {

            DataScale dataScale = dstm.getDataScaleAt(i);

            pw.println("<h1>Column name " + dataScale.getColumn() +":</h1>");
            pw.println("<table border=\"1\">");
            pw.println("<tr>");

            ArrayList<String> intervalReports = dataScale.getIntervalReports();

            for( String toolTip : intervalReports )
            {
               pw.println("<td>" + toolTip + "</td>");
            }
            pw.println("<tr>");
            pw.println("</table>");

            pw.println();
         }

         pw.println("</body>");
         pw.println("</html>");
         pw.flush();
         sw.flush();
         pw.close();
         sw.close();

         Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
         StringSelection data = new StringSelection(sw.getBuffer().toString());
         clip.setContents(data, data);
      }
      catch(IOException e)
      {
         throw new RuntimeException(e);
      }
   }


   private void onShowInTableWin()
   {
      DataSetViewerTablePanel simpleTable = createSimpleTable();
      openWindowForTable(simpleTable);
   }

   private void openWindowForTable(DataSetViewerTablePanel simpleTable)
   {
      Window parent = SwingUtilities.windowForComponent(_overviewPanel.btnShowInTableWin);

      OverviewFrame overviewFrame = new OverviewFrame(simpleTable, _app, parent);
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
         dsv.init(null, new DefaultDataModelImplementationDetails(_session));
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
      return _overviewPanel;
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
            col.setPreferredWidth(DataScaleTableColumnWidthsPersister.getColumnWidthForColName(DataScaleTableModel.COL_NAME_COLUMN));
         }
         else if (DataScaleTableModel.COL_NAME_DATA.equals(DataScaleTableModel.getColumnNames()[i]))
         {
            col.setPreferredWidth(DataScaleTableColumnWidthsPersister.getColumnWidthForColName(DataScaleTableModel.COL_NAME_DATA));
         }
         tcm.addColumn(col);
      }

      _overviewHolder.setOverview(dataScaleTable);
      initGui();

   }

   private void initGui()
   {
      _overviewPanel.btnPrev.setEnabled(_overviewHolder.hasParent());
      _overviewPanel.btnNext.setEnabled(_overviewHolder.hasKid());

      _overviewPanel.btnShowInTable.setEnabled(_overviewHolder.canShowInSimpleTable());
      _overviewPanel.btnShowInTableWin.setEnabled(_overviewHolder.canShowInSimpleTable());

      _overviewPanel.scrollPane.setViewportView(_overviewHolder.getComponent());

      if (_overviewHolder.isScaleTable() && 0 < _overviewHolder.getDataScaleTable().getAllRows().size())
      {
         _overviewPanel.cboColumns.setEnabled(true);

         Object selectedItem = _overviewPanel.cboColumns.getSelectedItem();
         DefaultComboBoxModel defaultComboBoxModel = new DefaultComboBoxModel(ColumnComboModel.createColumnComboModels(_overviewHolder.getDataScaleTable().getColumnDisplayDefinitions()));
         _overviewPanel.cboColumns.setModel(defaultComboBoxModel);
         _overviewPanel.cboColumns.setSelectedItem(selectedItem);
         if(null == _overviewPanel.cboColumns.getSelectedItem())
         {
            _overviewPanel.cboColumns.setSelectedIndex(0);
         }

         _overviewPanel.btnChart.setEnabled(true);
         _overviewPanel.btnReport.setEnabled(true);
      }
      else
      {
         _overviewPanel.cboColumns.setEnabled(false);
         _overviewPanel.btnChart.setEnabled(false);
         _overviewPanel.btnReport.setEnabled(false);
      }

   }
}
