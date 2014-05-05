package org.squirrelsql.services.sqlwrap;

import org.squirrelsql.drivers.ClassLoaderListener;
import org.squirrelsql.drivers.EnumerationIterator;
import org.squirrelsql.services.Conversions;
import org.squirrelsql.services.I18n;
import org.squirrelsql.services.MessageHandler;
import org.squirrelsql.services.MessageHandlerDestination;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;


public class SQLURLClassLoader extends URLClassLoader
{
   private MessageHandler _mh = new MessageHandler(getClass(), MessageHandlerDestination.MESSAGE_PANEL);

   private I18n _i18n = new I18n(this.getClass());

   private Map<String, Class> _classes = new HashMap<String, Class>();

   List<ClassLoaderListener> listeners = new ArrayList<ClassLoaderListener>();

   public SQLURLClassLoader(String fileName) throws IOException
   {
      this(new File(fileName).toURI().toURL());
   }

   public SQLURLClassLoader(URL url)
   {
      this(Arrays.asList(url));
   }

   public SQLURLClassLoader(List<URL> urls)
   {
      super(urls.toArray(new URL[urls.size()]), ClassLoader.getSystemClassLoader());
   }

   public void addClassLoaderListener(ClassLoaderListener listener)
   {
      if (listener != null)
      {
         listeners.add(listener);
      }
   }

   /**
    * Notify listeners that we're loading the specified file.
    *
    * @param filename the name of the file (doesn't include path)
    */
   private void notifyListenersLoadedZipFile(String filename)
   {
      Iterator<ClassLoaderListener> i = listeners.iterator();
      while (i.hasNext())
      {
         ClassLoaderListener listener = i.next();
         listener.loadedZipFile(filename);
      }
   }

   /**
    * Notify listeners that we've finished loading files.
    */
   private void notifyListenersFinished()
   {
      Iterator<ClassLoaderListener> i = listeners.iterator();
      while (i.hasNext())
      {
         ClassLoaderListener listener = i.next();
         listener.finishedLoadingZipFiles();
      }
   }

   public void removeClassLoaderListener(ClassLoaderListener listener)
   {
      listeners.remove(listener);
   }

   public List<Class> getAssignableClasses(Class type)
   {
      List<Class> classes = new ArrayList<>();
      URL[] urls = getURLs();
      for (int i = 0; i < urls.length; ++i)
      {
         URL url = urls[i];

         File file = getFileFromUrl(url);
         if (file == null)
         {
            continue;
         }

         if (!file.isDirectory() && file.exists() && file.canRead())
         {
            try (ZipFile zipFile = new ZipFile(file))
            {
	            notifyListenersLoadedZipFile(file.getName());
	
	
	            for (Iterator it = new EnumerationIterator(zipFile.entries()); it.hasNext(); )
	            {
	               Class cls = null;
	               String entryName = ((ZipEntry) it.next()).getName();
	               String className = Conversions.changeFileNameToClassName(entryName);
	               if (className != null)
	               {
	                  try
	                  {
	                     cls = Class.forName(className, false, this);
	                  }
	                  catch (Throwable th)
	                  {
	                  }
	                  if (cls != null)
	                  {
	                     if (type.isAssignableFrom(cls))
	                     {
	                        classes.add(cls);
	                     }
	                  }
	               }
	            }
            }
            catch (IOException ex)
            {
               Object[] args = {file.getAbsolutePath(),};
               String msg = _i18n.t("MyURLClassLoader.errorLoadingFile", args);
               _mh.error(msg, ex);
               continue;
            }

         } 
      }
      notifyListenersFinished();
      return classes;
   }

   /**
    * Returns a File object whose absolute path is equivalent to the specified URL (minus any URL encoding
    * fragments)
    *
    * @param url    the URL to get a File for
    * @return a valid File, or null if the URL cannot be converted to a File object
    */
   private File getFileFromUrl(URL url)
   {
      File file = null;
      try
      {
         // Bug 2480365: It is very important to get the URI from the URL and not simply do
         // new File(url.getFile()).  In the case where the path contains spaces, they get encoded as %20 in
         // the string returned from url.getFile().  java.io.File doesn't know how to deal with them
         // (i.e. replace them with spaces) and the resulting File is rendered non-existent.
         URI fileUri = url.toURI();
         file = new File(url.toURI());
      }
      catch (URISyntaxException e)
      {
         _mh.error("Unable to convert URL (" + url + ") to a URI:" + e.getMessage(), e);
      }
      return file;
   }

   protected synchronized Class findClass(String className) throws ClassNotFoundException
   {
      Class cls = _classes.get(className);
      if (cls == null)
      {
         cls = super.findClass(className);
         _classes.put(className, cls);
      }
      return cls;
   }

   @SuppressWarnings("unused")
   protected void classHasBeenLoaded(Class cls)
   {
      // Empty
   }
}
