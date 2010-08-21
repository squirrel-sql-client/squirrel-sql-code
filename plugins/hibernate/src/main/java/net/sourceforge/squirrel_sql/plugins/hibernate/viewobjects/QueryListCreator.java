package net.sourceforge.squirrel_sql.plugins.hibernate.viewobjects;

import net.sourceforge.squirrel_sql.client.session.ISession;
import net.sourceforge.squirrel_sql.fw.util.ExceptionFormatter;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.Utilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;
import net.sourceforge.squirrel_sql.plugins.hibernate.HibernateConnection;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class QueryListCreator extends SwingWorker
{

   private static ILogger s_log = LoggerController.createLogger(ObjectResultController.class);
   private static final StringManager s_stringMgr = StringManagerFactory.getStringManager(QueryListCreator.class);


   private QueryListCreatorListener _queryListCreatorListener;
   private String _hqlQuery;
   private int _maxNumResults;
   private HibernateConnection _con;
   private ISession _session;
   private WaitPanel _waitPanel;
   private volatile long _duration;
   private List _list;

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
   protected Object doInBackground() throws Exception
   {
      long begin = System.currentTimeMillis();
      List ret = _con.createQueryList(_hqlQuery, _maxNumResults);
      _duration = System.currentTimeMillis() - begin;
      return ret;
   }


   @Override
   protected void done()
   {
      try
      {
         _list = (List) get();
         _session.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("ObjectResultController.hqlReadObjectsSuccess", _list.size(), _duration));
      }
      catch (Exception e)
      {
         Throwable t = Utilities.getDeepestThrowable(e);
         ExceptionFormatter formatter = _session.getExceptionFormatter();
         try
         {
            String message = formatter.format(t);
            _session.showErrorMessage(message);
         }
         catch (Exception e1)
         {
            _session.showErrorMessage(e1);
            _session.showErrorMessage(t);
         }

         if (_session.getProperties().getWriteSQLErrorsToLog() ||
            (-1 == t.getClass().getName().toLowerCase().indexOf("hibernate") && -1 == t.getClass().getName().toLowerCase().indexOf("antlr")))
         {
            // If this is not a hibernate error we write a log entry
            s_log.error(t);
         }
      }

      _queryListCreatorListener.listRead(this);

   }

   public List getList()
   {
      return _list;
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



//   private List readObjects(HibernateConnection con, String hqlQuery, int sqlLimitRows)
//   {
//      long begin = System.currentTimeMillis();
//      long duration;
//      try
//      {
//         List objects;
//
//         objects = con.createQueryList(hqlQuery, sqlLimitRows);
//
//         duration = System.currentTimeMillis() - begin;
//
//
//         _session.getApplication().getMessageHandler().showMessage(s_stringMgr.getString("ObjectResultController.hqlReadObjectsSuccess", objects.size(), duration));
//
//         return objects;
//      }
//      catch (Exception e)
//      {
//         Throwable t = Utilities.getDeepestThrowable(e);
//         ExceptionFormatter formatter = _session.getExceptionFormatter();
//         try
//         {
//            String message = formatter.format(t);
//            _session.showErrorMessage(message);
//         }
//         catch (Exception e1)
//         {
//            _session.showErrorMessage(e1);
//            _session.showErrorMessage(t);
//         }
//
//         if (_session.getProperties().getWriteSQLErrorsToLog() ||
//            (-1 == t.getClass().getName().toLowerCase().indexOf("hibernate") && -1 == t.getClass().getName().toLowerCase().indexOf("antlr")))
//         {
//            // If this is not a hibernate error we write a log entry
//            s_log.error(t);
//         }
//
//         return null;
//      }
//   }

}
