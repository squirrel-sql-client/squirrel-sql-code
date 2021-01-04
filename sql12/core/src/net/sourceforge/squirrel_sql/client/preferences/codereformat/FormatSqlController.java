package net.sourceforge.squirrel_sql.client.preferences.codereformat;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformatorConfigFactory;
import net.sourceforge.squirrel_sql.client.util.codereformat.ColumnListSpiltMode;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;

import javax.swing.DefaultComboBoxModel;
import javax.swing.SwingUtilities;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.ArrayList;

public class FormatSqlController
{
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(FormatSqlController.class);


   private FormatSqlPanel _formatSqlPanel;
   private FormatSqlPref _formatSqlPref;

   public FormatSqlController(IApplication app)
   {

      _formatSqlPref = FormatSqlPrefReader.loadPref();
      _formatSqlPanel = new FormatSqlPanel(_formatSqlPref.getKeywordBehaviourPrefs());

      FontInfo fontInfo = app.getSquirrelPreferences().getSessionProperties().getFontInfo();
      _formatSqlPanel.txtExampleSqls.setEditable(false);
      _formatSqlPanel.txtExampleSqls.setFont(fontInfo.createFont());

      _formatSqlPanel.txtIndentCount.setValue(_formatSqlPref.getIndent());

      ActionListener adjustValuesToConstraintListeners = e -> adjustValuesToConstraints();

      _formatSqlPanel.radUseVerticalBlankFormatter.setSelected(_formatSqlPref.isUseVerticalBlankFormatter());
      _formatSqlPanel.radUseSquirrelInternalFormatter.setSelected(false == _formatSqlPref.isUseVerticalBlankFormatter());
      _formatSqlPanel.radUseVerticalBlankFormatter.addActionListener(adjustValuesToConstraintListeners);
      _formatSqlPanel.radUseSquirrelInternalFormatter.addActionListener(adjustValuesToConstraintListeners);

      _formatSqlPanel.chkIndentSections.setSelected(_formatSqlPref.isIndentSections());
      _formatSqlPanel.chkIndentSections.addActionListener(adjustValuesToConstraintListeners);

      _formatSqlPanel.radCommasAtLineBeginNo.setSelected(!_formatSqlPref.isCommasAtLineBegin());
      _formatSqlPanel.radCommasAtLineBeginNo.addActionListener(adjustValuesToConstraintListeners);
      _formatSqlPanel.radCommasAtLineBeginYes.setSelected(_formatSqlPref.isCommasAtLineBegin());
      _formatSqlPanel.radCommasAtLineBeginYes.addActionListener(adjustValuesToConstraintListeners);


      _formatSqlPanel.txtIndentCount.addFocusListener(new FocusAdapter()
      {
         @Override
         public void focusLost(FocusEvent e)
         {
            refreshExampleSql(createFormatSqlPrefFromGui());
         }
      });

      _formatSqlPanel.txtPreferedLineLength.setValue(_formatSqlPref.getPreferedLineLength());
      _formatSqlPanel.txtPreferedLineLength.addFocusListener(new FocusAdapter()
      {
         @Override
         public void focusLost(FocusEvent e)
         {
            refreshExampleSql(createFormatSqlPrefFromGui());
         }
      });

      for (KeywordBehaviourPrefCtrl keywordBehaviourPrefCtrl : _formatSqlPanel.keywordBehaviourPrefCtrls)
      {
         keywordBehaviourPrefCtrl.addKeyWordBehaviourChangedListener(adjustValuesToConstraintListeners);
      }

      _formatSqlPanel.chkDoInsertValuesAlign.setSelected(_formatSqlPref.isDoInsertValuesAlign());
      _formatSqlPanel.chkDoInsertValuesAlign.addActionListener(adjustValuesToConstraintListeners);

      _formatSqlPanel._cboColumnListSplitMode.setModel(new DefaultComboBoxModel(ColumnListSpiltMode.values()));
      _formatSqlPanel._cboColumnListSplitMode.setSelectedItem(ColumnListSpiltMode.valueOf(_formatSqlPref.getColumnListSplitMode()));
      _formatSqlPanel._cboColumnListSplitMode.addActionListener(adjustValuesToConstraintListeners);

      _formatSqlPanel._chkLineBreakFor_AND_OR_in_FROM_clause.setSelected(_formatSqlPref.isLineBreakFor_AND_OR_in_FROM_clause());
      _formatSqlPanel._chkLineBreakFor_AND_OR_in_FROM_clause.addActionListener(adjustValuesToConstraintListeners);

      adjustValuesToConstraints();

   }

