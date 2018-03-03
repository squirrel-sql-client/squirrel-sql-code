package org.squirrelsql.services;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import javafx.scene.control.TextArea;
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

//      if(false == stage.isShowing())
//      {
//         throw new IllegalArgumentException("Stage must be showing to be positioned");
//      }

      double stageWidth = stage.getScene().getWidth();
      double stageHeight = stage.getScene().getHeight();

      centerWithinParent(stage, stageWidth, stageHeight);
   }

   public static void centerWithinParent(Stage stage, double stageWidth, double stageHeight)
   {
      Window owner = stage.getOwner();

      if(null == owner)
      {
         throw new IllegalArgumentException("Owner must not be null");
      }

      stage.setX(owner.getX() + owner.getWidth() / 2 - stageWidth / 2);
      stage.setY(owner.getY() + owner.getHeight() / 2 - stageHeight / 2);
   }

   public static Stage createFixedSizeModalDialog(Region region, Pref pref, double initialWidth, double initialHeight, String prefPrefix)
   {
      return _createDialog(region, pref, initialWidth, initialHeight, prefPrefix, Modality.WINDOW_MODAL, AppState.get().getPrimaryStage(), false);
   }

   public static Stage createModalDialog(Region region, Pref pref, double initialWidth, double initialHeight, String prefPrefix)
   {
      return _createDialog(region, pref, initialWidth, initialHeight, prefPrefix, Modality.WINDOW_MODAL, AppState.get().getPrimaryStage(), true);
   }

   public static Stage createModalDialog(Region region, Pref pref, double initialWidth, double initialHeight, String prefPrefix, Stage owner)
   {
      return _createDialog(region, pref, initialWidth, initialHeight, prefPrefix, Modality.WINDOW_MODAL, owner, true);
   }

   public static Stage createNonModalDialog(Region region, Pref pref, double initialWidth, double initialHeight, String prefPrefix)
   {
      return _createDialog(region, pref, initialWidth, initialHeight, prefPrefix, Modality.NONE, AppState.get().getPrimaryStage(), true);
   }

   private static Stage _createDialog(Region region, Pref pref, double initialWidth, double initialHeight, String prefPrefix, Modality modality, Stage owner, boolean restoreStageDimension)
   {
      Stage ret = createWindow(region, owner);

      ret.initModality(modality);

      makeEscapeClosable(region);

      if (restoreStageDimension)
      {
         new StageDimensionSaver(prefPrefix, ret, pref, initialWidth, initialHeight, owner);
      }
      else
      {
         ret.setWidth(initialWidth);
         ret.setHeight(initialHeight);
         centerWithinParent(ret, initialWidth, initialHeight);
      }

      return ret;
   }

   public static Stage createWindow(Region region, Stage owner)
   {
      Stage ret = new Stage();

      ret.setScene(new Scene(region));

      ret.initOwner(owner);

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


   public static void addContextMenuItemToStandardTextAreaMenu(final TextArea textArea, MenuItem menuItem)
   {
      if(null == textArea.getContextMenu())
      {
         textArea.setContextMenu(new ContextMenu());
      }


      textArea.getContextMenu().getItems().add(new SeparatorMenuItem());
      textArea.getContextMenu().getItems().add(menuItem);
   }
}
