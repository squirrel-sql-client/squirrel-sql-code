package org.squirrelsql.services;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Region;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.squirrelsql.AppState;

public class GuiUtils
{
   public static final String STYLE_GROUP_BORDER = "-fx-border-width: 2; -fx-border-color: darkgray lightgray lightgray darkgray;";

   public static void makeEscapeClosable(Node sceneRoot)
   {
      sceneRoot.setOnKeyPressed(new EventHandler<KeyEvent>()
      {
         public void handle(KeyEvent ke)
         {
            if (ke.getCode() == KeyCode.ESCAPE)
            {
               ((Stage) sceneRoot.getScene().getWindow()).close();
            }
         }
      });
   }

   public static void centerWithinParent(Stage stage)
   {
      Window owner = stage.getOwner();

      if(null == owner)
      {
         throw new IllegalArgumentException("Owner must not be null");
      }

      if(false == stage.isShowing())
      {
         throw new IllegalArgumentException("Stage must be showing to be positioned");
      }

      stage.setX(owner.getX() + owner.getWidth() / 2 - stage.getScene().getWidth() / 2);
      stage.setY(owner.getY() + owner.getHeight() / 2 - stage.getScene().getHeight() / 2);
      stage.show();


   }

   public static Stage createModalDialog(Region region, Pref pref, double initialWidth, double initialHeight, String prefPrefix)
   {
      return _createDialog(region, pref, initialWidth, initialHeight, prefPrefix, Modality.WINDOW_MODAL);
   }

   public static Stage createNonModalDialog(Region region, Pref pref, double initialWidth, double initialHeight, String prefPrefix)
   {
      return _createDialog(region, pref, initialWidth, initialHeight, prefPrefix, Modality.NONE);
   }

   private static Stage _createDialog(Region region, Pref pref, double initialWidth, double initialHeight, String prefPrefix, Modality modality)
   {
      Stage ret = createWindow(region);

      ret.initModality(modality);

      makeEscapeClosable(region);

      new StageDimensionSaver(prefPrefix, ret, pref, initialWidth, initialHeight, AppState.get().getPrimaryStage());

      return ret;
   }

   public static Stage createWindow(Region region)
   {
      Stage ret = new Stage();

      ret.setScene(new Scene(region));

      ret.initOwner(AppState.get().getPrimaryStage());

      return ret;
   }

   public static void executeOnEDT(Runnable runnable)
   {
      if(Platform.isFxApplicationThread())
      {
         runnable.run();
      }
      else
      {
         Platform.runLater(runnable);
      }
   }


}
