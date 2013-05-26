package org.squirrelsql;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Pref;

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
   private SplitController _splitController;

   @Override
   public void start(Stage primaryStage) throws Exception
   {
      _primaryStage = primaryStage;

      primaryStage.setTitle(i18n.t("mainWin.title") + " " + VERSION);

      DockPaneChanel dockPaneChanel = new DockPaneChanel();

      _splitController = new SplitController(dockPaneChanel);



      BorderPane borderPane = new BorderPane();
      primaryStage.setScene(new Scene(borderPane));

      borderPane.setCenter(_splitController.getNode());



      DockButtonsCtrl dockButtonsCtrl = new DockButtonsCtrl(dockPaneChanel);
      Node dockButtons = dockButtonsCtrl.getNode();

      borderPane.setLeft(dockButtons);

      borderPane.setBottom(AppState.get().getStatusBarCtrl().getNode());

      primaryStage.setX(pref.getDouble(PREF_MAIN_WIN_X, 0d));
      primaryStage.setY(pref.getDouble(PREF_MAIN_WIN_Y, 0d));
      primaryStage.setWidth(pref.getDouble(PREF_MAIN_WIN_WIDTH, 500d));
      primaryStage.setHeight(pref.getDouble(PREF_MAIN_WIN_HEIGHT, 500d));
      adjustMessageSplit();



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

   private void adjustMessageSplit()
   {
      Runnable runnable = new Runnable()
      {
         public void run()
         {
            _splitController.adjustMessageSplit();
         }
      };

      Platform.runLater(runnable);
   }

   private void onClose()
   {
      _splitController.close();
      pref.set(PREF_MAIN_WIN_X, _primaryStage.getX());
      pref.set(PREF_MAIN_WIN_Y, _primaryStage.getY());
      pref.set(PREF_MAIN_WIN_WIDTH, _primaryStage.getWidth());
      pref.set(PREF_MAIN_WIN_HEIGHT, _primaryStage.getHeight());

      Platform.exit();
      System.exit(0);
   }


   public static void main(String[] args)
   {
      ExceptionHandler.initHandling();
      launch(args);
   }
}
