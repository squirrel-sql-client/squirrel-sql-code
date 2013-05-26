package org.squirrelsql;

import javafx.stage.Stage;
import javafx.stage.Window;

public class AppState
{
   private static AppState _appState;

   private StatusBarCtrl _statusBarCtrl = new StatusBarCtrl();
   private MessagePanelCtrl _messagePanelCtrl = new MessagePanelCtrl();


   public static AppState get()
   {
      return _appState;
   }

   private PrefImpl _prefImpl = new PrefImpl();

   private Stage _primaryStage;

   public AppState(Stage primaryStage)
   {
      _primaryStage = primaryStage;
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

   public static void init(Stage primaryStage)
   {
      _appState = new AppState(primaryStage);
   }
}
