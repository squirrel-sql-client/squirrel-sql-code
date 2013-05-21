package org.squirrelsql;

public class AppState
{
   private static AppState _appState = new AppState();

   private StatusBarCtrl _statusBarCtrl = new StatusBarCtrl();
   private MessagePanelCtrl _messagePanelCtrl = new MessagePanelCtrl();

   public static AppState get()
   {
      return _appState;
   }

   private PrefImpl _prefImpl = new PrefImpl();

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
}
