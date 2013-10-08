package org.squirrelsql.aliases.dbconnector;

import javafx.scene.layout.Region;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.squirrelsql.aliases.Alias;
import org.squirrelsql.services.*;

public class ConnectingController
{
   private I18n _i18n = new I18n(this.getClass());

   private Window _owner;
   private FxmlHelper<ConnectingView> _connectingViewFxml;
   private ProgressibleStage _progressibleStage;

   private ConnectFailureDecisionListener _connectFailureDecisionListener;

   public ConnectingController(Window owner, Alias alias)
   {
      _owner = owner;
      _connectingViewFxml = new FxmlHelper<>(ConnectingView.class);

      _connectingViewFxml.getView().lblAliasConnecting.setText(_i18n.t("connectingctrl.alias.display", alias.getName(), alias.getUrl(), alias.getUserName()));

      setVisibility(false);


      _progressibleStage = ProgressUtil.makeProgressible(new Stage(), true);


      _connectingViewFxml.getView().btnClose.setOnAction(e -> close());
      _connectingViewFxml.getView().btnRelogin.setOnAction(e -> onRelogin());
      _connectingViewFxml.getView().btnEditAlias.setOnAction(e -> onEditAlias());

   }

   private void onEditAlias()
   {
      close();
      _connectFailureDecisionListener.decided(ConnectFailureDecisionListener.Decision.EDIT_ALIAS_REQUESTED);
   }

   private void onRelogin()
   {
      close();
      _connectFailureDecisionListener.decided(ConnectFailureDecisionListener.Decision.RELOGIN_REQUESTED);
   }

   private void setVisibility(boolean b)
   {
      _connectingViewFxml.getView().lblErrorOccured.setVisible(b);
      _connectingViewFxml.getView().txtErrMessage.setVisible(b);
      _connectingViewFxml.getView().btnEditAlias.setVisible(b);
      _connectingViewFxml.getView().btnRelogin.setVisible(b);
      _connectingViewFxml.getView().btnClose.setVisible(b);
   }

   public void startConnecting(ProgressTask<DbConnectorResult> pt)
   {
      _progressibleStage.getStage().initOwner(_owner);
      Region sceneRoot = _connectingViewFxml.getRegion();
      _progressibleStage.setSceneRoot(sceneRoot);

      _progressibleStage.getStage().show();

      GuiUtils.centerWithinParent(_progressibleStage.getStage());

      new StageDimensionSaver("connecting", _progressibleStage.getStage(), new Pref(ConnectingController.class), sceneRoot.getPrefWidth(), sceneRoot.getPrefHeight(), _owner);


      ProgressUtil.start(pt, _progressibleStage.getStage());
   }

   public void displayAndDecideOnConnectFailure(DbConnectorResult dbConnectorResult, ConnectFailureDecisionListener connectFailureDecisionListener)
   {
      _connectFailureDecisionListener = connectFailureDecisionListener;
      setVisibility(true);

      String text = dbConnectorResult.getConnectException().getMessage();

      text += "\nSQLState: " + dbConnectorResult.getConnectException().getSQLState();
      text += "\nErrorCode: " + dbConnectorResult.getConnectException().getErrorCode();
      text += "\n\n" + ExceptionUtils.getStackTrace(dbConnectorResult.getConnectException());

      _connectingViewFxml.getView().txtErrMessage.setStyle("-fx-vbar-policy: always");
      _connectingViewFxml.getView().txtErrMessage.setText(text);

      GuiUtils.makeEscapeClosable(_connectingViewFxml.getRegion());

   }

   public void close()
   {
      _progressibleStage.getStage().close();
   }
}
