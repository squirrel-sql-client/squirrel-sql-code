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
import org.squirrelsql.session.action.ActionUtil;
import org.squirrelsql.settings.SettingsDialogController;
import org.squirrelsql.splash.SquirrelSplashScreen;

public class Main extends Application
{

   private I18n i18n = new I18n(getClass());
   private Pref pref = new Pref(getClass());

   private SplitController _splitController;

   @Override
   public void start(Stage primaryStage) throws Exception
   {

      SquirrelSplashScreen squirrelSplashScreen = new SquirrelSplashScreen(5);

      squirrelSplashScreen.indicateNewTask("Initializing exception handling ...");
      ExceptionHandler.initHandling();


      AppState.init(primaryStage, getParameters());

      primaryStage.setTitle(i18n.t("mainWin.title"));

      DockPaneChanel dockPaneChanel = new DockPaneChanel();

      _splitController = new SplitController(dockPaneChanel);



      squirrelSplashScreen.indicateNewTask("Creating application window ...");
      BorderPane borderPane = new BorderPane();
      primaryStage.setScene(new Scene(borderPane));

      borderPane.setCenter(_splitController.getNode());



      squirrelSplashScreen.indicateNewTask("Loading drivers and aliases ...");
      DockButtonsCtrl dockButtonsCtrl = new DockButtonsCtrl(dockPaneChanel);
      Node dockButtons = dockButtonsCtrl.getNode();


      borderPane.setLeft(dockButtons);

      borderPane.setBottom(AppState.get().getStatusBarCtrl().getNode());

      borderPane.setTop(createMenuBar(primaryStage));

      squirrelSplashScreen.indicateNewTask("Configuring application window ...");


      final StageDimensionSaver dimensionSaver = new StageDimensionSaver("main", primaryStage, pref, 1000d, 800d, null);

      adjustMessageSplit();



      primaryStage.setOnCloseRequest(windowEvent -> onClose(dimensionSaver));


      primaryStage.getIcons().add(new Props(getClass()).getImage("acorn.png"));


      squirrelSplashScreen.indicateNewTask("Opening application window ...");

      primaryStage.show();

      Platform.runLater(() -> AppState.get().doAfterBootstrap());
      Platform.runLater(() -> primaryStage.toFront());
      squirrelSplashScreen.close();

   }

   private MenuBar createMenuBar(Stage primaryStage)
   {
      MenuBar ret = new MenuBar();

      Menu file = new Menu(i18n.t("main.menu.file"));
      ret.getMenus().add(file);

      MenuItem showLogs = new MenuItem(i18n.t("main.menu.show.logs"));
      file.getItems().add(showLogs);
      showLogs.setOnAction(e -> new ShowLogsController());

      MenuItem showSettings = new MenuItem(i18n.t("main.menu.show.settings"));
      file.getItems().add(showSettings);
      showSettings.setOnAction(e -> new SettingsDialogController());

      MenuItem exit = new MenuItem(i18n.t("main.menu.exit"));
      file.getItems().add(exit);
      exit.setOnAction(e -> onExit(primaryStage));


      ret.getMenus().add(ActionUtil.getSessionMenu());


      Menu help = new Menu(i18n.t("main.menu.help"));
      ret.getMenus().add(help);

      MenuItem about = new MenuItem(i18n.t("main.menu.about"));
      help.getItems().add(about);
      about.setOnAction(e -> new AboutController());

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
      AppState.get().getPrefImpl().flush();

      Platform.exit();
      System.exit(0);
   }


   public static void main(String[] args)
   {
      launch(args);
   }
}
