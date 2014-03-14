package org.squirrelsql.session;

import javafx.animation.AnimationTimer;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.I18n;

public class SQLCancelTabCtrl
{
   private final SQLCancelTabView _view;
   private final Region _region;

   private I18n _i18n = new I18n(getClass());


   public SQLCancelTabCtrl(String sql, StatementChannel statementChannel)
   {
      FxmlHelper<SQLCancelTabView> fxmlHelper = new FxmlHelper<>(SQLCancelTabView.class);

      _view = fxmlHelper.getView();
      _region = fxmlHelper.getRegion();

      _view.txtSql.setText(sql);
      _view.txtStatus.setText(StatementExecutionState.PREPARING.getText());

      long begin = System.currentTimeMillis();

      AnimationTimer animationTimer = new AnimationTimer()
      {
         @Override
         public void handle(long l)
         {
            _view.txtExecTime.setText("" + (System.currentTimeMillis() - begin));
         }
      };
      animationTimer.start();



      _view.btnCancel.setOnAction((e) -> statementChannel.cancelStatement());

      statementChannel.setStateChannelListener(statementExecutionState -> onExecutionStateChanged(statementExecutionState, animationTimer));


      _region.setPrefWidth(Double.MAX_VALUE);
      _region.setPrefHeight(Double.MAX_VALUE);
   }

   private void onExecutionStateChanged(StatementExecutionState statementExecutionState, AnimationTimer animationTimer)
   {
      _view.txtStatus.setText(statementExecutionState.getText());

      if(StatementExecutionState.isEndState(statementExecutionState))
      {
         animationTimer.stop();
      }
   }

   public Node getNode()
   {
      return _region;
   }
}
