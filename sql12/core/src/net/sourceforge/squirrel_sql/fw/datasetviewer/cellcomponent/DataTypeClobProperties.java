package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

public class DataTypeClobProperties
{
   /** Default length of CLOB to read */
   public static int LARGE_COLUMN_DEFAULT_READ_LENGTH = 255;

   /*
    * Properties settable by the user
    */
   // flag for whether we have already loaded the properties or not
   private boolean _propertiesAlreadyLoaded = false;

   /** Read the contents of Clobs from Result sets when first loading the tables. */
   private boolean _readClobsOnTableLoading = false;

   /**
    * If <TT>_readClobs</TT> is <TT>true</TT> this specifies if the complete
    * CLOB should be read in.
    */
   private boolean _readCompleteClobs = false;

   /**
    * If <TT>_readClobs</TT> is <TT>true</TT> and <TT>_readCompleteClobs</TT>
    * is <tt>false</TT> then this specifies the number of characters to read.
    */
   private int _readClobsSize = DataTypeClobProperties.LARGE_COLUMN_DEFAULT_READ_LENGTH;

   private boolean _readClobsOnCellFocused = false;
   private boolean _readClobsOnCellVisible = false;
   private boolean _readClobsNever = true;


   /**
    * If <tt>true</tt> then show newlines as "\n" for the in-cell display,
    * otherwise do not display newlines in the in-cell display
    * (i.e. they are thrown out by JTextField when it loads the text document behind the cell).
    */
   private boolean _makeNewlinesVisibleInCell = true;

   public boolean isReadClobsOnTableLoading()
   {
      return _readClobsOnTableLoading;
   }

   public void setReadClobsOnTableLoading(boolean b)
   {
      _readClobsOnTableLoading = b;

      if(_readClobsOnTableLoading)
      {
         _readClobsOnCellFocused = false;
         _readClobsOnCellVisible = false;
         _readClobsNever = false;
      }

   }

   public boolean isReadCompleteClobs()
   {
      return _readCompleteClobs;
   }

   public void setReadCompleteClobs(boolean readCompleteClobs)
   {
      _readCompleteClobs = readCompleteClobs;
   }

   public int getReadClobsSize()
   {
      return _readClobsSize;
   }

   public void setReadClobsSize(int readClobsSize)
   {
      _readClobsSize = readClobsSize;
   }

   public boolean isMakeNewlinesVisibleInCell()
   {
      return _makeNewlinesVisibleInCell;
   }

   public void setMakeNewlinesVisibleInCell(boolean makeNewlinesVisibleInCell)
   {
      _makeNewlinesVisibleInCell = makeNewlinesVisibleInCell;
   }

   public boolean isReadClobsOnCellFocused()
   {
      return _readClobsOnCellFocused;
   }

   public void setReadClobsOnCellFocused(boolean b)
   {
      _readClobsOnCellFocused = b;

      if(_readClobsOnCellFocused)
      {
         _readClobsOnTableLoading = false;
         _readClobsOnCellVisible = false;
         _readClobsNever = false;
      }

   }

   public boolean isReadClobsOnCellVisible()
   {
      return _readClobsOnCellVisible;
   }

   public void setReadClobsOnCellVisible(boolean b)
   {
      _readClobsOnCellVisible = b;

      if(_readClobsOnCellVisible)
      {
         _readClobsOnTableLoading = false;
         _readClobsOnCellFocused = false;
         _readClobsNever = false;
      }

   }

   public boolean isReadClobsNever()
   {
      return _readClobsNever;
   }

   public void setReadClobsNever(boolean readClobsNever)
   {
      _readClobsNever = readClobsNever;
   }

   public void loadProperties()
   {

      if (_propertiesAlreadyLoaded == false)
      {
         _readClobsOnTableLoading = DTProperties.getBoolean(DataTypeClob.class, "readClobs", false);
         _readCompleteClobs = DTProperties.getBoolean(DataTypeClob.class, "readCompleteClobs", false);
         _readClobsSize= DTProperties.getInteger(DataTypeClob.class, "readClobsSize", LARGE_COLUMN_DEFAULT_READ_LENGTH);

         _readClobsOnCellFocused = DTProperties.getBoolean(DataTypeClob.class, "readClobsOnCellFocused", false);

         _readClobsOnCellVisible = DTProperties.getBoolean(DataTypeClob.class, "readClobsOnCellVisible", false);

         _readClobsNever = DTProperties.getBoolean(DataTypeClob.class, "readClobsNever", true);



         _makeNewlinesVisibleInCell= DTProperties.getBoolean(DataTypeClob.class, "makeNewlinesVisibleInCell", true);

         _propertiesAlreadyLoaded = true;
      }
   }

   public void saveProperties()
   {
      DTProperties.put(DataTypeClob.class,"readClobs", Boolean.valueOf(_readClobsOnTableLoading).toString());
      DTProperties.put(DataTypeClob.class,"readCompleteClobs", Boolean.valueOf(_readCompleteClobs).toString());
      DTProperties.put(DataTypeClob.class,"readClobsSize", Integer.toString(_readClobsSize));

      DTProperties.put(DataTypeClob.class, "readClobsOnCellFocused", Boolean.valueOf(_readClobsOnCellFocused).toString());

      DTProperties.put(DataTypeClob.class, "readClobsOnCellVisible", Boolean.valueOf(_readClobsOnCellVisible).toString());

      DTProperties.put(DataTypeClob.class, "readClobsNever", Boolean.valueOf(_readClobsNever).toString());


      DTProperties.put(DataTypeClob.class,"makeNewlinesVisibleInCell", Boolean.valueOf(_makeNewlinesVisibleInCell).toString());
   }
}
