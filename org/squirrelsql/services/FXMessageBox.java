package org.squirrelsql.services;


import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class FXMessageBox
{


   public enum Icon
   {
      ICON_ERROR,
      ICON_WARNING,
      ICON_INFORMATION,
      ICON_QUESTION
   }


   public static final String OK = new I18n(FXMessageBox.class).t("FXMessageBox.OK");
   public static final String YES =new I18n(FXMessageBox.class).t("FXMessageBox.YES");
   public static final String NO = new I18n(FXMessageBox.class).t("FXMessageBox.NO");
   public static final String ABORT = new I18n(FXMessageBox.class).t("FXMessageBox.ABORT");
   public static final String RETRY = new I18n(FXMessageBox.class).t("FXMessageBox.RETRY");
   public static final String IGNORE = new I18n(FXMessageBox.class).t("FXMessageBox.IGNORE");
   public static final String CANCEL = new I18n(FXMessageBox.class).t("FXMessageBox.CANCEL");

   public static final String TITLE_TEXT_INFORMATION = new I18n(FXMessageBox.class).t("FXMessageBox.Information");



   public static void showInfoOk(Window parent, String msg)
   {
      showMessageBox(parent, Icon.ICON_INFORMATION, TITLE_TEXT_INFORMATION, msg, 0, OK);
   }

   public static String showYesNo(Window parent, String msg)
   {
      return showMessageBox(parent, Icon.ICON_QUESTION, new I18n(FXMessageBox.class).t("FXMessageBox.Question"), msg, 0, YES, NO);
   }

   public static String showMessageBox(Window parent, Icon icon, String title, String msg, Integer defaultOptionIndex, final String... options)
   {

      final Stage dialog = new Stage(StageStyle.UTILITY);
      dialog.setTitle(title);
      dialog.setResizable(false);
      dialog.initModality(Modality.WINDOW_MODAL);
      if (parent != null)
      {
         // Only set in case of not null.
         dialog.initOwner(parent);
      }

      final VBox totalPane = new VBox();
      dialog.setScene(new Scene(totalPane));
      totalPane.setAlignment(Pos.CENTER);
      totalPane.setSpacing(2);

      final HBox pane = new HBox();
      totalPane.getChildren().add(pane);

      pane.setSpacing(10);

      // Pad left space.
      pane.getChildren().add(new Label("")); //$NON-NLS-1$
      pane.getChildren().add(new Label("")); //$NON-NLS-1$

      VBox vbox;
      vbox = new VBox();
      pane.getChildren().add(vbox);
      vbox.setAlignment(Pos.CENTER);

      if (null != icon)
      {
         if (Icon.ICON_ERROR == icon)
         {
            final Group group = MessageIconBuilder.drawErrorIcon(3);
            vbox.getChildren().add(group);
         }
         else if (Icon.ICON_WARNING == icon)
         {
            final Group group = MessageIconBuilder.drawWarningIcon(3);
            vbox.getChildren().add(group);
         }
         else if (Icon.ICON_INFORMATION == icon)
         {
            final Group group = MessageIconBuilder.drawInformationIcon(3);
            vbox.getChildren().add(group);
         }
         else if (Icon.ICON_QUESTION == icon)
         {
            final Group group = MessageIconBuilder.drawQuestionIcon(3);
            vbox.getChildren().add(group);
         }
      }

      vbox = new VBox();
      pane.getChildren().add(vbox);

      vbox.setAlignment(Pos.CENTER);

      vbox.getChildren().add(new Label(""));//$NON-NLS-1$
      vbox.getChildren().add(new Label(msg));

      // Pad right space.
      pane.getChildren().add(new Label("")); //$NON-NLS-1$
      pane.getChildren().add(new Label("")); //$NON-NLS-1$

      // Pad message and buttons.
      vbox.getChildren().add(new Label("")); //$NON-NLS-1$
      vbox.getChildren().add(new Label("")); //$NON-NLS-1$

      boolean isButtonExists = false;


      final HBox hboxButtons = new HBox();
      totalPane.getChildren().add(hboxButtons);
      hboxButtons.setSpacing(10);
      hboxButtons.setAlignment(Pos.CENTER);
      hboxButtons.getChildren().add(new Label("")); //$NON-NLS-1$

      final String result[] = new String[1];
      for (int i = 0; i < options.length; i++)
      {
         final Button btn = new Button();
         btn.setText(options[i]);
         final int finalIndex = i;
         btn.setOnAction(new EventHandler<ActionEvent>()
         {
            @Override
            public void handle(ActionEvent e)
            {
               result[0] = options[finalIndex];
               dialog.close();
            }
         });
         hboxButtons.getChildren().add(btn);

         if (null != defaultOptionIndex && defaultOptionIndex.equals(i))
         {
            btn.setDefaultButton(true);
         }
      }


      hboxButtons.getChildren().add(new Label("")); //$NON-NLS-1$

      totalPane.getChildren().add(new Label(""));//$NON-NLS-1$

      // Below method is supported JavaFX 2.2 or lator.
      dialog.showAndWait();

      return result[0];
   }


}
