package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.datasetviewer.ColumnDisplayDefinition;
import net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.whereClause.IWhereClausePart;
import net.sourceforge.squirrel_sql.fw.dialects.DialectType;
import net.sourceforge.squirrel_sql.fw.gui.OkJPanel;
import net.sourceforge.squirrel_sql.fw.sql.ISQLDatabaseMetaData;
import net.sourceforge.squirrel_sql.fw.sql.TableColumnInfo;
import net.sourceforge.squirrel_sql.fw.util.SquirrelConstants;
import net.sourceforge.squirrel_sql.fw.util.StringUtilities;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import javax.swing.DefaultCellEditor;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Arrays;


/**
 * @author gwg
 * <p>
 * This class is used by other parts of SQuirreL to handle all
 * DataType-specific behavior for the ContentsTab.
 * This includes reading/updating the DB, formatting data for display,
 * validating user input, converting user input into an internal object
 * of the appropriate type, and saving the data to or reading from a file.
 * The actual work is handled by separate DataType-specific classes,
 * so this class is a facade that selects the class to use and calls
 * the desired method on that class.  All of the DataType-specifc classes
 * implement the IDataTypeComponent interface.
 * <p>
 * At this time we use only the type of the data to determine which DataType
 * class to use for the requested component.  In the future it may become
 * useful to include other factors, such as the specific table and column
 * being displayed.  This info could be used to select a specialized class
 * (or a general class using an external resource file) to display table
 * and column specific translations of data, such as mapping an integer
 * code in the DB into a mnemonic representation (eg. 1='dial-up', 2='cable', 3='DSL').
 * <p>
 * The JTable is needed to allow the components to identify which cell
 * is being referred to by a double-click mouse event, which causes
 * a popup editing window to be generated.
 * <p>
 * <B>Creating new DataType handlers</B>
 * Plugins and other code may need to create and install handlers for
 * data types that are not included in the standard SQuirreL product.
 * This might be needed to handle DBMS-specific data types,
 * or to override the standard behavior for a specific data type.
 * For example:
 * <DL>
 * <LI>
 * PostgreSQL defines several non-standard data types, such as "bytea",
 * "tid", "xid", int2vector", etc.  All of these have the same SQL type-code
 * of "1111", which means "OTHER".
 * The default ContesTab operation on type 1111 is to not display it
 * and not allow editing.  However, if a plugin is able to define the
 * operations on those fields, it can register a handler that will
 * display the data appropriately and allow editing on those fields.
 * <LI>
 * If a DBMS defines a standard SQL data type in a non-standard way,
 * a plugin for that DBMS may need to override the normal DataType class
 * for that data type with another.
 * An example would be if a DBMS implemented SQL type SMALLINT,
 * which is handled internally as a Short, as an INTEGER, which is
 * handled as an Integer.
 * In order to correctly read and display values of that type in the ContentsTab,
 * the handler for SQL type SMALLINT (=5) should be changed from
 * DataTypeShort to DataTypeInteger.
 * </DL>
 * <p>
 * Here is how to create and register a DataType handler:
 * <DL>
 * <LI>
 * Using SQuirrel, connect to the DBMS.
 * Click on the "Data Types" tab.
 * Get the "TYPE_NAME" and "DATA_TYPE" values for the data type
 * for which you want to create a handler.
 * <LI>
 * Create a handler for that type of data.
 * The handler must implement the IDataTypeComponet interface.
 * The files whose names start with "DataType..."
 * in the package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent
 * (i.e. the same place as this file)
 * are examples of how to handle different data types.
 * The data must be held in the JTable as a Java object.
 * You must first identify what class of that object is.
 * It may be your own local class or one of the standard Java classes
 * since all of the code outside of the DataType class just treats it as an Object.
 * The DataType class that you create must handle all transformations
 * between that internal Java class and the Database,
 * rendering in a cell, rendering in the Popup editing window
 * (which may be the same as in a cell), and export/import with files.
 * The DataType class also determines whether or not these verious translations
 * are allowed.
 * <LI>
 * As part of the initialization of the application or plugin,
 * register the DataType class as the handler for the data type.
 * This is done using the static method registerDataType() in this class.
 * The first argument is the fully-qualified name of the method,
 * and the other two arguments identify the data type.
 * For example:
 * <PRE>
 * CellComponentFactory.registerDataType(
 * "net.sourceforge.squirrel_sql.plugins.postgreSQLPlugin.DataTypeBytea",
 * 1111, "bytea");
 * </PRE>
 * Another example, in the case where a SMALLINT is actually handled
 * by the DBMS as an integer:
 * <PRE>
 * CellComponentFactory.registerDataType(
 * "net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeInteger",
 * 5, "SHORT");
 * </PRE>
 * Once the DataType class is registered,
 * that class is called to process that data type in all of the
 * associated data type.
 * </DL>
 * <p>
 * The DataType registration process does not associate DataType handlers
 * with particular DBMSs.  Therefore, if two plugins for two different DBMSs
 * register exactly the same SQL Type code and data type name,
 * one of the databases will not be handled correctly.
 */
