package org.squirrelsql;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class Main extends Application
{
   public static final String PREF_MAIN_WIN_WIDTH = "mainWin.width";
   public static final String PREF_MAIN_WIN_HEIGHT = "mainWin.height";
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

      VBox dockButtons = new VBox();

      borderPane.setLeft(dockButtons);

      dockButtons.getChildren().add(new VerticalToggleButton(i18n.t("dock.button.aliases")));
      dockButtons.getChildren().add(new VerticalToggleButton(i18n.t("dock.button.drivers")));

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
      pref.set(PREF_MAIN_WIN_WIDTH, _primaryStage.getWidth());
      pref.set(PREF_MAIN_WIN_HEIGHT, _primaryStage.getHeight());
   }


   public static void main(String[] args)
   {
      launch(args);
   }
}