   private void adjustValuesToConstraints()
   {
      KeywordBehaviourPrefCtrl andCtrl = null;
      KeywordBehaviourPrefCtrl orCtrl = null;


      for (KeywordBehaviourPrefCtrl keywordBehaviourPrefCtrl : _formatSqlPanel.keywordBehaviourPrefCtrls)
      {
         String keyWord = keywordBehaviourPrefCtrl.getKeywordBehaviourPref().getKeyWord();
         if (FormatSqlPref.INSERT.equals(keyWord))
         {
            if(_formatSqlPanel.chkDoInsertValuesAlign.isSelected())
            {
               keywordBehaviourPrefCtrl.setBehaviour(FormatSqlPanel.KeywordBehaviour.START_NEW_LINE);
               keywordBehaviourPrefCtrl.setEnabled(false);
            }
            else
            {
               keywordBehaviourPrefCtrl.setEnabled(true);
            }
         }
         else if (FormatSqlPref.VALUES.equals(keyWord))
         {
            if(_formatSqlPanel.chkDoInsertValuesAlign.isSelected())
            {
               keywordBehaviourPrefCtrl.setBehaviour(FormatSqlPanel.KeywordBehaviour.NO_INFLUENCE_ON_NEW_LINE);
               keywordBehaviourPrefCtrl.setEnabled(false);
            }
            else
            {
               keywordBehaviourPrefCtrl.setEnabled(true);
            }
         }
         else if (FormatSqlPref.AND.equals(keyWord))
         {
            andCtrl = keywordBehaviourPrefCtrl;
         }
         else if (FormatSqlPref.OR.equals(keyWord))
         {
            orCtrl = keywordBehaviourPrefCtrl;
         }
      }

      if(FormatSqlPanel.KeywordBehaviour.START_NEW_LINE == andCtrl.getSelectedKeywordBehaviour() || FormatSqlPanel.KeywordBehaviour.START_NEW_LINE == orCtrl.getSelectedKeywordBehaviour())
      {
         _formatSqlPanel._chkLineBreakFor_AND_OR_in_FROM_clause.setEnabled(true);
      }
      else
      {
         _formatSqlPanel._chkLineBreakFor_AND_OR_in_FROM_clause.setEnabled(false);
      }


      refreshExampleSql(createFormatSqlPrefFromGui());
   }

   private void refreshExampleSql(FormatSqlPref formatSqlPref)
   {
      String sqls;

      CodeReformator codeReformator = new CodeReformator(CodeReformatorConfigFactory.createConfig(formatSqlPref));

      sqls = codeReformator.reformat("SELECT table1.id,table2.number,table2.name,table2.info1,table2.info2,table2.info3,table2.info4,table2.info5,table2.info6,SUM(table1.amount) FROM table1 INNER JOIN table2 ON table.id1 = table2.table1_id1 AND table.id2 = table2.table1_id2 LEFT OUTER JOIN table3 ON table.id1 = table3.table1_id1 AND table.id2 = table3.table1_id3 WHERE table1.id IN (SELECT table1_id FROM table3 WHERE table3.name = 'Foo Bar' and table3.type = 'unknown_type') AND table2.name LIKE '%g%' GROUP BY table1.id,table2.number ORDER BY table1.id");
      sqls += "\n\n";
      sqls += codeReformator.reformat("UPDATE table1 SET name = 'Hello', number = '1456-789' WHERE id = 42");
      sqls += "\n\n";
      sqls += codeReformator.reformat("INSERT INTO table1 (name, number) SELECT name,number FROM table1_bak");
      sqls += "\n\n";
      sqls += codeReformator.reformat("INSERT INTO table1 (name,number,type) VALUES ('Foo',42,'VA')");
      sqls += "\n\n";
      sqls += codeReformator.reformat("DELETE FROM table1 WHERE  name = 'Hello' OR number = '1456-789'");

      _formatSqlPanel.txtExampleSqls.setText(sqls);

      SwingUtilities.invokeLater(() -> _formatSqlPanel.txtExampleSqls.scrollRectToVisible(new Rectangle(0,0,1,1)));
   }


   public void applyChanges()
   {
      try
      {
         _formatSqlPref = createFormatSqlPrefFromGui();

         XMLBeanWriter bw = new XMLBeanWriter(_formatSqlPref);
         bw.save(FormatSqlPrefReader.getPrefsFile());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private FormatSqlPref createFormatSqlPrefFromGui()
   {
      FormatSqlPref ret = new FormatSqlPref();

      ret.setUseVerticalBlankFormatter(_formatSqlPanel.radUseVerticalBlankFormatter.isSelected());

      if (null != _formatSqlPanel.txtIndentCount.getText())
      {
         try
         {
            Integer indent = Integer.valueOf(_formatSqlPanel.txtIndentCount.getText());
            if (indent >= 0)
            {
               ret.setIndent(indent);
            }
         }
         catch (NumberFormatException e)
         {
            // ignore
         }
      }

      if (null != _formatSqlPanel.txtPreferedLineLength.getText())
      {
         try
         {
            Integer preferedLineLength = Integer.valueOf(_formatSqlPanel.txtPreferedLineLength.getText());
            if (preferedLineLength >= 0)
            {
               ret.setPreferedLineLength(preferedLineLength);
            }
         }
         catch (NumberFormatException e)
         {
            // ignore
         }
      }

      ret.setIndentSections(_formatSqlPanel.chkIndentSections.isSelected());
      ret.setCommasAtLineBegin(_formatSqlPanel.radCommasAtLineBeginYes.isSelected());


      ArrayList<KeywordBehaviourPref> buf = new ArrayList<>();
      for (KeywordBehaviourPrefCtrl keywordBehaviourPrefCtrl : _formatSqlPanel.keywordBehaviourPrefCtrls)
      {
         keywordBehaviourPrefCtrl.applyChanges();
         buf.add(keywordBehaviourPrefCtrl.getKeywordBehaviourPref());
      }
      ret.setKeywordBehaviourPrefs(buf.toArray(new KeywordBehaviourPref[buf.size()]));

      ret.setDoInsertValuesAlign(_formatSqlPanel.chkDoInsertValuesAlign.isSelected());

      ColumnListSpiltMode selMode = (ColumnListSpiltMode) _formatSqlPanel._cboColumnListSplitMode.getSelectedItem();
      ret.setColumnListSplitMode(selMode.name());

      ret.setLineBreakFor_AND_OR_in_FROM_clause(_formatSqlPanel._chkLineBreakFor_AND_OR_in_FROM_clause.isSelected());

      return ret;
   }

   public FormatSqlPanel getPanel()
   {
      return _formatSqlPanel;
   }
}
