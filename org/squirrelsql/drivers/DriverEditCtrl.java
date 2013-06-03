package org.squirrelsql.drivers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.Region;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.squirrelsql.AppState;
import org.squirrelsql.services.*;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class DriverEditCtrl
{
   private static final String PREF_LAST_CLASSPATH_DIR = "last.classpath.dir";


   private I18n _i18n = new I18n(this.getClass());
   private Pref _pref = new Pref(getClass());
   private DriverEditView _driverEditView;
   private final ProgressibleStage _dialog;


   public DriverEditCtrl(SQLDriver SQLDriver)
   {
      try
      {

         FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("DriverEditView.fxml"), ResourceBundle.getBundle(getClass().getPackage().getName() + ".i18n"));
         Region parent = (Region) fxmlLoader.load();
         _driverEditView = fxmlLoader.getController();

         String title = _i18n.t("change.driver.title", SQLDriver.getName());

         _driverEditView.lblChangeDriver.setText(title);

         _driverEditView.txtName.setText(SQLDriver.getName());
         _driverEditView.txtUrl.setText(SQLDriver.getUrl());
         _driverEditView.txtWebUrl.setText(SQLDriver.getWebsiteUrl());
         _driverEditView.txtSelectedDriver.setText(SQLDriver.getDriverClassName());

         _driverEditView.lstClasspath.setItems(FXCollections.observableList(new ArrayList(SQLDriver.getJarFileNamesList())));

         _driverEditView.lstClasspath.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);



         initListeners();

         _dialog = ProgressUtil.makeProgressible(new Stage());
         _dialog.getStage().initModality(Modality.WINDOW_MODAL);
         _dialog.getStage().setTitle(title);
         _dialog.getStage().initOwner(AppState.get().getPrimaryStage());
         _dialog.setSceneRoot(parent);

         new StageDimensionSaver("driveredit", _dialog.getStage(), _pref, parent.getPrefWidth(), parent.getPrefHeight(), _dialog.getStage().getOwner());

         _dialog.getStage().show();

      }
      catch (IOException e)
      {
         throw new RuntimeException(e);
      }
   }

   private void initListeners()
   {
      _driverEditView.btnDriverCPAdd.setOnAction((e) -> onDriverCPAdd());
      _driverEditView.btnDriverCPRemove.setOnAction((e) -> onDriverCPRemove());
      _driverEditView.btnDriverCPUp.setOnAction((e) -> onDriverCPUp());
      _driverEditView.btnDriverCPDown.setOnAction((e) -> onDriverCPDown());
      _driverEditView.btnListDrivers.setOnAction((e) -> onListDrivers());


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
      try
      {
         ObservableList<String> fileNames = _driverEditView.lstClasspath.getItems();

         ArrayList<URL> urls = new ArrayList<>();

         for (String fileName : fileNames)
         {
            urls.add(new File(fileName).toURI().toURL());
         }


         SQLDriverClassLoader cl = new SQLDriverClassLoader(urls);

         return cl.getDriverClasses();
      }
      catch (MalformedURLException e)
      {
         throw new RuntimeException(e);
      }
   }

   private void fillDriverList(ArrayList<Class> driverClassNames)
   {
      _driverEditView.lstDriverClasses.getItems().clear();
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


}
