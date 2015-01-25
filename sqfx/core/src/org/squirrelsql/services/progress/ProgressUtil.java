package org.squirrelsql.services.progress;

import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.scene.Node;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.squirrelsql.AppState;
import org.squirrelsql.services.CancelableProgressTask;

public class ProgressUtil
{
   public static <T> void  start(final ProgressTask<T> pt)
   {
      start(pt, null);
   }

   public static <T> void  start(final ProgressTask<T> pt, Stage stage)
   {
      final Task<T> concurrentTask = new Task<T>()
      {
         @Override
         protected T call() throws Exception
         {
            return pt.call();
         }
      };

      Service<T> service = new Service<T>()
      {
         @Override
         protected Task<T> createTask()
         {
            return concurrentTask;
         }
      };

      service.setOnSucceeded(workerStateEvent -> onSucceeded(service, pt, concurrentTask));

      service.setOnFailed(workerStateEvent -> onFailed(service, concurrentTask));

      service.setOnCancelled(workerStateEvent -> onCanceled(service, (CancelableProgressTask) pt));

      boolean isCancelableTask = pt instanceof CancelableProgressTask;


      if (null != stage)
      {
         showProgressStage(stage, service, isCancelableTask);
      }

      AppState.get().getRunningServicesManager().registerService(service);

      service.start();

   }

   private static <T> void onCanceled(Service service, CancelableProgressTask pt)
   {
      AppState.get().getRunningServicesManager().unRegisterService(service);
      ((CancelableProgressTask)pt).cancel();
   }

   private static <T> void onSucceeded(Service service, ProgressTask<T> pt, Task<T> concurrentTask)
   {
      AppState.get().getRunningServicesManager().unRegisterService(service);
      pt.goOn(concurrentTask.getValue());
   }

   private static <T> void showProgressStage(Stage stage, Service<T> service, boolean isCancelableTask)
   {
      if(false == stage.getScene().getRoot() instanceof StackPane)
      {
         throw new IllegalArgumentException("The stage parameter is not progressible. Use ProgressUtil.makeProgressible()");
      }

      StackPane sp = (StackPane) stage.getScene().getRoot();


      ProgressIndicator progressIndicator = null;
      ProgressRegion veil = null;
      Node applicationNode = null;

      for (Node node : sp.getChildren())
      {
         if(node instanceof  ProgressIndicator)
         {
            progressIndicator = (ProgressIndicator) node;
         }
         else if(node instanceof ProgressRegion)
         {
            veil = (ProgressRegion) node;

            if(veil.isCancelable())
            {
               if(false == isCancelableTask)
               {
                  throw new IllegalArgumentException("The progress window is cancelable. Please provide a CancelableProgressTask.");
               }

               veil.getCancelButton().setOnAction(e -> service.cancel());
            }
         }
         else
         {
            if(null == applicationNode)
            {
               applicationNode = node;
            }
            else
            {
               throw new IllegalArgumentException("More than one node in StackPane. Use ProgressUtil.makeProgressible()");
            }
         }
      }

      if(null == veil || null == progressIndicator)
      {
         throw new IllegalArgumentException("The stage parameter is not progressible. Use ProgressUtil.makeProgressible()");
      }

      veil.visibleProperty().unbind();
      progressIndicator.visibleProperty().unbind();

      veil.visibleProperty().bind(service.runningProperty());
      progressIndicator.visibleProperty().bind(service.runningProperty());
   }

   private static <T> void onFailed(Service service, Task<T> concurrentTask)
   {
      AppState.get().getRunningServicesManager().unRegisterService(service);
      Throwable t = new RuntimeException("Progress task execution failed");

      try
      {
         concurrentTask.get();
      }
      catch (Throwable tOrig)
      {
         t = tOrig;
      }

      throw new RuntimeException(t);
   }

   public static ProgressibleStage makeProgressible(Stage stage, boolean cancelable)
   {
      return new ProgressibleStage(stage, cancelable);
   }

}