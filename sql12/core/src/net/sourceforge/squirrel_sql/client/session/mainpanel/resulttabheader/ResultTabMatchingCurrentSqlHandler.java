package net.sourceforge.squirrel_sql.client.session.mainpanel.resulttabheader;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.client.session.ISQLEntryPanel;
import net.sourceforge.squirrel_sql.client.session.editorpaint.TextAreaPaintListener;
import net.sourceforge.squirrel_sql.client.session.mainpanel.IResultTab;
import net.sourceforge.squirrel_sql.client.session.mainpanel.SQLResultExecutorPanel;
import net.sourceforge.squirrel_sql.client.session.sqlbounds.BoundsOfSqlHandler;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformator;
import net.sourceforge.squirrel_sql.client.util.codereformat.CodeReformatorConfigFactory;
import net.sourceforge.squirrel_sql.fw.sql.querytokenizer.IQueryTokenizer;
import org.apache.commons.lang3.StringUtils;

import javax.swing.Timer;

public class ResultTabMatchingCurrentSqlHandler
{
   private boolean _resultTabHeaderMarkingActive;

   private BoundsOfSqlHandler _boundsOfSqlHandler;
   private ISQLEntryPanel _entryPanel;
   private SQLResultExecutorPanel _sqlExecPanel;
   private TextAreaPaintListener _textAreaPaintListener;

   private Timer _paintTimer;


   public ResultTabMatchingCurrentSqlHandler(ISQLEntryPanel entryPanel, SQLResultExecutorPanel sqlExecPanel)
   {
      _resultTabHeaderMarkingActive = Main.getApplication().getSquirrelPreferences().isResultTabHeaderMarkCurrentSQLsHeader();
      if(false == _resultTabHeaderMarkingActive)
      {
         return;
      }

      _entryPanel = entryPanel;
      _sqlExecPanel = sqlExecPanel;
      _boundsOfSqlHandler = new BoundsOfSqlHandler(entryPanel.getTextComponent(), entryPanel.getSession());


      _paintTimer = new Timer(200, e -> onTextAreaPaint(false));
      _paintTimer.setRepeats(false);
      _textAreaPaintListener = () -> _paintTimer.restart();
      _entryPanel.addTextAreaPaintListener(_textAreaPaintListener);
   }

   public void activateLastMarkedResultTabHeader()
   {
      if(false == _resultTabHeaderMarkingActive)
      {
         return;
      }

      onTextAreaPaint(true);
   }

   private void onTextAreaPaint(boolean forceActivateLastMarked)
   {
      String sqlToBeExecuted = _boundsOfSqlHandler.getSQLToBeExecuted();

      for(int i = 0; i < _sqlExecPanel.getTabbedPane().getTabCount(); i++)
      {
         ResultTabComponent resultTabComponent = _sqlExecPanel.getResultTabComponentAt(i);
         if( null != resultTabComponent ) // Happens during bootstrap
         {
            resultTabComponent.showTabHeaderMark(false);
         }
      }

      for(IResultTab sqlResultTab : _sqlExecPanel.getAllSqlResultTabs())
      {
         boolean tabMatchesSqlToBeExecuted;

         if(Main.getApplication().getSquirrelPreferences().isResultTabHeaderCompareSqlsNormalized())
         {

            IQueryTokenizer qt = _entryPanel.getSession().getNewQueryTokenizer();
            qt.setScriptToTokenize(sqlToBeExecuted);

            if(false == qt.hasQuery())
            {
               tabMatchesSqlToBeExecuted = false;
            }
            else
            {
               CodeReformator cr = new CodeReformator(CodeReformatorConfigFactory.createConfig(_entryPanel.getSession()));
               String resultTabSqlNormalized = cr.getNormalizedSql(sqlResultTab.getSqlString());
               String sqlToBeExecutedNormalized = cr.getNormalizedSql(qt.nextQuery().getCleanQuery());

               tabMatchesSqlToBeExecuted = StringUtils.equalsIgnoreCase(resultTabSqlNormalized, sqlToBeExecutedNormalized);
            }

         }
         else
         {
            tabMatchesSqlToBeExecuted = StringUtils.equalsIgnoreCase(sqlResultTab.getOriginalSqlString(), sqlToBeExecuted);
         }

         if(false == tabMatchesSqlToBeExecuted)
         {
            continue;
         }

         int lastMarkedIndex = -1;
         for(int i = 0; i < _sqlExecPanel.getTabbedPane().getTabCount(); i++)
         {
             if( sqlResultTab == _sqlExecPanel.getTabbedPane().getComponentAt(i) )
             {
                ResultTabComponent resultTabComponent = _sqlExecPanel.getResultTabComponentAt(i);
                if( null != resultTabComponent ) // Happens during bootstrap
                {
                   resultTabComponent.showTabHeaderMark(true);
                   lastMarkedIndex = i;
                }
             }
         }

         if(    -1 != lastMarkedIndex
             && (forceActivateLastMarked || Main.getApplication().getSquirrelPreferences().isResultTabHeaderAutoActivateTab()))
         {
            _sqlExecPanel.getTabbedPane().setSelectedIndex(lastMarkedIndex);
         }
      }
   }

   public void close()
   {
      if(false == _resultTabHeaderMarkingActive)
      {
         return;
      }

      _paintTimer.stop();
      _entryPanel.removeTextAreaPaintListener(_textAreaPaintListener);
   }
}
