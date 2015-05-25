package org.squirrelsql.settings;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.util.HashMap;

public class SQLReformatView
{
   @FXML TextField txtIndentSpaceCount;
   @FXML TextField txtPreferedLineLength;

   @FXML ComboBox cboSelect;
   @FXML ComboBox cboUnion;
   @FXML ComboBox cboFrom;
   @FXML ComboBox cboInnerleftrightjoin;
   @FXML ComboBox cboWhere;
   @FXML ComboBox cboAnd;
   @FXML ComboBox cboOr;
   @FXML ComboBox cboGroup;
   @FXML ComboBox cboOrder;
   @FXML ComboBox cboUpdate;
   @FXML ComboBox cboDelete;
   @FXML ComboBox cboInsert;
   @FXML ComboBox cboValues;

   @FXML CheckBox chkAlignmInsertValues;

   @FXML TextArea txtExample;

   public HashMap<SQLKeyWord, ComboBox> getComboBoxesByKeyWords()
   {
      HashMap<SQLKeyWord, ComboBox> ret = new HashMap<>();

      ret.put(SQLKeyWord.SELECT, cboSelect);
      ret.put(SQLKeyWord.UNION, cboUnion);
      ret.put(SQLKeyWord.FROM, cboFrom);
      ret.put(SQLKeyWord.INNER_LEFT_RIGHT_JOIN, cboInnerleftrightjoin);
      ret.put(SQLKeyWord.WHERE, cboWhere);
      ret.put(SQLKeyWord.AND, cboAnd);
      ret.put(SQLKeyWord.OR, cboOr);
      ret.put(SQLKeyWord.GROUP, cboGroup);
      ret.put(SQLKeyWord.ORDER, cboOrder);
      ret.put(SQLKeyWord.UPDATE, cboUpdate);
      ret.put(SQLKeyWord.DELETE, cboDelete);
      ret.put(SQLKeyWord.INSERT, cboInsert);
      ret.put(SQLKeyWord.VALUES, cboValues);

      return ret;

   }
}
