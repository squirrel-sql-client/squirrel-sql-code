package net.sourceforge.squirrel_sql.client.gui.dnd;

import net.sourceforge.squirrel_sql.client.IApplication;
import net.sourceforge.squirrel_sql.client.Main;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;
import net.sourceforge.squirrel_sql.fw.util.log.ILogger;
import net.sourceforge.squirrel_sql.fw.util.log.LoggerController;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDropEvent;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

public class DropedFileExtractor
{
   /** Logger for this class. */
   private static final ILogger s_log =
       LoggerController.createLogger(DropedFileExtractor.class);
   /** Internationalized strings for this class. */
   private static final StringManager s_stringMgr =
       StringManagerFactory.getStringManager(DropedFileExtractor.class);

   public static File getFile(DropTargetDropEvent dtde, IApplication app)
   {
      List<File> files = _getFiles(dtde, app, true);
      if (0 == files.size())
      {
         return null;
      }
      else
      {
         return files.get(0);
      }
   }

   public static List<File> getFiles(DropTargetDropEvent dtde)
   {
      return _getFiles(dtde, Main.getApplication(), true);
   }
   private static List<File> _getFiles(DropTargetDropEvent dtde, IApplication app, boolean allowMoreThanOneFile)
   {
      try
      {
         DropTargetContext context = dtde.getDropTargetContext();
         dtde.acceptDrop(DnDConstants.ACTION_COPY_OR_MOVE);
         Transferable t = dtde.getTransferable();
         List<File> filesToOpen = new ArrayList<File>();

         if (t.isDataFlavorSupported(DataFlavor.javaFileListFlavor)) {
             filesToOpen = handleJavaFileListFlavor(t, app, allowMoreThanOneFile);
         } else if (t.isDataFlavorSupported(DataFlavor.stringFlavor)) {
             filesToOpen = handleStringFlavor(t, app, allowMoreThanOneFile);
         } else {
             filesToOpen = handleUriListFlavor(t);
         }
         context.dropComplete(true);

         if (null != filesToOpen)
         {
            return filesToOpen;
         }
         else
         {
            return new ArrayList<File>();
         }
      }
      catch (Exception e)
      {
         throw new RuntimeException(e);
      }
   }

   private static List<File> handleUriListFlavor(Transferable transferable)
        throws ClassNotFoundException
    {
        DataFlavor uriListFlavor = new DataFlavor("text/uri-list;class=java.lang.String");
        File result = null;
        try {
            if (transferable.isDataFlavorSupported(uriListFlavor)) {
                String data = (String)transferable.getTransferData(uriListFlavor);
                List<File> fileList = (textURIListToFileList(data));
                result = fileList.get(0);
            } else {
                s_log.error("handleUriListFlavor: no support for "
                        + "text/uri-list data flavor");
            }
        } catch (Exception e) {
            s_log.error("handleUriListFlavor: unexpected excption - "+e.getMessage(), e);

        }
       ArrayList<File> ret = new ArrayList<File>();
       ret.add(result);
       return ret;
    }

   /**
     * Handles the String data flavor which returns the data as a list of
     * java.io.File objects.
     *
     *
    * @param t
    *            the transferable to get the list from
    * @param app
   * @param allowMoreThanOneFile
    * @return the only file in the list
     * @throws java.awt.datatransfer.UnsupportedFlavorException
     * @throws java.io.IOException
     */
    private static List<File> handleStringFlavor(Transferable t, IApplication app, boolean allowMoreThanOneFile)
        throws UnsupportedFlavorException, IOException
    {
        ArrayList<File> result = new ArrayList<File>();

        String transferData =
            (String)t.getTransferData(DataFlavor.stringFlavor);

        if (transferData != null) {
            // Check to see if the string is a file uri.
            if (transferData.startsWith("file://")) {
                try {
                    // we can have more than one file in the string so tokenize
                    // on whitespace.  Let the user know if we find multiple
                    // tokens that they cannot place drop than one file at a
                    // time
                    StringTokenizer st = new StringTokenizer(transferData);
                    if (st.countTokens() > 1 && false == allowMoreThanOneFile) {
                       app.getMessageHandler().showErrorMessage(i18n.ONE_FILE_DROP_MESSAGE);
                    } else {
                        while (st.hasMoreTokens()) {
                            String fileUrlStr = st.nextToken();
                            URI uri = new URI(fileUrlStr);
                           if (uri.isAbsolute())
                           {
                              result.add(new File(uri));
                           }
                        }
                    }
                } catch (URISyntaxException e) {
                    s_log.error("handleUriListString: encountered an "
                            + "invalid URI: " + transferData, e);
                }
            } else {
                // Not a uri - assume it is a string filename.
                result.add(new File(transferData));
            }
        }
       return result;
    }

   /**
    * Handles the JavaFileList data flavor which returns the data as a list of
    * java.io.File objects.
    *
    *
    * @param t
    *            the transferable to get the list from

    * @param app
    * @param allowMoreThanOneFile
    * @return the only file in the list
    * @throws java.awt.datatransfer.UnsupportedFlavorException
    * @throws java.io.IOException
    */
   private static List<File> handleJavaFileListFlavor(Transferable t, IApplication app, boolean allowMoreThanOneFile)
       throws UnsupportedFlavorException, IOException
   {
      List<File> result = null;

       @SuppressWarnings("unchecked")
       List<File> transferData =
           (List<File>)t.getTransferData(DataFlavor.javaFileListFlavor);

       if (transferData == null || transferData.size() == 0) {
          s_log.error("Transferable.getTransferData returned a null/empty list");
          app.getMessageHandler().showErrorMessage(i18n.INTERNAL_ERROR_MESSAGE);
       } else if (transferData.size() > 1 && false == allowMoreThanOneFile) {
          app.getMessageHandler().showErrorMessage(i18n.ONE_FILE_DROP_MESSAGE);
       } else {
           result = transferData;
           if (s_log.isInfoEnabled()) {
               s_log.info("drop: path="+result.get(0).getAbsolutePath());
           }

       }
       return result;
   }

   private static List<File> textURIListToFileList(String data) {
       List<File> list = new ArrayList<File>(1);
       for (StringTokenizer st = new StringTokenizer(data, "\r\n");
               st.hasMoreTokens();) {
           String s = st.nextToken();
           if (s.startsWith("#")) {
               // the line is a comment (as per the RFC 2483)
               continue;
           }
           try {
               URI uri = new URI(s);
               File file = new File(uri);
               list.add(file);
           } catch (URISyntaxException e) {
               // malformed URI
           } catch (IllegalArgumentException e) {
               // the URI is not a valid 'file:' URI
           }
       }
       return list;
   }

   private static interface i18n {

       //i18n[FileEditorDropTargetListener.oneFileDropMessage=Only one file
       //may be dropped onto the editor at a time.]
       String ONE_FILE_DROP_MESSAGE =
           s_stringMgr.getString("FileEditorDropTargetListener.oneFileDropMessage");

       //i18n[FileEditorDropTargetListener.internalErrorMessage=Internal error occurred.
       //See log for details]
       String INTERNAL_ERROR_MESSAGE =
          s_stringMgr.getString("FileEditorDropTargetListener.internalErrorMessage");
   }
}
