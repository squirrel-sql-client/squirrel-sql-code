package org.squirrelsql.session;

import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import org.squirrelsql.Props;

public class SessionTabHeaderCtrl
{
   private final Button _btnFileInfo;
   private final BorderPane _node;

   public SessionTabHeaderCtrl(SessionTabContext tabContext)
   {
      this(tabContext, null);
   }


   public SessionTabHeaderCtrl(SessionTabContext tabContext, ImageView icon)
   {
      String title = SessionUtil.getSessionTabTitle(tabContext);

      _node = new BorderPane();
      Label label = new Label(title);

      if(null != icon)
      {
         label.setGraphic(icon);
      }

      _node.setCenter(label);

      _btnFileInfo = new Button(null, null);
      _btnFileInfo.setText(null);
      _btnFileInfo.setPadding(new Insets(0, 2, 0, 2));
      _node.setRight(_btnFileInfo);
      BorderPane.setMargin(_btnFileInfo, new Insets(3));

      setFileState(FileState.NO_FILE);


   }


   public BorderPane getTabHeader()
   {
      return _node;
   }

   public void setFileState(FileState fileState)
   {
      switch (fileState)
      {
         case NO_FILE:
            _btnFileInfo.setGraphic(null);
            _btnFileInfo.setVisible(false);
            break;

         case CLEAN:
            ImageView imgClean = new ImageView(new Props(getClass()).getImage("smallFile.gif"));
            _btnFileInfo.setGraphic(imgClean);
            _btnFileInfo.setVisible(true);
            break;

         case CHANGED:
            ImageView imgChanged = new ImageView(new Props(getClass()).getImage("smallFileChanged.gif"));
            _btnFileInfo.setGraphic(imgChanged);
            _btnFileInfo.setVisible(true);
            break;
      }
   }
}
