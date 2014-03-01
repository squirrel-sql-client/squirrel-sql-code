package net.sourceforge.squirrel_sql.client.preferences.codereformat;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformatorConfigFactory;
import net.sourceforge.squirrel_sql.fw.gui.FontInfo;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;

import java.awt.event.*;
import java.util.ArrayList;

public class FormatSqlController
{
   private static final StringManager s_stringMgr =
         StringManagerFactory.getStringManager(FormatSqlController.class);


   private FormatSqlPanel _formatSqlPanel;
   private FormatSqlPref _formatSqlPref;
   private IApplication _app;

   public FormatSqlController(IApplication app)
   {
      _app = app;

      _formatSqlPref = FormatSqlPrefReader.loadPref();
      _formatSqlPanel = new FormatSqlPanel(_formatSqlPref.getKeywordBehaviourPrefs());

      FontInfo fontInfo = app.getSquirrelPreferences().getSessionProperties().getFontInfo();
      _formatSqlPanel.txtExampleSqls.setEditable(false);
      _formatSqlPanel.txtExampleSqls.setFont(fontInfo.createFont());
      refreshExampleSql(_formatSqlPref);



      _formatSqlPanel.txtIndentCount.setValue(_formatSqlPref.getIndent());

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

      ActionListener actionListener = new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            refreshExampleSql(createFormatSqlPrefFromGui());
         }
      };

      for (KeywordBehaviourPrefCtrl keywordBehaviourPrefCtrl : _formatSqlPanel.keywordBehaviourPrefCtrls)
      {
         keywordBehaviourPrefCtrl.addKeyWordBehaviourChangedListener(actionListener);
      }

      _formatSqlPanel.chkDoInsertValuesAlign.setSelected(_formatSqlPref.isDoInsertValuesAlign());
      _formatSqlPanel.chkDoInsertValuesAlign.addActionListener(new ActionListener()
      {
         @Override
         public void actionPerformed(ActionEvent e)
         {
            adjustInsertValuesState();
         }
      });
      adjustInsertValuesState();

   }

   private void adjustInsertValuesState()
   {
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
      }
      refreshExampleSql(createFormatSqlPrefFromGui());
   }

   private void refreshExampleSql(FormatSqlPref formatSqlPref)
   {
      String sqls;CodeReformator codeReformator = new CodeReformator(CodeReformatorConfigFactory.createConfig(formatSqlPref));

      sqls = codeReformator.reformat("SELECT table1.id, table2.number, SUM(table1.amount) FROM table1 INNER JOIN table2 ON table.id = table2.table1_id WHERE table1.id IN (SELECT table1_id FROM table3 WHERE table3.name = 'Foo Bar' and table3.type = 'unknown_type') GROUP BY table1.id, table2.number ORDER BY table1.id");
      sqls += "\n\n";
      sqls += codeReformator.reformat("UPDATE table1 SET name = 'Hello', number = '1456-789' WHERE id = 42");
      sqls += "\n\n";
      sqls += codeReformator.reformat("INSERT INTO table1 (name, number) SELECT name, number FROM table1_bak");
      sqls += "\n\n";
      sqls += codeReformator.reformat("INSERT INTO table1 (name, number, type) VALUES ('Foo', 42, 'VA')");
      sqls += "\n\n";
      sqls += codeReformator.reformat("DELETE FROM table1 WHERE  name = 'Hello' OR number = '1456-789'");

      _formatSqlPanel.txtExampleSqls.setText(sqls);
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

      ArrayList<KeywordBehaviourPref> buf = new ArrayList<KeywordBehaviourPref>();
      for (KeywordBehaviourPrefCtrl keywordBehaviourPrefCtrl : _formatSqlPanel.keywordBehaviourPrefCtrls)
      {
         keywordBehaviourPrefCtrl.applyChanges();
         buf.add(keywordBehaviourPrefCtrl.getKeywordBehaviourPref());
      }
      ret.setKeywordBehaviourPrefs(buf.toArray(new KeywordBehaviourPref[buf.size()]));

      ret.setDoInsertValuesAlign(_formatSqlPanel.chkDoInsertValuesAlign.isSelected());

      return ret;
   }

   public FormatSqlPanel getPanel()
   {
      return _formatSqlPanel;
   }
}
