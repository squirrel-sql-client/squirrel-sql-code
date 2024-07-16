package net.sourceforge.squirrel_sql.fw.datasetviewer.cellcomponent;

public class DataTypeBlobProperties
{
   /** Default length of BLOB to read */
   public static final int LARGE_COLUMN_DEFAULT_READ_LENGTH = 255;

   /*
    * Properties settable by the user
    */
   // flag for whether we have already loaded the properties or not
   private boolean _propertiesAlreadyLoaded = false;


   /** Read the contents of Blobs from Result sets when first loading the tables. */
   private boolean _readBlobsOnTableLoading = false;

   /**
    * If <TT>_readBlobs</TT> is <TT>true</TT> this specifies if the complete
    * BLOB should be read in.
    */
   private boolean _readCompleteBlobs = false;

   /**
    * If <TT>_readBlobs</TT> is <TT>true</TT> and <TT>_readCompleteBlobs</TT>
    * is <tt>false</TT> then this specifies the number of characters to read.
    */
   private int _readBlobsSize = DataTypeBlobProperties.LARGE_COLUMN_DEFAULT_READ_LENGTH;

   private boolean _readBlobsOnCellFocused = false;
   private boolean _readBlobsOnCellVisible = false;
   private boolean _readBlobsNever = true;

   void loadProperties()
   {
      if(_propertiesAlreadyLoaded == false)
      {
         _readBlobsOnTableLoading = DataTypeProps.getBooleanProperty(DataTypeBlob.class, "readBlobs", false);
         _readCompleteBlobs = DataTypeProps.getBooleanProperty(DataTypeBlob.class, "readCompleteBlobs", false);
         _readBlobsSize = DataTypeProps.getIntegerProperty(DataTypeBlob.class, "readBlobsSize", LARGE_COLUMN_DEFAULT_READ_LENGTH);

         _readBlobsOnCellFocused = DataTypeProps.getBooleanProperty(DataTypeBlob.class, "readBlobsOnCellFocused", false);

         _readBlobsOnCellVisible = DataTypeProps.getBooleanProperty(DataTypeBlob.class, "readBlobsOnCellVisible", false);

         _readBlobsNever = DataTypeProps.getBooleanProperty(DataTypeBlob.class, "readBlobsNever", true);

         _propertiesAlreadyLoaded = true;
      }
   }

   public void saveProperties()
   {
      DataTypeProps.putDataTypeProperty(DataTypeBlob.class, "readBlobs", Boolean.valueOf(_readBlobsOnTableLoading).toString());
      DataTypeProps.putDataTypeProperty(DataTypeBlob.class, "readCompleteBlobs", Boolean.valueOf(_readCompleteBlobs).toString());
      DataTypeProps.putDataTypeProperty(DataTypeBlob.class, "readBlobsSize", Integer.toString(_readBlobsSize));

      DataTypeProps.putDataTypeProperty(DataTypeBlob.class, "readBlobsOnCellFocused", Boolean.valueOf(_readBlobsOnCellFocused).toString());

      DataTypeProps.putDataTypeProperty(DataTypeBlob.class, "readBlobsOnCellVisible", Boolean.valueOf(_readBlobsOnCellVisible).toString());

      DataTypeProps.putDataTypeProperty(DataTypeBlob.class, "readBlobsNever", Boolean.valueOf(_readBlobsNever).toString());
   }


   public boolean isReadBlobsOnTableLoading()
   {
      return _readBlobsOnTableLoading;
   }

   public void setReadBlobsOnTableLoading(boolean b)
   {
      _readBlobsOnTableLoading = b;

      if(_readBlobsOnTableLoading)
      {
         _readBlobsOnCellFocused = false;
         _readBlobsOnCellVisible = false;
         _readBlobsNever = false;
      }
   }

   public boolean isReadCompleteBlobs()
   {
      return _readCompleteBlobs;
   }

   public void setReadCompleteBlobs(boolean b)
   {
      _readCompleteBlobs = b;
   }

   public int getReadBlobsSize()
   {
      return _readBlobsSize;
   }

   public void setReadBlobsSize(int readBlobsSize)
   {
      _readBlobsSize = readBlobsSize;
   }

   public void setReadBlobsOnCellFocused(boolean b)
   {
      _readBlobsOnCellFocused = b;

      if(_readBlobsOnCellFocused)
      {
         _readBlobsOnTableLoading = false;
         _readBlobsOnCellVisible = false;
         _readBlobsNever = false;
      }
   }

   public boolean isReadBlobsOnCellFocused()
   {
      return _readBlobsOnCellFocused;
   }

   public void setReadBlobsOnCellVisible(boolean b)
   {
      _readBlobsOnCellVisible = b;

      if(_readBlobsOnCellVisible)
      {
         _readBlobsOnTableLoading = false;
         _readBlobsOnCellFocused = false;
         _readBlobsNever = false;
      }

   }

   public boolean isReadBlobsOnCellVisible()
   {
      return _readBlobsOnCellVisible;
   }

   public boolean isReadBlobsNever()
   {
      return _readBlobsNever;
   }

   public void setReadBlobsNever(boolean b)
   {
      _readBlobsNever = b;

      if(_readBlobsNever)
      {
         _readBlobsOnTableLoading = false;
         _readBlobsOnCellFocused = false;
         _readBlobsOnCellVisible = false;
      }
   }
}
