package org.squirrelsql;

import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

import java.util.Timer;
import java.util.TimerTask;


public class StatusBarCtrl
{
   private I18n _i18n = new I18n(this.getClass());
   private Props _props = new Props(this.getClass());


   private final ImageView _iconMessage = new ImageView(_props.getImage("message.png"));
   private final ImageView _iconInfo = new ImageView(_props.getImage("information.png"));
   private final ImageView _iconWarning = new ImageView(_props.getImage("warning.png"));
   private final ImageView _iconError = new ImageView(_props.getImage("error.png"));

   private final TextField _txtMessages = new TextField();

   private int _countInfo = 0;
   private int _countWarning = 0;
   private int _countError = 0;
   private final Button _msgButton = new Button();
   private Timer _curTimer;
   private ImageView _lastCountedIcon;

   public Node getNode()
   {
      HBox hBox = new HBox();

      hBox.setAlignment(Pos.CENTER_RIGHT);
      _txtMessages.setPrefColumnCount(30);

      _txtMessages.setEditable(false);

      hBox.getChildren().add(_txtMessages);

      _msgButton.setGraphic(_iconMessage);
      hBox.getChildren().add(_msgButton);


      _msgButton.addEventHandler(MouseEvent.MOUSE_ENTERED,
            new EventHandler<MouseEvent>()
            {
               @Override
               public void handle(MouseEvent e)
               {
                  if (null != _lastCountedIcon)
                  {
                     _msgButton.setGraphic(_lastCountedIcon);
                  }
               }
            });

      _msgButton.addEventHandler(MouseEvent.MOUSE_EXITED,
            new EventHandler<MouseEvent>()
            {
               @Override
               public void handle(MouseEvent e)
               {
                  _msgButton.setGraphic(_iconMessage);
               }
            });


      refreshMessage(_iconMessage);

      return hBox;
   }

   private void refreshMessage(ImageView icon)
   {
      _txtMessages.setText(_i18n.t("statusbar.msg.status", _countError, _countWarning, _countInfo));
      _msgButton.setGraphic(icon);


      if(null != _curTimer)
      {
         _curTimer.cancel();
         _curTimer.purge();
         _curTimer = null;
      }

      _curTimer = new Timer();
      if(icon != _iconMessage)
      {
         _lastCountedIcon = icon;
         TimerTask timerTask = new TimerTask()
         {
            @Override
            public void run()
            {
               resetState(_curTimer);
            }
         };
         _curTimer.schedule(timerTask, 3000);

      }
   }

   private void resetState(Timer timer)
   {
      timer.cancel();
      timer.purge();

      Runnable runnable = new Runnable()
      {
         public void run()
         {
            refreshMessage(_iconMessage);
         }
      };

      Platform.runLater(runnable);
   }

   public void error(Throwable t)
   {
      ++_countError;
      refreshMessage(_iconError);
   }

   public void warning(String s)
   {
      ++_countWarning;
      refreshMessage(_iconWarning);
   }

   public void info(String s)
   {
      ++_countInfo;
      refreshMessage(_iconInfo);
   }
}
