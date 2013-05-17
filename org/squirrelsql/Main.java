package org.squirrelsql;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application
{
   public static final String PREF_MAIN_WIN_WIDTH = "mainWin.width";
   public static final String PREF_MAIN_WIN_HEIGHT = "mainWin.height";
   public static final String PREF_MAIN_WIN_X = "mainWin.x";
   public static final String PREF_MAIN_WIN_Y = "mainWin.y";

   private I18n i18n = new I18n(getClass());
   private Pref pref = new Pref(getClass());

   public static final String VERSION = "4fx";
   private Stage _primaryStage;

   @Override
   public void start(Stage primaryStage) throws Exception
   {
      _primaryStage = primaryStage;

      primaryStage.setTitle(i18n.t("mainWin.title") + " " + VERSION);

      BorderPane borderPane = new BorderPane();
      primaryStage.setScene(new Scene(borderPane));

      final SplitController splitController = new SplitController();
      borderPane.setCenter(splitController.getNode());


      DockButtonsListener dockButtonsListener = new DockButtonsListener()
      {
         @Override
         public void driversChanged(boolean selected)
         {
            splitController.showDrivers(selected);
         }

         @Override
         public void aliasesChanged(boolean selected)
         {
            splitController.showAliases(selected);
         }
      };

      DockButtonsCtrl dockButtonsCtrl = new DockButtonsCtrl(dockButtonsListener);
      Node dockButtons = dockButtonsCtrl.getNode();

      borderPane.setLeft(dockButtons);

      primaryStage.setX(pref.getDouble(PREF_MAIN_WIN_X, 0d));
      primaryStage.setY(pref.getDouble(PREF_MAIN_WIN_Y, 0d));
      primaryStage.setWidth(pref.getDouble(PREF_MAIN_WIN_WIDTH, 500d));
      primaryStage.setHeight(pref.getDouble(PREF_MAIN_WIN_HEIGHT, 500d));

      primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>()
      {
         @Override
         public void handle(WindowEvent windowEvent)
         {
            onClose();
         }
      });

      primaryStage.show();

   }

   private void onClose()
   {
      pref.set(PREF_MAIN_WIN_X, _primaryStage.getX());
      pref.set(PREF_MAIN_WIN_Y, _primaryStage.getY());
      pref.set(PREF_MAIN_WIN_WIDTH, _primaryStage.getWidth());
      pref.set(PREF_MAIN_WIN_HEIGHT, _primaryStage.getHeight());
   }


   public static void main(String[] args)
   {
      launch(args);
   }
}
