package org.squirrelsql;

import javafx.application.Application;
import javafx.stage.Stage;

import org.squirrelsql.services.PropertiesHandler;
import org.squirrelsql.services.RunningServicesManager;
import org.squirrelsql.services.SettingsManager;
import org.squirrelsql.services.SquirrelProperty;
import org.squirrelsql.session.SessionManager;
import org.squirrelsql.session.action.ActionManager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class AppState
{
   private static AppState _appState;
   private final PropertiesHandler _propertiesHandler;

   private StatusBarCtrl _statusBarCtrl = new StatusBarCtrl();
   private MessagePanelCtrl _messagePanelCtrl = new MessagePanelCtrl();

   private List<CloseListenerWithFireTime> _applicationCloseListeners = new ArrayList<>();
   private SessionManager _sessionManager = new SessionManager();
   private ActionManager _actionManager = new ActionManager(_sessionManager);
   private SqlHistoryManager _sqlHistoryManager = new SqlHistoryManager();

   private RunningServicesManager _runningServicesManager = new RunningServicesManager();

   private PrefImpl _prefImpl = new PrefImpl();
   private SettingsManager _settingsManager = new SettingsManager();



   public static AppState get()
   {
      return _appState;
   }


   private Stage _primaryStage;

   public AppState(Stage primaryStage, Application.Parameters parameters)
   {
      _primaryStage = primaryStage;
      _propertiesHandler = new PropertiesHandler(parameters);
   }


   public PrefImpl getPrefImpl()
   {
      return _prefImpl;
   }


   public StatusBarCtrl getStatusBarCtrl()
   {
      return _statusBarCtrl;
   }

   public MessagePanelCtrl getMessagePanelCtrl()
   {
      return _messagePanelCtrl;
   }

   public Stage getPrimaryStage()
   {
      return _primaryStage;
   }

   public static void init(Stage primaryStage, Application.Parameters parameters)
   {
      _appState = new AppState(primaryStage, parameters);
   }

   public PropertiesHandler getPropertiesHandler()
   {
      return _propertiesHandler;
   }

   public File getUserDir()
   {
      String userDir = _propertiesHandler.getProperty(SquirrelProperty.USER_DIR).trim();
      File file = new File(userDir);
      file.mkdirs();
      return file;
   }

   public File getLogDir()
   {
      File file = new File(getUserDir(), "logs");
      file.mkdirs();
      return file;
   }


   public void doAfterBootstrap()
   {
      _propertiesHandler.doAfterBootstrap();
   }

   public void addApplicationCloseListener(ApplicationCloseListener l, ApplicationCloseListener.FireTime fireTime)
   {
      _applicationCloseListeners.add(new CloseListenerWithFireTime(l, fireTime));
   }

   public void fireApplicationClosing()
   {
      _fireApplicationClosing(ApplicationCloseListener.FireTime.WITHIN_SESSION_FIRE_TIME);
      _fireApplicationClosing(ApplicationCloseListener.FireTime.AFTER_SESSION_FIRE_TIME);
   }

   private void _fireApplicationClosing(ApplicationCloseListener.FireTime fireTime)
   {
      CloseListenerWithFireTime[] clone = _applicationCloseListeners.toArray(new CloseListenerWithFireTime[_applicationCloseListeners.size()]);

      for (CloseListenerWithFireTime applicationCloseListener : clone)
      {
         if (applicationCloseListener.getFireTime() == fireTime)
         {
            applicationCloseListener.getListener().applicationClosing();
         }
      }
   }

   public SessionManager getSessionManager()
   {
      return _sessionManager;
   }

   public ActionManager getActionManager()
   {
      return _actionManager;
   }

   public void removeApplicationCloseListener(ApplicationCloseListener l)
   {
      for (CloseListenerWithFireTime applicationCloseListener : _applicationCloseListeners)
      {
         if(applicationCloseListener.getListener().equals(l))
         {
            _applicationCloseListeners.remove(applicationCloseListener);
            break;
         }
      }
   }

   public SqlHistoryManager getSqlHistoryManager()
   {
      return _sqlHistoryManager;
   }

   public RunningServicesManager getRunningServicesManager()
   {
      return _runningServicesManager;
   }


   public SettingsManager getSettingsManager()
   {
      return _settingsManager;
   }
}