public class CellComponentFactory
{
   private static ILogger s_log = LoggerController.createLogger(CellComponentFactory.class);

   /**
    * Return the name of the Java class that is used to represent
    * this data type within the application.
    */
   public static String getClassName(ColumnDisplayDefinition colDef)
   {
      IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
      if (dataTypeObject != null)
         return dataTypeObject.getClassName();
      else
         return Object.class.getName();
   }

   /**
    * Determine if the values of two objects are the same.
    */
   public static boolean areEqual(ColumnDisplayDefinition colDef, Object newValue, Object oldValue)
   {

      IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);
      if (dataTypeObject != null)
         return dataTypeObject.areEqual(newValue, oldValue);

      // we should never get here because the areEqual function is only
      // called when we are trying to update the database, so we know
      // that we have a DataType object for this column (or we would
      // have been stopped from editing by the isEditableXXX methods),
      // but we need a return here to keep the compiler happy.
      return false;
   }


   /*
    * Operations for Text and in-cell work
    */

   /**
    * Render value of object as a string for text output.
    * Used by Text version of table.
    */
   public static String renderObject(Object value, ColumnDisplayDefinition colDef)
   {
      return renderObject(value, colDef, DataTypeRenderingHint.NONE);
   }

   public static String renderObject(Object value, ColumnDisplayDefinition colDef, DataTypeRenderingHint renderingHint)
   {
      IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

      if(dataTypeObject != null)
      {
         return dataTypeObject.renderObject(value, renderingHint);
      }

      // default behavior: toString
      if (null == value)
      {
         return StringUtilities.NULL_AS_STRING;
      }
      else
      {
         return value.toString();
      }
   }

   /**
    * Get a TableCellRenderer for the given column.
    */
   public static CellRenderer getTableCellRenderer(ColumnDisplayDefinition colDef)
   {
      return new CellRenderer(getDataTypeObject(null, colDef));
   }


   /**
    * Return true if the data type for the column may be edited
    * within the table cell, false if not.
    */
   public static boolean isEditableInCell(ColumnDisplayDefinition colDef,
                                          Object originalValue)
   {
      if (colDef.isAutoIncrement())
      {
         return false;
      }
      IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

      if (dataTypeObject != null)
         return dataTypeObject.isEditableInCell(originalValue);

      // there was no data type object, so this data type is unknown
      // to squirrel and thus cannot be edited.
      return false;
   }

   /**
    * See if a value in a column has been limited in some way and
    * needs to be re-read before being used for editing.
    * For read-only tables this may actually return true since we want
    * to be able to view the entire contents of the cell even if it was not
    * completely loaded during the initial table setup.
    */
   public static boolean needToReRead(ColumnDisplayDefinition colDef, Object originalValue)
   {
      IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

      if (dataTypeObject != null)
         return dataTypeObject.needToReRead(originalValue);

      // default - if we do not know the data type, then we cannot re-read it
      return false;
   }

   ;

   /**
    * Return a DefaultCellEditor using a JTextField with appropriate
    * handlers to manage the type of input for the cell.
    */
   public static DefaultCellEditor getInCellEditor(JTable table, ColumnDisplayDefinition colDef)
   {
      DefaultCellEditor ed;

      IDataTypeComponent dataTypeObject = getDataTypeObject(table, colDef);

      JTextField textField;

      // Default behavior if no data type found is to use a restorable text field
      // with no other special behavior and hope the object has a toString().
      if (dataTypeObject != null)
      {
         textField = dataTypeObject.getJTextField(table);
      }
      else
      {
         textField = new RestorableJTextField();
      }

      // When changing the backgroud color, it helps to set the inner component's border to zero.  Otherwise,
      // the border can obscure the text and make it hard to see.  This is especially seen when using the
      // kunstoff l&f.
      textField.setBackground(SquirrelConstants.CELL_EDITABLE_COLOR);
      textField.setBorder(new EmptyBorder(0, 0, 0, 0));

      ed = new CellEditorUsingRenderer(textField, dataTypeObject);
      ed.setClickCountToStart(1);
      return ed;
   }

   /**
    * Call the validate and convert method in the appropriate
    * DataType object.
    */
   public static Object validateAndConvert(ColumnDisplayDefinition colDef, Object originalValue, String inputValue, StringBuffer messageBuffer)
   {

      IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

      if (dataTypeObject != null)
      {
         // we have an appropriate data type object
         return dataTypeObject.validateAndConvert(inputValue, originalValue, messageBuffer);
      }

      // No appropriate DataType for this column, so do the best
      // we can with what we know.
      //
      // THIS MAY NOT BE THE BEST BEHAVIOR HERE!!!!!!!

      // Default Operation
      if (inputValue.equals(StringUtilities.NULL_AS_STRING))
         return null;
      else return inputValue;
   }

   /**
    * Return the flag from the component saying
    * whether to do editing in the special binary editing panel
    * or the component will handle all text input.
    */
   public static boolean useBinaryEditingPanel(ColumnDisplayDefinition colDef)
   {
      IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

      if (dataTypeObject != null)
      {
         // we have an appropriate data type object
         return dataTypeObject.useBinaryEditingPanel();
      }
      return false;   // no object, so do not assume binary editing will work
   }


   /*
    * Operations for Popup work.
    */

   /**
    * Return true if the data type for the column may be edited
    * in the popup, false if not.
    */
   public static boolean isEditableInPopup(ColumnDisplayDefinition colDef, Object originalValue)
   {
      if (colDef != null && colDef.isAutoIncrement())
      {
         return false;
      }

      IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

      if (dataTypeObject != null)
      {
         return dataTypeObject.isEditableInPopup(originalValue);
      }

      // there was no data type object, so this data type is unknown
      // to squirrel and thus cannot be edited.
      return false;
   }

   /**
    * Return a JTextArea with appropriate handlers for editing
    * the type of data in the cell.
    */
   public static RestorableRSyntaxTextArea getRestorableRSyntaxTextArea(ColumnDisplayDefinition colDef, Object value)
   {

      // The first argument is a JTable, which is only used by instances
      // of JTextField to convert coordinates on a double-click.  Since that
      // cannot happen with the JTextArea, do not bother passing the table.

      IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

      if (dataTypeObject != null)
      {
         return dataTypeObject.getRestorableRSyntaxTextArea(value, colDef);
      }

      // default behavior if no appropriate data type found is to create
      // a simple JTextArea with no special handling.
      //
      // In Theory, this cannot happen because if there is no data type object
      // for this column's data type, then isEditableInPopup returns false, so
      // we should not get here.  If there IS a data type object, and isEditableInPopup
      // returns true, then we would have executed the return statement above.
      // Assume that the value can be represented as a string.
      RestorableRSyntaxTextArea textArea = new RestorableRSyntaxTextArea();
      if (value != null)
      {
         textArea.setText(value.toString());
      }
      else
      {
         textArea.setText("");
      }

      return textArea;
   }

   /**
    * Call the validate and convert method in the appropriate
    * DataType object.
    */
   public static Object validateAndConvertInPopup(ColumnDisplayDefinition colDef, Object originalValue, String inputValue, StringBuffer messageBuffer)
   {

      IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

      if (dataTypeObject != null)
      {
         // we have an appropriate data type object
         return dataTypeObject.validateAndConvertInPopup(inputValue, originalValue, messageBuffer);
      }

      // No appropriate DataType for this column, so do the best
      // we can with what we know.
      //
      // THIS MAY NOT BE THE BEST BEHAVIOR HERE!!!!!!!

      // Default Operation
      if (inputValue.equals(StringUtilities.NULL_AS_STRING))
      {
         return null;
      }
      else
      {
         return inputValue;
      }
   }




   /*
    * DataBase-related functions
    */


   /**
    * Returns the result for the column at the specified index as determined
    * by a previously registered plugin DataTypeComponent.  Will return null
    * if the type cannot be handled by any plugin-registered DataTypeComponent.
    *
    * @param rs          the ResultSet to read
    * @param sqlType     the Java SQL type of the column
    * @param sqlTypeName the SQL type name of the column
    * @param index       the index of the column that should be read
    * @return the value as interpreted by the plugin-registered
    * DataTypeComponent, or null if no plugin DataTypeComponent has
    * been registered for the specified sqlType and sqlTypename.
    * @throws Exception
    */
   public static Object readResultWithPluginRegisteredDataType(ResultSet rs, int sqlType, String sqlTypeName, int index, DialectType dialectType) throws Exception
   {

      Object result = null;
      IDataTypeComponentFactory factory = findMatchingFactory(dialectType, sqlType, sqlTypeName);
      if (factory != null)
      {
         IDataTypeComponent dtComp = factory.constructDataTypeComponent();
         ColumnDisplayDefinition colDef = new ColumnDisplayDefinition(
               rs, index, factory.getDialectType());
         dtComp.setColumnDisplayDefinition(colDef);
         result = dtComp.readResultSet(rs, index, false);
      }
      return result;
   }

   /**
    * On input from the DB, read the data from the ResultSet into the appropriate
    * type of object to be stored in the table cell.
    */
   public static Object readResultSet(ColumnDisplayDefinition colDef, ResultSet rs, int index, boolean limitDataRead)
         throws java.sql.SQLException
   {

      IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

      if (dataTypeObject != null)
      {
         // we have an appropriate data type object
         return dataTypeObject.readResultSet(rs, index, limitDataRead);
      }

      //?? Best guess: read object?
      //?? This is probably the wrong thing to do here, but
      //?? I don't know what else to try.
      return rs.getObject(index);
   }

   /**
    * When updating the database, generate a string form of this object value
    * that can be used in the WHERE clause to match the value in the database.
    * A return value of null means that this column cannot be used in the WHERE
    * clause, while a return of "null" (or "is null", etc) means that the column
    * can be used in the WHERE clause and the value is actually a null value.
    * This function must also include the column label so that its output
    * is of the form:
    * "columnName = value"
    * or
    * "columnName is null"
    * or whatever is appropriate for this column in the database.
    */
   public static IWhereClausePart getWhereClauseValue(ColumnDisplayDefinition colDef, Object value, ISQLDatabaseMetaData md)
   {
      IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

      if (dataTypeObject != null)
      {
         // we have an appropriate data type object
         return dataTypeObject.getWhereClauseValue(value, md);
      }

      // if no object for this data type, then cannot use value in where clause
      return null;
   }

   /**
    * When updating the database, insert the appropriate datatype into the
    * prepared statment at the given variable position.
    */
   public static void setPreparedStatementValue(ColumnDisplayDefinition colDef, PreparedStatement pstmt, Object value, int position) throws java.sql.SQLException
   {

      IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

      // We should never NOT have an object here because we only get here
      // when a DataType object has claimed that the column is editable.
      // If there is no DataType for the column, then the default in the
      // isEditableXXX() methods in this class is to say that the column
      // is not editable, and therefore we should never have this method
      // called in that case.
      if (dataTypeObject != null)
      {
         // we have an appropriate data type object
         dataTypeObject.setPreparedStatementValue(pstmt, value, position);
      }
   }

   /**
    * Get a default value for the table used to input data for a new row to be
    * inserted into the DB.
    */
   static public Object getDefaultValue(ColumnDisplayDefinition colDef, String dbDefaultValue)
   {
      IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

      if (dataTypeObject != null)
         return dataTypeObject.getDefaultValue(dbDefaultValue);

      // there was no data type object, so this data type is unknown
      // to squirrel and thus cannot be edited.
      return null;
   }



   /*
    * File IO related functions
    */


   /**
    * Say whether or not object can be exported to and imported from
    * a file.  We put both export and import together in one test
    * on the assumption that all conversions can be done both ways.
    * <p>
    * If no DataType object, then we simply output as text, see {@link #exportObject(ColumnDisplayDefinition, FileOutputStream, String)} below.
    */
   public static boolean canDoFileIO(ColumnDisplayDefinition colDef)
   {
      IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

      // if no DataType object, then we simply output as text, see CellComponentFactory.exportObject(...) below
      if (dataTypeObject == null)
      {
         return true;
      }

      // let DataType object speak for itself
      return dataTypeObject.canDoFileIO();
   }

   /**
    * Read a file and construct a valid object from its contents.
    * Errors are returned by throwing an IOException containing the
    * cause of the problem as its message.
    */
   public static String importObject(ColumnDisplayDefinition colDef, FileInputStream inStream)
         throws IOException
   {

      IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

      // if no DataType object, then there is nothing to handle File IO,
      // so cannot do it
      if (dataTypeObject == null)
         throw new IOException(
               "No internal Data Type class for this column's SQL type");

      // let DataType object speak for itself
      return dataTypeObject.importObject(inStream);
   }


   /**
    * Given a text string from the Popup, validate that it makes sense
    * for the given DataType, then write it out to a file in the
    * appropriate format.
    * Errors are returned by throwing an IOException containing the
    * cause of the problem as its message.
    * <p>
    * Uses the column's {@link IDataTypeComponent} to export. If there is no such object we simply output the text.
    */
   public static void exportObject(ColumnDisplayDefinition colDef, FileOutputStream outStream, String text)
         throws IOException
   {
      IDataTypeComponent dataTypeObject = getDataTypeObject(null, colDef);

      if (dataTypeObject == null)
      {
         // if no DataType object we just write the text
         outStream.write(text.getBytes());
      }
      else
      {
         // let DataType object speak for itself
         dataTypeObject.exportObject(outStream, text);
      }
   }

   /*
    * @param dialectType
    *           the type of dialect that describes the session that is in use.
    *           This is an important component in making the key because it
    *           allows plugins for example to provide IDataTypeComponents for
    *           standard types that are only used when a session that the plugin
    *           is interested in is in use.
    * @param sqlType
    *           the JDBC type code supplied by the driver
    * @param sqlTypeName
    *           the JDBC type name supplied by the driver
    *
    * @return a key that can be used to store/retreive a custom type.
    */
   private static IDataTypeComponentFactory findMatchingFactory(DialectType dialectType, int sqlType, String sqlTypeName)
   {
      return Main.getApplication().getDataTypeComponentFactoryRegistry().findMatchingFactory(dialectType, sqlType, sqlTypeName);
   }


   /*
    * Get control panels to let user adjust properties
    * on DataType classes.
    */

   /**
    * Get the Control Panels (JPanels containing controls) that let the
    * user adjust the properties of static properties in specific DataTypes.
    * The only DataType objects checked for here are:
    * - those that are registered through the registerDataType method, and
    * - those that are specifically listed in the variable initialClassNameList
    */
   public static OkJPanel[] getControlPanels()
   {
      ArrayList<OkJPanel> panelList = new ArrayList<OkJPanel>();

      /*
       * This is the list of names of classes that:
       * 	- support standard SQL type codes and thus do not need to be registered
       * 	- provide the getControlPanel method to allow manipulation of properties
       * These classes should all be named
       * 	net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeXXXX
       * because they are part of the standard delivery of the product, and thus should
       * be local to this directory.
       */
      String[] initialClassNameList = {
            net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeGeneral.class.getName(),
            net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeBlob.class.getName(),
            net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeClob.class.getName(),
            net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeString.class.getName(),
            net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeOther.class.getName(),
            net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeUnknown.class.getName(),
            net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeDate.class.getName(),
            net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeTime.class.getName(),
            net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.DataTypeTimestamp.class.getName(),
            net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent.FloatingPointBase.class.getName(),
      };


      // make a single list of all class names that we need to check.
      // Start with the names of known, standard classes that provide Control Panels
      ArrayList<String> classNameList =
            new ArrayList<String>(Arrays.asList(initialClassNameList));

      // add to that the list of all names that have been registered by plugins
//		Iterator<IDataTypeComponentFactory> pluginDataTypeFactories = 
//		    _registeredDataTypes.values().iterator();
//		while (pluginDataTypeFactories.hasNext()) {
//		    TODO: add support for plugin-registered data-type preferences panels
//		          when it is needed.
//		}

      // Now go through the list in the given order to get the panels
      for (int i = 0; i < classNameList.size(); i++)
      {
         String className = classNameList.get(i);
         Class<?>[] parameterTypes = new Class<?>[0];
         try
         {
            Method panelMethod =
                  Class.forName(className).getMethod("getControlPanel", parameterTypes);

            OkJPanel panel = (OkJPanel) panelMethod.invoke(null, (Object[]) null);
            panelList.add(panel);
         }
         catch (Exception e)
         {
            s_log.error("Unexpected exception: " + e.getMessage(), e);
         }
      }

      return panelList.toArray(new OkJPanel[0]);
   }


   /*
    * Internal method used for both cell and popup work.
    */

   /**
    * Identify the type of data in the cell and get an instance
    * of the appropriate DataType object to work with it.
    * <p>
    * The JTable argument is used only by the DataType objects, not here.
    * Also, since it is used only for converting coordinates when the user
    * double-clicks in a cell, the JTextArea component does not use it, so
    * it may be null in that case.
    * <p>
    * NOTE: This currently gets a new copy of the DataType object for every
    * column even when multiple columns have the same SQL data type.  JTable's
    * Render and Edit operations typically re-use the same CellRenderer and
    * CellEditor objects for every cell by moving the viewpoint of the component
    * to the location of the cell to be rendered/edited, setting the value in
    * the component to the value at that cell, telling the component to paint,
    * then moving that same component to the next cell.  For us, the cells have
    * specific syntax or size constraints based on the SQL data type and the
    * metadata from the DB, so we use different CellRenderer/Editor components
    * for each column.  However, JTable's rendering/editing algorithm allows
    * us to re-use the same component for all cells in the same column, which
    * is what we do.  By saving the component, we avoid the need to create
    * new instances each time the userstarts editing, creates the popup dialog,
    * or does an operation requireing a static method call (e.g. validateAndConvert).
    *
    * @param table  the JTable that will render the cells
    * @param colDef the ColumnDisplayDefinition that describes the column.  It
    *               contains SQL type, SQL type name and DialectType, and these
    *               three criteria are examined to determine if a type has been
    *               registered
    */
   public static IDataTypeComponent getDataTypeObject(JTable table, ColumnDisplayDefinition colDef)
   {
      IDataTypeComponent dataTypeComponent;

      dataTypeComponent = getCustomDataType(table, colDef);

      if(dataTypeComponent == null)
      {
         // we have not already created a DataType object for this column
         // so do that now and save it
         dataTypeComponent = getGenericDataType(table, colDef);
      }


      // If we get here, then no data type object was found for this column.
      // (should not get here because switch default returns null.)
      return dataTypeComponent;
   }

   /**
    * Look for a plugin-registered custom IDataTypeComponent implementation.
    *
    * @param table  the JTable that will render the cells
    * @param colDef the ColumnDisplayDefinition that describes the column.  It
    *               contains SQL type, SQL type name and DialectType, and these
    *               three criteria are examined to determine if a type has been
    *               registered
    * @return the plugin-registered IDataTypeCompoenent, or null if no plugin
    * has registered one for the column specified by colDef.
    */
   private static IDataTypeComponent getCustomDataType(JTable table, ColumnDisplayDefinition colDef)
   {
      IDataTypeComponent dataTypeComponent = null;
      if (dataTypeComponent == null && colDef.getDialectType() != null)
      {
         IDataTypeComponentFactory factory = findMatchingFactory(colDef.getDialectType(), colDef.getSqlType(), colDef.getSqlTypeName());
         if (factory != null)
         {
            dataTypeComponent = factory.constructDataTypeComponent();
            if (colDef != null)
            {
               dataTypeComponent.setColumnDisplayDefinition(colDef);
            }
         }
      }
      return dataTypeComponent;
   }

   /**
    * @param table
    * @param colDef
    * @return
    */
   private static IDataTypeComponent getGenericDataType(JTable table, ColumnDisplayDefinition colDef)
   {
      IDataTypeComponent dataTypeComponent = null;

      // Use the standard SQL type code to get the right handler
      // for this data type.
      if (dataTypeComponent == null)
      {
         switch (colDef.getSqlType())
         {
            case Types.NULL: // should never happen
               if (s_log.isDebugEnabled())
               {
                  s_log.debug("getGenericDataType: encountered an sql type = Types.NULL for column: " +
                        colDef.getFullTableColumnName() + ". A DataTypeComponent is not available for this type.");
               }
               break;

            case Types.BIT:
            case Types.BOOLEAN:
               dataTypeComponent = new DataTypeBoolean(table, colDef);
               break;

            case Types.TIME:
               dataTypeComponent = new DataTypeTime(table, colDef);
               break;

            case Types.DATE:
               // Some databases store a time component in DATE columns (Oracle)
               // The user can set a preference for DATEs that allows them
               // to be read as TIMESTAMP columns instead. This doesn't
               // appear to have ill effects for databases that are standards
               // compliant (such as MySQL or PostgreSQL).  If the user
               // prefers it, use the TIMESTAMP data type instead of DATE.
               if (DataTypeDate.getReadDateAsTimestamp())
               {
                  colDef.setSqlType(Types.TIMESTAMP);
                  colDef.setSqlTypeName("TIMESTAMP");
                  dataTypeComponent = new DataTypeTimestamp(table, colDef);
               }
               else
               {
                  dataTypeComponent = new DataTypeDate(table, colDef);
               }
               break;

            case Types.TIMESTAMP:
            case -101: // Oracle's 'TIMESTAMP WITH TIME ZONE' == -101
            case -102: // Oracle's 'TIMESTAMP WITH LOCAL TIME ZONE' == -102
            case -155: // MSSQL Server's 'TIMESTAMP WITH LOCAL TIME ZONE' == -155
               dataTypeComponent = new DataTypeTimestamp(table, colDef);
               break;

            case Types.BIGINT:
               dataTypeComponent = new DataTypeLong(table, colDef);
               break;

            case Types.DOUBLE:
            case Types.FLOAT:
               dataTypeComponent = new DataTypeDouble(table, colDef);
               break;

            case Types.REAL:
               dataTypeComponent = new DataTypeFloat(table, colDef);
               break;

            case Types.DECIMAL:
            case Types.NUMERIC:
               dataTypeComponent = new DataTypeBigDecimal(table, colDef);
               break;

            case Types.INTEGER:
               // set up for integers
               dataTypeComponent = new DataTypeInteger(table, colDef);
               break;

            case Types.SMALLINT:
               dataTypeComponent = new DataTypeShort(table, colDef);
               break;

            case Types.TINYINT:
               dataTypeComponent = new DataTypeByte(table, colDef);
               break;

            case Types.CHAR:
            case Types.NCHAR:
            case Types.VARCHAR:
            case Types.NVARCHAR:
            case Types.LONGVARCHAR:
            case Types.LONGNVARCHAR:
               // set up for string types
               dataTypeComponent = new DataTypeString(table, colDef);
               break;

            // -8 is ROWID in Oracle. It's a string, but it's auto-assigned
            case Types.ROWID:
               dataTypeComponent = new DataTypeString(table, colDef);
               // Oracle jdbc driver doesn't properly identify this column
               // in ResultSetMetaData as read-only. For now, just use
               // isAutoIncrement flag to simulate this setting.
               colDef.setIsAutoIncrement(true);
               break;

            case Types.BINARY:
            case Types.VARBINARY:
            case Types.LONGVARBINARY:
               // set up for Binary types
               dataTypeComponent = new DataTypeBinary(table, colDef);
               break;

            case Types.BLOB:
               dataTypeComponent = new DataTypeBlob(table, colDef);
               break;

            case Types.CLOB:
               dataTypeComponent = new DataTypeClob(table, colDef);
               break;

            // TODO: ResultSet has it's own NCLOB support (rs.getNClob(i)).  It is probably not valid to
            // call getClob on an NClob column ??  So, may need to implement new DataTypeNClob type
            // component (see below):
            //
            //case Types.NCLOB:
            //  dataTypeComponent = new DataTypeNClob(table, colDef);

            case Types.OTHER:
               dataTypeComponent = new DataTypeOther(table, colDef);
               break;

            //Add begin
            case Types.JAVA_OBJECT:
               dataTypeComponent = new DataTypeJavaObject(table, colDef);
               break;
            //Add end

            default:
               // data type is unknown to us.
               // It may be an unusual type like "JAVA OBJECT" or "ARRAY",
               // or it may be a DBMS-specific type
               dataTypeComponent = new DataTypeUnknown(table, colDef);

         }
      }
      return dataTypeComponent;
   }

   /**
    * Allows to get Cell Component based on TableColumnInfo and DialectType.
    * Table is just for compatibility with other methods. Might be null
    *
    * @param table
    * @param column
    * @param dialectType
    * @return
    */
   public static IDataTypeComponent getDataTypeObject(JTable table, TableColumnInfo column, DialectType dialectType)
   {
      ColumnDisplayDefinition colDef = new ColumnDisplayDefinition(0, "");
      colDef.setColumnName(column.getColumnName());
      colDef.setDialectType(dialectType);
      colDef.setSqlType(column.getDataType());
      colDef.setSqlTypeName(column.getTypeName());
      return getDataTypeObject(table, colDef);
   }

   /**
    * Gets prefix column for select. Components can override it in order to modify select clause.
    * For example retrieving all geometries does not make any sense. Better is to get only summary and reRead on demand
    *
    * @param table
    * @param tableColumnInfo
    * @param dialectType
    * @param prefix
    * @return
    */
   public static String getColumnForContentSelect(JTable table, TableColumnInfo tableColumnInfo, DialectType dialectType, String prefix)
   {
      return getDataTypeObject(table, tableColumnInfo, dialectType).getColumnForContentSelect(dialectType, prefix);
   }

}
