package org.squirrelsql.session.sql.searchchandreplace;

import javafx.scene.layout.BorderPane;
import org.squirrelsql.services.EditableComboCtrl;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.session.action.StdActionCfg;
import org.squirrelsql.session.sql.SQLTextAreaServices;

public class ReplaceCtrl
{
   private BorderPane _borderPane;
   private SQLTextAreaServices _sqlTextAreaServices;
   private EditableComboCtrl _editableComboCtrl;
   private ReplaceView _view;
   private SearchCtrl _searchCtrl;

   public ReplaceCtrl(BorderPane borderPane, SQLTextAreaServices sqlTextAreaServices)
   {
      _borderPane = borderPane;
      _sqlTextAreaServices = sqlTextAreaServices;
      StdActionCfg.REPLACE_IN_TEXT.setAction(this::onOpenReplace);

   }

   private void onOpenReplace()
   {
      FxmlHelper<ReplaceView> fxmlHelper = new FxmlHelper<>(ReplaceView.class);

      _view = fxmlHelper.getView();
      _searchCtrl = SearchCtrl.create(_borderPane, _sqlTextAreaServices, _view.searchViewController);

      _editableComboCtrl = new EditableComboCtrl(_view.cboReplaceText, getClass().getName(), null);

      _view.btnReplace.setOnAction(e -> onReplace(false));
      _view.btnExclude.setOnAction(e -> _searchCtrl.findNext());
      _view.btnReplaceAll.setOnAction(e -> onReplaceAll());

      _borderPane.setTop(fxmlHelper.getRegion());
   }

   private void onReplaceAll()
   {
      onReplace(false);

      while(onReplace(true))
         ;

   }

   private boolean onReplace(boolean adjustStartPos)
   {
      if(false == _searchCtrl.isFoundPositionSelected())
      {
         _searchCtrl.findNext();
         return false;
      }

      _sqlTextAreaServices.replaceSelection(_editableComboCtrl.getText(), false);

      if(adjustStartPos && _sqlTextAreaServices.getTextArea().getSelectedText().length() < _editableComboCtrl.getText().length())
      {
         // This is to prevent endless loops when onReplaceAll() is executed and a string like '12' is replaced by '1212'
         _searchCtrl.increaseNextStartPosBy(_editableComboCtrl.getText().length() - _sqlTextAreaServices.getTextArea().getSelectedText().length());
      }
      _searchCtrl.findNext();
      return false == _searchCtrl.isEOFReached();
   }


}
