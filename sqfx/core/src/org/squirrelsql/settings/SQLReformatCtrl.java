package org.squirrelsql.settings;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.text.Font;
import org.squirrelsql.services.Dao;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Utils;
import org.squirrelsql.sqlreformat.CodeReformator;
import org.squirrelsql.sqlreformat.CodeReformatorFractory;
import org.squirrelsql.table.tableedit.StringInterpreter;

import java.util.HashMap;

public class SQLReformatCtrl implements SettingsTabController
{

   private final Tab _tab;
   private final SQLReformatView _view;

   public SQLReformatCtrl()
   {
      FxmlHelper<SQLReformatView> fxmlHelper = new FxmlHelper<>(SQLReformatView.class);

      _tab = new Tab(new I18n(getClass()).t("reformat.tab.title"), fxmlHelper.getRegion());

      _view = fxmlHelper.getView();


      SQLFormatSettings sqlFormatSettings = Dao.loadSQLFormatSeetings();

      Utils.makePositiveIntegerField(_view.txtIndentSpaceCount);
      _view.txtIndentSpaceCount.setText("" + sqlFormatSettings.getIndentSpaceCount());
      _view.txtIndentSpaceCount.textProperty().addListener((observable, oldValue, newValue) -> updateUi());

      Utils.makePositiveIntegerField(_view.txtPreferedLineLength);
      _view.txtPreferedLineLength.setText("" + sqlFormatSettings.getPreferedLineLength());
      _view.txtPreferedLineLength.textProperty().addListener((observable, oldValue, newValue) -> updateUi());

      _view.chkAlignmInsertValues.setSelected(sqlFormatSettings.isAlignmInsertValues());
      _view.chkAlignmInsertValues.setOnAction(e -> updateUi());

      HashMap<SQLKeyWord, KeyWordBehavior> behaviorsByKeyWord = sqlFormatSettings.getBehaviorsByKeyWord();

      HashMap<SQLKeyWord, ComboBox> comboBoxesByKeyWords = _view.getComboBoxesByKeyWords();
      for (SQLKeyWord sqlKeyWord : behaviorsByKeyWord.keySet())
      {
         ComboBox comboBox = comboBoxesByKeyWords.get(sqlKeyWord);
         comboBox.getItems().addAll(KeyWordBehavior.values());
         comboBox.getSelectionModel().select(behaviorsByKeyWord.get(sqlKeyWord));

         comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateUi());
      }


      _view.txtExample.setEditable(false);
      _view.txtExample.setFont(Font.font("monospace"));
      updateUi();
   }

   private void updateUi()
   {
      if(_view.chkAlignmInsertValues.isSelected())
      {
         _view.cboInsert.getSelectionModel().select(KeyWordBehavior.START_NEW_LINE);
         _view.cboValues.getSelectionModel().select(KeyWordBehavior.NO_INFLUENCE_ON_LINEBREAK);
      }

      _view.cboInsert.setDisable(_view.chkAlignmInsertValues.isSelected());
      _view.cboValues.setDisable(_view.chkAlignmInsertValues.isSelected());

      refreshExample();
   }

   private void refreshExample()
   {
      String sqls;

      CodeReformator codeReformator = CodeReformatorFractory.createCodeReformator(readSettingsFromControls());

      sqls = codeReformator.reformat("SELECT table1.id, table2.number, SUM(table1.amount) FROM table1 INNER JOIN table2 ON table.id = table2.table1_id WHERE table1.id IN (SELECT table1_id FROM table3 WHERE table3.name = 'Foo Bar' and table3.type = 'unknown_type') GROUP BY table1.id, table2.number ORDER BY table1.id");
      sqls += "\n\n";
      sqls += codeReformator.reformat("UPDATE table1 SET name = 'Hello', number = '1456-789' WHERE id = 42");
      sqls += "\n\n";
      sqls += codeReformator.reformat("INSERT INTO table1 (name, number) SELECT name, number FROM table1_bak");
      sqls += "\n\n";
      sqls += codeReformator.reformat("INSERT INTO table1 (name, number, type) VALUES ('Foo', 42, 'VA')");
      sqls += "\n\n";
      sqls += codeReformator.reformat("DELETE FROM table1 WHERE  name = 'Hello' OR number = '1456-789'");

      _view.txtExample.setText(sqls);
   }


   @Override
   public void saveSettings()
   {
      SQLFormatSettings sqlFormatSettings = readSettingsFromControls();

      Dao.writerSQLFormatSeetings(sqlFormatSettings);

   }

   private SQLFormatSettings readSettingsFromControls()
   {
      SQLFormatSettings sqlFormatSettings = new SQLFormatSettings();

      sqlFormatSettings.setIndentSpaceCount(StringInterpreter.interpretNonNull(_view.txtIndentSpaceCount.getText(), Integer.class, sqlFormatSettings.getIndentSpaceCount()));
      sqlFormatSettings.setPreferedLineLength(StringInterpreter.interpretNonNull(_view.txtPreferedLineLength.getText(), Integer.class, sqlFormatSettings.getPreferedLineLength()));
      sqlFormatSettings.setAlignmInsertValues(_view.chkAlignmInsertValues.isSelected());


      HashMap<SQLKeyWord, KeyWordBehavior> behaviorsByKeyWord = sqlFormatSettings.getBehaviorsByKeyWord();
      HashMap<SQLKeyWord, ComboBox> comboBoxesByKeyWords = _view.getComboBoxesByKeyWords();
      for (SQLKeyWord sqlKeyWord : behaviorsByKeyWord.keySet())
      {
         ComboBox comboBox = comboBoxesByKeyWords.get(sqlKeyWord);
         behaviorsByKeyWord.put(sqlKeyWord, (KeyWordBehavior) comboBox.getSelectionModel().getSelectedItem());
      }
      return sqlFormatSettings;
   }

   @Override
   public Tab getTab()
   {
      return _tab;
   }

   @Override
   public void setSettingsContext(SettingsContext settingsContext)
   {

   }
}
