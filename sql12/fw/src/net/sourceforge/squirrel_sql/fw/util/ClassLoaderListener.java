/*
 * Copyright (C) CollabraSpace Inc. All rights reserved.
 */
package net.sourceforge.squirrel_sql.fw.util;

public interface ClassLoaderListener {

    public void loadedZipFile(String filename);
        
    public void finishedLoadingZipFiles();
    
}
