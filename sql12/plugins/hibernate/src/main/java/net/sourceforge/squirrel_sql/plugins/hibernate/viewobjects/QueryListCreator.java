package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernateConnection;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.HqlQueryResult;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.ObjectSubstitute;
import net.sourceforge.squirrel_sql.plugins.hibernate.server.ObjectSubstituteRoot;
import net.sourceforge.squirrel_sql.plugins.hibernate.util.HqlQueryErrorUtil;

import javax.swing.*;
import java.util.List;

public class QueryListCreator extends SwingWorker<HqlQueryResult, Object>
{

   private static ILogger s_log = LoggerController.createLogger(QueryListCreator.class);
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(QueryListCreator.class);


   private QueryListCreatorListener _queryListCreatorListener;
   private String _hqlQuery;
   private int _maxNumResults;
   private HibernateConnection _con;
   private ISession _session;
   private WaitPanel _waitPanel;
   private volatile long _duration;
   private HqlQueryResult _hqlQueryResult;

   public QueryListCreator(QueryListCreatorListener queryListCreatorListener,
                           String hqlQuery,
                           int maxNumResults,
                           HibernateConnection con,
                           ISession session,
                           WaitPanel waitPanel)
   {
      _queryListCreatorListener = queryListCreatorListener;
      _hqlQuery = hqlQuery;
      _maxNumResults = maxNumResults;
      _con = con;
      _session = session;
      _waitPanel = waitPanel;
   }

   @Override
   protected HqlQueryResult doInBackground()
   {
      // Note: if an exception occurs here it will be thrown from the call to get() in method done() below.
      long begin = System.currentTimeMillis();
      HqlQueryResult queryRes = _con.createQueryList(_hqlQuery, _maxNumResults);
      _duration = System.currentTimeMillis() - begin;
      return queryRes;
   }


   @Override
   protected void done()
   {
      try
      {
         _hqlQueryResult = get();

         for (String msgKey : _hqlQueryResult.getSessionAdminExceptions().keySet())
         {
            s_log.error(msgKey, _hqlQueryResult.getSessionAdminExceptions().get(msgKey));
         }

         if (null != _hqlQueryResult.getExceptionOccuredWhenExecutingQuery())
         {
            // Maybe this error should be displayed as an error result tab like it is done for SQL errors.
            _waitPanel.displayHqlQueryError(HqlQueryErrorUtil.handleHqlQueryError(_hqlQueryResult.getExceptionOccuredWhenExecutingQuery(), _session, false));
            return;
         }

         if (null == _hqlQueryResult.getQueryResultList())
         {
            s_log.error(new NullPointerException("HqlQueryResult didn't contain a resultlist although it should according to its error state."));
         }
         else
         {
            _session.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("ObjectResultController.hqlReadObjectsSuccess", _hqlQueryResult.getQueryResultList().size(), _duration));
         }
      }
      catch (Throwable t)
      {
         s_log.error(t);
         _waitPanel.displayError(t);
      }
      finally
      {
         _queryListCreatorListener.queryExecuted(this);
      }
   }

   public List<ObjectSubstituteRoot> getList()
   {
      return _hqlQueryResult.getQueryResultList();
   }

   public int getMaxNumResults()
   {
      return _maxNumResults;
   }

   public HibernateConnection getConnection()
   {
      return _con;
   }

   public String getHqlQuery()
   {
      return _hqlQuery;
   }

   public WaitPanel getWaitPanel()
   {
      return _waitPanel;
   }
}
