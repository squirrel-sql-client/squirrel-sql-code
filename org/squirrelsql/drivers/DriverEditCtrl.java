package org.squirrelsql.drivers;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.SelectionMode;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.squirrelsql.AppState;
import org.squirrelsql.services.*;
import org.squirrelsql.services.sqlwrap.SQLDriverClassLoader;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DriverEditCtrl
{
   private static final String PREF_LAST_CLASSPATH_DIR = "last.classpath.dir";


   private I18n _i18n = new I18n(this.getClass());
   private Pref _pref = new Pref(getClass());
   private DriverEditView _driverEditView;
   private final ProgressibleStage _dialog;
   private SQLDriver _sqlDriver;
   private boolean _ok;


   public DriverEditCtrl(SQLDriver sqlDriver)
   {
      _sqlDriver = sqlDriver;

      FxmlHelper<DriverEditView> fxmlHelper = new FxmlHelper<>(DriverEditView.class);
      Region parent = fxmlHelper.getRegion();
      _driverEditView = fxmlHelper.getView();

      String title = _i18n.t("change.driver.title", sqlDriver.getName());

      _driverEditView.lblChangeDriver.setText(title);

      _driverEditView.txtName.setText(sqlDriver.getName());
      _driverEditView.txtUrl.setText(sqlDriver.getUrl());
      _driverEditView.txtWebUrl.setText(sqlDriver.getWebsiteUrl());
      _driverEditView.txtDriverToUse.setText(sqlDriver.getDriverClassName());

      _driverEditView.lstClasspath.setItems(FXCollections.observableList(new ArrayList(sqlDriver.getJarFileNamesList())));

      _driverEditView.lstClasspath.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);



      initListeners();

      _dialog = ProgressUtil.makeProgressible(new Stage(), false);
      _dialog.getStage().initModality(Modality.WINDOW_MODAL);
      _dialog.getStage().setTitle(title);
      _dialog.getStage().initOwner(AppState.get().getPrimaryStage());
      _dialog.setSceneRoot(parent);


      GuiUtils.makeEscapeClosable(parent);

      new StageDimensionSaver("driveredit", _dialog.getStage(), _pref, parent.getPrefWidth(), parent.getPrefHeight(), _dialog.getStage().getOwner());

      _dialog.getStage().showAndWait();
   }

   private void initListeners()
   {
      _driverEditView.btnDriverCPAdd.setOnAction((e) -> onDriverCPAdd());
      _driverEditView.btnDriverCPRemove.setOnAction((e) -> onDriverCPRemove());
      _driverEditView.btnDriverCPUp.setOnAction((e) -> onDriverCPUp());
      _driverEditView.btnDriverCPDown.setOnAction((e) -> onDriverCPDown());
      _driverEditView.btnListDrivers.setOnAction((e) -> onListDrivers());

      _driverEditView.lstDriverClasses.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> onDriverClassSelected((String)newValue));

      _driverEditView.lstDriverClasses.setOnMouseClicked(this::onMouseClickedDriverList);

      _driverEditView.btnClose.setOnAction((e) -> onClose());
      _driverEditView.btnOk.setOnAction((e) -> onOk());


   }

   private void onMouseClickedDriverList(MouseEvent mouseEvent)
   {
      if (mouseEvent.getButton().equals(MouseButton.PRIMARY))
      {
         if (mouseEvent.getClickCount() == 2)
         {
            ObservableList selectedItems = _driverEditView.lstDriverClasses.getSelectionModel().getSelectedItems();
            if (1 == selectedItems.size())
            {
               onDriverClassSelected((String) selectedItems.get(0));
            }
         }
      }
   }

   private void onDriverClassSelected(String newStr)
   {
      if(null != newStr)
      {
         _driverEditView.txtDriverToUse.setText(newStr);
      }
   }

   private void onOk()
   {
      if(Utils.isEmptyString(_driverEditView.txtName.getText()))
      {
         FXMessageBox.showInfoOk(_dialog.getStage(), _i18n.t("info.name.empty"));
         return;
      }

      if(Utils.isEmptyString(_driverEditView.txtUrl.getText()))
      {
         FXMessageBox.showInfoOk(_dialog.getStage(), _i18n.t("info.url.empty"));
         return;
      }

      if(Utils.isEmptyString(_driverEditView.txtDriverToUse.getText()))
      {
         FXMessageBox.showInfoOk(_dialog.getStage(), _i18n.t("info.driver.empty"));
         return;
      }

      ProgressTask<Boolean> pt = new ProgressTask<Boolean>()
      {
         @Override
         public Boolean call()
         {
            return DriversUtil.checkDriverLoading(DriverEditCtrl.this._driverEditView.lstClasspath.getItems(), DriverEditCtrl.this._driverEditView.txtDriverToUse.getText());
         }

         @Override
         public void goOn(Boolean driverFound)
         {
            onFinishOk(driverFound);
         }
      };

      ProgressUtil.start(pt, _dialog.getStage());
   }

   private void onFinishOk(boolean driverFound)
   {

      if (false == driverFound &&
          false == FXMessageBox.YES.equals(FXMessageBox.showYesNo(_dialog.getStage(), _i18n.t("info.driver.not.loaded"))))
      {
         return;
      }

      _sqlDriver.setDriverClassName(_driverEditView.txtDriverToUse.getText());
      _sqlDriver.setUrl(_driverEditView.txtUrl.getText());
      _sqlDriver.setName(_driverEditView.txtName.getText());
      _sqlDriver.setWebsiteUrl(_driverEditView.txtWebUrl.getText());
      _sqlDriver.setJarFileNamesList(_driverEditView.lstClasspath.getItems());
      _sqlDriver.setLoaded(driverFound);

      _ok = true;
      _dialog.getStage().close();
   }

   private void onClose()
   {
      _dialog.getStage().close();
   }

   private void onListDrivers()
   {
      ProgressTask<ArrayList<Class>> pt = new ProgressTask<ArrayList<Class>>()
      {
         @Override
         public ArrayList<Class> call()
         {
             return findDriverClassNames();
         }

         @Override
         public void goOn(ArrayList<Class> driverClasses)
         {
            fillDriverList(driverClasses);
         }
      };

      ProgressUtil.start(pt, _dialog.getStage());
   }

   private ArrayList<Class> findDriverClassNames()
   {
      ObservableList<String> fileNames = _driverEditView.lstClasspath.getItems();

      SQLDriverClassLoader cl = DriversUtil.createDriverClassLoader(fileNames);

      return cl.getDriverClasses();
   }

   private void fillDriverList(ArrayList<Class> driverClasses)
   {
      _driverEditView.lstDriverClasses.getItems().clear();

      ArrayList<String> driverClassNames = Conversions.toNames(driverClasses, (x) -> x.getName());

      _driverEditView.lstDriverClasses.getItems().addAll(FXCollections.observableArrayList(driverClassNames));
   }

   private void onDriverCPDown()
   {
      ObservableList<Integer> selectedIndices = _driverEditView.lstClasspath.getSelectionModel().getSelectedIndices();

      if (0 == selectedIndices.size())
      {
         return;
      }

      int[] newSelectedIndices = moveDown(Conversions.toInts(selectedIndices.toArray(new Integer[selectedIndices.size()])));

      _driverEditView.lstClasspath.getSelectionModel().clearSelection();

      for (int newSelectedIndex : newSelectedIndices)
      {
         _driverEditView.lstClasspath.getSelectionModel().select(newSelectedIndex);
      }

      //_driverEditView.lstClasspath.scrollTo(newSelectedIndices[newSelectedIndices.length - 1]);
      _driverEditView.lstClasspath.scrollTo(newSelectedIndices[0]);

   }

   private int[] moveDown(int[] toMoveDown)
   {
      for (int i : toMoveDown)
      {
         if (_driverEditView.lstClasspath.getItems().size() - 1 == i)
         {
            return toMoveDown;
         }
      }

      int[] newSelIndices = new int[toMoveDown.length];
      for (int i = toMoveDown.length - 1; i >= 0; --i)
      {
         Object item = _driverEditView.lstClasspath.getItems().get(toMoveDown[i]);
         _driverEditView.lstClasspath.getItems().remove((int)toMoveDown[i]);
         newSelIndices[i] = toMoveDown[i] + 1;
         _driverEditView.lstClasspath.getItems().add(newSelIndices[i], item);
      }

      return newSelIndices;
   }

   private void onDriverCPUp()
   {
      ObservableList<Integer> selectedIndices = _driverEditView.lstClasspath.getSelectionModel().getSelectedIndices();

      if (0 == selectedIndices.size())
      {
         return;
      }

      int[] newSelectedIndices = moveUp(Conversions.toInts(selectedIndices.toArray(new Integer[selectedIndices.size()])));

      _driverEditView.lstClasspath.getSelectionModel().clearSelection();

      for (int newSelectedIndex : newSelectedIndices)
      {
         _driverEditView.lstClasspath.getSelectionModel().select(newSelectedIndex);
      }
      _driverEditView.lstClasspath.scrollTo(newSelectedIndices[0]);
   }

   private void onDriverCPRemove()
   {
      ObservableList selectedItems = _driverEditView.lstClasspath.getSelectionModel().getSelectedItems();

      _driverEditView.lstClasspath.getItems().removeAll(selectedItems.toArray(new Object[selectedItems.size()]));

   }

   public int[] moveUp(int[] toMoveUp)
   {
      for (int i : toMoveUp)
      {
         if (0 == i)
         {
            return toMoveUp;
         }
      }

      int[] newSelIndices = new int[toMoveUp.length];
      for (int i = 0; i < toMoveUp.length; ++i)
      {
         Object item = _driverEditView.lstClasspath.getItems().get(toMoveUp[i]);
         _driverEditView.lstClasspath.getItems().remove((int)toMoveUp[i]);
         newSelIndices[i] = toMoveUp[i] - 1;
         _driverEditView.lstClasspath.getItems().add(newSelIndices[i], item);
      }

      return newSelIndices;
   }



   private void onDriverCPAdd()
   {
      FileChooser fc = new FileChooser();

      fc.setTitle(_i18n.t("driver.select.classpath.entry"));

      String lastClasspathDir = _pref.getString(PREF_LAST_CLASSPATH_DIR, System.getProperty("user.home"));

      fc.setInitialDirectory(new File(lastClasspathDir));

      List<File> files = fc.showOpenMultipleDialog(_dialog.getStage());

      if(null == files || 0 == files.size())
      {
         return;
      }

      String lastDir = files.get(0).getParent();
      _pref.set(PREF_LAST_CLASSPATH_DIR, lastDir);

      _driverEditView.lstClasspath.getItems().addAll(FXCollections.observableArrayList(Conversions.toPathString(files)));
   }


   public SQLDriver getDriver()
   {
      return _sqlDriver;
   }

   public boolean isOk()
   {
      return _ok;
   }
}
