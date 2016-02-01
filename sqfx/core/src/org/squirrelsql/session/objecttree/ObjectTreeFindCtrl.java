package org.squirrelsql.session.objecttree;

import javafx.scene.Node;
import javafx.scene.control.Tooltip;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import org.squirrelsql.Props;
import org.squirrelsql.globalicons.GlobalIconNames;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;

import java.util.List;

public class ObjectTreeFindCtrl
{
   private final FxmlHelper<ObjectTreeFindView> _fxmlHelper;
   private final Props _props = new Props(getClass());

   private I18n _i18n = new I18n(getClass());
   private TreeView<ObjectTreeNode> _objectsTree;
   private ObjectTreeFindView _view;

   public ObjectTreeFindCtrl(TreeView<ObjectTreeNode> objectsTree)
   {
      _objectsTree = objectsTree;
      _fxmlHelper = new FxmlHelper<>(ObjectTreeFindView.class);
      _view = _fxmlHelper.getView();

      _view.btnFind.setGraphic(_props.getImageView(GlobalIconNames.SEARCH));
      _view.btnFind.setTooltip(new Tooltip(_i18n.t("objecttreefind.find.tooltip")));
      _view.btnFind.setOnAction(e -> onFind());


      _view.btnFilter.setGraphic(_props.getImageView("filter.png"));
      _view.btnFilter.setTooltip(new Tooltip(_i18n.t("objecttreefind.filter.tooltip")));
   }

   private void onFind()
   {
      List<TreeItem<ObjectTreeNode>> objectsMatchingNames = ObjectTreeUtil.findObjectsMatchingName(_objectsTree, _view.txtText.getText());

      if(0 == objectsMatchingNames.size())
      {
         return;
      }

      ObjectTreeUtil.selectItem(objectsMatchingNames.get(0), _objectsTree);
   }

   public Node getNode()
   {
      return _fxmlHelper.getRegion();
   }
}
