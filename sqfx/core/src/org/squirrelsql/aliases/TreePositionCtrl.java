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

public class TreePositionCtrl
{
   private final TreePositionView _treePositionView;
   private I18n _i18n = new I18n(this.getClass());


   public TreePositionCtrl(TreePositionView treePositionView, boolean parentNodeSelected, boolean parentAllowsChildren)
   {
      _treePositionView = treePositionView;

      ToggleGroup toggleGroup = new ToggleGroup();

      toggleGroup.getToggles().addAll
         (
            _treePositionView.radToRoot,
            _treePositionView.radToSelectedAsChild,
            _treePositionView.radToSelectedAsAncestor,
            _treePositionView.radToSelectedAsSuccessor
         );

      if(parentNodeSelected)
      {
         if (parentAllowsChildren)
         {
            _treePositionView.radToSelectedAsChild.setSelected(true);
         }
         else
         {
            _treePositionView.radToSelectedAsChild.setDisable(true);
            _treePositionView.radToSelectedAsSuccessor.setSelected(true);
         }
      }
      else
      {
         _treePositionView.radToRoot.setSelected(true);

         _treePositionView.radToRoot.setDisable(true);
         _treePositionView.radToSelectedAsChild.setDisable(true);
         _treePositionView.radToSelectedAsAncestor.setDisable(true);
         _treePositionView.radToSelectedAsSuccessor.setDisable(true);
      }
   }

   public RelativeNodePosition getRelativeNodePosition()
   {
      if(_treePositionView.radToRoot.isSelected())
      {
         return RelativeNodePosition.ROOT;
      }
      else if(_treePositionView.radToSelectedAsChild.isSelected())
      {
         return RelativeNodePosition.CHILD;
      }
      else if(_treePositionView.radToSelectedAsAncestor.isSelected())
      {
         return RelativeNodePosition.UPPER_SIBLING;
      }
      else if(_treePositionView.radToSelectedAsSuccessor.isSelected())
      {
         return RelativeNodePosition.LOWER_SIBLING;
      }
      else
      {
         throw new IllegalStateException("You may ask yourself: How did I get here");
      }


   }
}
