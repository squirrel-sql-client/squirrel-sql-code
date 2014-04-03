package org.squirrelsql.services;

import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import javafx.stage.Window;

public class GuiUtils
{
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
}
