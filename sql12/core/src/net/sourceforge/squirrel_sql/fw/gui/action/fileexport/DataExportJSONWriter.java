package net.sourceforge.squirrel_sql.fw.gui.action.fileexport;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.CellComponentFactory;
import net.sourceforge.squirrel_sql.fw.sql.ProgressAbortCallback;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;

import java.io.File;
import java.io.PrintWriter;
import java.math.BigDecimal;

public class DataExportJSONWriter extends AbstractDataExportFileWriter
{

   private ObjectMapper _mapper;
   private ObjectNode _root;
   private ObjectNode _tableNode;
   private ArrayNode _columns;
   private ArrayNode _rows;
   int _currentRowNumber = 0;
   private ObjectNode _currentRow;
   private ArrayNode _currentValues;

   public DataExportJSONWriter(File file, TableExportPreferences prefs, ProgressAbortCallback progressController)
   {
      super(file, prefs, progressController);
   }

   @Override
   protected void beforeWorking(File file)
   {
      _mapper = new ObjectMapper();

      _root = _mapper.createObjectNode();

      _tableNode = _mapper.createObjectNode();

      _root.set("table", _tableNode);

      _columns = _mapper.createArrayNode();
      _tableNode.set("columns", _columns);

   }

   @Override
   public void beforeRows()
   {
      _rows = _mapper.createArrayNode();
      _tableNode.set("rows", _rows);
   }

   @Override
   public void beforeRow(int rowIdx)
   {
      _currentRow = _mapper.createObjectNode();
      _rows.add(_currentRow);

      _currentRow.put("rowNumber", ++_currentRowNumber);
      _currentValues = _mapper.createArrayNode();
      _currentRow.set("values", _currentValues);
   }

   @Override
   protected void addCell(IExportDataCell cell)
   {
      ObjectNode objectNode = _mapper.createObjectNode();

      String fieldName = "value";

      if(cell.getObject() != null)
      {
         if(cell.getObject() instanceof Integer)
         {
            objectNode.put(fieldName, (Integer) cell.getObject());
         }
         else if(cell.getObject() instanceof Double)
         {
            objectNode.put(fieldName, (Double) cell.getObject());
         }
         else if(cell.getObject() instanceof Float)
         {
            objectNode.put(fieldName, (Float) cell.getObject());
         }
         else if(cell.getObject() instanceof Short)
         {
            objectNode.put(fieldName, (Short) cell.getObject());
         }
         else if(cell.getObject() instanceof Boolean)
         {
            objectNode.put(fieldName, (Boolean) cell.getObject());
         }
         else if(cell.getObject() instanceof Byte)
         {
            objectNode.put(fieldName, (Byte) cell.getObject());
         }
         else if(cell.getObject() instanceof Long)
         {
            objectNode.put(fieldName, (Long) cell.getObject());
         }
         else if(cell.getObject() instanceof BigDecimal)
         {
            objectNode.put(fieldName, (BigDecimal) cell.getObject());
         }
         else if (getPrefs().isUseGlobalPrefsFormating() && cell.getColumnDisplayDefinition() != null)
         {
            objectNode.put(fieldName, CellComponentFactory.renderObject(cell.getObject(), cell.getColumnDisplayDefinition()));
         }
         else
         {
            objectNode.put(fieldName, cell.getObject().toString());
         }
      }
      else
      {
         objectNode.put(fieldName, StringUtilities.NULL_AS_STRING);
      }

      _currentValues.add(objectNode);
   }


   @Override
   protected void addHeaderCell(int colIdx, String columnName)
   {
      _columns.add(_mapper.createObjectNode().put("name", columnName));
   }

   @Override
   protected void afterWorking() throws Exception
   {
      String jsonString = _mapper.writerWithDefaultPrettyPrinter().writeValueAsString(_root);

      try(PrintWriter pw = new PrintWriter(getFile(), getCharset().name()))
      {
         pw.print(jsonString.toCharArray());
      }
   }
}
