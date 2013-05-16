package org.squirrelsql;

public class AppState
{
   private static AppState _appState = new AppState();

   public static AppState get()
   {
      return _appState;
   }

   private PrefImpl _prefImpl = new PrefImpl();

   public PrefImpl getPrefImpl()
   {
      return _prefImpl;
   }
}
