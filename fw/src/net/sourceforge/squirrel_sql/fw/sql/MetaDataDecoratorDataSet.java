package net.sourceforge.squirrel_sql.fw.sql;
/*
 * Copyright (C) 2006 Rob Manning
 * manningr@users.sourceforge.net
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */

import net.sourceforge.squirrel_sql.fw.util.IMessageHandler;
import net.sourceforge.squirrel_sql.fw.util.StringManager;
import net.sourceforge.squirrel_sql.fw.util.StringManagerFactory;

import java.io.File;
import java.sql.DatabaseMetaData;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * A simple decorator of MetaDataDataSet which adds two additional properties - 
 * which are not really MetaData, so here I'm cheating just a little - for the
 * purpose of telling the user which JDBC driver CLASSNAME is in use for this
 * session and what jar files make up the 'CLASSPATH' for the driver being used
 * in this session.  I call this class a decorator of MetaDataDataSet since it 
 * wraps this class, providing the two aforementioned additional properties. 
 * 
 * @author manningr
 */
public class MetaDataDecoratorDataSet extends MetaDataDataSet {

    /** Internationalized strings for this class. */
    private static final StringManager s_stringMgr =
       StringManagerFactory.getStringManager(MetaDataDecoratorDataSet.class);
    
    boolean finishedLocalRows = false;
    
    Iterator<Object[]> iter = null;
    ArrayList<Object[]> data = new ArrayList<Object[]>();
    
    Object[] currentRow = null;
    
    private static interface i18n {
        //1i8n[MetaDataDecoratorDataSet.classNameLabel=JDBC Driver CLASSNAME]
        String CLASS_NAME_LABEL = 
            s_stringMgr.getString("MetaDataDecoratorDataSet.classNameLabel");
        //1i8n[MetaDataDecoratorDataSet.classPathLabel=JDBC Driver CLASSPATH]
        String CLASS_PATH_LABEL = 
            s_stringMgr.getString("MetaDataDecoratorDataSet.classPathLabel");
        //i18n[MetaDataDecoratorDataSet.noJarFiles=No files specified in 'Extra ClassPath' tab for driver]
        String NO_JAR_FILES = 
            s_stringMgr.getString("MetaDataDecoratorDataSet.noJarFiles");
    }
    
    /**
     * 
     * @param md
     * @param driverClassName
     * @param jarFileNames
     */
    public MetaDataDecoratorDataSet(DatabaseMetaData md, String driverClassName, String[] jarFileNames) 
    {
        super(md, null);
        Object[] className =
            new Object[] {i18n.CLASS_NAME_LABEL, driverClassName};
        
        data.add(className);
        String[] classPathFiles = jarFileNames;
        StringBuffer classPathBuffer = new StringBuffer();
        if (classPathFiles.length == 0) {
            classPathBuffer.append(i18n.NO_JAR_FILES);
        } else {
            for (int i = 0; i < classPathFiles.length; i++) {
                classPathBuffer.append(classPathFiles[i]);
                if (i+1 < classPathFiles.length) {
                    classPathBuffer.append(File.pathSeparator);
                }
            }            
        }
        Object[] classPath = 
            new Object[] {i18n.CLASS_PATH_LABEL,
                          classPathBuffer.toString()};
        data.add(classPath);
        iter = data.iterator();
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.MetaDataDataSet#get(int)
     */
    public synchronized Object get(int columnIndex) {
        if (finishedLocalRows) {
            return super.get(columnIndex);
        } else {
            return currentRow[columnIndex];
        }
    }

    /* (non-Javadoc)
     * @see net.sourceforge.squirrel_sql.fw.sql.MetaDataDataSet#next(net.sourceforge.squirrel_sql.fw.util.IMessageHandler)
     */
    public synchronized boolean next(IMessageHandler msgHandler) {
        if (finishedLocalRows) {
            return super.next(msgHandler);
        } else {
            if (iter.hasNext()) {
                currentRow = iter.next();
                return true;
            } else {
                finishedLocalRows = true;
                return super.next(msgHandler);
            }
        }
    }
    
    
    
    
}
