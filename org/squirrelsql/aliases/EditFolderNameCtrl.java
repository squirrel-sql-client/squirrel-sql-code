package org.squirrelsql.aliases;

import com.google.common.base.Strings;
import javafx.scene.Scene;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.squirrelsql.AppState;
import org.squirrelsql.services.FXMessageBox;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.GuiUtils;
import org.squirrelsql.services.I18n;

public class EditFolderNameCtrl
{
   private final EditFolderNameView _editFolderNameView;
   private I18n _i18n = new I18n(this.getClass());
   private final Stage _dialog;
   private String _newFolderName;


   public EditFolderNameCtrl(boolean parentNodeSelected, boolean allowsChildern)
   {
      FxmlHelper<EditFolderNameView> fxmlHelper = new FxmlHelper<>(EditFolderNameView.class);
      _editFolderNameView = fxmlHelper.getView();

      _editFolderNameView.btnOk.setOnAction(actionEvent -> onOk());

      _editFolderNameView.btnCancel.setOnAction(actionEvent -> onCancel());


      ToggleGroup toggleGroup = new ToggleGroup();

      toggleGroup.getToggles().addAll
         (
            _editFolderNameView.radToRoot,
            _editFolderNameView.radToSelectedAsChild,
            _editFolderNameView.radToSelectedAsAncestor,
            _editFolderNameView.radToSelectedAsSuccessor
         );

      if(parentNodeSelected)
      {
         if (allowsChildern)
         {
            _editFolderNameView.radToSelectedAsChild.setSelected(true);
         }
         else
         {
            _editFolderNameView.radToSelectedAsChild.setDisable(true);
            _editFolderNameView.radToSelectedAsSuccessor.setSelected(true);
         }
      }
      else
      {
         _editFolderNameView.radToRoot.setSelected(true);

         _editFolderNameView.radToRoot.setDisable(true);
         _editFolderNameView.radToSelectedAsChild.setDisable(true);
         _editFolderNameView.radToSelectedAsAncestor.setDisable(true);
         _editFolderNameView.radToSelectedAsSuccessor.setDisable(true);
      }


      _dialog = new Stage();
      _dialog.initModality(Modality.WINDOW_MODAL);
      _dialog.setTitle(_i18n.t("aliastree.edit.folder.name.title"));
      _dialog.initOwner(AppState.get().getPrimaryStage());
      _dialog.setScene(new Scene(fxmlHelper.getRegion()));
      GuiUtils.makeEscapeClosable(fxmlHelper.getRegion());

      _dialog.showAndWait();

   }

   private void onCancel()
   {
      _dialog.close();
   }

   private void onOk()
   {
      String newFolderName = _editFolderNameView.txtFolderName.getText();
      if(Strings.isNullOrEmpty(newFolderName))
      {
         FXMessageBox.showInfoOk(_dialog, _i18n.t("aliastree.edit.folder.please.enter"));
         return;
      }

      _newFolderName = newFolderName;

      _dialog.close();
   }

   public String getNewFolderName()
   {
      return _newFolderName;
   }


   public boolean isAddToRoot()
   {
      return _editFolderNameView.radToRoot.isSelected();
   }

   public boolean isAddToSelectedAsChild()
   {
      return _editFolderNameView.radToSelectedAsChild.isSelected();
   }

   public boolean isAddToSelectedAsAncestor()
   {
      return _editFolderNameView.radToSelectedAsAncestor.isSelected();
   }

   public boolean isAddToSelectedAsSuccessor()
   {
      return _editFolderNameView.radToSelectedAsSuccessor.isSelected();
   }


}
