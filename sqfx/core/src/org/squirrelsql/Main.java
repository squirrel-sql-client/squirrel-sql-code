package org.squirrelsql;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Pref;
import org.squirrelsql.services.StageDimensionSaver;
import org.squirrelsql.session.action.ActionManager;

public class Main extends Application
{

   private I18n i18n = new I18n(getClass());
   private Pref pref = new Pref(getClass());

   public static final String VERSION = "4fx";
   private SplitController _splitController;

   @Override
   public void start(Stage primaryStage) throws Exception
   {
      ExceptionHandler.initHandling();

      AppState.init(primaryStage, getParameters());

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

      borderPane.setTop(createMenuBar(primaryStage));


      final StageDimensionSaver dimensionSaver = new StageDimensionSaver("main", primaryStage, pref, 500d, 500d, null);

      adjustMessageSplit();



      primaryStage.setOnCloseRequest(windowEvent -> onClose(dimensionSaver));


      primaryStage.getIcons().add(new Props(getClass()).getImage("acorn.png"));

      primaryStage.show();

      Platform.runLater(() -> AppState.get().doAfterBootstrap());
   }

   private MenuBar createMenuBar(Stage primaryStage)
   {
      MenuBar ret = new MenuBar();

      Menu file = new Menu(i18n.t("main.menu.file"));
      ret.getMenus().add(file);

      MenuItem exit = new MenuItem(i18n.t("main.menu.exit"));
      file.getItems().add(exit);


      exit.setOnAction(e -> onExit(primaryStage));

      ret.getMenus().add(new ActionManager().getSessionMenu());


      return ret;
   }

   private void onExit(Stage primaryStage)
   {
      primaryStage.fireEvent(new WindowEvent(primaryStage, WindowEvent.WINDOW_CLOSE_REQUEST));
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

   private void onClose(StageDimensionSaver dimesionSaver)
   {
      AppState.get().fireApplicationClosing();
      _splitController.close();
      dimesionSaver.save(); // Needed because we are going to exit
      Platform.exit();
      System.exit(0);
   }


   public static void main(String[] args)
   {
      launch(args);
   }
}
