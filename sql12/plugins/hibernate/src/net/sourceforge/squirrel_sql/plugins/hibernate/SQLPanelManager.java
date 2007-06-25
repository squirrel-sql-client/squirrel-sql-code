package net.sourceforge.squirrel_sql.plugins.hibernate;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLResultExecuterPanel;
import net.sourceforge.squirrel_sql.client.session.event.SessionAdapter;
import net.sourceforge.squirrel_sql.client.session.event.SessionEvent;
import net.sourceforge.squirrel_sql.fw.codereformat.CommentSpec;
import net.sourceforge.squirrel_sql.fw.codereformat.CodeReformator;

import javax.swing.*;
import java.util.ArrayList;
import java.util.prefs.Preferences;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class SQLPanelManager extends EntryPanelManagerBase
{
   private HibernateSQLPanel _hibernateSQLPanel;
   private static final String PREF_KEY_APPEND_SQL = "SquirrelSQL.hibernate.sqlAppendSql";
   private static final String PREF_KEY_FORMAT_SQL = "SquirrelSQL.hibernate.sqlFormatSql";
   private static final String PREF_KEY_EXECUTE_SQL = "SquirrelSQL.hibernate.sqlExecuteSql";

   SQLResultExecuterPanel _resultExecuterPanel;


   public SQLPanelManager(final ISession session)
   {
      super(session);
      _hibernateSQLPanel = new HibernateSQLPanel(super.getComponent());

      _resultExecuterPanel = new SQLResultExecuterPanel(session);

      session.getApplication().getSessionManager().addSessionListener(
         new SessionAdapter()
         {

            public void sessionClosing(SessionEvent evt)
            {
               onSessionClosing();
               session.getApplication().getSessionManager().removeSessionListener(this);
            }
         }
      );

      _hibernateSQLPanel._btnFormatSql.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onFormatSql();
         }
      });


      _hibernateSQLPanel._btnExecuteSql.addActionListener(new ActionListener()
      {
         public void actionPerformed(ActionEvent e)
         {
            onExecuteSql();
         }
      });


      _hibernateSQLPanel._chkAppendSql.setSelected(Preferences.userRoot().getBoolean(PREF_KEY_APPEND_SQL, false));
      _hibernateSQLPanel._chkAlwaysFormatSql.setSelected(Preferences.userRoot().getBoolean(PREF_KEY_FORMAT_SQL, false));
      _hibernateSQLPanel._chkAlwaysExecuteSql.setSelected(Preferences.userRoot().getBoolean(PREF_KEY_EXECUTE_SQL, false));
   }

   private void onExecuteSql()
   {
      displaySqlResult(getEntryPanel().getText());   
   }

   private void onFormatSql()
   {
      if (null != getEntryPanel().getText())
      {
         getEntryPanel().setText(format(getEntryPanel().getText()));
      }
   }

   private void onSessionClosing()
   {
      Preferences.userRoot().putBoolean(PREF_KEY_APPEND_SQL, _hibernateSQLPanel._chkAppendSql.isSelected());
      Preferences.userRoot().putBoolean(PREF_KEY_FORMAT_SQL, _hibernateSQLPanel._chkAlwaysFormatSql.isSelected());
      Preferences.userRoot().putBoolean(PREF_KEY_EXECUTE_SQL, _hibernateSQLPanel._chkAlwaysExecuteSql.isSelected());
   }


   public JComponent getComponent()
   {
      return _hibernateSQLPanel;
   }

   public void displaySqls(ArrayList<String> sqls)
   {
      String allSqls = createAllSqlsString(sqls);


      if (_hibernateSQLPanel._chkAlwaysExecuteSql.isSelected())
      {
         displaySqlResult(allSqls);
      }
      else
      {
         displaySqlCode(allSqls);
      }
   }

   private void displaySqlResult(String allSqls)
   {
      _hibernateSQLPanel.setMainComponent(_resultExecuterPanel);
      _resultExecuterPanel.executeSQL(allSqls);
   }

   private void displaySqlCode(String allSqls)
   {
      _hibernateSQLPanel.setMainComponent(super.getComponent());

      if (_hibernateSQLPanel._chkAlwaysFormatSql.isSelected())
      {
         allSqls = format(allSqls);
      }


      if (_hibernateSQLPanel._chkAppendSql.isSelected())
      {
         getEntryPanel().appendText(allSqls);

         scrollEntryPlanel(allSqls);

      }
      else
      {
         getEntryPanel().setText(allSqls, false);
      }
   }

   private String createAllSqlsString(ArrayList<String> sqls)
   {
      String allSqls = "";

      String sep = getSession().getProperties().getSQLStatementSeparator();

      for (String sql : sqls)
      {
         allSqls += sql;

         if (1 < sep.length())
         {
            allSqls += "\n";
         }
         allSqls += (sep + "\n\n");
      }
      return allSqls;
   }

   private String format(String sqls)
   {
      CommentSpec[] commentSpecs =
         new CommentSpec[]
            {
               new CommentSpec("/*", "*/"),
               new CommentSpec("--", "\n")
            };

      String statementSep = getSession().getQueryTokenizer().getSQLStatementSeparator();

      CodeReformator cr = new CodeReformator(statementSep, commentSpecs);

      sqls = cr.reformat(sqls) + "\n";
      return sqls;
   }


   private void scrollEntryPlanel(String allSqls)
   {
      getEntryPanel().getTextComponent().setCaretPosition(getEntryPanel().getText().length() - allSqls.length());
      Point p = getEntryPanel().getTextComponent().getCaret().getMagicCaretPosition();
      if (p != null)
      {
         getEntryPanel().getTextComponent().scrollRectToVisible(new Rectangle(p.x, p.y, 1, 100));
      }
   }
}
