package net.sourceforge.squirrel_sql.client.preferences.codereformat;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.xml.XMLBeanWriter;

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
      _formatSqlPanel.txtIndentCount.setValue(_formatSqlPref.getIndent());
      _formatSqlPanel.txtPreferedLineLength.setValue(_formatSqlPref.getPreferedLineLength());
   }


   public void applyChanges()
   {
      try
      {
         if (null != _formatSqlPanel.txtIndentCount.getText())
         {
            try
            {
               Integer indent = Integer.valueOf(_formatSqlPanel.txtIndentCount.getText());
               if (indent >= 0)
               {
                  _formatSqlPref.setIndent(indent);
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
                  _formatSqlPref.setPreferedLineLength(preferedLineLength);
               }
            }
            catch (NumberFormatException e)
            {
               // ignore
            }
         }

         ArrayList<KeywordBehaviourPref> buf = new ArrayList<KeywordBehaviourPref>();
         for (KeywordBehaviourPrefCtrl keywordBehaviourPrefCtrl : _formatSqlPanel._keywordBehaviourPrefCtrls)
         {
            keywordBehaviourPrefCtrl.applyChanges();
            buf.add(keywordBehaviourPrefCtrl.getKeywordBehaviourPref());
         }
         _formatSqlPref.setKeywordBehaviourPrefs(buf.toArray(new KeywordBehaviourPref[buf.size()]));

         XMLBeanWriter bw = new XMLBeanWriter(_formatSqlPref);
         bw.save(FormatSqlPrefReader.getPrefsFile());
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   public FormatSqlPanel getPanel()
   {
      return _formatSqlPanel;
   }
}
