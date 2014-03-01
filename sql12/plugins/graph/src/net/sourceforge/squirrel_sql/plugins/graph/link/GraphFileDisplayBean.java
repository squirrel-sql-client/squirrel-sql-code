package net.sourceforge.squirrel_sql.plugins.graph.link;

import net.sourceforge.squirrel_sql.plugins.graph.xmlbeans.GraphXmlSerializer;

import javax.swing.*;
import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

public class GraphFileDisplayBean
{
   public static final String NOT_LOADED_DUMMY_NAME = "...";
   private StringBuffer _name = new StringBuffer(NOT_LOADED_DUMMY_NAME);
   private File _graphFile;


   private GraphXmlSerializer _serializer;
   private volatile boolean _nameLoadInitialized;
   private NameLoadFinishListener _nameLoadFinishListener;
   private ExecutorService _executorService;

   public GraphFileDisplayBean(File graphFile, GraphXmlSerializer serializer, NameLoadFinishListener nameLoadFinishListener, ExecutorService executorService)
   {
      _serializer = serializer;
      _graphFile = graphFile;
      _nameLoadFinishListener = nameLoadFinishListener;
      _executorService = executorService;
   }

   public StringBuffer getName()
   {
      if(false == _nameLoadInitialized)
      {
         _nameLoadInitialized = true;

         SwingWorker<String, Object> sw =
            new SwingWorker<String, Object>()
            {
               @Override
               protected String doInBackground() throws Exception
               {
                  _name.setLength(0);
                  _name.append(_serializer.read().getTitle());
                  return null;
               }

               @Override
               protected void done()
               {
                  _nameLoadFinishListener.finishedNameLoad();
               }
            };

         _executorService.submit(sw);
      }

      return _name;
   }

   public String getGraphFileName()
   {
      return _graphFile.getName();
   }

   public File getGraphFile()
   {
      return _graphFile;
   }
   
   String getLoadedName()
   {
      try
      {
         if(NOT_LOADED_DUMMY_NAME.equals(_name.toString()))
         {
            getName();
            _executorService.awaitTermination(60, TimeUnit.SECONDS);
         }

         return _name.toString();
      }
      catch (InterruptedException e)
      {
         throw new RuntimeException(e);
      }
   }
}
