package org.squirrelsql.session.objecttree;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyEvent;
import org.squirrelsql.Props;
import org.squirrelsql.globalicons.GlobalIconNames;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.I18n;
import org.squirrelsql.session.Session;
import org.squirrelsql.session.action.StdActionCfg;
import org.squirrelsql.session.completion.CompletionCtrl;
import org.squirrelsql.session.completion.TextFieldTextComponentAdapter;
import org.squirrelsql.workaround.KeyMatchWA;

import java.util.List;

public class ObjectTreeFindCtrl
{
   private final FxmlHelper<ObjectTreeFindView> _fxmlHelper;
   private final Props _props = new Props(getClass());
   private final CompletionCtrl _completionCtrl;

   private I18n _i18n = new I18n(getClass());
   private TreeView<ObjectTreeNode> _objectsTree;
   private Session _session;
   private ObjectTreeFindView _view;

   public ObjectTreeFindCtrl(TreeView<ObjectTreeNode> objectsTree, Session session)
   {
      _objectsTree = objectsTree;
      _session = session;
      _fxmlHelper = new FxmlHelper<>(ObjectTreeFindView.class);
      _view = _fxmlHelper.getView();

      _view.btnFind.setGraphic(_props.getImageView(GlobalIconNames.SEARCH));
      _view.btnFind.setTooltip(new Tooltip(_i18n.t("objecttreefind.find.tooltip")));
      _view.btnFind.setOnAction(e -> onFind());


      _view.btnFilter.setGraphic(_props.getImageView("filter.png"));
      _view.btnFilter.setTooltip(new Tooltip(_i18n.t("objecttreefind.filter.tooltip")));
      _view.btnFilter.setOnAction(e -> onFilter());

      _completionCtrl = new CompletionCtrl(_session, new TextFieldTextComponentAdapter(_view.txtText));

      _view.txtText.setOnKeyPressed(e -> onHandleKeyEvent(e, false));
      _view.txtText.setOnKeyTyped(e -> onHandleKeyEvent(e, true));

   }

   private void onFilter()
   {
      new FilterResultCtrl(_session, _objectsTree, _view.txtText.getText());
   }

   private void onHandleKeyEvent(KeyEvent keyEvent, boolean consumeOnly)
   {
      if ( KeyMatchWA.matches(keyEvent, StdActionCfg.SQL_CODE_COMPLETION.getActionCfg().getKeyCodeCombination()) )
      {
         if (false == consumeOnly)
         {
            _completionCtrl.completeCode();
         }
         keyEvent.consume();
         return;
      }
      if ( KeyMatchWA.matches(keyEvent, new KeyCodeCombination(KeyCode.ENTER)))
      {
         if (false == consumeOnly)
         {
            onFind();
         }
         keyEvent.consume();
         return;
      }
   }


   private void onFind()
   {
      List<TreeItem<ObjectTreeNode>> objectsMatchingNames = ObjectTreeUtil.findObjectsMatchingName(_objectsTree, _view.txtText.getText(), NameMatchMode.EQUALS);

      if(0 == objectsMatchingNames.size())
      {
         return;
      }

      ObjectTreeUtil.selectItem(_objectsTree, objectsMatchingNames.get(0));
   }

   public Node getNode()
   {
      return _fxmlHelper.getRegion();
   }
}
