package org.squirrelsql.session.sql;

import javafx.scene.Node;
import javafx.scene.layout.Region;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.Pref;
import org.squirrelsql.services.Utils;

public class SQLEditTopPanelCtrl
{
   private static final String LIMIT_ROWS = "sql.limit.rows";
   private static final String  LIMIT_ROWS_COUNT = "sql.limit.rows.count";
   public static final int DEFAULT_ROW_LIMIT_COUNT = 100;
   private Pref _pref = new Pref(getClass());


   private final SQLEditTopPanelView _view;
   private final Region _region;

   public SQLEditTopPanelCtrl()
   {
      FxmlHelper<SQLEditTopPanelView> fxmlHelper = new FxmlHelper<>(SQLEditTopPanelView.class);

      _view = fxmlHelper.getView();

      _region = fxmlHelper.getRegion();

      _view.chkLimitRows.setSelected(_pref.getBoolean(LIMIT_ROWS, true));
      _view.txtRowLimit.setText("" + _pref.getInt(LIMIT_ROWS_COUNT, DEFAULT_ROW_LIMIT_COUNT));

      Utils.makePositiveIntegerField(_view.txtRowLimit);

      onChkRowLimitChanged();

      _view.chkLimitRows.setOnAction(event -> onChkRowLimitChanged());

   }

   private void onChkRowLimitChanged()
   {
      _view.txtRowLimit.setDisable(!_view.chkLimitRows.isSelected());
   }

   public Node getView()
   {
      return _region;
   }

   public void close()
   {
      _pref.set(LIMIT_ROWS, _view.chkLimitRows.isSelected());
      _pref.set(LIMIT_ROWS_COUNT, getRowLimit());
   }

   private int _getRowLimit()
   {
      String intText = _view.txtRowLimit.getText();
      if(Utils.isEmptyString(intText))
      {
         return DEFAULT_ROW_LIMIT_COUNT;
      }
      else
      {
         return Integer.parseInt(intText);
      }
   }

   public Integer getRowLimit()
   {
      if(_view.chkLimitRows.isSelected())
      {
         return _getRowLimit();
      }

      return null;
   }
}
