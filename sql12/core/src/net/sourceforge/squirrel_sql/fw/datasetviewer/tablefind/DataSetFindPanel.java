package net.sourceforge.squirrel_sql.fw.datasetviewer.tablefind;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.resources.LibraryResources;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import javax.swing.*;
import java.awt.*;

public class DataSetFindPanel extends JPanel
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(DataSetFindPanel.class);

   JComboBox cboMatchType;
   JCheckBox chkCaseSensitive;
   JComboBox cboString;

   JButton btnDown;
   JButton btnUp;
   JButton btnUnhighlightResult;
   JButton btnHideFindPanel;
   JButton btnHighlightFindResult;
   JButton btnShowRowsFoundInTable;
   JButton btnColorMatchedCells;

   public DataSetFindPanel()
   {
      setLayout(new GridBagLayout());

      GridBagConstraints gbc;



      gbc = new GridBagConstraints(0,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      cboMatchType = new JComboBox(MatchTypeCboItem.values());
      cboMatchType.setSelectedIndex(0);
      add(cboMatchType, gbc);

      gbc = new GridBagConstraints(1,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      chkCaseSensitive = new JCheckBox(s_stringMgr.getString("DataSetFindPanel.caseSensitive"));
      add(chkCaseSensitive, gbc);

      gbc = new GridBagConstraints(2,0,1,1,1,0, GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(5,5,5,5), 0,0);
      cboString = new JComboBox();
      add(cboString, gbc);


      LibraryResources rsrc = Main.getApplication().getResourcesFw();


      gbc = new GridBagConstraints(3,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,0), 0,0);
      btnDown = new JButton(rsrc.getIcon(LibraryResources.IImageNames.TABLE_DESCENDING));
      btnDown.setToolTipText(s_stringMgr.getString("DataSetFindPanel.findNext"));
      btnDown.setBorder(BorderFactory.createEtchedBorder());
      add(btnDown, gbc);

      gbc = new GridBagConstraints(4,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,0), 0,0);
      btnUp = new JButton(rsrc.getIcon(LibraryResources.IImageNames.TABLE_ASCENDING));
      btnUp.setToolTipText(s_stringMgr.getString("DataSetFindPanel.findPrevious"));
      btnUp.setBorder(BorderFactory.createEtchedBorder());
      add(btnUp, gbc);

      gbc = new GridBagConstraints(5,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,0), 0,0);
      btnHighlightFindResult = new JButton(rsrc.getIcon(LibraryResources.IImageNames.TABLE_MARKED));
      btnHighlightFindResult.setToolTipText(s_stringMgr.getString("DataSetFindPanel.markFindResult"));
      btnHighlightFindResult.setBorder(BorderFactory.createEtchedBorder());
      add(btnHighlightFindResult, gbc);

      gbc = new GridBagConstraints(6,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,0), 0,0);
      btnUnhighlightResult = new JButton(rsrc.getIcon(LibraryResources.IImageNames.TABLE_CLEAN));
      btnUnhighlightResult.setToolTipText(s_stringMgr.getString("DataSetFindPanel.unmarkFindResult"));
      btnUnhighlightResult.setBorder(BorderFactory.createEtchedBorder());
      add(btnUnhighlightResult, gbc);

      gbc = new GridBagConstraints(7,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,0), 0,0);
      btnShowRowsFoundInTable = new JButton(rsrc.getIcon(LibraryResources.IImageNames.MARKED_TO_NEW_TABLE));
      btnShowRowsFoundInTable.setToolTipText(s_stringMgr.getString("DataSetFindPanel.showFoundRowsInTable"));
      btnShowRowsFoundInTable.setBorder(BorderFactory.createEtchedBorder());
      add(btnShowRowsFoundInTable, gbc);

      gbc = new GridBagConstraints(8,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,0), 0,0);
      btnColorMatchedCells = new JButton(rsrc.getIcon(LibraryResources.IImageNames.COLOR_CHOOSE));
      btnColorMatchedCells.setToolTipText(s_stringMgr.getString("DataSetFindPanel.colorMatchedCells"));
      btnColorMatchedCells.setBorder(BorderFactory.createEtchedBorder());
      add(btnColorMatchedCells, gbc);

      gbc = new GridBagConstraints(9,0,1,1,0,0, GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(5,5,5,5), 0,0);
      btnHideFindPanel = new JButton(rsrc.getIcon(LibraryResources.IImageNames.HIDE));
      btnHideFindPanel.setPressedIcon(rsrc.getIcon(LibraryResources.IImageNames.HIDE_SELECTED));
      btnHideFindPanel.setToolTipText(s_stringMgr.getString("DataSetFindPanel.hideFind"));
      btnHideFindPanel.setBorder(BorderFactory.createEtchedBorder());
      add(btnHideFindPanel, gbc);

   }

   static enum MatchTypeCboItem
   {
      // i18n[DataSetFindPanel.filterCboContains=contains]
      CONTAINS (s_stringMgr.getString("DataSetFindPanel.filterCboContains")),

      // i18n[DataSetFindPanel.filterCboContains=contains]
      EXACT (s_stringMgr.getString("DataSetFindPanel.exact")),

      // i18n[DataSetFindPanel.filterCboStartsWith=starts with]
      STARTS_WITH (s_stringMgr.getString("DataSetFindPanel.filterCboStartsWith")),

      // i18n[DataSetFindPanel.filterCboEndsWith=ends with]
      ENDS_WITH (s_stringMgr.getString("DataSetFindPanel.filterCboEndsWith")),

      // i18n[DataSetFindPanel.filterCboRegEx=regular exp]
      REG_EX (s_stringMgr.getString("DataSetFindPanel.filterCboRegEx"));
      private String _name;


      MatchTypeCboItem(String name)
      {
         _name = name;
      }


      public String toString()
      {
         return _name;
      }
   }

}
