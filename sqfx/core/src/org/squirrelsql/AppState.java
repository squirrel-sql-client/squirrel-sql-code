package org.squirrelsql;

import javafx.application.Application;
import javafx.stage.Stage;
import org.squirrelsql.services.PropertiesHandler;
import org.squirrelsql.services.SquirrelProperty;
import org.squirrelsql.session.SessionManager;
import org.squirrelsql.session.action.ActionMangerImpl;

import java.io.File;
import java.util.ArrayList;

public class AppState
{
   private static AppState _appState;
   private final PropertiesHandler _propertiesHandler;

   private StatusBarCtrl _statusBarCtrl = new StatusBarCtrl();
   private MessagePanelCtrl _messagePanelCtrl = new MessagePanelCtrl();

   private ArrayList<ApplicationCloseListener> _applicationCloseListeners = new ArrayList<>();
   private SessionManager _sessionManager = new SessionManager();
   private ActionMangerImpl _actionMangerImpl = new ActionMangerImpl(_sessionManager);


   public static AppState get()
   {
      return _appState;
   }

   private PrefImpl _prefImpl = new PrefImpl();

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

   public void doAfterBootstrap()
   {
      _propertiesHandler.doAfterBootstrap();
   }

   public void addApplicationCloseListener(ApplicationCloseListener l)
   {
      _applicationCloseListeners.add(l);
   }

   public void fireApplicationClosing()
   {
      ApplicationCloseListener[] clone = _applicationCloseListeners.toArray(new ApplicationCloseListener[_applicationCloseListeners.size()]);

      for (ApplicationCloseListener applicationCloseListener : clone)
      {
         applicationCloseListener.applicationClosing();
      }
   }

   public SessionManager getSessionManager()
   {
      return _sessionManager;
   }

   public ActionMangerImpl getActionMangerImpl()
   {
      return _actionMangerImpl;
   }
}
