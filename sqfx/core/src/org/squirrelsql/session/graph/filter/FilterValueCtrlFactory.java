package org.squirrelsql.session.graph.filter;

import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import org.squirrelsql.session.ColumnInfo;
import org.squirrelsql.session.graph.Operator;

import java.sql.Types;

class FilterValueCtrlFactory
{
   static FilterValueCtrl getCtrl(ColumnInfo columnInfo, BorderPane bpValueContainer, Button btnDate, Label lblEncloseApostrophes, Stage dlg, ComboBox<Operator> cboOperator)
   {
      if(columnInfo.getColType() == Types.TIMESTAMP ||
            columnInfo.getColType() == Types.DATE ||
            columnInfo.getColType() == Types.TIME)
      {
         return new DateValuesCtrl(bpValueContainer, btnDate, lblEncloseApostrophes, dlg);

      }
      else if(columnInfo.getColType() == Types.VARCHAR ||
            columnInfo.getColType() == Types.LONGVARCHAR||
            columnInfo.getColType() == Types.CHAR)
      {
         return new StringValuesCtrl(bpValueContainer, btnDate, lblEncloseApostrophes, cboOperator);
      }
      else
      {
         return new DefaultValuesCtrl(bpValueContainer, btnDate, lblEncloseApostrophes);
      }

   }

}
