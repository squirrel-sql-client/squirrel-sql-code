package org.squirrelsql.workaround;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.SplitPane;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

public class SessionTabSelectionRepaintWA
{
   public SessionTabSelectionRepaintWA(TabPane sessionTabPane)
   {
      sessionTabPane.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Tab>()
      {
         @Override
         public void changed(ObservableValue<? extends Tab> observable, Tab oldValue, Tab newValue)
         {
            SplitPane newValueContent = (SplitPane) newValue.getContent();


//            newValue.setContent(newValue.getContent());
            Node lastItem = newValueContent.getItems().get(newValueContent.getItems().size() - 1);
            newValueContent.getItems().remove(lastItem);
            newValueContent.getItems().add(lastItem);
         }
      });

   }
}
