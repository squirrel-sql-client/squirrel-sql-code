package org.squirrelsql.settings;

import javafx.scene.control.ComboBox;
import javafx.scene.control.Tab;
import javafx.scene.control.ToggleGroup;
import javafx.scene.text.Font;
import org.squirrelsql.services.Dao;
import org.squirrelsql.services.FxmlHelper;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.Utils;
import org.squirrelsql.sqlreformat.CodeReformator;
import org.squirrelsql.sqlreformat.CodeReformatorFractory;
import org.squirrelsql.sqlreformat.ColumnListSpiltMode;
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

      _view.chkIndentSections.setSelected(sqlFormatSettings.isIndentSections());
      _view.chkIndentSections.setOnAction(e -> updateUi());

      ToggleGroup tg = new ToggleGroup();
      _view.radCommasAtLineBegin.setSelected(sqlFormatSettings.isCommaAtLineBegin());
      _view.radCommasAtLineEnd.setSelected(!sqlFormatSettings.isCommaAtLineBegin());
      _view.radCommasAtLineBegin.setToggleGroup(tg);
      _view.radCommasAtLineEnd.setToggleGroup(tg);
      _view.radCommasAtLineBegin.setOnAction(e -> updateUi());
      _view.radCommasAtLineEnd.setOnAction(e -> updateUi());


      _view.chkLineBreakFor_AND_OR_in_FROM_clause.setSelected(sqlFormatSettings.isLineBreakFor_AND_OR_in_FROM_clause());
      _view.chkLineBreakFor_AND_OR_in_FROM_clause.setOnAction(e -> updateUi());

      _view.cboColumnListSplitMode.getItems().addAll(ColumnListSpiltMode.values());
      _view.cboColumnListSplitMode.getSelectionModel().select(ColumnListSpiltMode.valueOf(sqlFormatSettings.getColumnListSpiltMode()));
      _view.cboColumnListSplitMode.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> updateUi());

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


      if(   KeyWordBehavior.START_NEW_LINE ==  _view.getComboBoxesByKeyWords().get(SQLKeyWord.AND).getSelectionModel().getSelectedItem()
         || KeyWordBehavior.START_NEW_LINE == _view.getComboBoxesByKeyWords().get(SQLKeyWord.OR).getSelectionModel().getSelectedItem())
      {
         _view.chkLineBreakFor_AND_OR_in_FROM_clause.setDisable(false);
      }
      else
      {
         _view.chkLineBreakFor_AND_OR_in_FROM_clause.setDisable(true);
      }


      refreshExample();
   }

   private void refreshExample()
   {
      String sqls;

      CodeReformator codeReformator = CodeReformatorFractory.createCodeReformator(readSettingsFromControls());

      sqls = codeReformator.reformat("SELECT table1.id,table2.number,table2.name,table2.info1,table2.info2,table2.info3,table2.info4,table2.info5,table2.info6,SUM(table1.amount) FROM table1 INNER JOIN table2 ON table.id1 = table2.table1_id1 AND table.id2 = table2.table1_id2 LEFT OUTER JOIN table3 ON table.id1 = table3.table1_id1 AND table.id2 = table3.table1_id3 WHERE table1.id IN (SELECT table1_id FROM table3 WHERE table3.name = 'Foo Bar' and table3.type = 'unknown_type') AND table2.name LIKE '%g%' GROUP BY table1.id,table2.number ORDER BY table1.id");
      sqls += "\n\n";
      sqls += codeReformator.reformat("UPDATE table1 SET name = 'Hello', number = '1456-789' WHERE id = 42");
      sqls += "\n\n";
      sqls += codeReformator.reformat("INSERT INTO table1 (name, number) SELECT name,number FROM table1_bak");
      sqls += "\n\n";
      sqls += codeReformator.reformat("INSERT INTO table1 (name,number,type) VALUES ('Foo',42,'VA')");
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

      sqlFormatSettings.setIndentSections(_view.chkIndentSections.isSelected());
      sqlFormatSettings.setCommaAtLineBegin(_view.radCommasAtLineBegin.isSelected());
      sqlFormatSettings.setLineBreakFor_AND_OR_in_FROM_clause(_view.chkLineBreakFor_AND_OR_in_FROM_clause.isSelected());

      sqlFormatSettings.setColumnListSpiltMode(((ColumnListSpiltMode) _view.cboColumnListSplitMode.getSelectionModel().getSelectedItem()).name());

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
