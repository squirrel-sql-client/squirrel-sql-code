package org.squirrelsql;

import javafx.geometry.Orientation;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextArea;

public class SplitController
{
   private SplitPane _spltVert;

   public SplitController()
   {
      SplitPane spltHoriz = new SplitPane();
      spltHoriz.setOrientation(Orientation.HORIZONTAL);

      _spltVert = new SplitPane();
      _spltVert.setOrientation(Orientation.VERTICAL);

      spltHoriz.getItems().add(new TextArea("Alias/Drivers"));
      spltHoriz.getItems().add(new TextArea("Session"));
      _spltVert.getItems().add(spltHoriz);

      _spltVert.getItems().add(new TextArea("Message"));

   }

   public void showDrivers(boolean selected)
   {
      //To change body of created methods use File | Settings | File Templates.
   }

   public void showAliases(boolean selected)
   {
      //To change body of created methods use File | Settings | File Templates.
   }

   public Node getNode()
   {
      return _spltVert;  //To change body of created methods use File | Settings | File Templates.
   }
}
