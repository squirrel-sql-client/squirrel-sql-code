package org.squirrelsql.services.progress;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.scene.control.ProgressIndicator;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.squirrelsql.services.*;

public class SimpleProgressCtrl
{
   private final FxmlHelper<SimpleProgressView> _fxmlHelper;
   private final Progressible _progressible;
   private Stage _dialog;
   private boolean _closeOnFinishOrCancel;
   private I18n _i18n = new I18n(getClass());

   public SimpleProgressCtrl()
   {
      this(true, true);
   }
   public SimpleProgressCtrl(boolean cancelable, boolean closeOnFinishOrCancel)
   {
      _closeOnFinishOrCancel = closeOnFinishOrCancel;
      _fxmlHelper = new FxmlHelper<>(SimpleProgressView.class);
      _progressible = new Progressible();

      _fxmlHelper.getView().btnCancelClose.setOnAction(e -> cancelInBackground());

      if(false == cancelable)
      {
         _fxmlHelper.getView().btnCancelClose.setDisable(true);
      }

   }

   public Progressible getProgressible()
   {
      return _progressible;
   }

   public void start(ProgressTask progressTask)
   {
      _progressible.setProgressTask(progressTask);
      showDialogAndStartService();
   }

   public void start(Runnable runnable)
   {
      _progressible.setRunnable(runnable);
      showDialogAndStartService();
   }

   private void showDialogAndStartService()
   {
      _dialog = GuiUtils.createModalDialog(_fxmlHelper.getRegion(), new Pref(getClass()), 600, 400, "SimpleProgressCtrl");

      _dialog.setOnCloseRequest(e -> cancelInBackground());

      Platform.runLater(() -> _start());
      _dialog.showAndWait();
   }

   private void cancelInBackground()
   {
      new Thread(() -> doCancelInBackground()).start();
   }

   private void doCancelInBackground()
   {
      _progressible.cancel();
      _progressible.update(_i18n.t("simpleProgressView.RequestedCancel"));
   }


   private void _start()
   {
      Service service = new Service()
      {
         @Override
         protected Task createTask()
         {
            return _progressible;
         }
      };


      service.stateProperty().addListener(new ChangeListener<Worker.State>() {
         @Override
         public void changed(ObservableValue<? extends Worker.State> observableValue, Worker.State oldState, Worker.State newState){
            onServiceStateChanged(service, newState);
         }
      });

      service.start();
   }

   private void onServiceStateChanged(Service service, Worker.State newState)
   {
      switch(newState){
         case SUCCEEDED:
            onSucceded();
            break;
         case RUNNING:
            _fxmlHelper.getView().progressIndicator.progressProperty().bind(service.progressProperty());

            service.messageProperty().addListener(new ChangeListener<String>()
            {
               @Override
               public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue)
               {
                  onSetProgressMessage(newValue);
               }
            });

            break;
         case SCHEDULED:
            _fxmlHelper.getView().progressIndicator.setProgress(ProgressIndicator.INDETERMINATE_PROGRESS);
            break;
         case FAILED:
            onFailed();
            break;
         case CANCELLED:
            onCanceled();
            break;
         default:
            break;
      }

   }

   private void onSetProgressMessage(String newValue)
   {
      _fxmlHelper.getView().txtProgressMsg.setText(newValue + "\n"); // \n to make it really scroll to bottom
      _fxmlHelper.getView().txtProgressMsg.setScrollTop(Double.MAX_VALUE);
   }

   private void onCanceled()
   {
      updateControlsAfterFinish();
   }

   private void onFailed()
   {
      Throwable exception = _progressible.getException();
      _progressible.update(_i18n.t("simpleProgressView.Failed", exception.getMessage()));
      _progressible.update(_i18n.t("simpleProgressView.seeLogs"));
      new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_LOG).error(exception);
      updateControlsAfterFinish();
   }

   private void onSucceded()
   {
      _progressible.update("Finished");
      updateControlsAfterFinish();
   }

   private void updateControlsAfterFinish()
   {
      _fxmlHelper.getView().btnCancelClose.setOnAction((e) -> _dialog.close());
      _fxmlHelper.getView().btnCancelClose.setText(_i18n.t("simpleProgressView.close"));
      _fxmlHelper.getView().btnCancelClose.setDisable(false);


      if(_closeOnFinishOrCancel)
      {
         Timeline tl = new Timeline(new KeyFrame(new Duration(500), (e) -> _dialog.close()));
         tl.play();
      }
   }
}
