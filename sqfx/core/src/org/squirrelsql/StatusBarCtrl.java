package org.squirrelsql;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.ImageView;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import org.squirrelsql.globalicons.GlobalIconNames;
import org.squirrelsql.services.Dao;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.progress.TextProgressBar;
import org.squirrelsql.services.Utils;

import java.util.Timer;
import java.util.TimerTask;


public class StatusBarCtrl
{
   private I18n _i18n = new I18n(this.getClass());
   private Props _props = new Props(this.getClass());


   private final ImageView _iconMessage = new ImageView(_props.getImage("message.png"));
   private final ImageView _iconInfo = new ImageView(_props.getImage(GlobalIconNames.INFORMATION));
   private final ImageView _iconWarning = new ImageView(_props.getImage(GlobalIconNames.WARNING));
   private final ImageView _iconGc = new ImageView(_props.getImage("trash.png"));

   private final ImageView _iconError = new ImageView(_props.getImage(GlobalIconNames.ERROR));

   private final TextField _txtMessages = new TextField();

   private int _countInfo = 0;
   private int _countWarning = 0;
   private int _countError = 0;
   private final Button _msgButton = new Button();
   private Timer _curTimer;
   private ImageView _lastCountedIcon;
   
   private Long _totalMemory;
   private Long _freeMemory;
   private Long _usedMemory;
   private final static Double _refreshTime = 1.0;
   private static final int _megaBytes = 1024 * 1024;
   private static final TextProgressBar _memoryBar = new TextProgressBar();
   private final Button _garbageCollectButton = new Button();   

   public Node getNode()
   {
      HBox hBox = new HBox();

      hBox.setAlignment(Pos.CENTER_RIGHT);
      
      updateMemory();
      Timeline memoryUpdater = new Timeline(new KeyFrame(Duration.seconds(_refreshTime), new EventHandler<ActionEvent>() {
    	  @Override
    	  public void handle(ActionEvent event) {
    		  updateMemory();
    	  }
      }));
      memoryUpdater.setCycleCount(Timeline.INDEFINITE);
      memoryUpdater.play();      
      hBox.getChildren().add(_memoryBar);
      
      _garbageCollectButton.setTooltip(new Tooltip(_i18n.t("statusbar.gc.tooltip")));
      _garbageCollectButton.setGraphic(_iconGc);
      _garbageCollectButton.addEventHandler(MouseEvent.MOUSE_CLICKED, e -> Utils.gc());
      hBox.getChildren().add(_garbageCollectButton);
      HBox.setMargin(_garbageCollectButton, new Insets(0,10,0,0));

      _txtMessages.setPrefColumnCount(30);

      _txtMessages.setEditable(false);

      hBox.getChildren().add(_txtMessages);

      _msgButton.setGraphic(_iconMessage);
      _msgButton.setTooltip(new Tooltip(_i18n.t("statusbar.button.tooltip")));

      hBox.getChildren().add(_msgButton);


      _msgButton.addEventHandler(MouseEvent.MOUSE_ENTERED, e -> onMouseEntered());

      _msgButton.addEventHandler(MouseEvent.MOUSE_EXITED, e -> _msgButton.setGraphic(_iconMessage));

      _msgButton.setOnAction(e -> onShowLogs());


      refreshMessage(_iconMessage);

      return hBox;
   }

   private void onShowLogs()
   {
      new ShowLogsController();
   }

   private void onMouseEntered()
   {
      if (null != _lastCountedIcon)
      {
         _msgButton.setGraphic(_lastCountedIcon);
      }
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

      Platform.runLater(() -> refreshMessage(_iconMessage));
   }

   public void warning(String s, Throwable t)
   {
      Dao.log("warning", s, t);

      ++_countWarning;
      refreshMessage(_iconWarning);
   }

   public void info(String s)
   {
      Dao.log("info", s, null);


      ++_countInfo;
      refreshMessage(_iconInfo);
   }

   public void error(Throwable t)
   {
      error(null, t);
   }

   public void error(String s)
   {
      error(s, null);
   }

   public void error(String s, Throwable t)
   {

      Dao.log("error", s, t);

      ++_countError;
      refreshMessage(_iconError);
   }
   private void updateMemory()
   {
	  _totalMemory = Runtime.getRuntime().totalMemory() / _megaBytes;
	  _freeMemory = Runtime.getRuntime().freeMemory() / _megaBytes;
	  _usedMemory = _totalMemory - _freeMemory;
	  _memoryBar.setText(_usedMemory + " MB of " + _totalMemory + " MB");
	  _memoryBar.setBarHeight(25);
	  _memoryBar.setBarWidth(200);
	  _memoryBar.getProgressBar().setProgress((double)_usedMemory/_totalMemory);	  
	}   
}
