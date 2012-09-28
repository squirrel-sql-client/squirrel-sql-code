package net.sourceforge.squirrel_sql.client.session.mainpanel.overview;

import net.sourceforge.squirrel_sql.client.resources.SquirrelResources;
import net.sourceforge.squirrel_sql.client.session.mainpanel.overview.datascale.DataScaleTable;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class OverviewPanel extends JPanel
{
   JScrollPane scrollPane = new JScrollPane();

   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(OverviewPanel.class);


   JButton btnPrev;
   JButton btnNext;

   JButton btnShowInTableWin;
   JButton btnShowInTable;

   JButton btnReport;
   JComboBox cboColumns;
   JButton btnChart;

   JComboBox cboCallDepth;


   public OverviewPanel(SquirrelResources rsrc)
   {
      setLayout(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      createButtonPanel(rsrc);
      add(createButtonPanel(rsrc), gbc);

      gbc = new GridBagConstraints(0,1,1,1,1,1,GridBagConstraints.NORTHEAST, GridBagConstraints.BOTH, new Insets(5,5,5,5),0,0);
      add(scrollPane, gbc);
   }

   private JPanel createButtonPanel(SquirrelResources rsrc)
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,0,0,5),0,0);
      btnPrev = new JButton(rsrc.getIcon(SquirrelResources.IImageNames.PREV_SCALE));
      btnPrev.setToolTipText(s_stringMgr.getString("OverwiewPanel.prevOverview"));
      btnPrev.setEnabled(false);
      ret.add(btnPrev, gbc);


      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,0,0,20),0,0);
      btnNext = new JButton(rsrc.getIcon(SquirrelResources.IImageNames.NEXT_SCALE));
      btnNext.setToolTipText(s_stringMgr.getString("OverwiewPanel.nextOverview"));
      btnNext.setEnabled(false);
      ret.add(btnNext, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,0,0,5),0,0);
      btnShowInTable = new JButton(s_stringMgr.getString("OverwiewPanel.showInTable"));
      btnShowInTable.setToolTipText(s_stringMgr.getString("OverwiewPanel.showInTableToolTip"));
      ret.add(btnShowInTable, gbc);

      gbc = new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,0,0,5),0,0);
      btnShowInTableWin = new JButton(s_stringMgr.getString("OverwiewPanel.showTableInWin"));
      btnShowInTableWin.setToolTipText(s_stringMgr.getString("OverwiewPanel.showTableInWinToolTip"));
      ret.add(btnShowInTableWin, gbc);

      gbc = new GridBagConstraints(4,0,1,1,0,0,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,10,0,0),0,0);
      btnReport = new JButton(s_stringMgr.getString("OverwiewPanel.report"));
      btnReport.setToolTipText(s_stringMgr.getString("OverwiewPanel.reportToolTip"));
      ret.add(btnReport, gbc);


      gbc = new GridBagConstraints(5,0,1,1,1,1,GridBagConstraints.NORTHEAST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5),0,0);
      ret.add(new JPanel(), gbc);


      gbc = new GridBagConstraints(6,0,1,1,0,0,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,15,0,0),0,0);
      ret.add(createChartPanel(), gbc);

      return ret;
   }

   private JPanel createChartPanel()
   {
      JPanel ret = new JPanel(new GridBagLayout());

      GridBagConstraints gbc;

      gbc = new GridBagConstraints(0,0,1,1,0,0,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,0,0,5),0,0);
      ret.add(new JLabel(s_stringMgr.getString("OverviewPanel.showChartBegin")), gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,0,0,5),0,0);
      cboColumns = new JComboBox();
      ret.add(cboColumns, gbc);

      gbc = new GridBagConstraints(2,0,1,1,0,0,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,0,0,10),0,0);
      btnChart = new JButton(s_stringMgr.getString("OverviewPanel.showChartEndButton"));
      ret.add(btnChart, gbc);


      gbc = new GridBagConstraints(3,0,1,1,0,0,GridBagConstraints.EAST, GridBagConstraints.NONE, new Insets(0,0,0,5),0,0);
      ret.add(new JLabel(s_stringMgr.getString("OverviewPanel.maxBarCount")), gbc);

      gbc = new GridBagConstraints(4,0,1,1,0,0,GridBagConstraints.NORTHEAST, GridBagConstraints.NONE, new Insets(0,0,0,5),0,0);
      cboCallDepth = new JComboBox();
      ret.add(cboCallDepth, gbc);


      return ret;
   }

   public void setTable(DataScaleTable dataScaleTable)
   {
      scrollPane.setViewportView(dataScaleTable);
   }
}
