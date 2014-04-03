package org.squirrelsql.drivers;

public interface ClassLoaderListener
{

   public void loadedZipFile(String filename);

   public void finishedLoadingZipFiles();

}
